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
package org.kaazing.k3po.driver.internal.netty.bootstrap.agrona;

import static org.jboss.netty.channel.Channels.fireChannelOpen;

import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelSink;

public final class AgronaClientChannel extends AgronaChannel {

    final AgronaClientBoss boss;

    AgronaClientChannel(ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink,
            AgronaClientBoss boss, AgronaWorker worker) {
        super(null, factory, pipeline, sink, worker);

        this.boss = boss;

        fireChannelOpen(this);
    }

}
