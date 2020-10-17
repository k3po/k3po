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
package org.kaazing.k3po.driver.internal.behavior.visitor;

import static java.lang.String.format;
import static org.jboss.netty.channel.Channels.pipeline;
import static org.jboss.netty.util.CharsetUtil.UTF_8;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.el.ValueExpression;

import org.agrona.LangUtil;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.k3po.driver.internal.RobotException;
import org.kaazing.k3po.driver.internal.behavior.Barrier;
import org.kaazing.k3po.driver.internal.behavior.BehaviorSystem;
import org.kaazing.k3po.driver.internal.behavior.Configuration;
import org.kaazing.k3po.driver.internal.behavior.handler.CompletionHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.RejectedHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.barrier.AwaitBarrierDownstreamHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.barrier.AwaitBarrierUpstreamHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.barrier.NotifyBarrierHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.Masker;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.Maskers;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.MessageDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.MessageEncoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.ReadByteArrayBytesDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.ReadByteLengthBytesDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.ReadExactBytesDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.ReadExactTextDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.ReadExpressionDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.ReadIntLengthBytesDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.ReadLongLengthBytesDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.ReadNumberDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.ReadRegexDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.ReadShortLengthBytesDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.ReadVariableLengthBytesDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.WriteByteEncoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.WriteBytesEncoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.WriteExpressionEncoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.WriteIntegerEncoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.WriteLongEncoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.WriteShortEncoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.WriteTextEncoder;
import org.kaazing.k3po.driver.internal.behavior.handler.command.CloseHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.command.ConnectAbortHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.command.DisconnectHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.command.FlushHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.command.ReadAbortHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.command.ShutdownOutputHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.command.UnbindHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.command.WriteAbortHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.command.WriteHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.event.BoundHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.event.ChildClosedHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.event.ChildOpenedHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.event.ClosedHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.event.ConnectAbortedHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.event.ConnectedHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.event.DisconnectedHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.event.InputShutdownHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.event.OpenedHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.event.ReadAbortedHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.event.ReadHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.event.UnboundHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.event.WriteAbortedHandler;
import org.kaazing.k3po.driver.internal.behavior.visitor.GenerateConfigurationVisitor.State;
import org.kaazing.k3po.driver.internal.netty.bootstrap.BootstrapFactory;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddressFactory;
import org.kaazing.k3po.driver.internal.resolver.ClientBootstrapResolver;
import org.kaazing.k3po.driver.internal.resolver.OptionsResolver;
import org.kaazing.k3po.driver.internal.resolver.ServerBootstrapResolver;
import org.kaazing.k3po.lang.internal.RegionInfo;
import org.kaazing.k3po.lang.internal.ast.AstAcceptNode;
import org.kaazing.k3po.lang.internal.ast.AstAcceptableNode;
import org.kaazing.k3po.lang.internal.ast.AstAcceptedNode;
import org.kaazing.k3po.lang.internal.ast.AstBoundNode;
import org.kaazing.k3po.lang.internal.ast.AstChildClosedNode;
import org.kaazing.k3po.lang.internal.ast.AstChildOpenedNode;
import org.kaazing.k3po.lang.internal.ast.AstCloseNode;
import org.kaazing.k3po.lang.internal.ast.AstClosedNode;
import org.kaazing.k3po.lang.internal.ast.AstConnectAbortNode;
import org.kaazing.k3po.lang.internal.ast.AstConnectAbortedNode;
import org.kaazing.k3po.lang.internal.ast.AstConnectNode;
import org.kaazing.k3po.lang.internal.ast.AstConnectedNode;
import org.kaazing.k3po.lang.internal.ast.AstDisconnectNode;
import org.kaazing.k3po.lang.internal.ast.AstDisconnectedNode;
import org.kaazing.k3po.lang.internal.ast.AstNode;
import org.kaazing.k3po.lang.internal.ast.AstOpenedNode;
import org.kaazing.k3po.lang.internal.ast.AstPropertyNode;
import org.kaazing.k3po.lang.internal.ast.AstReadAbortNode;
import org.kaazing.k3po.lang.internal.ast.AstReadAbortedNode;
import org.kaazing.k3po.lang.internal.ast.AstReadAdviseNode;
import org.kaazing.k3po.lang.internal.ast.AstReadAdvisedNode;
import org.kaazing.k3po.lang.internal.ast.AstReadAwaitNode;
import org.kaazing.k3po.lang.internal.ast.AstReadClosedNode;
import org.kaazing.k3po.lang.internal.ast.AstReadConfigNode;
import org.kaazing.k3po.lang.internal.ast.AstReadNotifyNode;
import org.kaazing.k3po.lang.internal.ast.AstReadOptionNode;
import org.kaazing.k3po.lang.internal.ast.AstReadValueNode;
import org.kaazing.k3po.lang.internal.ast.AstRejectedNode;
import org.kaazing.k3po.lang.internal.ast.AstScriptNode;
import org.kaazing.k3po.lang.internal.ast.AstStreamNode;
import org.kaazing.k3po.lang.internal.ast.AstStreamableNode;
import org.kaazing.k3po.lang.internal.ast.AstUnbindNode;
import org.kaazing.k3po.lang.internal.ast.AstUnboundNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteAbortNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteAbortedNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteAdviseNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteAdvisedNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteAwaitNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteCloseNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteConfigNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteFlushNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteNotifyNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteOptionNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteValueNode;
import org.kaazing.k3po.lang.internal.ast.matcher.AstByteLengthBytesMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstExactBytesMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstExactTextMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstExpressionMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstFixedLengthBytesMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstIntLengthBytesMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstLongLengthBytesMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstNumberMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstRegexMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstShortLengthBytesMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstValueMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstVariableLengthBytesMatcher;
import org.kaazing.k3po.lang.internal.ast.value.AstExpressionValue;
import org.kaazing.k3po.lang.internal.ast.value.AstLiteralByteValue;
import org.kaazing.k3po.lang.internal.ast.value.AstLiteralBytesValue;
import org.kaazing.k3po.lang.internal.ast.value.AstLiteralIntegerValue;
import org.kaazing.k3po.lang.internal.ast.value.AstLiteralLongValue;
import org.kaazing.k3po.lang.internal.ast.value.AstLiteralShortValue;
import org.kaazing.k3po.lang.internal.ast.value.AstLiteralTextValue;
import org.kaazing.k3po.lang.internal.ast.value.AstLiteralURIValue;
import org.kaazing.k3po.lang.internal.ast.value.AstValue;
import org.kaazing.k3po.lang.internal.el.ExpressionContext;
import org.kaazing.k3po.lang.internal.parser.types.DefaultTypeSystem;
import org.kaazing.k3po.lang.types.TypeInfo;

