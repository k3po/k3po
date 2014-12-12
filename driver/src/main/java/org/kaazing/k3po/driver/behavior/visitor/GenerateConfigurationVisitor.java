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

package org.kaazing.k3po.driver.behavior.visitor;

import static java.util.Objects.requireNonNull;
import static org.jboss.netty.channel.Channels.pipeline;
import static org.jboss.netty.util.CharsetUtil.UTF_8;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.el.ELResolver;
import javax.el.ValueExpression;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.k3po.driver.RobotException;
import org.kaazing.k3po.driver.behavior.Barrier;
import org.kaazing.k3po.driver.behavior.Configuration;
import org.kaazing.k3po.driver.behavior.handler.CompletionHandler;
import org.kaazing.k3po.driver.behavior.handler.FailureHandler;
import org.kaazing.k3po.driver.behavior.handler.barrier.AwaitBarrierDownstreamHandler;
import org.kaazing.k3po.driver.behavior.handler.barrier.AwaitBarrierUpstreamHandler;
import org.kaazing.k3po.driver.behavior.handler.barrier.NotifyBarrierHandler;
import org.kaazing.k3po.driver.behavior.handler.codec.MaskingDecoder;
import org.kaazing.k3po.driver.behavior.handler.codec.MaskingDecoders;
import org.kaazing.k3po.driver.behavior.handler.codec.MessageDecoder;
import org.kaazing.k3po.driver.behavior.handler.codec.MessageEncoder;
import org.kaazing.k3po.driver.behavior.handler.codec.ReadByteArrayBytesDecoder;
import org.kaazing.k3po.driver.behavior.handler.codec.ReadByteLengthBytesDecoder;
import org.kaazing.k3po.driver.behavior.handler.codec.ReadExactBytesDecoder;
import org.kaazing.k3po.driver.behavior.handler.codec.ReadExactTextDecoder;
import org.kaazing.k3po.driver.behavior.handler.codec.ReadExpressionDecoder;
import org.kaazing.k3po.driver.behavior.handler.codec.ReadIntLengthBytesDecoder;
import org.kaazing.k3po.driver.behavior.handler.codec.ReadLongLengthBytesDecoder;
import org.kaazing.k3po.driver.behavior.handler.codec.ReadRegexDecoder;
import org.kaazing.k3po.driver.behavior.handler.codec.ReadShortLengthBytesDecoder;
import org.kaazing.k3po.driver.behavior.handler.codec.ReadVariableLengthBytesDecoder;
import org.kaazing.k3po.driver.behavior.handler.codec.WriteBytesEncoder;
import org.kaazing.k3po.driver.behavior.handler.codec.WriteExpressionEncoder;
import org.kaazing.k3po.driver.behavior.handler.codec.WriteTextEncoder;
import org.kaazing.k3po.driver.behavior.handler.codec.http.HttpContentLengthEncoder;
import org.kaazing.k3po.driver.behavior.handler.codec.http.HttpHeaderDecoder;
import org.kaazing.k3po.driver.behavior.handler.codec.http.HttpHeaderEncoder;
import org.kaazing.k3po.driver.behavior.handler.codec.http.HttpMethodDecoder;
import org.kaazing.k3po.driver.behavior.handler.codec.http.HttpMethodEncoder;
import org.kaazing.k3po.driver.behavior.handler.codec.http.HttpParameterDecoder;
import org.kaazing.k3po.driver.behavior.handler.codec.http.HttpParameterEncoder;
import org.kaazing.k3po.driver.behavior.handler.codec.http.HttpStatusDecoder;
import org.kaazing.k3po.driver.behavior.handler.codec.http.HttpStatusEncoder;
import org.kaazing.k3po.driver.behavior.handler.codec.http.HttpVersionDecoder;
import org.kaazing.k3po.driver.behavior.handler.codec.http.HttpVersionEncoder;
import org.kaazing.k3po.driver.behavior.handler.command.CloseHandler;
import org.kaazing.k3po.driver.behavior.handler.command.DisconnectHandler;
import org.kaazing.k3po.driver.behavior.handler.command.FlushHandler;
import org.kaazing.k3po.driver.behavior.handler.command.ReadConfigHandler;
import org.kaazing.k3po.driver.behavior.handler.command.ShutdownOutputHandler;
import org.kaazing.k3po.driver.behavior.handler.command.UnbindHandler;
import org.kaazing.k3po.driver.behavior.handler.command.WriteConfigHandler;
import org.kaazing.k3po.driver.behavior.handler.command.WriteHandler;
import org.kaazing.k3po.driver.behavior.handler.event.BoundHandler;
import org.kaazing.k3po.driver.behavior.handler.event.ChildClosedHandler;
import org.kaazing.k3po.driver.behavior.handler.event.ChildOpenedHandler;
import org.kaazing.k3po.driver.behavior.handler.event.ClosedHandler;
import org.kaazing.k3po.driver.behavior.handler.event.ConnectedHandler;
import org.kaazing.k3po.driver.behavior.handler.event.DisconnectedHandler;
import org.kaazing.k3po.driver.behavior.handler.event.InputShutdownHandler;
import org.kaazing.k3po.driver.behavior.handler.event.OpenedHandler;
import org.kaazing.k3po.driver.behavior.handler.event.ReadHandler;
import org.kaazing.k3po.driver.behavior.handler.event.UnboundHandler;
import org.kaazing.k3po.driver.behavior.visitor.GenerateConfigurationVisitor.State;
import org.kaazing.k3po.driver.netty.bootstrap.BootstrapFactory;
import org.kaazing.k3po.driver.netty.bootstrap.ClientBootstrap;
import org.kaazing.k3po.driver.netty.bootstrap.ServerBootstrap;
import org.kaazing.k3po.driver.netty.channel.ChannelAddress;
import org.kaazing.k3po.driver.netty.channel.ChannelAddressFactory;
import org.kaazing.k3po.lang.RegionInfo;
import org.kaazing.k3po.lang.ast.AstAcceptNode;
import org.kaazing.k3po.lang.ast.AstAcceptableNode;
import org.kaazing.k3po.lang.ast.AstBoundNode;
import org.kaazing.k3po.lang.ast.AstChildClosedNode;
import org.kaazing.k3po.lang.ast.AstChildOpenedNode;
import org.kaazing.k3po.lang.ast.AstCloseNode;
import org.kaazing.k3po.lang.ast.AstClosedNode;
import org.kaazing.k3po.lang.ast.AstConnectNode;
import org.kaazing.k3po.lang.ast.AstConnectedNode;
import org.kaazing.k3po.lang.ast.AstDisconnectNode;
import org.kaazing.k3po.lang.ast.AstDisconnectedNode;
import org.kaazing.k3po.lang.ast.AstFlushNode;
import org.kaazing.k3po.lang.ast.AstNode;
import org.kaazing.k3po.lang.ast.AstOpenedNode;
import org.kaazing.k3po.lang.ast.AstPropertyNode;
import org.kaazing.k3po.lang.ast.AstReadAwaitNode;
import org.kaazing.k3po.lang.ast.AstReadClosedNode;
import org.kaazing.k3po.lang.ast.AstReadConfigNode;
import org.kaazing.k3po.lang.ast.AstReadNotifyNode;
import org.kaazing.k3po.lang.ast.AstReadOptionNode;
import org.kaazing.k3po.lang.ast.AstReadValueNode;
import org.kaazing.k3po.lang.ast.AstScriptNode;
import org.kaazing.k3po.lang.ast.AstStreamNode;
import org.kaazing.k3po.lang.ast.AstStreamableNode;
import org.kaazing.k3po.lang.ast.AstUnbindNode;
import org.kaazing.k3po.lang.ast.AstUnboundNode;
import org.kaazing.k3po.lang.ast.AstWriteAwaitNode;
import org.kaazing.k3po.lang.ast.AstWriteCloseNode;
import org.kaazing.k3po.lang.ast.AstWriteConfigNode;
import org.kaazing.k3po.lang.ast.AstWriteNotifyNode;
import org.kaazing.k3po.lang.ast.AstWriteOptionNode;
import org.kaazing.k3po.lang.ast.AstWriteValueNode;
import org.kaazing.k3po.lang.ast.matcher.AstByteLengthBytesMatcher;
import org.kaazing.k3po.lang.ast.matcher.AstExactBytesMatcher;
import org.kaazing.k3po.lang.ast.matcher.AstExactTextMatcher;
import org.kaazing.k3po.lang.ast.matcher.AstExpressionMatcher;
import org.kaazing.k3po.lang.ast.matcher.AstFixedLengthBytesMatcher;
import org.kaazing.k3po.lang.ast.matcher.AstIntLengthBytesMatcher;
import org.kaazing.k3po.lang.ast.matcher.AstLongLengthBytesMatcher;
import org.kaazing.k3po.lang.ast.matcher.AstRegexMatcher;
import org.kaazing.k3po.lang.ast.matcher.AstShortLengthBytesMatcher;
import org.kaazing.k3po.lang.ast.matcher.AstValueMatcher;
import org.kaazing.k3po.lang.ast.matcher.AstVariableLengthBytesMatcher;
import org.kaazing.k3po.lang.ast.value.AstExpressionValue;
import org.kaazing.k3po.lang.ast.value.AstLiteralBytesValue;
import org.kaazing.k3po.lang.ast.value.AstLiteralTextValue;
import org.kaazing.k3po.lang.ast.value.AstValue;
import org.kaazing.k3po.lang.el.ExpressionContext;

