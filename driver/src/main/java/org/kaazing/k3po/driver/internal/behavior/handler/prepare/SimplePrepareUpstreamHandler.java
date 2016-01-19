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
package org.kaazing.k3po.driver.internal.behavior.handler.prepare;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.kaazing.k3po.driver.internal.netty.channel.SimpleChannelUpstreamHandler;

public abstract class SimplePrepareUpstreamHandler extends SimpleChannelUpstreamHandler {

    @Override
    public final void handleUpstream(ChannelHandlerContext ctx, ChannelEvent evt) throws Exception {

        if (evt instanceof PreparationEvent) {
            prepareRequested(ctx, (PreparationEvent) evt);
        }
        else {
            handleUpstream0(ctx, evt);
        }
    }

    public void prepareRequested(ChannelHandlerContext ctx, PreparationEvent evt) {
        ctx.sendUpstream(evt);
    }

    protected void handleUpstream0(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {

        super.handleUpstream(ctx, e);
    }

}
