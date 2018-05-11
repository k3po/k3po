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
import static java.util.Collections.singleton;
import static javax.net.ssl.SNIHostName.createSNIMatcher;
import static org.jboss.netty.channel.Channels.fireChannelBound;
import static org.jboss.netty.channel.Channels.fireChannelClosed;
import static org.jboss.netty.channel.Channels.fireChannelUnbound;
import static org.jboss.netty.channel.Channels.pipeline;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChildChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.kaazing.k3po.driver.internal.netty.bootstrap.ServerBootstrap;
import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.AbstractServerChannelSink;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;

public class TlsServerChannelSink extends AbstractServerChannelSink<TlsServerChannel> {

    private final SecureRandom random;
    private final ConcurrentNavigableMap<ChannelAddress, TlsServerChannel> tlsBindings;
    private final ConcurrentMap<ChannelAddress, TlsTransport> tlsTransports;

    public TlsServerChannelSink(SecureRandom random) {
        this(random, new ConcurrentSkipListMap<ChannelAddress, TlsServerChannel>(ChannelAddress.ADDRESS_COMPARATOR));
    }

    private TlsServerChannelSink(SecureRandom random, ConcurrentNavigableMap<ChannelAddress, TlsServerChannel> tlsBindings) {
        this.random = random;
        this.tlsBindings = tlsBindings;
        this.tlsTransports = new ConcurrentHashMap<>();
    }

    @Override
    protected void bindRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        final TlsServerChannel tlsBindChannel = (TlsServerChannel) evt.getChannel();
        final ChannelFuture tlsBindFuture = evt.getFuture();
        final ChannelAddress tlsLocalAddress = (ChannelAddress) evt.getValue();
        URI tlsLocation = tlsLocalAddress.getLocation();

        TlsServerChannel tlsBoundChannel = tlsBindings.putIfAbsent(tlsLocalAddress, tlsBindChannel);
        if (tlsBoundChannel != null) {
            tlsBindFuture.setFailure(new ChannelException(format("Duplicate bind failed: %s", tlsLocation)));
        }