/**
 * Builds the pipeline of handlers that are used to "execute" the Robot script.
 */
public class GenerateConfigurationVisitor implements AstNode.Visitor<Configuration, State> {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(GenerateConfigurationVisitor.class);

    private static final MaskingDecoder DEFAULT_READ_UNMASKER = new DefaultReadUnmasker();

    private final ChannelAddressFactory addressFactory;
    private final BootstrapFactory bootstrapFactory;

    public static final class State {
        private final ConcurrentMap<String, Barrier> barriersByName;
        private Configuration configuration;

        // the read unmasker is reset per stream
        private MaskingDecoder readUnmasker;

        /* The pipelineAsMap is built by each node that is visited. */
        private Map<String, ChannelHandler> pipelineAsMap;

        public State() {
            barriersByName = new ConcurrentHashMap<String, Barrier>();
        }

        private Barrier lookupBarrier(String barrierName) {
            Barrier barrier = barriersByName.get(barrierName);
            if (barrier == null) {
                Barrier newBarrier = new Barrier(barrierName);
                barrier = barriersByName.putIfAbsent(barrierName, newBarrier);
                if (barrier == null) {
                    barrier = newBarrier;
                }
            }

            return barrier;
        }

        public class PipelineFactory {
            private Map<URI, List<ChannelPipeline>> pipelines = new HashMap<URI, List<ChannelPipeline>>();

