/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.handler.codec;

import static java.lang.String.format;
import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.util.CharsetUtil.UTF_8;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferIndexFinder;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

import org.kaazing.robot.buffer.FastIndexFinder;
import org.kaazing.robot.lang.el.ExpressionContext;
import org.kaazing.robot.lang.regex.NamedGroupMatcher;
import org.kaazing.robot.lang.regex.NamedGroupPattern;

public class ReadRegexDecoder extends MessageDecoder {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ReadRegexDecoder.class);

    private final NamedGroupPattern pattern;
    private final Charset charset;
    private final ExpressionContext environment;
    private ChannelBuffer terminator;
    private ChannelBufferIndexFinder indexFinder;

    public ReadRegexDecoder(final NamedGroupPattern pattern, final Charset charset, final ExpressionContext environment) {
        this.pattern = pattern;
        this.environment = environment;
        this.charset = charset;
    }

    @Deprecated
    // provided for backward computability with Robot format V1 which uses a terminator
    public ReadRegexDecoder(final NamedGroupPattern pattern, final Charset charset, String terminator,
            final ExpressionContext environment) {
        this(pattern, charset, environment);
        this.terminator = copiedBuffer(terminator, charset);
        this.indexFinder = new FastIndexFinder(this.terminator);
    }

    // TODO: In the case of abort or a close event we need to make sure this gets called if a read is still pending so
    // that the script is accurate.
    @Override
    protected Object decodeBufferLast(final ChannelBuffer buffer) throws Exception {
        return decodeBuffer0(buffer, true);
    }

    @Override
    protected Object decodeBuffer(final ChannelBuffer buffer) throws Exception {
        return decodeBuffer0(buffer, false);
    }

    private Object decodeBuffer0(final ChannelBuffer buffer, boolean isLast) throws Exception {
        if (terminator == null) {
            return decodeBuffer(buffer, isLast);
        } else {
            // Provided for backward compatibility with Robot V1 which uses a terminator
            return decodeBufferWithTerminator(buffer);
        }
    }

    private Object decodeBuffer(final ChannelBuffer buffer, boolean isLast) throws Exception {

        final boolean isDebugEnabled = LOGGER.isDebugEnabled();

        final ChannelBuffer observedBytes = buffer.slice();
        final String observed = observedBytes.toString(charset);

        final NamedGroupMatcher matcher = pattern.matcher(observed);

        // TODO: Need to deal with anchoring
        boolean allInputMatched = matcher.matches();
        boolean prefixMatched = allInputMatched || matcher.lookingAt();
        boolean noMatchMayMatchLater = !prefixMatched && matcher.hitEnd();

        // We keep looking while we match or while we don't match but it is still possible to match
        if ((allInputMatched && !isLast) || noMatchMayMatchLater) {
            if (isDebugEnabled) {
                LOGGER.debug("Waiting for more data to match full regex");
            }
            return null;
        }

        // If we never matched we fail.
        if (!prefixMatched) {
            throw new MessageMismatchException(format("Regex %s mismatch.", pattern.pattern()), pattern.pattern(),
                    observed);
        }

        captureGroups(matcher);

        // skip the bytes we actually matched
        buffer.skipBytes(matcher.end());

        if (isDebugEnabled) {
            LOGGER.debug(format("Regex handler read %d bytes, leaving buffer=%s", matcher.end(), buffer.toString(UTF_8)));
        }

        return buffer;
    }

    private void captureGroups(NamedGroupMatcher matcher) {
        for (String captureName : matcher.groupNames()) {
            String captured = matcher.group(captureName);
            byte[] bytes = captured.getBytes(UTF_8);
            environment.getELResolver().setValue(environment, null, captureName, bytes);
        }
    }

    @Deprecated
    // for robot format V1
    private Object decodeBufferWithTerminator(final ChannelBuffer buffer) throws Exception {
        boolean isDebugEnabled = LOGGER.isDebugEnabled();

        if (buffer.bytesBefore(indexFinder) < 0) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Regex decoder has not encountered terminator yet. Waiting for more data");
            }
            // Terminating sequence not found; keep looking...
            return null;
        }

        int idx = buffer.bytesBefore(indexFinder);

        ChannelBuffer observedBytes = buffer.slice(buffer.readerIndex(), idx);
        String observed = observedBytes.toString(charset);

        NamedGroupMatcher matcher = pattern.matcher(observed);
        if (!matcher.matches()) {
            // use a mismatch exception subclass, include the regex and
            // terminator?
            throw new MessageMismatchException("Regex /" + pattern.pattern() + "/ mismatch", pattern.pattern(),
                    observed);
        }

        captureGroups(matcher);

        if (isDebugEnabled) {
            LOGGER.debug("Preread buffer was |" + buffer.toString(UTF_8));
        }

        // Skip all of the bytes examined, including the terminator
        buffer.skipBytes(idx + terminator.readableBytes());

        if (isDebugEnabled) {
            LOGGER.debug(String.format("Regex handler read idx=%d + terminator=%d bytes, leaving buffer=%s", idx,
                    terminator.readableBytes(), buffer.toString(UTF_8)));
        }

        return buffer;
    }
}