        ChannelAddress address = tlsLocalAddress.getTransport();
        TlsTransport tlsTransport = tlsTransports.get(address);
        if (tlsTransport == null) {
            TlsServerChannelConfig tlsConnectConfig = tlsBindChannel.getConfig();
            File keyStoreFile = tlsConnectConfig.getKeyStoreFile();
            File trustStoreFile = tlsConnectConfig.getTrustStoreFile();
            char[] keyStorePassword = tlsConnectConfig.getKeyStorePassword();
            char[] trustStorePassword = tlsConnectConfig.getTrustStorePassword();
            String[] applicationProtocols = tlsConnectConfig.getApplicationProtocols();
            boolean wantClientAuth = tlsConnectConfig.getWantClientAuth();
            boolean needClientAuth = tlsConnectConfig.getNeedClientAuth();

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

                    SSLEngine tlsEngine = tlsContext.createSSLEngine();
                    tlsEngine.setUseClientMode(false);
                    //sslEngine.setNeedClientAuth(true);

                    String tlsHostname = tlsLocation.getHost();

                    SSLParameters tlsParameters = new SSLParameters();
                    if (wantClientAuth) {
                        tlsParameters.setWantClientAuth(true);
                    }
                    if (needClientAuth) {
                        tlsParameters.setNeedClientAuth(true);
                    }
                    tlsParameters.setSNIMatchers(singleton(createSNIMatcher(tlsHostname)));
                    if (applicationProtocols != null && applicationProtocols.length > 0) {
                        setApplicationProtocols(tlsParameters, applicationProtocols);
                    }
                    tlsEngine.setSSLParameters(tlsParameters);

                    SslHandler sslHandler = new SslHandler(tlsEngine);
                    sslHandler.setIssueHandshake(true);

                    return pipeline(sslHandler, new TlsChildChannelSource(tlsBindings));
                }
            };

            String schemeName = address.getLocation().getScheme();
            String tlsSchemeName = tlsLocalAddress.getLocation().getScheme();

            ServerBootstrap bootstrap = bootstrapFactory.newServerBootstrap(schemeName);
            bootstrap.setParentHandler(createParentHandler(tlsBindChannel, address));
            bootstrap.setPipelineFactory(pipelineFactory);
            bootstrap.setOptions(tlsBindChannel.getConfig().getTransportOptions());
            bootstrap.setOption(format("%s.nextProtocol", schemeName), tlsSchemeName);

            // bind transport
            ChannelFuture bindFuture = bootstrap.bindAsync(address);
            TlsTransport newTlsTransport = new TlsTransport(bindFuture, 1);
            tlsTransport = tlsTransports.putIfAbsent(address, newTlsTransport);
            if (tlsTransport == null) {
                tlsTransport = newTlsTransport;
            }
        }
        else {
            tlsTransport.count.incrementAndGet();
        }

        if (tlsTransport.future.isDone()) {
            handleTlsTransportBindComplete(tlsBindChannel, tlsBindFuture, tlsLocalAddress, tlsTransport.future);
        }
        else {
            tlsTransport.future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    handleTlsTransportBindComplete(tlsBindChannel, tlsBindFuture, tlsLocalAddress, future);
                }
            });
        }
    }

    @Override
    protected void unbindRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        final TlsServerChannel tlsUnbindChannel = (TlsServerChannel) evt.getChannel();
        final ChannelFuture tlsUnbindFuture = evt.getFuture();
        ChannelAddress tlsLocalAddress = tlsUnbindChannel.getLocalAddress();

        if (!tlsBindings.remove(tlsLocalAddress, tlsUnbindChannel)) {
            tlsUnbindFuture.setFailure(new ChannelException("Channel not bound"));
            return;
        }

        ChannelAddress address = tlsLocalAddress.getTransport();
        TlsTransport tlsTransport = tlsTransports.get(address);
        assert tlsTransport != null;

        if (tlsTransport.count.decrementAndGet() == 0) {
            // ensure only zero count is removed
            TlsTransport oldTlsTransport = new TlsTransport(tlsTransport.future);
            if (tlsTransports.remove(address, oldTlsTransport)) {
                // unbind transport
                Channel transport = tlsUnbindChannel.getTransport();
                ChannelFuture unbindFuture = transport.unbind();
                if (unbindFuture.isDone()) {
                    handleTlsTransportUnbindComplete(tlsUnbindChannel, tlsUnbindFuture, unbindFuture);
                }
                else {
                    unbindFuture.addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture unbindFuture) throws Exception {
                            handleTlsTransportUnbindComplete(tlsUnbindChannel, tlsUnbindFuture, unbindFuture);
                        }
                    });
                }
            }
        }
        else {
            fireChannelUnbound(tlsUnbindChannel);
            tlsUnbindFuture.setSuccess();
        }
    }

    @Override
    protected void closeRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        final TlsServerChannel tlsCloseChannel = (TlsServerChannel) evt.getChannel();
        final ChannelFuture tlsCloseFuture = evt.getFuture();
        boolean wasBound = tlsCloseChannel.isBound();
        if (!tlsCloseFuture.isDone()) {
            if (wasBound) {
                unbindRequested(pipeline, evt);
            }

            Channel transport = tlsCloseChannel.getTransport();
            if (transport != null) {
                ChannelFuture closeFuture = transport.close();
                if (closeFuture.isDone()) {
                    handleTlsTransportCloseComplete(tlsCloseChannel, tlsCloseFuture, closeFuture);
                }
                else {
                    closeFuture.addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture closeFuture) throws Exception {
                            handleTlsTransportCloseComplete(tlsCloseChannel, tlsCloseFuture, closeFuture);
                        }
                    });
                }
            }
        }
    }

    private ChannelHandler createParentHandler(TlsServerChannel channel, final ChannelAddress address) {
        return new SimpleChannelHandler() {
            @Override
            public void childChannelOpen(ChannelHandlerContext ctx, ChildChannelStateEvent e) throws Exception {
                e.getChannel().setAttachment(address);
                super.childChannelOpen(ctx, e);
            }
        };
    }

    private static void handleTlsTransportBindComplete(
            TlsServerChannel tlsBindChannel,
            ChannelFuture tlsBindFuture,
            ChannelAddress tlsLocalAddress,
            ChannelFuture bindFuture) {

        if (bindFuture.isSuccess()) {
            tlsBindChannel.setTransport(bindFuture.getChannel());
            tlsBindChannel.setLocalAddress(tlsLocalAddress);
            tlsBindChannel.setBound();

            fireChannelBound(tlsBindChannel, tlsBindChannel.getLocalAddress());
            tlsBindFuture.setSuccess();
        }
        else {
            tlsBindFuture.setFailure(bindFuture.getCause());
        }
    }

    private static void handleTlsTransportUnbindComplete(
            TlsServerChannel tlsUnbindChannel,
            ChannelFuture tlsUnbindFuture,
            ChannelFuture unbindFuture) {

        if (unbindFuture.isSuccess()) {
            fireChannelUnbound(tlsUnbindChannel);
            tlsUnbindFuture.setSuccess();
        }
        else {
            tlsUnbindFuture.setFailure(unbindFuture.getCause());
        }
    }

    private static void handleTlsTransportCloseComplete(
            TlsServerChannel tlsCloseChannel,
            ChannelFuture tlsCloseFuture,
            ChannelFuture closeFuture) {

        if (closeFuture.isSuccess()) {
            fireChannelClosed(tlsCloseChannel);
            tlsCloseChannel.setClosed();
        }
        else {
            tlsCloseFuture.setFailure(closeFuture.getCause());
        }
    }

    private static final class TlsTransport {
        final ChannelFuture future;
        final AtomicInteger count;

        TlsTransport(ChannelFuture future) {
            this(future, 0);
        }

        TlsTransport(ChannelFuture future, int count) {
            this.future = future;
            this.count = new AtomicInteger(count);
        }

        @Override
        public int hashCode() {
            return Objects.hash(future, count);
        }

        @Override
        public boolean equals(Object obj) {
            TlsTransport that = (TlsTransport) obj;
            return Objects.equals(this.future, that.future) &&
                    this.count.get() == that.count.get();
        }

        @Override
        public String toString() {
            return format("[future=@%d, count=%d]", Objects.hashCode(future), count.get());
        }
    }

    static void setApplicationProtocols(SSLParameters parameters, String[] protocols) {
        try {
            Method setApplicationProtocolsMethod = SSLParameters.class.getMethod("setApplicationProtocols", String[].class);
            setApplicationProtocolsMethod.invoke(parameters, new Object[] { protocols });
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Cannot call SSLParameters#setApplicationProtocols(). Use JDK 9 to run k3po", e);
        }
    }

}