/**
 * Builds the pipeline of handlers that are used to "execute" the Robot script.
 */
public class GenerateConfigurationVisitor implements AstNode.Visitor<Configuration, State> {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(GenerateConfigurationVisitor.class);

    private final ChannelAddressFactory addressFactory;
    private final BootstrapFactory bootstrapFactory;

    public static final class State {
        private final ConcurrentMap<String, Barrier> barriersByName;
        private Configuration configuration;

        // the read / write maskers are reset per stream
        private Masker readUnmasker;
        private Masker writeMasker;

        /* The pipelineAsMap is built by each node that is visited. */
        private Map<String, ChannelHandler> pipelineAsMap;

        public State(ConcurrentMap<String, Barrier> barriersByName) {
            this.barriersByName = barriersByName;
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
            private Map<URI, List<ChannelPipeline>> pipelines = new HashMap<>();

            public List<ChannelPipeline> getPipeline(URI acceptURI) {
                List<ChannelPipeline> pipeline = pipelines.get(acceptURI);
                if (pipeline == null) {
                    pipeline = new ArrayList<>();
                    pipelines.put(acceptURI, pipeline);
                }
                return pipeline;
            }
        }

        public Map<String, Barrier> getBarriersByName() {
            return barriersByName;
        }

    }

    public GenerateConfigurationVisitor(BootstrapFactory bootstrapFactory, ChannelAddressFactory addressFactory) {
        this.bootstrapFactory = bootstrapFactory;
        this.addressFactory = addressFactory;
    }

