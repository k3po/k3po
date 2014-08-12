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

package org.kaazing.robot.driver.behavior.visitor;

import static org.jboss.netty.channel.Channels.pipeline;
import static org.jboss.netty.util.CharsetUtil.UTF_8;
import static org.kaazing.robot.driver.behavior.handler.codec.HttpUtils.HTTP_CODEC_NAME;
import static org.kaazing.robot.driver.behavior.handler.codec.HttpUtils.HTTP_MEESAGE_SPLITTING_CODEC_NAME;
import static org.kaazing.robot.driver.behavior.handler.codec.HttpUtils.HTTP_MESSAGE_AGGREGATING_CODEC_NAME;
import static org.kaazing.robot.driver.behavior.handler.codec.HttpUtils.isUriHttp;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.el.ValueExpression;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpServerCodec;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.robot.driver.netty.bootstrap.BootstrapFactory;
import org.kaazing.robot.driver.netty.bootstrap.ClientBootstrap;
import org.kaazing.robot.driver.netty.bootstrap.ServerBootstrap;
import org.kaazing.robot.driver.netty.channel.ChannelAddress;
import org.kaazing.robot.driver.netty.channel.ChannelAddressFactory;
import org.kaazing.robot.driver.RobotException;
import org.kaazing.robot.driver.behavior.Barrier;
import org.kaazing.robot.driver.behavior.Configuration;
import org.kaazing.robot.driver.behavior.handler.CompletionHandler;
import org.kaazing.robot.driver.behavior.handler.ExecutionHandler;
import org.kaazing.robot.driver.behavior.handler.FailureHandler;
import org.kaazing.robot.driver.behavior.handler.LogLastEventHandler;
import org.kaazing.robot.driver.behavior.handler.barrier.AwaitBarrierDownstreamHandler;
import org.kaazing.robot.driver.behavior.handler.barrier.AwaitBarrierUpstreamHandler;
import org.kaazing.robot.driver.behavior.handler.barrier.NotifyBarrierHandler;
import org.kaazing.robot.driver.behavior.handler.codec.HttpMessageAggregatingCodec;
import org.kaazing.robot.driver.behavior.handler.codec.HttpMessageSplittingCodec;
import org.kaazing.robot.driver.behavior.handler.codec.MaskingDecoder;
import org.kaazing.robot.driver.behavior.handler.codec.MaskingDecoders;
import org.kaazing.robot.driver.behavior.handler.codec.MessageDecoder;
import org.kaazing.robot.driver.behavior.handler.codec.MessageEncoder;
import org.kaazing.robot.driver.behavior.handler.codec.ReadByteArrayBytesDecoder;
import org.kaazing.robot.driver.behavior.handler.codec.ReadByteLengthBytesDecoder;
import org.kaazing.robot.driver.behavior.handler.codec.ReadExactBytesDecoder;
import org.kaazing.robot.driver.behavior.handler.codec.ReadExactTextDecoder;
import org.kaazing.robot.driver.behavior.handler.codec.ReadExpressionDecoder;
import org.kaazing.robot.driver.behavior.handler.codec.ReadIntLengthBytesDecoder;
import org.kaazing.robot.driver.behavior.handler.codec.ReadLongLengthBytesDecoder;
import org.kaazing.robot.driver.behavior.handler.codec.ReadRegexDecoder;
import org.kaazing.robot.driver.behavior.handler.codec.ReadShortLengthBytesDecoder;
import org.kaazing.robot.driver.behavior.handler.codec.ReadVariableLengthBytesDecoder;
import org.kaazing.robot.driver.behavior.handler.codec.WriteBytesEncoder;
import org.kaazing.robot.driver.behavior.handler.codec.WriteExpressionEncoder;
import org.kaazing.robot.driver.behavior.handler.codec.WriteTextEncoder;
import org.kaazing.robot.driver.behavior.handler.codec.http.HttpMessageContributingDecoder;
import org.kaazing.robot.driver.behavior.handler.codec.http.HttpMessageContributingEncoder;
import org.kaazing.robot.driver.behavior.handler.codec.http.ReadHttpHeaderDecoder;
import org.kaazing.robot.driver.behavior.handler.codec.http.ReadHttpMethodDecoder;
import org.kaazing.robot.driver.behavior.handler.codec.http.ReadHttpParameterDecoder;
import org.kaazing.robot.driver.behavior.handler.codec.http.ReadHttpStatusDecoder;
import org.kaazing.robot.driver.behavior.handler.codec.http.ReadHttpVersionDecoder;
import org.kaazing.robot.driver.behavior.handler.codec.http.WriteHttpHeaderEncoder;
import org.kaazing.robot.driver.behavior.handler.codec.http.WriteHttpMethodEncoder;
import org.kaazing.robot.driver.behavior.handler.codec.http.WriteHttpParameterEncoder;
import org.kaazing.robot.driver.behavior.handler.codec.http.WriteHttpStatusEncoder;
import org.kaazing.robot.driver.behavior.handler.codec.http.WriteHttpVersionEncoder;
import org.kaazing.robot.driver.behavior.handler.command.CloseHandler;
import org.kaazing.robot.driver.behavior.handler.command.DisconnectHandler;
import org.kaazing.robot.driver.behavior.handler.command.UnbindHandler;
import org.kaazing.robot.driver.behavior.handler.command.WriteHandler;
import org.kaazing.robot.driver.behavior.handler.command.http.CloseWriteHttpRequestHandler;
import org.kaazing.robot.driver.behavior.handler.command.http.CloseWriteHttpResponseHandler;
import org.kaazing.robot.driver.behavior.handler.command.http.EndOfWriteHttpHeadersHandler;
import org.kaazing.robot.driver.behavior.handler.command.http.WriteHttpContentLengthHandler;
import org.kaazing.robot.driver.behavior.handler.command.http.WriteHttpHandler;
import org.kaazing.robot.driver.behavior.handler.event.BoundHandler;
import org.kaazing.robot.driver.behavior.handler.event.ChildClosedHandler;
import org.kaazing.robot.driver.behavior.handler.event.ChildOpenedHandler;
import org.kaazing.robot.driver.behavior.handler.event.ClosedHandler;
import org.kaazing.robot.driver.behavior.handler.event.ConnectedHandler;
import org.kaazing.robot.driver.behavior.handler.event.DisconnectedHandler;
import org.kaazing.robot.driver.behavior.handler.event.OpenedHandler;
import org.kaazing.robot.driver.behavior.handler.event.ReadHandler;
import org.kaazing.robot.driver.behavior.handler.event.UnboundHandler;
import org.kaazing.robot.driver.behavior.handler.event.http.CloseReadHttpRequestHandler;
import org.kaazing.robot.driver.behavior.handler.event.http.CloseReadHttpResponseHandler;
import org.kaazing.robot.driver.behavior.handler.event.http.EndOfReadHttpHeadersHandler;
import org.kaazing.robot.driver.behavior.handler.event.http.ReadHttpHandler;
import org.kaazing.robot.driver.behavior.visitor.GenerateConfigurationVisitor.State;
import org.kaazing.robot.lang.LocationInfo;
import org.kaazing.robot.lang.ast.AstAcceptNode;
import org.kaazing.robot.lang.ast.AstAcceptableNode;
import org.kaazing.robot.lang.ast.AstBoundNode;
import org.kaazing.robot.lang.ast.AstChildClosedNode;
import org.kaazing.robot.lang.ast.AstChildOpenedNode;
import org.kaazing.robot.lang.ast.AstCloseHttpRequestNode;
import org.kaazing.robot.lang.ast.AstCloseHttpResponseNode;
import org.kaazing.robot.lang.ast.AstCloseNode;
import org.kaazing.robot.lang.ast.AstClosedNode;
import org.kaazing.robot.lang.ast.AstConnectNode;
import org.kaazing.robot.lang.ast.AstConnectedNode;
import org.kaazing.robot.lang.ast.AstDisconnectNode;
import org.kaazing.robot.lang.ast.AstDisconnectedNode;
import org.kaazing.robot.lang.ast.AstEndOfHttpHeadersNode;
import org.kaazing.robot.lang.ast.AstNode;
import org.kaazing.robot.lang.ast.AstOpenedNode;
import org.kaazing.robot.lang.ast.AstReadAwaitNode;
import org.kaazing.robot.lang.ast.AstReadHttpHeaderNode;
import org.kaazing.robot.lang.ast.AstReadHttpMethodNode;
import org.kaazing.robot.lang.ast.AstReadHttpParameterNode;
import org.kaazing.robot.lang.ast.AstReadHttpStatusNode;
import org.kaazing.robot.lang.ast.AstReadHttpVersionNode;
import org.kaazing.robot.lang.ast.AstReadNotifyNode;
import org.kaazing.robot.lang.ast.AstReadOptionNode;
import org.kaazing.robot.lang.ast.AstReadValueNode;
import org.kaazing.robot.lang.ast.AstScriptNode;
import org.kaazing.robot.lang.ast.AstStreamNode;
import org.kaazing.robot.lang.ast.AstStreamableNode;
import org.kaazing.robot.lang.ast.AstUnbindNode;
import org.kaazing.robot.lang.ast.AstUnboundNode;
import org.kaazing.robot.lang.ast.AstWriteAwaitNode;
import org.kaazing.robot.lang.ast.AstWriteHttpContentLengthNode;
import org.kaazing.robot.lang.ast.AstWriteHttpHeaderNode;
import org.kaazing.robot.lang.ast.AstWriteHttpMethodNode;
import org.kaazing.robot.lang.ast.AstWriteHttpParameterNode;
import org.kaazing.robot.lang.ast.AstWriteHttpStatusNode;
import org.kaazing.robot.lang.ast.AstWriteHttpVersionNode;
import org.kaazing.robot.lang.ast.AstWriteNotifyNode;
import org.kaazing.robot.lang.ast.AstWriteOptionNode;
import org.kaazing.robot.lang.ast.AstWriteValueNode;
import org.kaazing.robot.lang.ast.matcher.AstByteLengthBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstExactBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstExactTextMatcher;
import org.kaazing.robot.lang.ast.matcher.AstExpressionMatcher;
import org.kaazing.robot.lang.ast.matcher.AstFixedLengthBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstIntLengthBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstLongLengthBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstRegexMatcher;
import org.kaazing.robot.lang.ast.matcher.AstShortLengthBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstValueMatcher;
import org.kaazing.robot.lang.ast.matcher.AstVariableLengthBytesMatcher;
import org.kaazing.robot.lang.ast.value.AstExpressionValue;
import org.kaazing.robot.lang.ast.value.AstLiteralBytesValue;
import org.kaazing.robot.lang.ast.value.AstLiteralTextValue;
import org.kaazing.robot.lang.ast.value.AstValue;
import org.kaazing.robot.lang.el.ExpressionContext;
import org.kaazing.robot.driver.netty.bootstrap.SingletonBootstrapFactory;

