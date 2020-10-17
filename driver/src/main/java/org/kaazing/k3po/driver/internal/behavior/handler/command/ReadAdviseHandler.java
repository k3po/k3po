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

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static org.kaazing.k3po.driver.internal.netty.channel.Channels.adviseInput;

import java.util.List;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.ChannelEncoder;

public class ReadAdviseHandler extends AbstractCommandHandler {

    private final Object value;
    private final List<ChannelEncoder> encoders;

    public ReadAdviseHandler(Object value, ChannelEncoder encoder) {
        this(value, singletonList(encoder));
    }

    public ReadAdviseHandler(Object value, List<ChannelEncoder> encoders) {
        requireNonNull(encoders, "encoders");
        if (encoders.size() == 0) {
            throw new IllegalArgumentException("must have at least one encoder");
        }
        this.value = value;
        this.encoders = encoders;
    }

    @Override
    protected void invokeCommand(ChannelHandlerContext ctx) throws Exception {
        try {
            Channel channel = ctx.getChannel();
            for (ChannelEncoder encoder : encoders) {
                encoder.encode(channel);
            }
            adviseInput(ctx, getHandlerFuture(), value);
        }
        catch (Exception e) {
            getHandlerFuture().setFailure(e);
        }
    }

    @Override
    protected StringBuilder describe(StringBuilder sb) {
        return sb.append(format("read advise %s %s", value, encoders));
    }

}
