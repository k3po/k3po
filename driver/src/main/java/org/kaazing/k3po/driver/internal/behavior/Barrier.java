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
package org.kaazing.k3po.driver.internal.behavior;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.Channels;

public class Barrier {

    private final String name;
    private final ChannelFuture future;

    public Barrier(String name) {
        this.name = name;
        future = Channels.future(null);
    }

    public ChannelFuture getFuture() {
        return future;
    }

    @Override
    public String toString() {
        return name;
    }
}
