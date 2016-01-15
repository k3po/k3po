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

import java.util.Collection;
import java.util.LinkedHashSet;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.kaazing.k3po.driver.internal.netty.channel.CompositeChannelFuture;

public abstract class AbstractPreparationEvent implements PreparationEvent {

    private final Channel channel;
    private final ChannelFuture future;
    private final Collection<ChannelFuture> pipelineFutures;

    public AbstractPreparationEvent(Channel channel, ChannelFuture future) {
        this.channel = channel;
        this.future = future;
        this.pipelineFutures = new LinkedHashSet<>();
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public ChannelFuture getFuture() {
        return future;
    }

    @Override
    public Collection<ChannelFuture> getPipelineFutures() {
        return pipelineFutures;
    }

    @Override
    public ChannelFuture checkpoint(final ChannelFuture handlerFuture) {

        // We set the composite to failFast. This is so that as soon as one handler future fails ... any pipelinefutures
        // that contain it will also fail. This is needed so that the listener in the CompletionHandler will fire
        ChannelFuture pipelineFuture = new CompositeChannelFuture<>(channel, pipelineFutures, true);

        // Note: add handler future to pipeline futures afterwards
        // so pipelineFuture represents all members of
        // the pipeline up-to-but-not-including this handler
        // and handlerFuture represents just this handler
        pipelineFutures.add(handlerFuture);

        return pipelineFuture;
    }

}
