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

import static org.jboss.netty.channel.Channels.future;

import java.net.SocketAddress;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.kaazing.k3po.driver.internal.behavior.ScriptProgressException;

public class ConnectHandler extends AbstractCommandHandler {

    private final SocketAddress remoteAddress;

    public ConnectHandler(SocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @Override
    protected void invokeCommand(ChannelHandlerContext ctx) throws Exception {

        ChannelFuture handlerFuture = getHandlerFuture();
        ChannelFuture connectFuture = future(ctx.getChannel(), true);
        Channels.connect(ctx, connectFuture, remoteAddress);
        connectFuture.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {

                if (future.isSuccess()) {
                    handlerFuture.setSuccess();
                }
                else {
                    handlerFuture.setFailure(new ScriptProgressException(getRegionInfo(), "connect aborted"));
                }
            }
        });
    }

    @Override
    protected StringBuilder describe(StringBuilder sb) {
        return sb.append("connect");
    }

}
