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

package org.kaazing.k3po.driver.netty.bootstrap.channel;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.kaazing.k3po.driver.netty.channel.FlushEvent;
import org.kaazing.k3po.driver.netty.channel.ShutdownInputEvent;
import org.kaazing.k3po.driver.netty.channel.ShutdownOutputEvent;

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
        } else if (e instanceof ShutdownInputEvent) {
            shutdownInputRequested(pipeline, (ShutdownInputEvent) e);
        } else if (e instanceof ShutdownOutputEvent) {
            shutdownOutputRequested(pipeline, (ShutdownOutputEvent) e);
        } else if (e instanceof FlushEvent) {
            flushRequested(pipeline, (FlushEvent) e);
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

    protected void flushRequested(ChannelPipeline pipeline, FlushEvent evt) throws Exception {
    }

    protected void shutdownInputRequested(ChannelPipeline pipeline, ShutdownInputEvent evt) throws Exception {
    }

    protected void shutdownOutputRequested(ChannelPipeline pipeline, ShutdownOutputEvent evt) throws Exception {
    }
}
