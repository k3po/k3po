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
package org.kaazing.k3po.driver.internal.netty.bootstrap.bbosh;

import static java.lang.String.format;
import static org.jboss.netty.channel.Channels.fireChannelBound;
import static org.jboss.netty.channel.Channels.fireChannelClosed;
import static org.jboss.netty.channel.Channels.fireChannelConnected;
import static org.jboss.netty.channel.Channels.fireChannelOpen;
import static org.jboss.netty.channel.Channels.pipeline;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.METHOD_NOT_ALLOWED;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.SERVICE_UNAVAILABLE;
import static org.kaazing.k3po.driver.internal.netty.bootstrap.bbosh.BBoshHttpHeaders.getIntHeader;
import static org.kaazing.k3po.driver.internal.netty.channel.LocationFactories.changeSchemeOnly;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelConfig;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.kaazing.k3po.driver.internal.netty.bootstrap.BootstrapFactory;
import org.kaazing.k3po.driver.internal.netty.bootstrap.ServerBootstrap;
import org.kaazing.k3po.driver.internal.netty.bootstrap.bbosh.BBoshHttpHeaders.Names;
import org.kaazing.k3po.driver.internal.netty.bootstrap.bbosh.BBoshHttpHeaders.Values;
import org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpChannelConfig;
import org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpChildChannel;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddressFactory;
import org.kaazing.k3po.driver.internal.netty.channel.LocationFactory;

public class BBoshHandshakeChildChannelSource extends SimpleChannelHandler {

    private static final LocationFactory CHANGE_SCHEME_ONLY = changeSchemeOnly("bbosh");

    private final NavigableMap<URI, BBoshServerChannel> bboshBindings;

    private ChannelAddressFactory addressFactory;
    private BootstrapFactory bootstrapFactory;

    public BBoshHandshakeChildChannelSource(NavigableMap<URI, BBoshServerChannel> bboshBindings) {
        this.bboshBindings = bboshBindings;
    }

    public void setAddressFactory(ChannelAddressFactory addressFactory) {
        this.addressFactory = addressFactory;
    }

