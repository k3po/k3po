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

package org.kaazing.k3po.driver.internal.behavior.handler.event;

import static java.util.Collections.singletonList;
import static java.util.EnumSet.of;
import static org.kaazing.k3po.driver.internal.behavior.handler.event.AbstractEventHandler.ChannelEventKind.WRITE_ADVISED;

import java.util.List;
import java.util.Objects;

import javax.el.ELException;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.kaazing.k3po.driver.internal.behavior.ScriptProgressException;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.ChannelDecoder;
import org.kaazing.k3po.driver.internal.netty.channel.WriteAdviseEvent;

public class WriteAdvisedHandler extends AbstractEventHandler {

    private final Object value;
    private final List<ChannelDecoder> decoders;

    public WriteAdvisedHandler(Object value, ChannelDecoder decoder) {
        this(value, singletonList(decoder));
    }

    public WriteAdvisedHandler(Object value, List<ChannelDecoder> decoders) {
        super(of(WRITE_ADVISED));
        this.value = value;
        this.decoders = decoders;
    }

    @Override
    public void outputAdvised(ChannelHandlerContext ctx, WriteAdviseEvent e) {

        ChannelFuture handlerFuture = getHandlerFuture();
        assert handlerFuture != null;

        outer:
        try {
            if (!Objects.equals(value, e.getValue()))
            {
                handlerFuture.setFailure(new ScriptProgressException(getRegionInfo(), String.valueOf(e.getValue())));
                break outer;
            }

            Channel channel = ctx.getChannel();
            for (ChannelDecoder decoder : decoders) {
                boolean decoded = decoder.decode(channel);
                if (!decoded)
                {
                    handlerFuture.setFailure(new ScriptProgressException(getRegionInfo(), "decode failed"));
                    break outer;
                }
            }
            handlerFuture.setSuccess();
        }
        catch (ELException ele) {
            ScriptProgressException exception = new ScriptProgressException(getRegionInfo(), ele.getMessage());
            exception.initCause(ele);
            handlerFuture.setFailure(exception);
        }
        catch (Exception ex) {
            handlerFuture.setFailure(ex);
        }
    }

    @Override
    protected StringBuilder describe(StringBuilder sb) {
        return sb.append(String.format("read advised %s %s", value, decoders));
    }
}