            public List<ChannelPipeline> getPipeline(URI acceptURI) {
                List<ChannelPipeline> pipeline = pipelines.get(acceptURI);
                if (pipeline == null) {
                    pipeline = new ArrayList<ChannelPipeline>();
                    pipelines.put(acceptURI, pipeline);
                }
                return pipeline;
            }
        }
    }

    private static final class DefaultReadUnmasker extends MaskingDecoder {

        @Override
        public ChannelBuffer applyMask(ChannelBuffer buffer) throws Exception {
            return buffer;
        }

        @Override
        public ChannelBuffer undoMask(ChannelBuffer buffer) throws Exception {
            return buffer;
        }

    }

    public GenerateConfigurationVisitor(BootstrapFactory bootstrapFactory, ChannelAddressFactory addressFactory) {
        this.bootstrapFactory = bootstrapFactory;
        this.addressFactory = addressFactory;
    }

    @Override
    public Configuration visit(AstScriptNode script, State state) throws Exception {

        state.configuration = new Configuration();

        for (AstPropertyNode property : script.getProperties()) {
            property.accept(this, state);
        }

        for (AstStreamNode stream : script.getStreams()) {
            stream.accept(this, state);
        }

        return state.configuration;
    }

    @Override
    public Configuration visit(AstPropertyNode propertyNode, State state) throws Exception {

        String propertyName = propertyNode.getPropertyName();
        AstValue propertyValue = propertyNode.getPropertyValue();

        Configuration config = state.configuration;
        ExpressionContext environment = config.getExpressionContext();
        Object value = propertyValue.accept(new GeneratePropertyValueVisitor(), environment);
        ELResolver resolver = environment.getELResolver();
        resolver.setValue(environment, null, propertyName, value);

        return state.configuration;
    }

    private static class GeneratePropertyValueVisitor implements AstValue.Visitor<Object, ExpressionContext> {

        @Override
        public Object visit(AstExpressionValue value, ExpressionContext environment) throws Exception {
            return value.getValue().getValue(environment);
        }

        @Override
        public Object visit(AstLiteralTextValue value, ExpressionContext environment) throws Exception {
            return value.getValue();
        }

        @Override
        public Object visit(AstLiteralBytesValue value, ExpressionContext environment) throws Exception {
            return value.getValue();
        }

    }

    @Override
    public Configuration visit(AstAcceptableNode acceptedNode, State state) throws Exception {

        // masking is a no-op by default for each stream
        state.readUnmasker = DEFAULT_READ_UNMASKER;
        state.pipelineAsMap = new LinkedHashMap<String, ChannelHandler>();

        for (AstStreamableNode streamable : acceptedNode.getStreamables()) {
            streamable.accept(this, state);
        }

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("completion#%d", pipelineAsMap.size() + 1);

        CompletionHandler handler = new CompletionHandler();
        handler.setRegionInfo(acceptedNode.getRegionInfo());
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstAcceptNode acceptNode, State state) throws Exception {

        Map<String, ChannelHandler> savedPipelineAsMap = state.pipelineAsMap;

        // masking is a no-op by default for each stream
        state.readUnmasker = DEFAULT_READ_UNMASKER;

        URI acceptURI = acceptNode.getLocation();

        /* Create a list of pipelines, for each acceptable */
        final List<ChannelPipeline> pipelines = new ArrayList<ChannelPipeline>();
        state.pipelineAsMap = new LinkedHashMap<String, ChannelHandler>();

        for (AstAcceptableNode acceptableNode : acceptNode.getAcceptables()) {

            acceptableNode.accept(this, state);

            ChannelPipeline pipeline = pipelineFromMap(state.pipelineAsMap);
            pipelines.add(pipeline);
        }
        state.pipelineAsMap = savedPipelineAsMap;

        // retain pipelines for tear down
        RegionInfo acceptInfo = acceptNode.getRegionInfo();
        state.configuration.getServerPipelines(acceptInfo).addAll(pipelines);
        state.configuration.getClientAndServerPipelines().addAll(pipelines);

        /*
         * As new connections are accepted we grab a pipeline line off the list. Note the pipelines map is ordered. Note
         * that the final pipeline is just a Fail and Complete so that additional connect attempts will fail.
         */
        ChannelPipelineFactory pipelineFactory = new ChannelPipelineFactory() {
            private final Iterator<ChannelPipeline> i = pipelines.iterator();

            @Override
            public ChannelPipeline getPipeline() throws Exception {
                return i.hasNext() ? i.next() : pipeline(new FailureHandler(), new CompletionHandler());
            }
        };

        Map<String, Object> acceptOptions = acceptNode.getOptions();
        ChannelAddress localAddress = addressFactory.newChannelAddress(acceptURI, acceptOptions);

        ServerBootstrap serverBootstrap = bootstrapFactory.newServerBootstrap(acceptURI.getScheme());
        serverBootstrap.setOptions(acceptOptions);
        serverBootstrap.setPipelineFactory(pipelineFactory);
        serverBootstrap.setOption("localAddress", localAddress);
        serverBootstrap.setOption("regionInfo", acceptInfo);

        state.configuration.getServerBootstraps().add(serverBootstrap);

        return state.configuration;
    }