    public void setBootstrapFactory(BootstrapFactory bootstrapFactory) {
        this.bootstrapFactory = bootstrapFactory;
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        final HttpChildChannel httpChannel = (HttpChildChannel) ctx.getChannel();
        final HttpChannelConfig httpConfig = httpChannel.getConfig();

        ChannelAddress httpHandshakeLocalAddress = httpChannel.getLocalAddress();
        URI httpHandshakeLocalURI = httpHandshakeLocalAddress.getLocation();
        ChannelAddress httpHandshakeRemoteAddress = httpChannel.getRemoteAddress();
        URI httpHandshakeRemoteURI = httpHandshakeRemoteAddress.getLocation();

        // require no trailing path info (handles expired BBOSH connections)
        if (!httpHandshakeRemoteURI.equals(httpHandshakeLocalURI)) {
            httpConfig.setStatus(NOT_FOUND);
            httpChannel.close();
            return;
        }

        final URI handshakeLocalURI = CHANGE_SCHEME_ONLY.createURI(httpHandshakeLocalURI);
        Entry<URI, BBoshServerChannel> binding = bboshBindings.floorEntry(handshakeLocalURI);

        if (binding == null) {
            httpConfig.setStatus(NOT_FOUND);
            httpChannel.close();
            return;
        }

        if (httpConfig.getMethod() != HttpMethod.POST) {
            httpConfig.setStatus(METHOD_NOT_ALLOWED);
            httpChannel.close();
            return;
        }

        HttpHeaders httpReadHeaders = httpConfig.getReadHeaders();
        if (!Values.BBOSH_1_0.equals(httpReadHeaders.get(Names.X_PROTOCOL))) {
            httpConfig.setStatus(BAD_REQUEST);
            httpChannel.close();
            return;
        }

        if (!Values.APPLICATION_OCTET_STREAM.equals(httpReadHeaders.get(Names.ACCEPT))) {
            httpConfig.setStatus(BAD_REQUEST);
            httpChannel.close();
            return;
        }

        List<BBoshStrategy> acceptStrategies = readAcceptStrategies(httpReadHeaders);
        BBoshStrategy strategy = null;
        for (BBoshStrategy acceptStrategy : acceptStrategies) {
            switch (acceptStrategy.getKind()) {
            case POLLING:
                strategy = acceptStrategy;
                break;
            default:
                // TODO: support other strategies
                break;
            }
        }
        final BBoshStrategy negotiatedStrategy = strategy;

        if (strategy == null) {
            httpConfig.setStatus(BAD_REQUEST);
            httpChannel.close();
            return;
        }

        switch (strategy.getKind()) {
        case POLLING:
            if (strategy.getInterval(TimeUnit.SECONDS) < 5L) {
                httpConfig.setStatus(BAD_REQUEST);
                httpChannel.close();
                return;
            }
            break;
        case LONG_POLLING:
            if (strategy.getInterval(TimeUnit.SECONDS) < 30L) {
                httpConfig.setStatus(BAD_REQUEST);
                httpChannel.close();
                return;
            }
            break;
        case STREAMING:
            break;
        }

        UUID uuid = UUID.randomUUID();
        String connectionId = uuid.toString();
        String httpHandshakeLocalPath = httpHandshakeLocalURI.getPath();
        if (!httpHandshakeLocalPath.endsWith("/")) {
            httpHandshakeLocalPath += "/";
        }
        final String connectionPath = format("%s%s", httpHandshakeLocalPath, connectionId);

        int sequenceNo = getIntHeader(httpReadHeaders, Names.X_SEQUENCE_NO);
        int nextSequenceNo = sequenceNo + 1;

        BBoshServerChannel parent = binding.getValue();
        ChannelFactory factory = parent.getFactory();
        ChannelConfig config = parent.getConfig();
        ChannelPipelineFactory pipelineFactory = config.getPipelineFactory();
        ChannelPipeline pipeline = pipelineFactory.getPipeline();
        BBoshPollingChildChannelSink sink = new BBoshPollingChildChannelSink(nextSequenceNo);
        final BBoshChildChannel channel = new BBoshChildChannel(parent, factory, pipeline, sink);
        fireChannelOpen(channel);

        final URI httpConnectionLocalURI = httpHandshakeLocalURI.resolve(connectionPath);
        final ChannelAddress httpConnectionLocalAddress = addressFactory.newChannelAddress(httpConnectionLocalURI);
        ServerBootstrap server = bootstrapFactory.newServerBootstrap("http");
        server.setPipeline(pipeline(new BBoshPollingChildChannelSource(channel)));
        ChannelFuture httpBindFuture = server.bindAsync(httpConnectionLocalAddress);
        httpBindFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture httpBindFuture) throws Exception {
                if (httpBindFuture.isSuccess()) {
                    final URI connectionLocalURI = handshakeLocalURI.resolve(connectionPath);
                    Map<String, Object> options = new HashMap<>();
                    options.put("bbosh.transport", httpConnectionLocalURI);
                    ChannelAddress connectionLocalAddress = addressFactory.newChannelAddress(connectionLocalURI, options);
                    channel.setLocalAddress(connectionLocalAddress);
                    channel.setBound();
                    fireChannelBound(channel, connectionLocalAddress);

                    ChannelAddress connectionRemoteAddress = connectionLocalAddress.newEphemeralAddress();
                    channel.setRemoteAddress(connectionRemoteAddress);
                    channel.setConnected();
                    fireChannelConnected(channel, connectionRemoteAddress);

                    // TODO: use interval to timeout connection before first polling request

                    httpConfig.setStatus(CREATED);
                    httpConfig.setMaximumBufferedContentLength(8192);
                    HttpHeaders httpWriteHeaders = httpConfig.getWriteHeaders();
                    httpWriteHeaders.set(Names.CACHE_CONTROL, Values.NO_CACHE);
                    httpWriteHeaders.set(Names.CONTENT_TYPE, Values.APPLICATION_OCTET_STREAM);
                    httpWriteHeaders.set(Names.LOCATION, connectionPath);
                    httpWriteHeaders.set(Names.X_STRATEGY, negotiatedStrategy.toString());
                    httpChannel.close();

                    final Channel httpBindChannel = httpBindFuture.getChannel();
                    channel.getCloseFuture().addListener(new ChannelFutureListener() {

                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            // unbind channel-specific handler
                            httpBindChannel.close();
                        }
                    });
                }
                else {
                    channel.setClosed();
                    fireChannelClosed(channel);

                    httpConfig.setStatus(SERVICE_UNAVAILABLE);
                    httpChannel.close();
                }
            }
        });
    }

    private static List<BBoshStrategy> readAcceptStrategies(HttpHeaders httpHeaders) {
        List<String> strategyValues = httpHeaders.getAll(Names.X_ACCEPT_STRATEGY);
        List<BBoshStrategy> strategies = new ArrayList<>(3);
        for (String strategyValue : strategyValues) {
            String[] strategyValueParts = strategyValue.split(",\\s+");
            for (String strategyValuePart : strategyValueParts) {
                BBoshStrategy strategy = BBoshStrategy.valueOf(strategyValuePart);
                strategies.add(strategy);
            }
        }
        return strategies;
    }

}
