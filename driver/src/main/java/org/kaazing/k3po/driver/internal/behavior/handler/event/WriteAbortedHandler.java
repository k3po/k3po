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

import static java.util.EnumSet.of;
import static org.kaazing.k3po.driver.internal.behavior.handler.event.AbstractEventHandler.ChannelEventKind.WRITE_ABORTED;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.kaazing.k3po.driver.internal.netty.channel.WriteAbortEvent;

public class WriteAbortedHandler extends AbstractEventHandler {

    public WriteAbortedHandler() {
        super(of(WRITE_ABORTED));
    }

    @Override
    public void outputAborted(ChannelHandlerContext ctx, WriteAbortEvent e) {

        ChannelFuture handlerFuture = getHandlerFuture();
        assert handlerFuture != null;
        handlerFuture.setSuccess();
    }

    @Override
    protected StringBuilder describe(StringBuilder sb) {
        return sb.append("write aborted");
    }
}
