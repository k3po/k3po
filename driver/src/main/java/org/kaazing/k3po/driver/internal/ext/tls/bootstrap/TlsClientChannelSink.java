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
package org.kaazing.k3po.driver.internal.ext.tls.bootstrap;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.jboss.netty.channel.Channels.fireChannelBound;
import static org.jboss.netty.channel.Channels.fireChannelClosed;
import static org.jboss.netty.channel.Channels.fireChannelConnected;
import static org.jboss.netty.channel.Channels.fireChannelDisconnected;
import static org.jboss.netty.channel.Channels.fireChannelUnbound;
import static org.jboss.netty.channel.Channels.future;
import static org.jboss.netty.channel.Channels.pipeline;
import static org.kaazing.k3po.driver.internal.channel.Channels.chainFutures;
import static org.kaazing.k3po.driver.internal.channel.Channels.chainWriteCompletes;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.ssl.SslHandler;
import org.kaazing.k3po.driver.internal.netty.bootstrap.BootstrapFactory;
import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.AbstractChannelSink;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;
import org.kaazing.k3po.driver.internal.netty.channel.FlushEvent;
import org.kaazing.k3po.driver.internal.netty.channel.ShutdownOutputEvent;
import org.kaazing.k3po.driver.internal.netty.channel.WriteAbortEvent;

public class TlsClientChannelSink extends AbstractChannelSink {

    private final SecureRandom random;
    private final BootstrapFactory bootstrapFactory;

    private Channel transport;

    public TlsClientChannelSink(SecureRandom random, BootstrapFactory bootstrapFactory) {
        this.random = random;
        this.bootstrapFactory = bootstrapFactory;
    }

    @Override
    public ChannelFuture execute(ChannelPipeline tlsPipeline, Runnable task) {

        if (transport != null) {
            ChannelPipeline pipeline = transport.getPipeline();
            ChannelFuture future = pipeline.execute(task);
            Channel tlsChannel = pipeline.getChannel();
            ChannelFuture tlsFuture = future(tlsChannel);
            chainFutures(future, tlsFuture);
            return tlsFuture;
        }

        return super.execute(tlsPipeline, task);
    }

