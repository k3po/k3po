/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.event;

import static java.util.EnumSet.of;
import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.channel.Channels.fireMessageReceived;
import static org.jboss.netty.util.CharsetUtil.UTF_8;

import java.util.Iterator;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.UpstreamMessageEvent;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

import com.kaazing.robot.behavior.handler.codec.MessageDecoder;

public class ReadHandler extends AbstractEventHandler {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ReadHandler.class);

    private final List<MessageDecoder> decoders;

    public ReadHandler(List<MessageDecoder> decoders) {
        super(of(ChannelEventKind.MESSAGE));
        if (decoders == null) {
            throw new NullPointerException("decoders");
        } else if (decoders.size() == 0) {
            throw new IllegalArgumentException("must have at least one decoder");
        }
        this.decoders = decoders;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        messageReceived(ctx, e, false);
    }

    @Override
    protected void handleUnexpectedEvent(ChannelHandlerContext ctx, ChannelEvent evt) throws Exception {
        Channel channel = evt.getChannel();
        MessageEvent msg = new UpstreamMessageEvent(channel, copiedBuffer("", UTF_8), channel.getRemoteAddress());
        // We create a message with an empty string. We need make sure our decoders get the decoder Last call.
        messageReceived(ctx, msg, true);

        // If the above caused a completion we are done. Otherwise we still need to handle the unexpected event
        if (!getHandlerFuture().isDone()) {
            super.handleUnexpectedEvent(ctx, evt);
        }
    }

    private void messageReceived(ChannelHandlerContext ctx, MessageEvent e, boolean isLast) throws Exception {

        final boolean isDebugEnabled = LOGGER.isDebugEnabled();
        ChannelBuffer buf = (ChannelBuffer) e.getMessage();
        ChannelFuture handlerFuture = getHandlerFuture();
        assert handlerFuture != null;

        Iterator<MessageDecoder> iterator = decoders.iterator();
        while (iterator.hasNext()) {
            MessageDecoder decoder = iterator.next();

            try {
                if (isLast) {
                    buf = decoder.decodeLast(buf);
                } else {
                    buf = decoder.decode(buf);
                }
            } catch (Exception mme) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.error("read handler failed ", mme);
                } else {
                    LOGGER.error("read handler failed " + mme);
                }
                // TODO: We will eventually have to create an AstRead node containing what we actually saw. This will come
                // later.
                handlerFuture.setFailure(mme);
                return;
            }
            if (buf == null) {
                // Need more data to complete the decode
                return;
            }
            if (isDebugEnabled) {
                LOGGER.debug("decoder " + decoder + " completed");
            }
            // Remove the decoder because it is done
            iterator.remove();
        }

        LOGGER.info("Read handler completed");

        // If we get through the list of decoders without an exception we are done.
        handlerFuture.setSuccess();

        // Propagate remaining data for next handler(s)
        if (buf.readable()) {
            LOGGER.debug("More bytes are available for reading. Firing message received.");
            fireMessageReceived(ctx, buf, ctx.getChannel().getRemoteAddress());
        }
    }


}
