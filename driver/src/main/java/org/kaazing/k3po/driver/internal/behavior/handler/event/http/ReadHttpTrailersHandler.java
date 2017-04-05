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
package org.kaazing.k3po.driver.internal.behavior.handler.event.http;

import static java.util.EnumSet.of;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.http.HttpTrailerDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.event.AbstractEventHandler;
import org.kaazing.k3po.driver.internal.netty.channel.ShutdownInputEvent;

public class ReadHttpTrailersHandler extends AbstractEventHandler {

    private HttpTrailerDecoder httpTrailerDecoder;

    public ReadHttpTrailersHandler(HttpTrailerDecoder httpTrailerDecoder) {
        super(of(ChannelEventKind.INPUT_SHUTDOWN));
        this.httpTrailerDecoder = httpTrailerDecoder;
    }

    @Override
    public void inputShutdown(ChannelHandlerContext ctx, ShutdownInputEvent e) {

        ChannelFuture handlerFuture = getHandlerFuture();
        try {
            httpTrailerDecoder.decode(ctx.getChannel());
            assert handlerFuture != null;
            handlerFuture.setSuccess();
        } catch (Exception e1) {
            handlerFuture.setFailure(e1);
        }
        super.inputShutdown(ctx, e);
    }

    @Override
    protected StringBuilder describe(StringBuilder sb) {
        return sb.append("closed");
    }
}