    @Override
    protected void setInterestOpsRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        ChannelFuture tlsFuture = evt.getFuture();
        TlsClientChannel tlsClientChannel = (TlsClientChannel) evt.getChannel();
        tlsClientChannel.setInterestOpsNow((int) evt.getValue());
        tlsFuture.setSuccess();
    }

    @Override
    protected void bindRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        ChannelFuture tlsBindFuture = evt.getFuture();
        TlsClientChannel tlsConnectChannel = (TlsClientChannel) evt.getChannel();
        ChannelAddress tlsLocalAddress = (ChannelAddress) evt.getValue();
        tlsConnectChannel.setLocalAddress(tlsLocalAddress);
        tlsConnectChannel.setBound();

        fireChannelBound(tlsConnectChannel, tlsLocalAddress);
        tlsBindFuture.setSuccess();
    }

    @Override
    protected void connectRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        final TlsClientChannel tlsConnectChannel = (TlsClientChannel) evt.getChannel();
        final ChannelFuture tlsConnectFuture = evt.getFuture();
        final ChannelAddress tlsRemoteAddress = (ChannelAddress) evt.getValue();
        URI tlsLocation = tlsRemoteAddress.getLocation();
        ChannelAddress address = tlsRemoteAddress.getTransport();
        String schemeName = address.getLocation().getScheme();
        String tlsSchemeName = tlsLocation.getScheme();

        TlsChannelConfig tlsConnectConfig = tlsConnectChannel.getConfig();
        File keyStoreFile = tlsConnectConfig.getKeyStoreFile();
        File trustStoreFile = tlsConnectConfig.getTrustStoreFile();
        char[] keyStorePassword = tlsConnectConfig.getKeyStorePassword();
        char[] trustStorePassword = tlsConnectConfig.getTrustStorePassword();

        ChannelPipelineFactory pipelineFactory = new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {

                KeyManager[] keyManagers = null;
                if (keyStoreFile != null)
                {
                    KeyStore keys = KeyStore.getInstance("JKS");
                    keys.load(new FileInputStream(keyStoreFile), keyStorePassword);

                    KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                    kmf.init(keys, keyStorePassword);
                    keyManagers = kmf.getKeyManagers();
                }

                TrustManager[] trustManagers = null;
                if (trustStoreFile != null)
                {
                    KeyStore trusts = KeyStore.getInstance("JKS");
                    trusts.load(new FileInputStream(trustStoreFile), trustStorePassword);

                    TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
                    tmf.init(trusts);
                    trustManagers = tmf.getTrustManagers();
                }

                SSLContext tlsContext = SSLContext.getInstance("TLS");
                tlsContext.init(keyManagers, trustManagers, random);

                String tlsHostname = tlsLocation.getHost();
                int tlsPort = tlsLocation.getPort();

                SSLEngine sslEngine = tlsContext.createSSLEngine(tlsHostname, tlsPort);
                sslEngine.setUseClientMode(true);

                SSLParameters tlsParameters = sslEngine.getSSLParameters();
                tlsParameters.setEndpointIdentificationAlgorithm("HTTPS");
                tlsParameters.setServerNames(asList(new SNIHostName(tlsHostname)));
                sslEngine.setSSLParameters(tlsParameters);

                SslHandler sslHandler = new SslHandler(sslEngine);
                sslHandler.setIssueHandshake(true);

                return pipeline(sslHandler, new TlsClientChannelSource());
            }
        };

        ClientBootstrap bootstrap = bootstrapFactory.newClientBootstrap(schemeName);
        bootstrap.setPipelineFactory(pipelineFactory);
        bootstrap.setOptions(tlsConnectChannel.getConfig().getTransportOptions());
        bootstrap.setOption(format("%s.nextProtocol", schemeName), tlsSchemeName);

        ChannelFuture connectFuture = bootstrap.connect(address);
        connectFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture connectFuture) throws Exception {
                if (connectFuture.isSuccess()) {
                    transport = connectFuture.getChannel();

                    ChannelPipeline pipeline = transport.getPipeline();
                    SslHandler sslHandler = pipeline.get(SslHandler.class);
                    ChannelFuture handshakeFuture = sslHandler.handshake();
                    handshakeFuture.addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture handshakeFuture) throws Exception {
                            if (handshakeFuture.isSuccess()) {
                                TlsChannelConfig tlsConnectConfig = tlsConnectChannel.getConfig();
                                SSLEngine sslEngine = sslHandler.getEngine();
                                tlsConnectConfig.setParameters(sslEngine.getSSLParameters());

                                TlsClientChannelSource tlsChannelSource = pipeline.get(TlsClientChannelSource.class);

                                if (!tlsConnectChannel.isBound()) {
                                    ChannelAddress tlsLocalAddress = tlsRemoteAddress;
                                    tlsConnectChannel.setLocalAddress(tlsLocalAddress);
                                    tlsConnectChannel.setBound();
                                    fireChannelBound(tlsConnectChannel, tlsLocalAddress);
                                }

                                tlsChannelSource.setTlsChannel(tlsConnectChannel);
                                tlsConnectChannel.setRemoteAddress(tlsRemoteAddress);
                                tlsConnectChannel.setConnected();

                                tlsConnectFuture.setSuccess();
                                fireChannelConnected(tlsConnectChannel, tlsRemoteAddress);
                            }
                            else {
                                tlsConnectFuture.setFailure(handshakeFuture.getCause());
                            }
                        }
                    });
                } else {
                    tlsConnectFuture.setFailure(connectFuture.getCause());
                }
            }
        });
    }

    @Override
    protected void writeRequested(ChannelPipeline pipeline, MessageEvent e) throws Exception {

        ChannelFuture tlsFuture = e.getFuture();
        ChannelBuffer tlsContent = (ChannelBuffer) e.getMessage();
        int tlsReadableBytes = tlsContent.readableBytes();

        ChannelFuture future = transport.write(tlsContent);
        chainWriteCompletes(future, tlsFuture, tlsReadableBytes);
    }

    @Override
    protected void shutdownOutputRequested(ChannelPipeline pipeline, ShutdownOutputEvent evt) throws Exception {
        TlsClientChannel tlsClientChannel = (TlsClientChannel) pipeline.getChannel();
        ChannelFuture tlsFuture = evt.getFuture();

        shutdownOutputRequested(tlsClientChannel, tlsFuture);
    }

    @Override
    protected void flushRequested(ChannelPipeline pipeline, FlushEvent evt) throws Exception {
        TlsClientChannel tlsClientChannel = (TlsClientChannel) pipeline.getChannel();
        ChannelFuture tlsFuture = evt.getFuture();
        flushRequested(tlsClientChannel, tlsFuture);
    }

    @Override
    protected void abortOutputRequested(ChannelPipeline pipeline, final WriteAbortEvent evt) throws Exception {
        TlsClientChannel channel = (TlsClientChannel) pipeline.getChannel();
        ChannelFuture flushFuture = Channels.future(channel);
        flushRequested(channel, flushFuture);
        flushFuture.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                ChannelFuture disconnect = transport.disconnect();
                chainFutures(disconnect, evt.getFuture());
            }
        });
    };

    @Override
    protected void closeRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        TlsClientChannel tlsClientChannel = (TlsClientChannel) pipeline.getChannel();
        ChannelFuture tlsFuture = evt.getFuture();
        tlsFuture.setSuccess();

        if (transport != null)
        {
            transport.close();
        }

        boolean wasConnected = tlsClientChannel.isConnected();
        boolean wasBound = tlsClientChannel.isBound();
        if (tlsClientChannel.setClosed()) {
            if (wasConnected) {
                fireChannelDisconnected(tlsClientChannel);
            }
            if (wasBound) {
                fireChannelUnbound(tlsClientChannel);
            }
            fireChannelClosed(tlsClientChannel);
        }
    }

    private void shutdownOutputRequested(TlsClientChannel tlsClientChannel, ChannelFuture tlsFuture) throws Exception {

        flushRequested(tlsClientChannel, tlsFuture);
    }

    private void flushRequested(TlsClientChannel httpClientChannel, ChannelFuture tlsFuture) throws Exception {
        tlsFuture.setSuccess();
    }
}
