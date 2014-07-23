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

import static org.jboss.netty.channel.Channels.fireChannelBound;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;

import org.kaazing.robot.driver.netty.bootstrap.BootstrapFactory;
import org.kaazing.robot.driver.netty.bootstrap.ServerBootstrap;
import org.kaazing.robot.driver.netty.channel.ChannelAddress;

public abstract class AbstractServerChannelSink<T extends AbstractServerChannel<?>> extends AbstractChannelSink {

    private final ChannelPipelineFactory pipelineFactory;

    private BootstrapFactory bootstrapFactory;

    protected AbstractServerChannelSink(ChannelPipelineFactory pipelineFactory) {
        this.pipelineFactory = pipelineFactory;
    }

    public void setBootstrapFactory(BootstrapFactory bootstrapFactory) {
        this.bootstrapFactory = bootstrapFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void bindRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        T channel = (T) evt.getChannel();
        ChannelFuture future = evt.getFuture();
        ChannelAddress localAddress = (ChannelAddress) evt.getValue();

        ChannelAddress transportAddress = localAddress.getTransport();
        String transportName = transportAddress.getLocation().getScheme();

        ServerBootstrap bootstrap = bootstrapFactory.newServerBootstrap(transportName);
        bootstrap.setParentHandler(createParentHandler(channel));
        bootstrap.setPipelineFactory(pipelineFactory);
        bootstrap.setOption(String.format("%s.next-protocol", transportName), localAddress.getLocation().getScheme());

        try {
            Channel transport = bootstrap.bind(transportAddress);
            channel.setTransport(transport);
            channel.setLocalAddress(localAddress);
            channel.setBound();
            fireChannelBound(channel, localAddress);

            future.setSuccess();
        } catch (Exception e) {
            future.setFailure(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void unbindRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {

        T channel = (T) evt.getChannel();
        ChannelFuture future = evt.getFuture();

        try {
            Channel transport = channel.getTransport();
            transport.unbind().awaitUninterruptibly();

            close(channel, future);
        } catch (Exception e) {
            future.setFailure(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void closeRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {

        T channel = (T) evt.getChannel();
        ChannelFuture future = evt.getFuture();

        try {
            close(channel, future);
        } catch (Exception e) {
            future.setFailure(e);
        }
    }

    protected abstract ChannelHandler createParentHandler(T channel);

    private void close(T channel, ChannelFuture future) throws Exception {

        Channel transport = channel.getTransport();
        transport.close().awaitUninterruptibly();

        future.setSuccess();
    }
}
