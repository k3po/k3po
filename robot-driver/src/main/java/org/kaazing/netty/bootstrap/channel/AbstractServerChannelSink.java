/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.netty.bootstrap.channel;

import static org.jboss.netty.channel.Channels.fireChannelBound;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;

import org.kaazing.netty.bootstrap.BootstrapFactory;
import org.kaazing.netty.bootstrap.ServerBootstrap;
import org.kaazing.netty.channel.ChannelAddress;

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