/**
 * Builds the pipeline of handlers that are used to "execute" the Robot script.
 */
public class GenerateConfigurationVisitor implements AstNode.Visitor<Configuration, State> {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(GenerateConfigurationVisitor.class);

    private static final MaskingDecoder DEFAULT_READ_UNMASKER = new DefaultReadUnmasker();

    private final ChannelAddressFactory addressFactory = ChannelAddressFactory.newChannelAddressFactory();
    private final BootstrapFactory bootstrapFactory = SingletonBootstrapFactory.getInstance();

    public static final class State {
        private final ConcurrentMap<String, Barrier> barriersByName;
        private Configuration configuration;
        private LocationInfo streamStartLocation;
        private PROTOCOL protocol = PROTOCOL.TCP;

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
                Barrier newBarrier = new Barrier();
                barrier = barriersByName.putIfAbsent(barrierName, newBarrier);
                if (barrier == null) {
                    barrier = newBarrier;
                }
            }

            return barrier;
        }

        public PROTOCOL getProtocol() {
            return protocol;
        }

        public void setProtocol(PROTOCOL protocol) {
            this.protocol = protocol;
        }

        // Http State
        private HTTP_DIRECTION httpDirection;
        private HttpMessage httpMessage;
        private URI httpURI;

        public HttpMessage getHttpMessage() {
            return httpMessage;
        }

        public void setHttpMessage(HttpMessage httpMessage, URI httpURI) {
            if (httpMessage instanceof HttpRequest) {
                httpDirection = HTTP_DIRECTION.WRITE;
                String path = httpURI.getPath();
                if (path.length() == 0) {
                    path = "/";
                }
                ((HttpRequest) httpMessage).setUri(path);
            } else if (httpMessage instanceof HttpResponse) {
                httpDirection = HTTP_DIRECTION.READ;
            }
            this.httpMessage = httpMessage;
            this.setHttpURI(httpURI);
        }

        public HTTP_DIRECTION getHttpDirection() {
            return httpDirection;
        }

        public HTTP_DIRECTION getAndSwapDirection() {
            HTTP_DIRECTION currentDirection = httpDirection;
            switch (httpDirection) {
            case READ:
                httpDirection = HTTP_DIRECTION.WRITE;
                break;
            case WRITE:
                httpDirection = HTTP_DIRECTION.READ;
                break;
            }
            return currentDirection;
        }

        public URI getHttpURI() {
            return httpURI;
        }

        public void setHttpURI(URI httpURI) {
            this.httpURI = httpURI;
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

    public enum PROTOCOL {
        TCP, HTTP;
    }

    public enum HTTP_DIRECTION {
        READ, WRITE;
    }

    @Override
    public Configuration visit(AstScriptNode script, State state) throws Exception {

        state.configuration = new Configuration();

        for (AstStreamNode stream : script.getStreams()) {
            stream.accept(this, state);
        }

        return state.configuration;
    }

    @Override
    public Configuration visit(AstAcceptableNode acceptedNode, State state) throws Exception {

        // masking is a no-op by default for each stream
        state.readUnmasker = DEFAULT_READ_UNMASKER;

        switch (state.getProtocol()) {
        case HTTP:
             state.pipelineAsMap = new LinkedHashMap<String, ChannelHandler>();
             state.pipelineAsMap.put("loghandler", new LoggingHandler(true));
             state.pipelineAsMap.put("lastevent", new LogLastEventHandler(acceptedNode.getLocationInfo()));
             state.streamStartLocation = acceptedNode.getLocationInfo();
             HttpServerCodec httpServerCodec = new HttpServerCodec();
             HttpMessageAggregatingCodec httpMessageAggregatingCodec = new HttpMessageAggregatingCodec();
             HttpMessageSplittingCodec httpMessageSplittingCodec = new HttpMessageSplittingCodec();
             state.pipelineAsMap.put(HTTP_CODEC_NAME, httpServerCodec);
             state.pipelineAsMap.put(HTTP_MESSAGE_AGGREGATING_CODEC_NAME, httpMessageAggregatingCodec);
             state.pipelineAsMap.put(HTTP_MEESAGE_SPLITTING_CODEC_NAME, httpMessageSplittingCodec);
            break;
        case TCP:
            state.pipelineAsMap = new LinkedHashMap<String, ChannelHandler>();
            state.pipelineAsMap.put("loghandler", new LoggingHandler(true));
            state.pipelineAsMap.put("lastevent", new LogLastEventHandler(acceptedNode.getLocationInfo()));
            state.streamStartLocation = acceptedNode.getLocationInfo();
            break;
        }

        for (AstStreamableNode streamable : acceptedNode.getStreamables()) {
            streamable.accept(this, state);
        }

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("completion#%d", pipelineAsMap.size() + 1);

        CompletionHandler c = new CompletionHandler();
        state.configuration.getCompletionHandlers().add(c);
        pipelineAsMap.put(handlerName, c);

        return null;
    }

    @Override
    public Configuration visit(AstAcceptNode acceptNode, State state) throws Exception {

        Map<String, ChannelHandler> savedPipelineAsMap = state.pipelineAsMap;

        // masking is a no-op by default for each stream
        state.readUnmasker = DEFAULT_READ_UNMASKER;

        /*
         * Collection of Completion Futures. There should be one for each acceptable. We do this here instead of the
         * visit because the handlerFuture is not created until the pipeline is created here.
         */
        Collection<ChannelFuture> completionFutures = new HashSet<ChannelFuture>();

        URI acceptURI = acceptNode.getLocation();
        URI tcpURI = acceptURI;
        if (isUriHttp(acceptURI)) {
            state.setProtocol(PROTOCOL.HTTP);
            HttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            state.setHttpMessage(httpResponse, acceptURI);
            tcpURI = tcpBindURIFromHttpURI(acceptURI);
        }

        /* Create a list of pipelines, for each acceptable */
        final List<ChannelPipeline> pipelines = new ArrayList<ChannelPipeline>();

//        switch (state.getProtocol()) {
//        case HTTP:
//            state.pipelineAsMap = new LinkedHashMap<String, ChannelHandler>();
//            state.pipelineAsMap.put("loghandler", new LoggingHandler(true));
//            state.pipelineAsMap.put("lastevent", new LogLastEventHandler(acceptNode.getLocationInfo()));
//            state.streamStartLocation = acceptNode.getLocationInfo();
//            HttpServerCodec httpServerCodec = new HttpServerCodec();
//            HttpMessageAggregatingCodec httpMessageAggregatingCodec = new HttpMessageAggregatingCodec();
//            HttpMessageSplittingCodec httpMessageSplittingCodec = new HttpMessageSplittingCodec();
//            state.pipelineAsMap.put(HTTP_CODEC_NAME, httpServerCodec);
//            state.pipelineAsMap.put(HTTP_MESSAGE_AGGREGATING_CODEC_NAME, httpMessageAggregatingCodec);
//            state.pipelineAsMap.put(HTTP_MEESAGE_SPLITTING_CODEC_NAME, httpMessageSplittingCodec);

        for (AstAcceptableNode acceptableNode : acceptNode.getAcceptables()) {
            switch (state.getProtocol()) {
            case HTTP:
                state.pipelineAsMap = new LinkedHashMap<String, ChannelHandler>();
                break;
            case TCP:
                state.pipelineAsMap = new LinkedHashMap<String, ChannelHandler>();
                break;
            }

            acceptableNode.accept(this, state);

            ChannelPipeline pipeline = pipelineFromMap(state.pipelineAsMap, state);
            ChannelHandlerContext context = pipeline.getContext(CompletionHandler.class);

            /* Each pipeline must have a completion handler */
            assert context != null;

            completionFutures.add(((CompletionHandler) context.getHandler()).getHandlerFuture());

            pipelines.add(pipeline);
        }
        state.pipelineAsMap = savedPipelineAsMap;
        acceptURI = tcpURI;

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
        serverBootstrap.setOption("expectedChildCount", pipelines.size());

        /* Remember the set of complete handlers for this accept node */
        serverBootstrap.setOption("completionFutures", completionFutures);

        serverBootstrap.setOption("locationInfo", acceptNode.getLocationInfo());

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

        boolean isHttp = isUriHttp(connectURI);
        if (isHttp) {
            state.setProtocol(PROTOCOL.HTTP);
            HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
                    connectURI.toString());
            state.setHttpMessage(httpRequest, connectURI);
            connectURI = tcpBindURIFromHttpURI(connectURI);
        }

        Map<String, Object> connectOptions = connectNode.getOptions();

        state.pipelineAsMap = new LinkedHashMap<String, ChannelHandler>();
        state.pipelineAsMap.put("loghandler", new LoggingHandler(true));

        if (isHttp) {
            HttpClientCodec httpClientCodec = new HttpClientCodec();
            HttpMessageAggregatingCodec httpMessageAggregatingCodec = new HttpMessageAggregatingCodec();
            HttpMessageSplittingCodec httpMessageSplittingCodec = new HttpMessageSplittingCodec();
            state.pipelineAsMap.put(HTTP_CODEC_NAME, httpClientCodec);
            state.pipelineAsMap.put(HTTP_MESSAGE_AGGREGATING_CODEC_NAME, httpMessageAggregatingCodec);
            state.pipelineAsMap.put(HTTP_MEESAGE_SPLITTING_CODEC_NAME, httpMessageSplittingCodec);
        }

        state.pipelineAsMap.put("lastevent", new LogLastEventHandler(connectNode.getLocationInfo()));

        state.streamStartLocation = connectNode.getLocationInfo();
        for (AstStreamableNode streamable : connectNode.getStreamables()) {
            streamable.accept(this, state);
        }

        /* Add the completion handler */
        String handlerName = String.format("completion#%d", state.pipelineAsMap.size() + 1);
        CompletionHandler c = new CompletionHandler();
        state.pipelineAsMap.put(handlerName, c);
        state.configuration.getCompletionHandlers().add(c);

        ChannelAddress remoteAddress = addressFactory.newChannelAddress(connectURI);
        connectOptions.put("remoteAddress", remoteAddress);

        connectOptions.put("locationInfo", connectNode.getLocationInfo());

        ClientBootstrap clientBootstrap = bootstrapFactory.newClientBootstrap(connectURI.getScheme());

        final ChannelPipeline pipeline = pipelineFromMap(state.pipelineAsMap, state);

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

        LocationInfo locationInfo = node.getLocationInfo();
        String barrierName = node.getBarrierName();
        Barrier barrier = state.lookupBarrier(barrierName);

        AwaitBarrierUpstreamHandler handler = new AwaitBarrierUpstreamHandler(barrier);
        handler.setLocationInfo(locationInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("read.await#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        state.configuration.getBarriers().add(barrier);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstWriteAwaitNode node, State state) throws Exception {

        LocationInfo locationInfo = node.getLocationInfo();
        String barrierName = node.getBarrierName();
        Barrier barrier = state.lookupBarrier(barrierName);

        AwaitBarrierDownstreamHandler handler = new AwaitBarrierDownstreamHandler(barrier);
        handler.setLocationInfo(locationInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("write.await#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        state.configuration.getBarriers().add(barrier);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstReadNotifyNode node, State state) throws Exception {

        LocationInfo locationInfo = node.getLocationInfo();
        String barrierName = node.getBarrierName();
        Barrier barrier = state.lookupBarrier(barrierName);

        NotifyBarrierHandler handler = new NotifyBarrierHandler(barrier);
        handler.setLocationInfo(locationInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("read.notify#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        state.configuration.getBarriers().add(barrier);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstWriteNotifyNode node, State state) throws Exception {

        LocationInfo locationInfo = node.getLocationInfo();
        String barrierName = node.getBarrierName();
        Barrier barrier = state.lookupBarrier(barrierName);

        NotifyBarrierHandler handler = new NotifyBarrierHandler(barrier);
        handler.setLocationInfo(locationInfo);

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
        handler.setLocationInfo(node.getLocationInfo());
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

        LocationInfo locationInfo = node.getLocationInfo();

        DisconnectHandler handler = new DisconnectHandler();
        handler.setLocationInfo(locationInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("disconnect#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstUnbindNode node, State state) throws Exception {

        LocationInfo locationInfo = node.getLocationInfo();

        UnbindHandler handler = new UnbindHandler();
        handler.setLocationInfo(locationInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("unbind#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstCloseNode node, State state) throws Exception {

        LocationInfo locationInfo = node.getLocationInfo();

        CloseHandler handler = new CloseHandler();
        handler.setLocationInfo(locationInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("close#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstChildOpenedNode node, State state) throws Exception {

        LocationInfo locationInfo = node.getLocationInfo();

        ChildOpenedHandler handler = new ChildOpenedHandler();
        handler.setLocationInfo(locationInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("childOpened#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstChildClosedNode node, State state) throws Exception {

        LocationInfo locationInfo = node.getLocationInfo();

        ChildClosedHandler handler = new ChildClosedHandler();
        handler.setLocationInfo(locationInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("childClosed#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstOpenedNode node, State state) throws Exception {

        LocationInfo locationInfo = node.getLocationInfo();

        OpenedHandler handler = new OpenedHandler();
        handler.setLocationInfo(locationInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("opened#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstBoundNode node, State state) throws Exception {

        LocationInfo locationInfo = node.getLocationInfo();

        BoundHandler handler = new BoundHandler();
        handler.setLocationInfo(locationInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("bound#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstConnectedNode node, State state) throws Exception {

        LocationInfo locationInfo = node.getLocationInfo();

        ConnectedHandler handler = new ConnectedHandler();
        handler.setLocationInfo(locationInfo);

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
        handler.setLocationInfo(node.getLocationInfo());
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
            return new ReadExpressionDecoder(expression, environment);
        }

        @Override
        public MessageDecoder visit(AstFixedLengthBytesMatcher matcher, Configuration config) throws Exception {

            int length = matcher.getLength();
            String captureName = matcher.getCaptureName();
            ExpressionContext environment = config.getExpressionContext();
            MessageDecoder decoder = (captureName != null) ? new ReadByteArrayBytesDecoder(length, environment,
                    captureName) : new ReadByteArrayBytesDecoder(length);
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

            ExpressionContext environment = config.getExpressionContext();
            @SuppressWarnings("unchecked") Constructor<MessageDecoder> constructor = (Constructor<MessageDecoder>) clazz
                    .getConstructor(ExpressionContext.class, String.class);
            return constructor.newInstance(environment, captureName);

        }

        @Override
        public MessageDecoder visit(AstRegexMatcher matcher, Configuration config) throws Exception {
            ExpressionContext environment = config.getExpressionContext();
            MessageDecoder result;
            result = new ReadRegexDecoder(matcher.getValue(), UTF_8, environment);
            return result;
        }

        @Override
        public MessageDecoder visit(AstExactTextMatcher matcher, Configuration config) throws Exception {
            return new ReadExactTextDecoder(matcher.getValue(), UTF_8);
        }

        @Override
        public MessageDecoder visit(AstExactBytesMatcher matcher, Configuration config) throws Exception {
            return new ReadExactBytesDecoder(matcher.getValue());
        }

        @Override
        public MessageDecoder visit(AstVariableLengthBytesMatcher matcher, Configuration config) throws Exception {

            ValueExpression length = matcher.getLength();
            String captureName = matcher.getCaptureName();
            ExpressionContext environment = config.getExpressionContext();
            MessageDecoder decoder = (captureName != null) ? new ReadVariableLengthBytesDecoder(length, environment,
                    captureName) : new ReadVariableLengthBytesDecoder(length, environment);
            return decoder;
        }

    }

    @Override
    public Configuration visit(AstDisconnectedNode node, State state) throws Exception {

        LocationInfo locationInfo = node.getLocationInfo();

        DisconnectedHandler handler = new DisconnectedHandler();
        handler.setLocationInfo(locationInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("disconnected#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstUnboundNode node, State state) throws Exception {

        LocationInfo locationInfo = node.getLocationInfo();

        UnboundHandler handler = new UnboundHandler();
        handler.setLocationInfo(locationInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("unbound#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    @Override
    public Configuration visit(AstClosedNode node, State state) throws Exception {

        LocationInfo locationInfo = node.getLocationInfo();

        ClosedHandler handler = new ClosedHandler();
        handler.setLocationInfo(locationInfo);

        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("closed#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);

        return state.configuration;
    }

    private static ChannelPipeline pipelineFromMap(Map<String, ChannelHandler> pipelineAsMap, State state) {
        ChannelPipeline pipeline = pipeline();
        for (Map.Entry<String, ChannelHandler> entry : pipelineAsMap.entrySet()) {
            if (entry.getValue() instanceof ExecutionHandler) {
                ExecutionHandler handler = (ExecutionHandler) entry.getValue();
                handler.setStreamStartLocation(state.streamStartLocation);
            }
            pipeline.addLast(entry.getKey(), entry.getValue());
        }

        return pipeline;
    }

    // HTTP
    @Override
    public Configuration visit(AstReadHttpHeaderNode node, State state) throws Exception {

        AstLiteralTextValue name = node.getName();

        for (AstValueMatcher matcher : node.getMatchers()) {
            MessageDecoder valueDecoder = matcher.accept(new GenerateReadDecoderVisitor(), state.configuration);
            HttpMessageContributingDecoder headerDecoder = new ReadHttpHeaderDecoder(name.getValue(), valueDecoder);

            ReadHttpHandler handler = new ReadHttpHandler(headerDecoder);

            handler.setLocationInfo(node.getLocationInfo());
            Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
            String handlerName = String.format("readHttpHeader#%d", pipelineAsMap.size() + 1);
            pipelineAsMap.put(handlerName, handler);
        }

        return state.configuration;
    }

    @Override
    public Configuration visit(AstWriteHttpHeaderNode node, State state) throws Exception {

        AstValue name = node.getName();

        for (AstValue val : node.getValues()) {

            MessageEncoder nameEncoder = name.accept(new GenerateWriteEncoderVisitor(), state.configuration);
            MessageEncoder valueEncoder = val.accept(new GenerateWriteEncoderVisitor(), state.configuration);
            HttpMessageContributingEncoder httpEncoder = new WriteHttpHeaderEncoder(nameEncoder, valueEncoder);

            WriteHttpHandler handler = new WriteHttpHandler(state.getHttpMessage(), httpEncoder);

            handler.setLocationInfo(node.getLocationInfo());
            String handlerName = String.format("writeHttpHeader#%d", state.pipelineAsMap.size() + 1);
            state.pipelineAsMap.put(handlerName, handler);
        }

        return state.configuration;
    }

    @Override
    public Configuration visit(AstWriteHttpContentLengthNode node, State state) throws Exception {
        WriteHttpContentLengthHandler handler = new WriteHttpContentLengthHandler(state.getHttpMessage());
        handler.setLocationInfo(node.getLocationInfo());
        String handlerName = String.format("writeHttpContentLength#%d", state.pipelineAsMap.size() + 1);
        state.pipelineAsMap.put(handlerName, handler);
        return null;
    }

    @Override
    public Configuration visit(AstReadHttpMethodNode node, State state) throws Exception {

        AstValueMatcher method = node.getMethod();

        MessageDecoder methodValueDecoder = method.accept(new GenerateReadDecoderVisitor(), state.configuration);
        HttpMessageContributingDecoder httpDecoder = new ReadHttpMethodDecoder(methodValueDecoder);

        ReadHttpHandler handler = new ReadHttpHandler(httpDecoder);

        handler.setLocationInfo(node.getLocationInfo());
        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("readHttpMethod#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);
        return state.configuration;
    }

    @Override
    public Configuration visit(AstWriteHttpMethodNode node, State state) throws Exception {

        AstValue method = node.getMethod();

        MessageEncoder methodEncoder = method.accept(new GenerateWriteEncoderVisitor(), state.configuration);
        HttpMessageContributingEncoder httpEncoder = new WriteHttpMethodEncoder(methodEncoder);

        WriteHttpHandler handler = new WriteHttpHandler(state.getHttpMessage(), httpEncoder);
        handler.setLocationInfo(node.getLocationInfo());
        String handlerName = String.format("writeHttpMethod#%d", state.pipelineAsMap.size() + 1);
        state.pipelineAsMap.put(handlerName, handler);
        return state.configuration;
    }

    @Override
    public Configuration visit(AstReadHttpParameterNode node, State state) throws Exception {

        AstLiteralTextValue key = node.getKey();

        for (AstValueMatcher matcher : node.getMatchers()) {

            MessageDecoder valueDecoder = matcher.accept(new GenerateReadDecoderVisitor(), state.configuration);
            HttpMessageContributingDecoder paramDecoder = new ReadHttpParameterDecoder(key.getValue(), valueDecoder);

            ReadHttpHandler handler = new ReadHttpHandler(paramDecoder);

            handler.setLocationInfo(node.getLocationInfo());
            Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
            String handlerName = String.format("readHttpParameter#%d", pipelineAsMap.size() + 1);
            pipelineAsMap.put(handlerName, handler);
        }

        return state.configuration;
    }

    @Override
    public Configuration visit(AstWriteHttpParameterNode node, State state) throws Exception {

        AstValue key = node.getKey();

        for (AstValue val : node.getValues()) {

            MessageEncoder keyEncoder = key.accept(new GenerateWriteEncoderVisitor(), state.configuration);
            MessageEncoder valueEncoder = val.accept(new GenerateWriteEncoderVisitor(), state.configuration);
            HttpMessageContributingEncoder httpEncoder = new WriteHttpParameterEncoder(keyEncoder, valueEncoder);

            WriteHttpHandler handler = new WriteHttpHandler(state.getHttpMessage(), httpEncoder);

            handler.setLocationInfo(node.getLocationInfo());
            String handlerName = String.format("writeHttpParameter#%d", state.pipelineAsMap.size() + 1);
            state.pipelineAsMap.put(handlerName, handler);
        }

        return state.configuration;
    }

    @Override
    public Configuration visit(AstReadHttpVersionNode node, State state) throws Exception {
        AstValueMatcher version = node.getVersion();

        MessageDecoder versionDecoder = version.accept(new GenerateReadDecoderVisitor(), state.configuration);
        HttpMessageContributingDecoder httpVersionDecoder = new ReadHttpVersionDecoder(versionDecoder);

        ReadHttpHandler handler = new ReadHttpHandler(httpVersionDecoder);

        handler.setLocationInfo(node.getLocationInfo());
        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("readHttpVersion#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);
        return state.configuration;
    }

    @Override
    public Configuration visit(AstWriteHttpVersionNode node, State state) throws Exception {
        AstValue version = node.getVersion();

        MessageEncoder versionEncoder = version.accept(new GenerateWriteEncoderVisitor(), state.configuration);

        HttpMessageContributingEncoder httpEncoder = new WriteHttpVersionEncoder(versionEncoder);
        WriteHttpHandler handler = new WriteHttpHandler(state.getHttpMessage(), httpEncoder);

        handler.setLocationInfo(node.getLocationInfo());
        String handlerName = String.format("writeHttpVersion#%d", state.pipelineAsMap.size() + 1);
        state.pipelineAsMap.put(handlerName, handler);
        return state.configuration;
    }

    @Override
    public Configuration visit(AstReadHttpStatusNode node, State state) throws Exception {
        AstValueMatcher code = node.getCode();
        AstValueMatcher reason = node.getReason();

        MessageDecoder codeDecoder = code.accept(new GenerateReadDecoderVisitor(), state.configuration);
        MessageDecoder reasonDecoder = reason.accept(new GenerateReadDecoderVisitor(), state.configuration);
        HttpMessageContributingDecoder statusDecoder = new ReadHttpStatusDecoder(codeDecoder, reasonDecoder);

        ReadHttpHandler handler = new ReadHttpHandler(statusDecoder);

        handler.setLocationInfo(node.getLocationInfo());
        Map<String, ChannelHandler> pipelineAsMap = state.pipelineAsMap;
        String handlerName = String.format("readHttpStatus#%d", pipelineAsMap.size() + 1);
        pipelineAsMap.put(handlerName, handler);
        return state.configuration;
    }

    @Override
    public Configuration visit(AstWriteHttpStatusNode node, State state) throws Exception {
        AstValue code = node.getCode();
        AstValue reason = node.getReason();

        MessageEncoder codeEncoder = code.accept(new GenerateWriteEncoderVisitor(), state.configuration);
        MessageEncoder reasonEncoder = reason.accept(new GenerateWriteEncoderVisitor(), state.configuration);
        HttpMessageContributingEncoder httpEncoder = new WriteHttpStatusEncoder(codeEncoder, reasonEncoder);

        WriteHttpHandler handler = new WriteHttpHandler(state.getHttpMessage(), httpEncoder);

        handler.setLocationInfo(node.getLocationInfo());
        String handlerName = String.format("writeHttpStatus#%d", state.pipelineAsMap.size() + 1);
        state.pipelineAsMap.put(handlerName, handler);
        return state.configuration;
    }

    private URI tcpBindURIFromHttpURI(URI httpURI) {
        URI resultingURI;
        if (httpURI.getPort() == -1) {
            resultingURI = URI.create(String.format("tcp://%s:80", httpURI.getAuthority()));
        } else {
            resultingURI = URI.create(String.format("tcp://%s", httpURI.getAuthority()));
        }
        return resultingURI;
    }

    @Override
    public Configuration visit(AstCloseHttpRequestNode node, State state) throws Exception {
        HTTP_DIRECTION direction = state.getAndSwapDirection();
        String handlerName = String.format("closeHttpRequest#%d", state.pipelineAsMap.size() + 1);
        ExecutionHandler handler = null;
        switch (direction) {
        case READ:
            handler = new CloseReadHttpRequestHandler();
            break;
        case WRITE:
            handler = new CloseWriteHttpRequestHandler();
            break;
        }
        assert handler != null;
        state.protocol = PROTOCOL.TCP;
        state.pipelineAsMap.put(handlerName, handler);
        handler.setLocationInfo(node.getLocationInfo());
        return state.configuration;
    }

    @Override
    public Configuration visit(AstCloseHttpResponseNode node, State state) throws Exception {
        HTTP_DIRECTION direction = state.getHttpDirection();
        String handlerName = String.format("closeHttpResponse#%d", state.pipelineAsMap.size() + 1);
        ExecutionHandler handler = null;
        switch (direction) {
        case READ:
            handler = new CloseReadHttpResponseHandler();
            break;
        case WRITE:
            handler = new CloseWriteHttpResponseHandler();
            break;
        }
        assert handler != null;
        state.pipelineAsMap.put(handlerName, handler);
        handler.setLocationInfo(node.getLocationInfo());
        return state.configuration;
    }

    @Override
    public Configuration visit(AstEndOfHttpHeadersNode node, State state) throws Exception {
        HTTP_DIRECTION direction = state.getHttpDirection();
        String handlerName = String.format("endOfHttpHeaders#%d", state.pipelineAsMap.size() + 1);
        ExecutionHandler handler = null;
        switch (direction) {
        case READ:
            handler = new EndOfReadHttpHeadersHandler();
            break;
        case WRITE:
            handler = new EndOfWriteHttpHeadersHandler(state.getHttpMessage());
            break;
        }
        assert handler != null;
        state.pipelineAsMap.put(handlerName, handler);
        handler.setLocationInfo(node.getLocationInfo());
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
