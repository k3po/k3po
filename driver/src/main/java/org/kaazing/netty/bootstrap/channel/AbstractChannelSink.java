/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.netty.bootstrap.channel;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;

public abstract class AbstractChannelSink extends org.jboss.netty.channel.AbstractChannelSink {

    @Override
    public void eventSunk(ChannelPipeline pipeline, ChannelEvent e) throws Exception {

        if (e instanceof MessageEvent) {
            writeRequested(pipeline, (MessageEvent) e);
        } else if (e instanceof ChannelStateEvent) {
            ChannelStateEvent evt = (ChannelStateEvent) e;
            switch (evt.getState()) {
            case OPEN:
                if (!Boolean.TRUE.equals(evt.getValue())) {
                    closeRequested(pipeline, evt);
                }
                break;
            case BOUND:
                if (evt.getValue() != null) {
                    bindRequested(pipeline, evt);
                } else {
                    unbindRequested(pipeline, evt);
                }
                break;
            case CONNECTED:
                if (evt.getValue() != null) {
                    connectRequested(pipeline, evt);
                } else {
                    disconnectRequested(pipeline, evt);
                }
                break;
            case INTEREST_OPS:
                setInterestOpsRequested(pipeline, evt);
                break;
            default:
                eventSunk0(pipeline, evt);
                break;
            }
        } else {
            eventSunk0(pipeline, e);
        }
    }

    protected void writeRequested(ChannelPipeline pipeline, MessageEvent e) throws Exception {
    }

    protected void bindRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
    }

    protected void connectRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
    }

    protected void disconnectRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
    }

    protected void unbindRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
    }

    protected void closeRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
    }

    protected void setInterestOpsRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
    }

    protected void eventSunk0(ChannelPipeline pipeline, ChannelEvent e) throws Exception {
    }
}