    /**
     * Creates the pipeline, visits all streamable nodes and the creates the ClientBootstrap with the pipeline and
     * remote address,
     */
    @Override
    public Configuration visit(AstConnectNode connectNode, State state) throws Exception {

        URI connectURI = connectNode.getLocation();
        // masking is a no-op by default for each stream
        state.readUnmasker = DEFAULT_READ_UNMASKER;

        Map<String, Object> connectOptions = connectNode.getOptions();

        state.pipelineAsMap = new LinkedHashMap<String, ChannelHandler>();

        for (AstStreamableNode streamable : connectNode.getStreamables()) {
            streamable.accept(this, state);
        }

        /* Add the completion handler */
        String handlerName = String.format("completion#%d", state.pipelineAsMap.size() + 1);
        CompletionHandler completionHandler = new CompletionHandler();
        completionHandler.setRegionInfo(connectNode.getRegionInfo());
        state.pipelineAsMap.put(handlerName, completionHandler);

        ChannelAddress remoteAddress = addressFactory.newChannelAddress(connectURI);
        connectOptions.put("remoteAddress", remoteAddress);
        connectOptions.put("regionInfo", connectNode.getRegionInfo());

        ClientBootstrap clientBootstrap = bootstrapFactory.newClientBootstrap(connectURI.getScheme());

        final ChannelPipeline pipeline = pipelineFromMap(state.pipelineAsMap);

        // retain pipelines for tear down
        state.configuration.getClientAndServerPipelines().add(pipeline);

        /*
         * TODO. This is weird. I will only have one pipeline per connect. But if I don't set a factory When a connect
         * occurs it will create a shallow copy of the pipeline I set. This doesn't work due to the beforeAdd methods in
         * ExecutionHandler. Namely when the pipeline is cloned it uses the same handler objects so the handler future
         * is not null and we fail with an assertion error.
         */
        ChannelPipelineFactory pipelineFactory = new ChannelPipelineFactory() {
            private int numCalled;

            @Override
            public ChannelPipeline getPipeline() throws Exception {
                if (numCalled++ != 0) {
                    throw new RobotException("getPipeline called more than once");
                }
                return pipeline;
            }
        };
        clientBootstrap.setPipelineFactory(pipelineFactory);
        clientBootstrap.setOptions(connectOptions);

        state.configuration.getClientBootstraps().add(clientBootstrap);

        LOGGER.debug("Added client Bootstrap connecting to remoteAddress " + remoteAddress);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstReadAwaitNode node, State state) throws Exception {

        RegionInfo regionInfo = node.getRegionInfo();
        String barrierName = node.getBarrierName();
        Barrier barrier = state.lookupBarrier(barrierName);

        AwaitBarrierUpstreamHandler handler = new AwaitBarrierUpstreamHandler(barrier);
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("read.await#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        state.configuration.getBarriers().add(barrier);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstWriteAwaitNode node, State state) throws Exception {

        RegionInfo regionInfo = node.getRegionInfo();
        String barrierName = node.getBarrierName();
        Barrier barrier = state.lookupBarrier(barrierName);

        AwaitBarrierDownstreamHandler handler = new AwaitBarrierDownstreamHandler(barrier);
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("write.await#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        state.configuration.getBarriers().add(barrier);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstReadNotifyNode node, State state) throws Exception {

        RegionInfo regionInfo = node.getRegionInfo();
        String barrierName = node.getBarrierName();
        Barrier barrier = state.lookupBarrier(barrierName);

        NotifyBarrierHandler handler = new NotifyBarrierHandler(barrier);
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("read.notify#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        state.configuration.getBarriers().add(barrier);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstWriteNotifyNode node, State state) throws Exception {

        RegionInfo regionInfo = node.getRegionInfo();
        String barrierName = node.getBarrierName();
        Barrier barrier = state.lookupBarrier(barrierName);

        NotifyBarrierHandler handler = new NotifyBarrierHandler(barrier);
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("write.notify#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        state.configuration.getBarriers().add(barrier);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstWriteValueNode node, State state) throws Exception {
        List<MessageEncoder> messageEncoders = new ArrayList<MessageEncoder>();

        for (AstValue val : node.getValues()) {
            messageEncoders.add(val.accept(new GenerateWriteEncoderVisitor(), state.configuration));
        }
        WriteHandler handler = new WriteHandler(messageEncoders);
        handler.setRegionInfo(node.getRegionInfo());
        String handlerName = String.format("write#%d", state.pipelineAsMap.size() + 1);
        state.pipelineAsMap.put(handlerName, handler);
        return state.configuration;
    }

    private static final class GenerateWriteEncoderVisitor implements AstValue.Visitor<MessageEncoder, Configuration> {

        @Override
        public MessageEncoder visit(AstExpressionValue value, Configuration config) throws Exception {
            ExpressionContext environment = config.getExpressionContext();
            return new WriteExpressionEncoder(value.getValue(), environment);
        }

        @Override
        public MessageEncoder visit(AstLiteralTextValue value, Configuration config) throws Exception {
            return new WriteTextEncoder(value.getValue(), UTF_8);
        }

        @Override
        public MessageEncoder visit(AstLiteralBytesValue value, Configuration config) throws Exception {
            return new WriteBytesEncoder(value.getValue());
        }
    }

    @Override
    public Configuration visit(AstDisconnectNode node, State state) throws Exception {

        RegionInfo regionInfo = node.getRegionInfo();

        DisconnectHandler handler = new DisconnectHandler();
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("disconnect#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstUnbindNode node, State state) throws Exception {

        RegionInfo regionInfo = node.getRegionInfo();

        UnbindHandler handler = new UnbindHandler();
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("unbind#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstCloseNode node, State state) throws Exception {

        RegionInfo regionInfo = node.getRegionInfo();

        CloseHandler handler = new CloseHandler();
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("close#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstChildOpenedNode node, State state) throws Exception {

        RegionInfo regionInfo = node.getRegionInfo();

        ChildOpenedHandler handler = new ChildOpenedHandler();
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("childOpened#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstChildClosedNode node, State state) throws Exception {

        RegionInfo regionInfo = node.getRegionInfo();

        ChildClosedHandler handler = new ChildClosedHandler();
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("childClosed#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstOpenedNode node, State state) throws Exception {

        RegionInfo regionInfo = node.getRegionInfo();

        OpenedHandler handler = new OpenedHandler();
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("opened#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstBoundNode node, State state) throws Exception {

        RegionInfo regionInfo = node.getRegionInfo();

        BoundHandler handler = new BoundHandler();
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("bound#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstConnectedNode node, State state) throws Exception {

        RegionInfo regionInfo = node.getRegionInfo();

        ConnectedHandler handler = new ConnectedHandler();
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("connected#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstReadValueNode node, State state) throws Exception {

        List<MessageDecoder> messageDecoders = new ArrayList<MessageDecoder>();

        for (AstValueMatcher matcher : node.getMatchers()) {
            messageDecoders.add(matcher.accept(new GenerateReadDecoderVisitor(), state.configuration));
        }

        ReadHandler handler = new ReadHandler(messageDecoders, state.readUnmasker);
        handler.setRegionInfo(node.getRegionInfo());
        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("read#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);
        return state.configuration;
    }

    private static final class GenerateReadDecoderVisitor implements
            AstValueMatcher.Visitor<MessageDecoder, Configuration> {

        @Override
        public MessageDecoder visit(AstExpressionMatcher matcher, Configuration config) throws Exception {
            ValueExpression expression = matcher.getValue();
            ExpressionContext environment = config.getExpressionContext();
            return new ReadExpressionDecoder(matcher.getRegionInfo(), expression, environment);
        }

        @Override
        public MessageDecoder visit(AstFixedLengthBytesMatcher matcher, Configuration config) throws Exception {

            int length = matcher.getLength();
            String captureName = matcher.getCaptureName();
            ExpressionContext environment = config.getExpressionContext();
            MessageDecoder decoder = (captureName != null)
                    ? new ReadByteArrayBytesDecoder(matcher.getRegionInfo(), length, environment, captureName)
                    : new ReadByteArrayBytesDecoder(matcher.getRegionInfo(), length);
            return decoder;
        }

        @Override
        public MessageDecoder visit(AstByteLengthBytesMatcher matcher, Configuration config) throws Exception {

            // String captureName = matcher.getCaptureName();
            // ExpressionContext environment = state.configuration.getExpressionContext();
            // state.readDecoders.add(new ReadByteLengthBytesDecoder(environment, captureName));
            // return null;
            return fixedLengthVisit(matcher, config, ReadByteLengthBytesDecoder.class);
        }

        @Override
        public MessageDecoder visit(AstShortLengthBytesMatcher matcher, Configuration config) throws Exception {

            // String captureName = matcher.getCaptureName();
            // ExpressionContext environment = state.configuration.getExpressionContext();
            // state.readDecoders.add(new ReadShortLengthBytesDecoder(environment, captureName));
            // return null;
            return fixedLengthVisit(matcher, config, ReadShortLengthBytesDecoder.class);
        }

        @Override
        public MessageDecoder visit(AstIntLengthBytesMatcher matcher, Configuration config) throws Exception {

            // String captureName = matcher.getCaptureName();
            // ExpressionContext environment = state.configuration.getExpressionContext();
            // state.readDecoders.add(new ReadIntLengthBytesDecoder(environment, captureName));
            // return null;
            return fixedLengthVisit(matcher, config, ReadIntLengthBytesDecoder.class);
        }

        @Override
        public MessageDecoder visit(AstLongLengthBytesMatcher matcher, Configuration config) throws Exception {

            // String captureName = matcher.getCaptureName();
            // ExpressionContext environment = state.configuration.getExpressionContext();
            // state.readDecoders.add(new ReadLongLengthBytesDecoder(environment, captureName));
            // return null;
            return fixedLengthVisit(matcher, config, ReadLongLengthBytesDecoder.class);
        }

        private MessageDecoder fixedLengthVisit(AstFixedLengthBytesMatcher matcher, Configuration config, Class<?> clazz)
                throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
                IllegalArgumentException, InvocationTargetException {

            String captureName = matcher.getCaptureName();
            RegionInfo regionInfo = matcher.getRegionInfo();

            ExpressionContext environment = config.getExpressionContext();
            @SuppressWarnings("unchecked") Constructor<MessageDecoder> constructor = (Constructor<MessageDecoder>) clazz
                    .getConstructor(RegionInfo.class, ExpressionContext.class, String.class);
            return constructor.newInstance(regionInfo, environment, captureName);

        }

        @Override
        public MessageDecoder visit(AstRegexMatcher matcher, Configuration config) throws Exception {
            ExpressionContext environment = config.getExpressionContext();
            MessageDecoder result;
            result = new ReadRegexDecoder(matcher.getRegionInfo(), matcher.getValue(), UTF_8, environment);
            return result;
        }

        @Override
        public MessageDecoder visit(AstExactTextMatcher matcher, Configuration config) throws Exception {
            return new ReadExactTextDecoder(matcher.getRegionInfo(), matcher.getValue(), UTF_8);
        }

        @Override
        public MessageDecoder visit(AstExactBytesMatcher matcher, Configuration config) throws Exception {
            return new ReadExactBytesDecoder(matcher.getRegionInfo(), matcher.getValue());
        }

        @Override
        public MessageDecoder visit(AstVariableLengthBytesMatcher matcher, Configuration config) throws Exception {

            ValueExpression length = matcher.getLength();
            String captureName = matcher.getCaptureName();
            ExpressionContext environment = config.getExpressionContext();
            MessageDecoder decoder = (captureName != null)
                    ? new ReadVariableLengthBytesDecoder(matcher.getRegionInfo(), length, environment, captureName)
                    : new ReadVariableLengthBytesDecoder(matcher.getRegionInfo(), length, environment);
            return decoder;
        }

    }

    @Override
    public Configuration visit(AstDisconnectedNode node, State state) throws Exception {

        RegionInfo regionInfo = node.getRegionInfo();

        DisconnectedHandler handler = new DisconnectedHandler();
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("disconnected#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstUnboundNode node, State state) throws Exception {

        RegionInfo regionInfo = node.getRegionInfo();

        UnboundHandler handler = new UnboundHandler();
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("unbound#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstClosedNode node, State state) throws Exception {

        RegionInfo regionInfo = node.getRegionInfo();

        ClosedHandler handler = new ClosedHandler();
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("closed#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    private static ChannelPipeline pipelineFromMap(Map<String, ChannelHandler> pipelineAsMap) {
        ChannelPipeline pipeline = pipeline();
        for (Map.Entry<String, ChannelHandler> entry : pipelineAsMap.entrySet()) {
            pipeline.addLast(entry.getKey(), entry.getValue());
        }

        return pipeline;
    }

    // HTTP
    @Override
    public Configuration visit(AstReadConfigNode node, State state) throws Exception {

        switch (node.getType()) {
        case "method": {
            AstValueMatcher methodName = node.getMatcher("name");
            requireNonNull(methodName);

            MessageDecoder methodValueDecoder = methodName.accept(new GenerateReadDecoderVisitor(), state.configuration);

            // TODO: compareEqualsIgnoreCase
            ReadConfigHandler handler = new ReadConfigHandler(new HttpMethodDecoder(methodValueDecoder));

            handler.setRegionInfo(node.getRegionInfo());
            Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
            String handlerName = String.format("readConfig#%d (http method)", pipelineAsMap.size() + 1);
            pipelineAsMap.put(handlerName, handler);
            return state.configuration;
        }
        case "header": {
            AstLiteralTextValue name = (AstLiteralTextValue) node.getValue("name");
            requireNonNull(name);

            List<MessageDecoder> valueDecoders = new ArrayList<MessageDecoder>();
            for (AstValueMatcher matcher : node.getMatchers()) {
                valueDecoders.add(matcher.accept(new GenerateReadDecoderVisitor(), state.configuration));
            }

            HttpHeaderDecoder decoder = new HttpHeaderDecoder(name.getValue(), valueDecoders);
            decoder.setRegionInfo(node.getRegionInfo());
            ReadConfigHandler handler = new ReadConfigHandler(decoder);

            handler.setRegionInfo(node.getRegionInfo());
            Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
            String handlerName = String.format("readConfig#%d (http header)", pipelineAsMap.size() + 1);
            pipelineAsMap.put(handlerName, handler);
            return state.configuration;
        }
        case "parameter": {
            AstLiteralTextValue name = (AstLiteralTextValue) node.getValue("name");
            requireNonNull(name);

            List<MessageDecoder> valueDecoders = new ArrayList<MessageDecoder>();
            for (AstValueMatcher matcher : node.getMatchers()) {
                valueDecoders.add(matcher.accept(new GenerateReadDecoderVisitor(), state.configuration));
            }

            HttpParameterDecoder decoder = new HttpParameterDecoder(name.getValue(), valueDecoders);
            decoder.setRegionInfo(node.getRegionInfo());
            ReadConfigHandler handler = new ReadConfigHandler(decoder);

            handler.setRegionInfo(node.getRegionInfo());
            Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
            String handlerName = String.format("readConfig#%d (http parameter)", pipelineAsMap.size() + 1);
            pipelineAsMap.put(handlerName, handler);
            return state.configuration;
        }
        case "version": {
            AstValueMatcher version = node.getMatcher("version");

            MessageDecoder versionDecoder = version.accept(new GenerateReadDecoderVisitor(), state.configuration);

            ReadConfigHandler handler = new ReadConfigHandler(new HttpVersionDecoder(versionDecoder));

            handler.setRegionInfo(node.getRegionInfo());
            Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
            String handlerName = String.format("readConfig#%d (http version)", pipelineAsMap.size() + 1);
            pipelineAsMap.put(handlerName, handler);
            return state.configuration;
        }
        case "status": {
            AstValueMatcher code = node.getMatcher("code");
            AstValueMatcher reason = node.getMatcher("reason");

            MessageDecoder codeDecoder = code.accept(new GenerateReadDecoderVisitor(), state.configuration);
            MessageDecoder reasonDecoder = reason.accept(new GenerateReadDecoderVisitor(), state.configuration);

            ReadConfigHandler handler = new ReadConfigHandler(new HttpStatusDecoder(codeDecoder, reasonDecoder));

            handler.setRegionInfo(node.getRegionInfo());
            Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
            String handlerName = String.format("readConfig#%d (http status)", pipelineAsMap.size() + 1);
            pipelineAsMap.put(handlerName, handler);
            return state.configuration;
        }
        default:
            throw new IllegalStateException("Unrecognized configuration type: " + node.getType());
        }
    }

    @Override
    public Configuration visit(AstWriteConfigNode node, State state) throws Exception {
        switch (node.getType()) {
        case "header": {
            AstValue name = node.getName("name");
            MessageEncoder nameEncoder = name.accept(new GenerateWriteEncoderVisitor(), state.configuration);

            List<MessageEncoder> valueEncoders = new ArrayList<MessageEncoder>();
            for (AstValue value : node.getValues()) {
                valueEncoders.add(value.accept(new GenerateWriteEncoderVisitor(), state.configuration));
            }

            WriteConfigHandler handler = new WriteConfigHandler(new HttpHeaderEncoder(nameEncoder, valueEncoders));

            handler.setRegionInfo(node.getRegionInfo());
            String handlerName = String.format("writeConfig#%d (http header)", state.pipelineAsMap.size() + 1);
            state.pipelineAsMap.put(handlerName, handler);
            return state.configuration;
        }
        case "content-length": {
            WriteConfigHandler handler = new WriteConfigHandler(new HttpContentLengthEncoder());
            handler.setRegionInfo(node.getRegionInfo());
            String handlerName = String.format("writeConfig#%d (http content length)", state.pipelineAsMap.size() + 1);
            state.pipelineAsMap.put(handlerName, handler);
            return null;
        }
        case "method": {
            AstValue methodName = node.getValue();
            requireNonNull(methodName);

            MessageEncoder methodEncoder = methodName.accept(new GenerateWriteEncoderVisitor(), state.configuration);

            WriteConfigHandler handler = new WriteConfigHandler(new HttpMethodEncoder(methodEncoder));
            handler.setRegionInfo(node.getRegionInfo());
            String handlerName = String.format("writeConfig#%d (http method)", state.pipelineAsMap.size() + 1);
            state.pipelineAsMap.put(handlerName, handler);
            return state.configuration;
        }
        case "parameter": {
            AstValue name = node.getName("name");
            MessageEncoder nameEncoder = name.accept(new GenerateWriteEncoderVisitor(), state.configuration);

            List<MessageEncoder> valueEncoders = new ArrayList<MessageEncoder>();
            for (AstValue value : node.getValues()) {
                valueEncoders.add(value.accept(new GenerateWriteEncoderVisitor(), state.configuration));
            }

            WriteConfigHandler handler = new WriteConfigHandler(new HttpParameterEncoder(nameEncoder, valueEncoders));

            handler.setRegionInfo(node.getRegionInfo());
            String handlerName = String.format("writeConfig#%d (http parameter)", state.pipelineAsMap.size() + 1);
            state.pipelineAsMap.put(handlerName, handler);
            return state.configuration;
        }
        case "version": {
            AstValue version = node.getValue();

            MessageEncoder versionEncoder = version.accept(new GenerateWriteEncoderVisitor(), state.configuration);

            WriteConfigHandler handler = new WriteConfigHandler(new HttpVersionEncoder(versionEncoder));

            handler.setRegionInfo(node.getRegionInfo());
            String handlerName = String.format("writeConfig#%d (http version)", state.pipelineAsMap.size() + 1);
            state.pipelineAsMap.put(handlerName, handler);
            return state.configuration;
        }
        case "status": {
            AstValue code = node.getValue("code");
            AstValue reason = node.getValue("reason");

            MessageEncoder codeEncoder = code.accept(new GenerateWriteEncoderVisitor(), state.configuration);
            MessageEncoder reasonEncoder = reason.accept(new GenerateWriteEncoderVisitor(), state.configuration);

            WriteConfigHandler handler = new WriteConfigHandler(new HttpStatusEncoder(codeEncoder, reasonEncoder));

            handler.setRegionInfo(node.getRegionInfo());
            String handlerName = String.format("writeConfig#%d (http status)", state.pipelineAsMap.size() + 1);
            state.pipelineAsMap.put(handlerName, handler);
            return state.configuration;
        }
        default:
            throw new IllegalStateException("Unrecognized configuration type: " + node.getType());
        }
    }

    @Override
    public Configuration visit(AstReadClosedNode node, State state) throws Exception {
        InputShutdownHandler handler = new InputShutdownHandler();

        handler.setRegionInfo(node.getRegionInfo());
        String handlerName = String.format("readClosed#%d", state.pipelineAsMap.size() + 1);
        state.pipelineAsMap.put(handlerName, handler);
        return state.configuration;
    }

    @Override
    public Configuration visit(AstWriteCloseNode node, State state) throws Exception {
        ShutdownOutputHandler handler = new ShutdownOutputHandler();

        handler.setRegionInfo(node.getRegionInfo());
        String handlerName = String.format("writeClose#%d", state.pipelineAsMap.size() + 1);
        state.pipelineAsMap.put(handlerName, handler);
        return state.configuration;
    }

    @Override
    public Configuration visit(AstFlushNode node, State state) throws Exception {
        FlushHandler handler = new FlushHandler();

        handler.setRegionInfo(node.getRegionInfo());
        String handlerName = String.format("flush#%d", state.pipelineAsMap.size() + 1);
        state.pipelineAsMap.put(handlerName, handler);
        return state.configuration;
    }

    @Override
    public Configuration visit(AstReadOptionNode node, State state)
            throws Exception {

        String optionName = node.getOptionName();
        AstValue optionValue = node.getOptionValue();

        assert "mask".equals(optionName);
        state.readUnmasker = optionValue.accept(new GenerateReadMaskOptionValueVisitor(), state);

        return state.configuration;
    }

    private static final class GenerateReadMaskOptionValueVisitor implements AstValue.Visitor<MaskingDecoder, State> {

        @Override
        public MaskingDecoder visit(AstExpressionValue value, State state) throws Exception {

            ValueExpression expression = value.getValue();
            ExpressionContext environment = state.configuration.getExpressionContext();

            return MaskingDecoders.newMaskingDecoder(expression, environment);
        }

        @Override
        public MaskingDecoder visit(AstLiteralTextValue value, State state) throws Exception {

            String literalText = value.getValue();
            byte[] literalTextAsBytes = literalText.getBytes(UTF_8);

            for (int i = 0; i < literalTextAsBytes.length; i++) {
                if (literalTextAsBytes[i] != 0x00) {
                    return MaskingDecoders.newMaskingDecoder(literalTextAsBytes);
                }
            }

            // no need to unmask for all-zeros masking key
            return GenerateConfigurationVisitor.DEFAULT_READ_UNMASKER;
        }

        @Override
        public MaskingDecoder visit(AstLiteralBytesValue value, State state) throws Exception {

            byte[] literalBytes = value.getValue();

            for (int i = 0; i < literalBytes.length; i++) {
                if (literalBytes[i] != 0x00) {
                    return MaskingDecoders.newMaskingDecoder(literalBytes);
                }
            }

            // no need to unmask for all-zeros masking key
            return GenerateConfigurationVisitor.DEFAULT_READ_UNMASKER;
        }

    }


    @Override
    public Configuration visit(AstWriteOptionNode node, State parameter) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

}
