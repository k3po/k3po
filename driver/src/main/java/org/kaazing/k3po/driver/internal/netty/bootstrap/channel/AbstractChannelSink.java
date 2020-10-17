/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaazing.k3po.driver.internal.netty.bootstrap.channel;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.kaazing.k3po.driver.internal.netty.channel.FlushEvent;
import org.kaazing.k3po.driver.internal.netty.channel.ReadAbortEvent;
import org.kaazing.k3po.driver.internal.netty.channel.ReadAdviseEvent;
import org.kaazing.k3po.driver.internal.netty.channel.ShutdownInputEvent;
import org.kaazing.k3po.driver.internal.netty.channel.ShutdownOutputEvent;
import org.kaazing.k3po.driver.internal.netty.channel.WriteAbortEvent;
import org.kaazing.k3po.driver.internal.netty.channel.WriteAdviseEvent;

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
        } else if (e instanceof ReadAbortEvent) {
            abortInputRequested(pipeline, (ReadAbortEvent) e);
        } else if (e instanceof WriteAbortEvent) {
            abortOutputRequested(pipeline, (WriteAbortEvent) e);
        } else if (e instanceof ReadAdviseEvent) {
            adviseInputRequested(pipeline, (ReadAdviseEvent) e);
        } else if (e instanceof WriteAdviseEvent) {
            adviseOutputRequested(pipeline, (WriteAdviseEvent) e);
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

    protected void abortInputRequested(ChannelPipeline pipeline, ReadAbortEvent evt) throws Exception {
    }

    protected void abortOutputRequested(ChannelPipeline pipeline, WriteAbortEvent evt) throws Exception {
    }

    protected void adviseInputRequested(ChannelPipeline pipeline, ReadAdviseEvent evt) throws Exception {
    }

    protected void adviseOutputRequested(ChannelPipeline pipeline, WriteAdviseEvent evt) throws Exception {
    }

    protected void shutdownInputRequested(ChannelPipeline pipeline, ShutdownInputEvent evt) throws Exception {
    }

    protected void shutdownOutputRequested(ChannelPipeline pipeline, ShutdownOutputEvent evt) throws Exception {
    }
}