    @Override
    public Configuration visit(AstScriptNode script, State state) {

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
    public Configuration visit(AstPropertyNode propertyNode, State state) {

        String propertyName = propertyNode.getPropertyName();
        Object value = propertyNode.resolve();

        if (value instanceof AutoCloseable) {
            state.configuration.getResources().add((AutoCloseable) value);
        }

        if (LOGGER.isDebugEnabled()) {
            Object formatValue = (value instanceof byte[]) ? AstLiteralBytesValue.toString((byte[]) value) : value;
            LOGGER.debug(format("Setting value for ${%s} to %s", propertyName, formatValue));
        }

        return state.configuration;
    }

    @Override
    public Configuration visit(AstAcceptedNode acceptedNode, State state) {

        // masking is a no-op by default for each stream
        state.readUnmasker = Masker.IDENTITY_MASKER;
        state.writeMasker = Masker.IDENTITY_MASKER;

        state.pipelineAsMap = new LinkedHashMap<>();

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
    public Configuration visit(AstRejectedNode rejectedNode, State state) {

        // masking is a no-op by default for each stream
        state.readUnmasker = Masker.IDENTITY_MASKER;
        state.writeMasker = Masker.IDENTITY_MASKER;

        state.pipelineAsMap = new LinkedHashMap<>();

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String rejectedName = String.format("rejected#%d", pipelineAsMap.size() + 1);
        RejectedHandler rejected = new RejectedHandler();
        rejected.setRegionInfo(rejectedNode.getRegionInfo());
        pipelineAsMap.put(rejectedName, rejected);

        for (AstStreamableNode streamable : rejectedNode.getStreamables()) {
            streamable.accept(this, state);
        }

        String completionName = String.format("completion#%d", pipelineAsMap.size() + 1);
        CompletionHandler completion = new CompletionHandler();
        completion.setRegionInfo(rejectedNode.getRegionInfo());
        pipelineAsMap.put(completionName, completion);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstAcceptNode acceptNode, State state) {

        Map<String, ChannelHandler> savedPipelineAsMap = state.pipelineAsMap;

        // masking is a no-op by default for each stream
        state.readUnmasker = Masker.IDENTITY_MASKER;
        state.writeMasker = Masker.IDENTITY_MASKER;

        /* Create a list of pipelines, for each acceptable */
        final List<ChannelPipeline> pipelines = new ArrayList<>();
        state.pipelineAsMap = new LinkedHashMap<>();

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

        Map<String, Object> acceptOptions = new HashMap<>();
        acceptOptions.put("regionInfo", acceptInfo);
        acceptOptions.putAll(acceptNode.getOptions());
        OptionsResolver optionsResolver = new OptionsResolver(acceptOptions);

        String notifyName = acceptNode.getNotifyName();
        Barrier notifyBarrier = null;
        if (notifyName != null) {
            notifyBarrier = state.lookupBarrier(notifyName);
        }

        // Now that accept supports expression value, accept uri may not be available at this point.
        // To defer the evaluation of accept uri and initialization of  ServerBootstrap, LocationResolver and
        // ServerResolver are created with information necessary to create ClientBootstrap when the
        // accept uri is available.
        Supplier<URI> locationResolver = acceptNode.getLocation()::getValue;
        ServerBootstrapResolver serverResolver = new ServerBootstrapResolver(bootstrapFactory, addressFactory,
                pipelines, locationResolver, optionsResolver, notifyBarrier);

        state.configuration.getServerResolvers().add(serverResolver);

        return state.configuration;
    }

    /**
     * Creates the pipeline, visits all streamable nodes and the creates the ClientBootstrap with the pipeline and
     * remote address,
     */
    @Override
    public Configuration visit(AstConnectNode connectNode, State state) {

        // masking is a no-op by default for each stream
        state.readUnmasker = Masker.IDENTITY_MASKER;
        state.writeMasker = Masker.IDENTITY_MASKER;

        state.pipelineAsMap = new LinkedHashMap<>();

        for (AstStreamableNode streamable : connectNode.getStreamables()) {
            streamable.accept(this, state);
        }

        /* Add the completion handler */
        String handlerName = String.format("completion#%d", state.pipelineAsMap.size() + 1);
        CompletionHandler completionHandler = new CompletionHandler();
        completionHandler.setRegionInfo(connectNode.getRegionInfo());
        state.pipelineAsMap.put(handlerName, completionHandler);

        String awaitName = connectNode.getAwaitName();
        Barrier awaitBarrier = null;
        if (awaitName != null) {
            awaitBarrier = state.lookupBarrier(awaitName);
        }

        final ChannelPipeline pipeline = pipelineFromMap(state.pipelineAsMap);

        /*
         * TODO. This is weird. I will only have one pipeline per connect. But if I don't set a factory When a connect
         * occurs it will create a shallow copy of the pipeline I set. This doesn't work due to the beforeAdd methods in
         * ExecutionHandler. Namely when the pipeline is cloned it uses the same handler objects so the handler future
         * is not null and we fail with an assertion error.
         */
        ChannelPipelineFactory pipelineFactory = new ChannelPipelineFactory() {
            private int numCalled;

            @Override
            public ChannelPipeline getPipeline() {
                if (numCalled++ != 0) {
                    throw new RobotException("getPipeline called more than once");
                }
                return pipeline;
            }
        };

        // Now that connect supports barrier and expression value, connect uri may not be available at this point.
        // To defer the evaluation of connect uri and initialization of ClientBootstrap, LocationResolver and
        // ClientResolver are created with information necessary to create ClientBootstrap when the connect uri
        // is available.
        Supplier<URI> locationResolver = connectNode.getLocation()::getValue;
        OptionsResolver optionsResolver = new OptionsResolver(connectNode.getOptions());

        ClientBootstrapResolver clientResolver = new ClientBootstrapResolver(bootstrapFactory, addressFactory,
                pipelineFactory, locationResolver, optionsResolver, awaitBarrier);

        // retain pipelines for tear down
        state.configuration.getClientAndServerPipelines().add(pipeline);

        state.configuration.getClientResolvers().add(clientResolver);
        return state.configuration;
    }

    @Override
    public Configuration visit(AstReadAwaitNode node, State state) {

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
    public Configuration visit(AstWriteAwaitNode node, State state) {

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
    public Configuration visit(AstReadNotifyNode node, State state) {

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
    public Configuration visit(AstWriteNotifyNode node, State state) {

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
    public Configuration visit(AstWriteValueNode node, State state) {
        List<MessageEncoder> messageEncoders = new ArrayList<>();

        for (AstValue<?> val : node.getValues()) {
            messageEncoders.add(val.accept(new GenerateWriteEncoderVisitor(), null));
        }
        WriteHandler handler = new WriteHandler(messageEncoders, state.writeMasker);
        handler.setRegionInfo(node.getRegionInfo());
        String handlerName = String.format("write#%d", state.pipelineAsMap.size() + 1);
        state.pipelineAsMap.put(handlerName, handler);
        return state.configuration;
    }

    private static final class GenerateWriteEncoderVisitor implements AstValue.Visitor<MessageEncoder, Void> {

        @Override
        public MessageEncoder visit(AstExpressionValue<?> value, Void parameter) {
            return new WriteExpressionEncoder(value::getValue, value.getExpression());
        }

        @Override
        public MessageEncoder visit(AstLiteralTextValue value, Void parameter) {
            return new WriteTextEncoder(value.getValue(), UTF_8);
        }

        @Override
        public MessageEncoder visit(AstLiteralBytesValue value, Void parameter) {
            return new WriteBytesEncoder(value.getValue());
        }

        @Override
        public MessageEncoder visit(AstLiteralByteValue value, Void parameter) {
            return new WriteByteEncoder(value.getValue());
        }

        @Override
        public MessageEncoder visit(AstLiteralShortValue value, Void parameter) {
            return new WriteShortEncoder(value.getValue());
        }

        @Override
        public MessageEncoder visit(AstLiteralIntegerValue value, Void parameter) {
            return new WriteIntegerEncoder(value.getValue());
        }

        @Override
        public MessageEncoder visit(AstLiteralLongValue value, Void parameter) {
            return new WriteLongEncoder(value.getValue());
        }

        @Override
        public MessageEncoder visit(AstLiteralURIValue value, Void parameter) {
            return new WriteTextEncoder(value.getValue().toString(), UTF_8);
        }
    }

    @Override
    public Configuration visit(AstDisconnectNode node, State state) {

        RegionInfo regionInfo = node.getRegionInfo();

        DisconnectHandler handler = new DisconnectHandler();
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("disconnect#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstUnbindNode node, State state) {

        RegionInfo regionInfo = node.getRegionInfo();

        UnbindHandler handler = new UnbindHandler();
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("unbind#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstCloseNode node, State state) {

        RegionInfo regionInfo = node.getRegionInfo();

        CloseHandler handler = new CloseHandler();
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("close#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstWriteAbortNode node, State state) {

        RegionInfo regionInfo = node.getRegionInfo();

        WriteAbortHandler handler = new WriteAbortHandler();
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("write abort#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstReadAbortNode node, State state) {

        RegionInfo regionInfo = node.getRegionInfo();

        ReadAbortHandler handler = new ReadAbortHandler();
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("read abort#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstChildOpenedNode node, State state) {

        RegionInfo regionInfo = node.getRegionInfo();

        ChildOpenedHandler handler = new ChildOpenedHandler();
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("childOpened#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstChildClosedNode node, State state) {

        RegionInfo regionInfo = node.getRegionInfo();

        ChildClosedHandler handler = new ChildClosedHandler();
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("childClosed#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstOpenedNode node, State state) {

        RegionInfo regionInfo = node.getRegionInfo();

        OpenedHandler handler = new OpenedHandler();
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("opened#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstBoundNode node, State state) {

        RegionInfo regionInfo = node.getRegionInfo();

        BoundHandler handler = new BoundHandler();
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("bound#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstConnectedNode node, State state) {

        RegionInfo regionInfo = node.getRegionInfo();

        ConnectedHandler handler = new ConnectedHandler();
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("connected#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstConnectAbortNode node, State state) {

        RegionInfo regionInfo = node.getRegionInfo();

        ConnectAbortHandler handler = new ConnectAbortHandler();
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("connect abort#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstConnectAbortedNode node, State state) {

        RegionInfo regionInfo = node.getRegionInfo();

        ConnectAbortedHandler handler = new ConnectAbortedHandler();
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("connect aborted#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstReadValueNode node, State state) {

        List<MessageDecoder> messageDecoders = new ArrayList<>();

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

    private static final class GenerateReadDecoderVisitor implements AstValueMatcher.Visitor<MessageDecoder, Configuration> {

        @Override
        public MessageDecoder visit(AstExpressionMatcher matcher, Configuration config) {
            ValueExpression expression = matcher.getValue();
            ExpressionContext environment = matcher.getEnvironment();
            return new ReadExpressionDecoder(matcher.getRegionInfo(), expression, environment);
        }

        @Override
        public MessageDecoder visit(AstFixedLengthBytesMatcher matcher, Configuration config) {

            int length = matcher.getLength();
            String captureName = matcher.getCaptureName();
            ExpressionContext environment = matcher.getEnvironment();
            MessageDecoder decoder =
                    (captureName != null) ? new ReadByteArrayBytesDecoder(matcher.getRegionInfo(), length, environment,
                            captureName) : new ReadByteArrayBytesDecoder(matcher.getRegionInfo(), length);
            return decoder;
        }

        @Override
        public MessageDecoder visit(AstByteLengthBytesMatcher matcher, Configuration config) {

            // String captureName = matcher.getCaptureName();
            // ExpressionContext environment = state.configuration.getExpressionContext();
            // state.readDecoders.add(new ReadByteLengthBytesDecoder(environment, captureName));
            // return null;
            return fixedLengthVisit(matcher, config, ReadByteLengthBytesDecoder.class);
        }

        @Override
        public MessageDecoder visit(AstShortLengthBytesMatcher matcher, Configuration config) {

            // String captureName = matcher.getCaptureName();
            // ExpressionContext environment = state.configuration.getExpressionContext();
            // state.readDecoders.add(new ReadShortLengthBytesDecoder(environment, captureName));
            // return null;
            return fixedLengthVisit(matcher, config, ReadShortLengthBytesDecoder.class);
        }

        @Override
        public MessageDecoder visit(AstIntLengthBytesMatcher matcher, Configuration config) {

            // String captureName = matcher.getCaptureName();
            // ExpressionContext environment = state.configuration.getExpressionContext();
            // state.readDecoders.add(new ReadIntLengthBytesDecoder(environment, captureName));
            // return null;
            return fixedLengthVisit(matcher, config, ReadIntLengthBytesDecoder.class);
        }

        @Override
        public MessageDecoder visit(AstLongLengthBytesMatcher matcher, Configuration config) {

            // String captureName = matcher.getCaptureName();
            // ExpressionContext environment = state.configuration.getExpressionContext();
            // state.readDecoders.add(new ReadLongLengthBytesDecoder(environment, captureName));
            // return null;
            return fixedLengthVisit(matcher, config, ReadLongLengthBytesDecoder.class);
        }

        private MessageDecoder fixedLengthVisit(AstFixedLengthBytesMatcher matcher, Configuration config, Class<?> clazz) {

            MessageDecoder decoder = null;
            try
            {
                String captureName = matcher.getCaptureName();
                RegionInfo regionInfo = matcher.getRegionInfo();

                ExpressionContext environment = matcher.getEnvironment();
                @SuppressWarnings("unchecked") Constructor<MessageDecoder> constructor =
                        (Constructor<MessageDecoder>) clazz.getConstructor(RegionInfo.class, ExpressionContext.class, String.class);
                decoder = constructor.newInstance(regionInfo, environment, captureName);
            }
            catch (NoSuchMethodException
                    | SecurityException
                    | InstantiationException
                    | IllegalAccessException
                    | IllegalArgumentException
                    | InvocationTargetException ex)
            {
                LangUtil.rethrowUnchecked(ex);
            }

            return decoder;
        }

        @Override
        public MessageDecoder visit(AstRegexMatcher matcher, Configuration config) {
            ExpressionContext environment = matcher.getEnvironment();
            MessageDecoder result;
            result = new ReadRegexDecoder(matcher.getRegionInfo(), matcher.getValue(), UTF_8, environment);
            return result;
        }

        @Override
        public MessageDecoder visit(AstExactTextMatcher matcher, Configuration config) {
            return new ReadExactTextDecoder(matcher.getRegionInfo(), matcher.getValue(), UTF_8);
        }

        @Override
        public MessageDecoder visit(AstExactBytesMatcher matcher, Configuration config) {
            return new ReadExactBytesDecoder(matcher.getRegionInfo(), matcher.getValue());
        }

        @Override
        public MessageDecoder visit(AstNumberMatcher matcher, Configuration config) {
            return new ReadNumberDecoder(matcher.getRegionInfo(), matcher.getValue());
        }

        @Override
        public MessageDecoder visit(AstVariableLengthBytesMatcher matcher, Configuration config) {

            ValueExpression length = matcher.getLength();
            String captureName = matcher.getCaptureName();
            ExpressionContext environment = matcher.getEnvironment();
            MessageDecoder decoder =
                    (captureName != null) ? new ReadVariableLengthBytesDecoder(matcher.getRegionInfo(), length, environment,
                            captureName) : new ReadVariableLengthBytesDecoder(matcher.getRegionInfo(), length, environment);
            return decoder;
        }

    }

    @Override
    public Configuration visit(AstDisconnectedNode node, State state) {

        RegionInfo regionInfo = node.getRegionInfo();

        DisconnectedHandler handler = new DisconnectedHandler();
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("disconnected#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstUnboundNode node, State state) {

        RegionInfo regionInfo = node.getRegionInfo();

        UnboundHandler handler = new UnboundHandler();
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("unbound#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstClosedNode node, State state) {

        RegionInfo regionInfo = node.getRegionInfo();

        ClosedHandler handler = new ClosedHandler();
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("closed#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstReadAbortedNode node, State state) {

        RegionInfo regionInfo = node.getRegionInfo();

        ReadAbortedHandler handler = new ReadAbortedHandler();
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("read aborted#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstWriteAbortedNode node, State state) {

        RegionInfo regionInfo = node.getRegionInfo();

        WriteAbortedHandler handler = new WriteAbortedHandler();
        handler.setRegionInfo(regionInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("write aborted#%d", pipelineAsMap.size() + 1);
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

    private final BehaviorSystem behaviorSystem = BehaviorSystem.newInstance();

    @Override
    public Configuration visit(AstReadConfigNode node, State state) {

        Function<AstValueMatcher, MessageDecoder> decoderFactory = m -> m.accept(new GenerateReadDecoderVisitor(), state.configuration);
        ChannelHandler handler = behaviorSystem.newReadConfigHandler(node, decoderFactory);

        if (handler != null) {
            Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
            String handlerName = String.format("readConfig#%d (%s)", pipelineAsMap.size() + 1, node.getType().getName());
            pipelineAsMap.put(handlerName, handler);
            return state.configuration;
        }
        else {
            throw new IllegalStateException("Unrecognized configuration type: " + node.getType());
        }
    }

    @Override
    public Configuration visit(AstWriteConfigNode node, State state) {

        Function<AstValue<?>, MessageEncoder> encoderFactory = v -> v.accept(new GenerateWriteEncoderVisitor(), null);
        ChannelHandler handler = behaviorSystem.newWriteConfigHandler(node, encoderFactory);

        if (handler != null) {
            Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
            String handlerName = String.format("writeConfig#%d (%s)", pipelineAsMap.size() + 1, node.getType().getName());
            pipelineAsMap.put(handlerName, handler);
            return state.configuration;
        }
        else {
            throw new IllegalStateException("Unrecognized configuration type: " + node.getType());
        }
    }

    @Override
    public Configuration visit(AstReadAdviseNode node, State state) {

        Function<AstValue<?>, MessageEncoder> encoderFactory = v -> v.accept(new GenerateWriteEncoderVisitor(), null);
        ChannelHandler handler = behaviorSystem.newReadAdviseHandler(node, encoderFactory);

        if (handler != null) {
            Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
            String handlerName = String.format("readAdvise#%d (%s)", pipelineAsMap.size() + 1, node.getType().getName());
            pipelineAsMap.put(handlerName, handler);
            return state.configuration;
        }
        else {
            throw new IllegalStateException("Unrecognized advisory type: " + node.getType());
        }
    }

    @Override
    public Configuration visit(AstWriteAdviseNode node, State state) {

        Function<AstValue<?>, MessageEncoder> encoderFactory = v -> v.accept(new GenerateWriteEncoderVisitor(), null);
        ChannelHandler handler = behaviorSystem.newWriteAdviseHandler(node, encoderFactory);

        if (handler != null) {
            Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
            String handlerName = String.format("writeAdvise#%d (%s)", pipelineAsMap.size() + 1, node.getType().getName());
            pipelineAsMap.put(handlerName, handler);
            return state.configuration;
        }
        else {
            throw new IllegalStateException("Unrecognized advisory type: " + node.getType());
        }
    }

    @Override
    public Configuration visit(AstReadAdvisedNode node, State state) {

        Function<AstValueMatcher, MessageDecoder> decoderFactory = m -> m.accept(new GenerateReadDecoderVisitor(), state.configuration);
        ChannelHandler handler = behaviorSystem.newReadAdvisedHandler(node, decoderFactory);

        if (handler != null) {
            Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
            String handlerName = String.format("readAdvised#%d (%s)", pipelineAsMap.size() + 1, node.getType().getName());
            pipelineAsMap.put(handlerName, handler);
            return state.configuration;
        }
        else {
            throw new IllegalStateException("Unrecognized advisory type: " + node.getType());
        }
    }

    @Override
    public Configuration visit(AstWriteAdvisedNode node, State state) {

        Function<AstValueMatcher, MessageDecoder> decoderFactory = m -> m.accept(new GenerateReadDecoderVisitor(), state.configuration);
        ChannelHandler handler = behaviorSystem.newWriteAdvisedHandler(node, decoderFactory);

        if (handler != null) {
            Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
            String handlerName = String.format("writeAdvised#%d (%s)", pipelineAsMap.size() + 1, node.getType().getName());
            pipelineAsMap.put(handlerName, handler);
            return state.configuration;
        }
        else {
            throw new IllegalStateException("Unrecognized advisory type: " + node.getType());
        }
    }

    @Override
    public Configuration visit(AstReadClosedNode node, State state) {
        InputShutdownHandler handler = new InputShutdownHandler();

        handler.setRegionInfo(node.getRegionInfo());
        String handlerName = String.format("readClosed#%d", state.pipelineAsMap.size() + 1);
        state.pipelineAsMap.put(handlerName, handler);
        return state.configuration;
    }

    @Override
    public Configuration visit(AstWriteCloseNode node, State state) {
        ShutdownOutputHandler handler = new ShutdownOutputHandler();

        handler.setRegionInfo(node.getRegionInfo());
        String handlerName = String.format("writeClose#%d", state.pipelineAsMap.size() + 1);
        state.pipelineAsMap.put(handlerName, handler);
        return state.configuration;
    }

    @Override
    public Configuration visit(AstWriteFlushNode node, State state) {
        FlushHandler handler = new FlushHandler();

        handler.setRegionInfo(node.getRegionInfo());
        String handlerName = String.format("flush#%d", state.pipelineAsMap.size() + 1);
        state.pipelineAsMap.put(handlerName, handler);
        return state.configuration;
    }

    @Override
    public Configuration visit(AstReadOptionNode node, State state) {

        TypeInfo<?> optionType = node.getOptionType();
        if (optionType == DefaultTypeSystem.OPTION_MASK) {
            AstValue<?> maskValue = node.getOptionValue();
            state.readUnmasker = maskValue.accept(new GenerateMaskOptionValueVisitor(), state);
        }
        else {
            ChannelHandler handler = behaviorSystem.newReadOptionHandler(node);
            String optionName = node.getOptionName();
            if (handler != null) {
                String handlerName = String.format("readOption#%d (%s)", state.pipelineAsMap.size() + 1, optionName);
                state.pipelineAsMap.put(handlerName, handler);
            }
            else {
                throw new IllegalArgumentException("Unrecognized read option : " + optionName);
            }
        }

        return state.configuration;
    }

    @Override
    public Configuration visit(AstWriteOptionNode node, State state) {

        TypeInfo<?> optionType = node.getOptionType();
        if (optionType == DefaultTypeSystem.OPTION_MASK) {
            AstValue<?> maskValue = node.getOptionValue();
            state.writeMasker = maskValue.accept(new GenerateMaskOptionValueVisitor(), state);
        }
        else {
            ChannelHandler handler = behaviorSystem.newWriteOptionHandler(node);
            String optionName = node.getOptionName();
            if (handler != null) {
                String handlerName = String.format("writeOption#%d (%s)", state.pipelineAsMap.size() + 1, optionName);
                state.pipelineAsMap.put(handlerName, handler);
            }
            else {
                throw new IllegalArgumentException("Unrecognized write option : " + optionName);
            }
        }

        return state.configuration;
    }

    private static final class GenerateMaskOptionValueVisitor implements AstValue.Visitor<Masker, State> {

        @Override
        public Masker visit(AstExpressionValue<?> value, State state) {

            Supplier<byte[]> supplier = () -> value.getValue(byte[].class);
            return Maskers.newMasker(supplier);
        }

        @Override
        public Masker visit(AstLiteralTextValue value, State state) {

            String literalText = value.getValue();
            byte[] literalTextAsBytes = literalText.getBytes(UTF_8);

            for (byte literalTextAsByte : literalTextAsBytes) {
                if (literalTextAsByte != 0x00) {
                    return Maskers.newMasker(literalTextAsBytes);
                }
            }

            // no need to unmask for all-zeros masking key
            return Masker.IDENTITY_MASKER;
        }

        @Override
        public Masker visit(AstLiteralBytesValue value, State state) {

            byte[] literalBytes = value.getValue();

            for (byte literalByte : literalBytes) {
                if (literalByte != 0x00) {
                    return Maskers.newMasker(literalBytes);
                }
            }

            // no need to unmask for all-zeros masking key
            return Masker.IDENTITY_MASKER;
        }

        @Override
        public Masker visit(AstLiteralByteValue literal, State state) {
            byte value = literal.getValue();
            if (value != 0) {
                byte[] array = ByteBuffer.allocate(Byte.BYTES)
                                         .put(value)
                                         .array();
                return Maskers.newMasker(array);
            }

            // no need to unmask for all-zeros masking key
            return Masker.IDENTITY_MASKER;
        }

        @Override
        public Masker visit(AstLiteralShortValue literal, State state) {
            short value = literal.getValue();
            if (value != 0) {
                byte[] array = ByteBuffer.allocate(Short.BYTES)
                                         .putShort(value)
                                         .array();
                return Maskers.newMasker(array);
            }

            // no need to unmask for all-zeros masking key
            return Masker.IDENTITY_MASKER;
        }

        @Override
        public Masker visit(AstLiteralIntegerValue literal, State state) {
            int value = literal.getValue();
            if (value != 0) {
                byte[] array = ByteBuffer.allocate(Integer.BYTES)
                                         .putInt(value)
                                         .array();
                return Maskers.newMasker(array);
            }

            // no need to unmask for all-zeros masking key
            return Masker.IDENTITY_MASKER;
        }

        @Override
        public Masker visit(AstLiteralLongValue literal, State state) {
            long value = literal.getValue();
            if (value != 0L) {
                byte[] array = ByteBuffer.allocate(Long.BYTES)
                                         .putLong(value)
                                         .array();
                return Maskers.newMasker(array);
            }

            // no need to unmask for all-zeros masking key
            return Masker.IDENTITY_MASKER;
        }

        @Override
        public Masker visit(AstLiteralURIValue value, State state) {

            URI literalURI = value.getValue();
            String literalText = literalURI.toString();
            byte[] literalTextAsBytes = literalText.getBytes(UTF_8);

            for (byte literalTextAsByte : literalTextAsBytes) {
                if (literalTextAsByte != 0x00) {
                    return Maskers.newMasker(literalTextAsBytes);
                }
            }

            // no need to unmask for all-zeros masking key
            return Masker.IDENTITY_MASKER;
        }

    }

}
