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

package org.kaazing.k3po.driver.internal.behavior.handler.command;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;

public class ConnectAbortHandler extends AbstractCommandHandler {

    private ChannelFuture connectFuture;

    @Override
    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof ChannelStateEvent) {
            ChannelStateEvent cse = (ChannelStateEvent) e;
            if (cse.getState() == ChannelState.CONNECTED) {
                this.connectFuture = cse.getFuture();
            }
        }

        ctx.sendDownstream(e);
    }

    @Override
    protected void invokeCommand(ChannelHandlerContext ctx) throws Exception {

        ChannelFuture handlerFuture = getHandlerFuture();
        if (connectFuture == null || !connectFuture.cancel()) {
            handlerFuture.setFailure(new ChannelException("connect not aborted"));
        }
        else {
            handlerFuture.setSuccess();
        }
    }

    @Override
    protected StringBuilder describe(StringBuilder sb) {
        return sb.append("connect abort");
    }

}
