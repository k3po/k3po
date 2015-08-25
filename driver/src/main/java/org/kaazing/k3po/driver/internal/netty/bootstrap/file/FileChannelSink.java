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

package org.kaazing.k3po.driver.internal.netty.bootstrap.file;

import static java.lang.String.format;
import static org.jboss.netty.channel.Channels.close;
import static org.jboss.netty.channel.Channels.fireChannelBound;
import static org.jboss.netty.channel.Channels.fireChannelClosed;
import static org.jboss.netty.channel.Channels.fireChannelUnbound;

import java.net.URI;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ChildChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.UpstreamChannelStateEvent;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.ChannelGroupFutureListener;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.kaazing.k3po.driver.internal.netty.bootstrap.BootstrapFactory;
import org.kaazing.k3po.driver.internal.netty.bootstrap.ServerBootstrap;
import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.AbstractChannelSink;
import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.AbstractServerChannelSink;
import org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpClientChannel;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddressFactory;

public class FileChannelSink extends AbstractChannelSink {

    @Override
    protected void connectRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        System.out.println("connectRequested pipeline = " + pipeline + " evt = " + evt);

        //bootstrap.setPipelineFactory(pipelineFactory);

        final ChannelAddress fileAddress = (ChannelAddress) evt.getValue();
        final FileChannel fileChannel = (FileChannel) evt.getChannel();

        if (!fileChannel.isBound()) {
            fileChannel.setLocalAddress(fileAddress);
            fileChannel.setBound();
            fireChannelBound(fileChannel, fileAddress);
        }


        ChannelFuture connectFuture = evt.getFuture();
        connectFuture.setSuccess();

        Channels.fireChannelConnected(fileChannel, fileAddress);
    }

    @Override
    protected void writeRequested(ChannelPipeline pipeline, MessageEvent evt) throws Exception {
        System.out.println("writeRequested pipeline = " + pipeline + " evt = " + evt);

        ChannelFuture writeFuture = evt.getFuture();
        writeFuture.setSuccess();
    }

//    @Override
//    protected void bindRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
//        System.out.println("bindRequested pipeline = " + pipeline + " evt = " + evt);
//        ChannelFuture bindFuture = evt.getFuture();
//        bindFuture.setSuccess();
//        Channels.fireChannelBound(evt.getChannel(), null);
//    }
//
//    @Override
//    protected void unbindRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
//        System.out.println("unbindRequested pipeline = " + pipeline + " evt = " + evt);
//    }

    @Override
    protected void closeRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        System.out.println("closeRequested pipeline = " + pipeline + " evt = " + evt);
        final FileChannel fileChannel = (FileChannel) evt.getChannel();
        fileChannel.setClosed();

        ChannelFuture closeFuture = evt.getFuture();
        closeFuture.setSuccess();
    }

}
