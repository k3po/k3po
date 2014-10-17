/*
 * Copyright (c) 2014 "Kaazing Corporation," (www.kaazing.com)
 *
 * This file is part of Robot.
 *
 * Robot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.kaazing.robot.driver.netty.bootstrap.channel;

import static java.lang.String.format;
import static org.jboss.netty.channel.Channels.fireChannelBound;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.kaazing.robot.driver.netty.bootstrap.BootstrapFactory;
import org.kaazing.robot.driver.netty.bootstrap.ServerBootstrap;
import org.kaazing.robot.driver.netty.channel.ChannelAddress;

public abstract class AbstractServerChannelSink<T extends AbstractServerChannel<?>> extends AbstractChannelSink {

    protected final ChannelPipelineFactory pipelineFactory;

    protected BootstrapFactory bootstrapFactory;

    protected AbstractServerChannelSink(ChannelPipelineFactory pipelineFactory) {
        this.pipelineFactory = pipelineFactory;
    }

    public void setBootstrapFactory(BootstrapFactory bootstrapFactory) {
        this.bootstrapFactory = bootstrapFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void bindRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        final T channel = (T) evt.getChannel();
        final ChannelFuture future = evt.getFuture();
        final ChannelAddress localAddress = (ChannelAddress) evt.getValue();

        ChannelAddress transportAddress = localAddress.getTransport();
        String transportName = transportAddress.getLocation().getScheme();

        ServerBootstrap bootstrap = bootstrapFactory.newServerBootstrap(transportName);
        bootstrap.setParentHandler(createParentHandler(channel));
        bootstrap.setPipelineFactory(pipelineFactory);
        bootstrap.setOption(format("%s.nextProtocol", transportName), localAddress.getLocation().getScheme());

        ChannelFuture transportFuture = bootstrap.bindAsync(transportAddress);
        if (transportFuture.isDone()) {
            bindTransportCompleted(channel, future, localAddress, transportFuture);
        }
        else {
            transportFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture transportFuture) throws Exception {
                    bindTransportCompleted(channel, future, localAddress, transportFuture);
                }
            });
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void unbindRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {

        final T channel = (T) evt.getChannel();
        final ChannelFuture future = evt.getFuture();

        Channel transport = channel.getTransport();
        ChannelFuture transportFuture = transport.unbind();
        if  (transportFuture.isDone()) {
            unbindTransportCompleted(channel, future, transportFuture);
        }
        else {
            transportFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture transportFuture) throws Exception {
                    unbindTransportCompleted(channel, future, transportFuture);
                }
            });
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void closeRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {

        T channel = (T) evt.getChannel();
        ChannelFuture future = evt.getFuture();
        close(channel, future);
    }

    protected ChannelHandler createParentHandler(T channel) {
        return null;
    }

    private static <T extends AbstractServerChannel<?>> void close(T channel, final ChannelFuture future) throws Exception {

        Channel transport = channel.getTransport();
        ChannelFuture transportFuture = transport.close();
        if (transportFuture.isDone()) {
            closeTransportCompleted(future, transportFuture);
        }
        else {
            transportFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture transportFuture) throws Exception {
                    closeTransportCompleted(future, transportFuture);
                }
            });
        }
    }

    private static <T extends AbstractServerChannel<?>> void bindTransportCompleted(
            T channel,
            ChannelFuture future,
            ChannelAddress localAddress,
            ChannelFuture transportFuture) {

        if (transportFuture.isSuccess()) {
            Channel transport = transportFuture.getChannel();
            channel.setTransport(transport);
            channel.setLocalAddress(localAddress);
            channel.setBound();
            fireChannelBound(channel, localAddress);
            future.setSuccess();
        }
        else {
            future.setFailure(transportFuture.getCause());
        }
    }


    private static <T extends AbstractServerChannel<?>> void unbindTransportCompleted(
            T channel,
            ChannelFuture future,
            ChannelFuture transportFuture) throws Exception {
        if (transportFuture.isSuccess()) {
            close(channel, future);
        }
        else {
            future.setFailure(transportFuture.getCause());
        }
    }

    private static void closeTransportCompleted(ChannelFuture future,
            ChannelFuture transportFuture) {
        if (transportFuture.isSuccess()) {
            future.setSuccess();
        }
        else {
            future.setFailure(transportFuture.getCause());
        }
    }

}
