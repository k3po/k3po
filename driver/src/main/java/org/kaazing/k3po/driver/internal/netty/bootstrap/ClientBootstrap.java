/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
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

package org.kaazing.k3po.driver.internal.netty.bootstrap;

import java.net.SocketAddress;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineException;
import org.jboss.netty.channel.Channels;
import org.kaazing.k3po.driver.internal.behavior.Barrier;

public class ClientBootstrap extends org.jboss.netty.bootstrap.ClientBootstrap {

    public ClientBootstrap() {
        super();
    }

    public ClientBootstrap(ChannelFactory channelFactory) {
        super(channelFactory);
    }

//    @Override
//    public ChannelFuture connect(final SocketAddress localAddress, final SocketAddress remoteAddress) {
//
//        final Object barrier = getOption("barrier");
//        if (barrier == null) {
//            return super.connect(localAddress, remoteAddress);
//        } else {
//            // pulled code from super.connect in order to get access to the channel but not actually connect
//            // until later
//            if (localAddress == null) {
//                throw new NullPointerException("localAddress");
//            }
//
//            ChannelPipeline pipeline;
//            try {
//                pipeline = getPipelineFactory().getPipeline();
//            } catch (Exception e) {
//                throw new ChannelPipelineException("Failed to initialize a pipeline.", e);
//            }
//
//            // Set the options.
//            final Channel ch = getFactory().newChannel(pipeline);
//            boolean success = false;
//            try {
//                ch.getConfig().setOptions(getOptions());
//                success = true;
//            } finally {
//                if (!success) {
//                    ch.close();
//                }
//            }
//
//            // Bind.
//            if (remoteAddress != null) {
//                ch.bind(remoteAddress);
//            }
//
//            final ChannelFuture connectedFuture = Channels.future(ch, true);
//            ((Barrier) barrier).getFuture().addListener(new ChannelFutureListener() {
//                @Override
//                public void operationComplete(ChannelFuture future) throws Exception {
//                    if (!connectedFuture.isCancelled()) {
//                        ch.connect(localAddress).addListener(new ChannelFutureListener() {
//                            @Override
//                            public void operationComplete(ChannelFuture future) throws Exception {
//                                connectedFuture.setSuccess();
//                            }
//                        });
//                    } else {
//                        connectedFuture.setFailure(new Exception("Barrier: " + barrier + " never completed"));
//                    }
//                }
//            });
//            return connectedFuture;
//        }
//    }

}
