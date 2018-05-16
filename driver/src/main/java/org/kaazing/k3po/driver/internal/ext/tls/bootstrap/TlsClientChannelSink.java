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
import static org.kaazing.k3po.driver.internal.netty.channel.Channels.abortInputOrSuccess;
import static org.kaazing.k3po.driver.internal.netty.channel.Channels.abortOutputOrClose;
import static org.kaazing.k3po.driver.internal.netty.channel.Channels.fireInputShutdown;
import static org.kaazing.k3po.driver.internal.netty.channel.Channels.fireOutputShutdown;
import static org.kaazing.k3po.driver.internal.netty.channel.Channels.shutdownOutputOrClose;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Objects;

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
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.ssl.SslHandler;
import org.kaazing.k3po.driver.internal.netty.bootstrap.BootstrapFactory;
import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.AbstractChannelSink;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;
import org.kaazing.k3po.driver.internal.netty.channel.FlushEvent;
import org.kaazing.k3po.driver.internal.netty.channel.ReadAbortEvent;
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
        final TlsClientChannel tlsClientChannel = (TlsClientChannel) evt.getChannel();
        final ChannelFuture tlsConnectFuture = evt.getFuture();
        final ChannelAddress tlsRemoteAddress = (ChannelAddress) evt.getValue();
        URI tlsLocation = tlsRemoteAddress.getLocation();
        ChannelAddress address = tlsRemoteAddress.getTransport();
        String schemeName = address.getLocation().getScheme();
        String tlsSchemeName = tlsLocation.getScheme();

        TlsChannelConfig tlsConnectConfig = tlsClientChannel.getConfig();
        File keyStoreFile = tlsConnectConfig.getKeyStoreFile();
        File trustStoreFile = tlsConnectConfig.getTrustStoreFile();
        char[] keyStorePassword = tlsConnectConfig.getKeyStorePassword();
        char[] trustStorePassword = tlsConnectConfig.getTrustStorePassword();
        String[] applicationProtocols = tlsConnectConfig.getApplicationProtocols();

        ChannelPipelineFactory pipelineFactory = new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {

                KeyManager[] keyManagers = null;
                if (keyStoreFile != null)
                {
                    KeyStore keys = KeyStore.getInstance("JKS");
                    keys.load(new FileInputStream(keyStoreFile), keyStorePassword);

                    KeyManagerFactory kmf = KeyManagerFactory.getInstance("PKIX");
                    kmf.init(keys, keyStorePassword);
                    keyManagers = kmf.getKeyManagers();
                }

                TrustManager[] trustManagers = null;
                if (trustStoreFile != null)
                {
                    KeyStore trusts = KeyStore.getInstance("JKS");
                    trusts.load(new FileInputStream(trustStoreFile), trustStorePassword);

                    TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    tmf.init(trusts);
                    trustManagers = tmf.getTrustManagers();
                }

                SSLContext tlsContext = SSLContext.getInstance("TLS");
                tlsContext.init(keyManagers, trustManagers, random);

                String tlsHostname = tlsLocation.getHost();
                int tlsPort = tlsLocation.getPort();

                SSLEngine tlsEngine = tlsContext.createSSLEngine(tlsHostname, tlsPort);
                tlsEngine.setUseClientMode(true);

                SSLParameters tlsParameters = tlsEngine.getSSLParameters();
                tlsParameters.setEndpointIdentificationAlgorithm("HTTPS");
                tlsParameters.setServerNames(asList(new SNIHostName(tlsHostname)));
                if (applicationProtocols != null && applicationProtocols.length > 0) {
                    setApplicationProtocols(tlsParameters, applicationProtocols);
                }
                tlsEngine.setSSLParameters(tlsParameters);

                SslHandler sslHandler = new SslHandler(tlsEngine);
                sslHandler.setIssueHandshake(true);

                return pipeline(sslHandler, new TlsClientChannelSource());
            }
        };

        ClientBootstrap bootstrap = bootstrapFactory.newClientBootstrap(schemeName);
        bootstrap.setPipelineFactory(pipelineFactory);
        bootstrap.setOptions(tlsClientChannel.getConfig().getTransportOptions());
        bootstrap.setOption(format("%s.nextProtocol", schemeName), tlsSchemeName);

        ChannelFuture connectFuture = bootstrap.connect(address);
        connectFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture connectFuture) throws Exception {
                if (connectFuture.isSuccess()) {
                    transport = connectFuture.getChannel();
                    transport.getConfig().setBufferFactory(tlsConnectConfig.getBufferFactory());

                    ChannelPipeline pipeline = transport.getPipeline();
                    SslHandler sslHandler = pipeline.get(SslHandler.class);
                    ChannelFuture handshakeFuture = sslHandler.handshake();
                    handshakeFuture.addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture handshakeFuture) throws Exception {
                            if (handshakeFuture.isSuccess()) {
                                TlsChannelConfig tlsConnectConfig = tlsClientChannel.getConfig();
                                SSLEngine tlsEngine = sslHandler.getEngine();
                                tlsConnectConfig.setParameters(tlsEngine.getSSLParameters());

                                TlsClientChannelSource tlsChannelSource = pipeline.get(TlsClientChannelSource.class);

                                detectWriteTransportClosed(transport, tlsClientChannel);

                                ChannelFuture tlsCloseFuture = sslHandler.getSSLEngineInboundCloseFuture();
                                tlsCloseFuture.addListener(new ChannelFutureListener() {
                                    @Override
                                    public void operationComplete(ChannelFuture future) throws Exception {

                                        if (tlsClientChannel != null) {

                                            if (tlsClientChannel.setReadClosed()) {
                                                fireInputShutdown(tlsClientChannel);
                                                fireChannelDisconnected(tlsClientChannel);
                                                fireChannelUnbound(tlsClientChannel);
                                                fireChannelClosed(tlsClientChannel);
                                            }
                                            else
                                            {
                                                fireInputShutdown(tlsClientChannel);
                                            }
                                        }
                                    }
                                });

                                if (!tlsClientChannel.isBound()) {
                                    ChannelAddress tlsLocalAddress = tlsRemoteAddress;
                                    tlsClientChannel.setLocalAddress(tlsLocalAddress);
                                    tlsClientChannel.setBound();
                                    fireChannelBound(tlsClientChannel, tlsLocalAddress);
                                }

                                tlsChannelSource.setTlsChannel(tlsClientChannel);
                                tlsClientChannel.setRemoteAddress(tlsRemoteAddress);
                                tlsClientChannel.setConnected();

                                tlsConnectFuture.setSuccess();
                                fireChannelConnected(tlsClientChannel, tlsRemoteAddress);

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
    protected void flushRequested(ChannelPipeline pipeline, FlushEvent evt) throws Exception {
        TlsClientChannel tlsClientChannel = (TlsClientChannel) pipeline.getChannel();
        ChannelFuture tlsFuture = evt.getFuture();
        flushRequested(tlsClientChannel, tlsFuture);
    }

    @Override
    protected void abortInputRequested(ChannelPipeline pipeline, final ReadAbortEvent evt) throws Exception {
        ChannelHandlerContext ctx = transport.getPipeline().getContext(SslHandler.class);
        ChannelFuture tlsFuture = evt.getFuture();
        abortInputOrSuccess(ctx, tlsFuture);
    }

    @Override
    protected void abortOutputRequested(ChannelPipeline pipeline, final WriteAbortEvent evt) throws Exception {
        ChannelHandlerContext ctx = transport.getPipeline().getContext(SslHandler.class);
        ChannelFuture tlsFuture = evt.getFuture();
        abortOutputOrClose(ctx, tlsFuture);
    };

    @Override
    protected void shutdownOutputRequested(ChannelPipeline pipeline, ShutdownOutputEvent evt) throws Exception {
        TlsClientChannel tlsClientChannel = (TlsClientChannel) pipeline.getChannel();
        ChannelFuture tlsFuture = evt.getFuture();
        shutdownOutputRequested(tlsClientChannel, tlsFuture);
    }

    @Override
    protected void closeRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        TlsClientChannel tlsClientChannel = (TlsClientChannel) pipeline.getChannel();
        ChannelFuture tlsFuture = evt.getFuture();
        if (!tlsClientChannel.isOpen()) {
            tlsFuture.setSuccess();
        }
        else
        {
            tlsClientChannel.setReadClosed();
            shutdownOutputRequested(tlsClientChannel, tlsFuture);
        }
    }

    private void shutdownOutputRequested(TlsClientChannel tlsClientChannel, ChannelFuture tlsFuture) {
        SslHandler tlsHandler = transport.getPipeline().get(SslHandler.class);
        if (tlsClientChannel.isReadClosed()) {
            chainFutures(shutdownOutputOrClose(transport), tlsFuture);
        }
        else if (tlsHandler != null) {
            ChannelFuture tlsCloseFuture = tlsHandler.close();
            tlsCloseFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (tlsClientChannel.setWriteClosed()) {
                        fireOutputShutdown(tlsClientChannel);
                        fireChannelDisconnected(tlsClientChannel);
                        fireChannelUnbound(tlsClientChannel);
                        fireChannelClosed(tlsClientChannel);
                    }
                    else {
                        fireOutputShutdown(tlsClientChannel);
                    }
                }
            });
            tlsHandler.getSSLEngineInboundCloseFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    shutdownOutputOrClose(transport);
                }
            });
            chainFutures(tlsCloseFuture, tlsFuture);
        }
    }

    private void flushRequested(TlsClientChannel httpClientChannel, ChannelFuture tlsFuture) throws Exception {
        tlsFuture.setSuccess();
    }

    private void detectWriteTransportClosed(Channel transport, TlsClientChannel tlsClientChannel) {

        Objects.requireNonNull(tlsClientChannel);

        final ChannelFutureListener closeListener = new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (tlsClientChannel.setWriteClosed()) {
                    fireChannelDisconnected(tlsClientChannel);
                    fireChannelUnbound(tlsClientChannel);
                    fireChannelClosed(tlsClientChannel);
                }
            }
        };

        transport.getCloseFuture().addListener(closeListener);

        tlsClientChannel.getCloseFuture().addListener(new ChannelFutureListener()
        {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                transport.getCloseFuture().removeListener(closeListener);
            }
        });
    }

    static void setApplicationProtocols(SSLParameters parameters, String[] protocols) {
        try {
            Method setApplicationProtocolsMethod = SSLParameters.class.getMethod("setApplicationProtocols", String[].class);
            setApplicationProtocolsMethod.invoke(parameters, new Object[] { protocols } );
        } catch (Throwable t) {
            throw new RuntimeException("Cannot call SSLParameters#setApplicationProtocols(). Use JDK 9 to run k3po");
        }
    }
}
