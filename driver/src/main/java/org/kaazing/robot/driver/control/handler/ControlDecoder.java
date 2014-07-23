/*
 * Copyright (c) 2014 "Kaazing Corporation," (www.kaazing.com)
 *
 * This file is part of Robot.
 *
 * Robot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.kaazing.robot.driver.control.handler;

import static org.jboss.netty.util.CharsetUtil.UTF_8;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

import org.kaazing.robot.driver.control.AbortMessage;
import org.kaazing.robot.driver.control.ControlMessage;
import org.kaazing.robot.driver.control.ErrorMessage;
import org.kaazing.robot.driver.control.FinishedMessage;
import org.kaazing.robot.driver.control.PrepareMessage;
import org.kaazing.robot.driver.control.StartMessage;

public class ControlDecoder extends ReplayingDecoder<ControlDecoder.State> {

    static enum State {
        READ_INITIAL, READ_HEADER, READ_CONTENT
    }

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ControlDecoder.class);
    private final int maxInitialLineLength;
    private final int maxHeaderLineLength;
    private final int maxContentLength;

    private ControlMessage message;
    private int contentLength;

    public ControlDecoder() {
        this(1024, 1024, 32768);
    }

    public ControlDecoder(int maxInitialLineLength, int maxHeaderLineLength, int maxContentLength) {
        super(false);

        this.maxInitialLineLength = maxInitialLineLength;
        this.maxHeaderLineLength = maxHeaderLineLength;
        this.maxContentLength = maxContentLength;

        setState(State.READ_INITIAL);
    }

    private boolean hasReceivedPrepare;

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer, State state) throws Exception {

        LOGGER.debug("decode: state=" + state);

        switch (state) {
        case READ_INITIAL: {
            String initialLine = readLine(buffer, maxInitialLineLength);
            if (initialLine != null) {
                message = createMessage(initialLine);
                contentLength = 0;
                checkpoint(State.READ_HEADER);
                LOGGER.debug("Received initialLine. Message is=" + message);
            }
            LOGGER.debug("initialLine is null. Message is=" + message);
            return null;
        }
        case READ_HEADER: {
            State nextState = readHeader(buffer);
            checkpoint(nextState);
            if (nextState == State.READ_INITIAL) {
                LOGGER.debug("State changed to READ_INITIAL return message=" + message);
                return message;
            }
            LOGGER.debug("State didn't change to initial after reading header message=" + message);
            return null;
        }
        case READ_CONTENT: {
            State nextState = readContent(buffer);
            checkpoint(nextState);
            if (nextState != State.READ_CONTENT) {
                LOGGER.debug("State changed to " + nextState + " after reading content. Message=" + message);
                return message;
            }
            LOGGER.debug("State didn't change to initial after reading content. Message=" + message);
            return null;
        }
        default:
            throw new IllegalArgumentException(String.format("Unrecognized decoder state: %s", state));
        }
    }

    private static String readLine(ChannelBuffer buffer, int maxLineLength) {

        int readableBytes = buffer.readableBytes();
        if (readableBytes == 0) {
            return null;
        }

        int readerIndex = buffer.readerIndex();

        int endOfLineAt = buffer.indexOf(readerIndex, Math.min(readableBytes, maxLineLength) + 1, (byte) 0x0a);

        if (readerIndex > 0) {
            LOGGER.debug(String.format("readLine: endofLineAt=%d, readableBytes=%d, readerIndex=%d", endOfLineAt, readableBytes,
                    readerIndex));
        }

        // end-of-line not found
        if (endOfLineAt == -1) {
            if (readableBytes >= maxLineLength) {
                throw new IllegalArgumentException("Initial line too long");
            }
            return null;
        }

        // end-of-line found
        StringBuilder sb = new StringBuilder(endOfLineAt);
        for (int i = readerIndex; i < endOfLineAt; i++) {
            sb.append((char) buffer.readByte());
        }

        byte endOfLine = buffer.readByte();
        assert endOfLine == 0x0a;

        return sb.toString();
    }

    private ControlMessage createMessage(String initialLine) {

        LOGGER.debug("Creating message with line |" + initialLine + "|");
        ControlMessage.Kind messageKind = ControlMessage.Kind.valueOf(initialLine);
        switch (messageKind) {
        case PREPARE:
            hasReceivedPrepare = true;
            return new PrepareMessage();
        case START:
            // backwards compatibility
            if (!hasReceivedPrepare) {
                PrepareMessage newMessage = new PrepareMessage();
                newMessage.setCompatibilityKind(messageKind);
                return newMessage;
            }
            hasReceivedPrepare = false;
            return new StartMessage();
        case ABORT:
            hasReceivedPrepare = false;
            return new AbortMessage();
        default:
            throw new IllegalArgumentException(String.format("Unrecognized message kind: %s", messageKind));
        }
    }

    private State readHeader(ChannelBuffer buffer) {

        int readableBytes = buffer.readableBytes();
        if (readableBytes == 0) {
            LOGGER.debug("No readable bytes found");
            return null;
        }

        int endOfLineSearchFrom = buffer.readerIndex();
        // int endOfLineSearchTo = endOfLineSearchFrom + Math.min(readableBytes, maxHeaderLineLength);
        int endOfLineSearchTo = Math.min(readableBytes, maxHeaderLineLength);
        int endOfLineAt = buffer.indexOf(endOfLineSearchFrom, endOfLineSearchTo + 1, (byte) 0x0a);

        // end-of-line not found
        if (endOfLineAt == -1) {
            if (readableBytes >= maxHeaderLineLength) {
                throw new IllegalArgumentException("Header line too long");
            }
            LOGGER.debug("endOfLineAt=-1");
            return null;
        }

        if (endOfLineAt == endOfLineSearchFrom) {
            byte endOfLine = buffer.readByte();
            assert endOfLine == 0x0a;

            if (contentLength == 0) {
                LOGGER.debug("Content Length is 0 so returning state initial");
                return State.READ_INITIAL;
            }

            switch (message.getKind()) {
            case PREPARE:
            case FINISHED:
            case ERROR:
                LOGGER.debug("Change state to READ_CONTENT");
                // content for these message kinds
                return State.READ_CONTENT;
            }
            LOGGER.debug("Change state to READ_INITIAL. Message not recongized and endOfLineAt equals endOfLineSearchForm");
            return State.READ_INITIAL;
        }

        // end-of-line found
        int colonSearchFrom = buffer.readerIndex();
        // int colonSearchTo = colonSearchFrom + Math.min(readableBytes, endOfLineAt);
        int colonSearchTo = Math.min(readableBytes, endOfLineAt);
        int colonAt = buffer.indexOf(colonSearchFrom, colonSearchTo + 1, (byte) 0x3a);

        // colon not found
        if (colonAt == -1) {
            throw new IllegalArgumentException("Colon not found in header line");
        }

        // colon found
        int headerNameLength = colonAt - colonSearchFrom;
        StringBuilder headerNameBuilder = new StringBuilder(headerNameLength);
        for (int i = 0; i < headerNameLength; i++) {
            headerNameBuilder.append((char) buffer.readByte());
        }
        String headerName = headerNameBuilder.toString();

        byte colon = buffer.readByte();
        assert colon == 0x3a;

        int headerValueLength = endOfLineAt - colonAt - 1;
        StringBuilder headerValueBuilder = new StringBuilder(headerValueLength);
        for (int i = 0; i < headerValueLength; i++) {
            headerValueBuilder.append((char) buffer.readByte());
        }
        String headerValue = headerValueBuilder.toString();

        // add common headers
        if ("name".equals(headerName)) {
            message.setScriptName(headerValue);
        }
        else if ("content-length".equals(headerName)) {
            // determine content-length
            contentLength = Integer.parseInt(headerValue);
            if (contentLength > maxContentLength) {
                throw new IllegalArgumentException("Content too long");
            }
        }
        else {
            // add kind-specific headers
            switch (message.getKind()) {
                case ERROR:
                    ErrorMessage errorMessage = (ErrorMessage) message;
                    if ("summary".equals(headerName)) {
                        errorMessage.setSummary(headerValue);
                    }
                    break;
                case PREPARE:
                    PrepareMessage prepareMessage = (PrepareMessage) message;
                    if ("content-type".equals(headerName)) {
                        prepareMessage.setScriptFormatOverride(headerValue);
                    }
            }
        }

        byte endOfLine = buffer.readByte();
        assert endOfLine == 0x0a;
        return State.READ_HEADER;
    }

    private State readContent(ChannelBuffer buffer) {

        assert contentLength > 0;

        if (buffer.readableBytes() < contentLength) {
            return State.READ_CONTENT;
        }

        String content = buffer.readBytes(contentLength).toString(UTF_8);
        switch (message.getKind()) {
        case PREPARE:
            PrepareMessage prepareMessage = (PrepareMessage) message;
            prepareMessage.setExpectedScript(content);
            break;
        case FINISHED:
            FinishedMessage finishedMessage = (FinishedMessage) message;
            finishedMessage.setObservedScript(content);
            break;
        case ERROR:
            ErrorMessage errorMessage = (ErrorMessage) message;
            errorMessage.setDescription(content);
            break;
        default:
            throw new IllegalStateException("Unexpected message kind: " + message.getKind());
        }

        return State.READ_INITIAL;
    }
}
