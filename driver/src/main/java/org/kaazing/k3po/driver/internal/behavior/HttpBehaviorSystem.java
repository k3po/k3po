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
package org.kaazing.k3po.driver.internal.behavior;

import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import static org.kaazing.k3po.driver.internal.types.HttpTypeSystem.CONFIG_CONTENT_LENGTH;
import static org.kaazing.k3po.driver.internal.types.HttpTypeSystem.CONFIG_HEADER;
import static org.kaazing.k3po.driver.internal.types.HttpTypeSystem.CONFIG_HOST;
import static org.kaazing.k3po.driver.internal.types.HttpTypeSystem.CONFIG_METHOD;
import static org.kaazing.k3po.driver.internal.types.HttpTypeSystem.CONFIG_PARAMETER;
import static org.kaazing.k3po.driver.internal.types.HttpTypeSystem.CONFIG_REQUEST;
import static org.kaazing.k3po.driver.internal.types.HttpTypeSystem.CONFIG_STATUS;
import static org.kaazing.k3po.driver.internal.types.HttpTypeSystem.CONFIG_TRAILER;
import static org.kaazing.k3po.driver.internal.types.HttpTypeSystem.CONFIG_VERSION;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.kaazing.k3po.driver.internal.behavior.handler.codec.MessageDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.MessageEncoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.http.HttpContentLengthEncoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.http.HttpHeaderDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.http.HttpHeaderEncoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.http.HttpHeaderMissingDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.http.HttpHostEncoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.http.HttpMethodDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.http.HttpMethodEncoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.http.HttpParameterDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.http.HttpParameterEncoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.http.HttpRequestFormEncoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.http.HttpStatusDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.http.HttpStatusEncoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.http.HttpTrailerDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.http.HttpTrailerEncoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.http.HttpVersionDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.http.HttpVersionEncoder;
import org.kaazing.k3po.driver.internal.behavior.handler.command.ReadConfigHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.command.WriteConfigHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.event.http.ReadHttpTrailersHandler;
import org.kaazing.k3po.lang.internal.ast.AstReadConfigNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteConfigNode;
import org.kaazing.k3po.lang.internal.ast.matcher.AstExactTextMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstValueMatcher;
import org.kaazing.k3po.lang.internal.ast.value.AstLiteralTextValue;
import org.kaazing.k3po.lang.internal.ast.value.AstValue;
import org.kaazing.k3po.lang.types.StructuredTypeInfo;

public class HttpBehaviorSystem implements BehaviorSystemSpi {

    private final Set<StructuredTypeInfo> configTypes;
    private final Map<StructuredTypeInfo, ReadBehaviorFactory> readFactories;
    private final Map<StructuredTypeInfo, WriteBehaviorFactory> writeFactories;

    public HttpBehaviorSystem()
    {
        Set<StructuredTypeInfo> configTypes = new LinkedHashSet<>();
        configTypes.add(CONFIG_CONTENT_LENGTH);
        configTypes.add(CONFIG_HEADER);
        configTypes.add(CONFIG_HOST);
        configTypes.add(CONFIG_METHOD);
        configTypes.add(CONFIG_PARAMETER);
        configTypes.add(CONFIG_REQUEST);
        configTypes.add(CONFIG_STATUS);
        configTypes.add(CONFIG_TRAILER);
        configTypes.add(CONFIG_VERSION);
        this.configTypes = unmodifiableSet(configTypes);

        Map<StructuredTypeInfo, ReadBehaviorFactory> readFactories = new LinkedHashMap<>();
        readFactories.put(CONFIG_METHOD, HttpBehaviorSystem::newReadHttpMethodHandler);
        readFactories.put(CONFIG_HEADER, HttpBehaviorSystem::newReadHttpHeaderHandler);
        readFactories.put(CONFIG_PARAMETER, HttpBehaviorSystem::newReadHttpParameterHandler);
        readFactories.put(CONFIG_VERSION, HttpBehaviorSystem::newReadHttpVersionHandler);
        readFactories.put(CONFIG_STATUS, HttpBehaviorSystem::newReadHttpStatusHandler);
        readFactories.put(CONFIG_TRAILER, HttpBehaviorSystem::newReadHttpTrailerHandler);
        this.readFactories = readFactories;

        Map<StructuredTypeInfo, WriteBehaviorFactory> writeFactories = new LinkedHashMap<>();
        writeFactories.put(CONFIG_REQUEST, HttpBehaviorSystem::newWriteHttpRequestHandler);
        writeFactories.put(CONFIG_HOST, HttpBehaviorSystem::newWriteHttpHostHandler);
        writeFactories.put(CONFIG_CONTENT_LENGTH, HttpBehaviorSystem::newWriteHttpContentLengthHandler);
        writeFactories.put(CONFIG_METHOD, HttpBehaviorSystem::newWriteHttpMethodHandler);
        writeFactories.put(CONFIG_HEADER, HttpBehaviorSystem::newWriteHttpHeaderHandler);
        writeFactories.put(CONFIG_PARAMETER, HttpBehaviorSystem::newWriteHttpParameterHandler);
        writeFactories.put(CONFIG_VERSION, HttpBehaviorSystem::newWriteHttpVersionHandler);
        writeFactories.put(CONFIG_STATUS, HttpBehaviorSystem::newWriteHttpStatusHandler);
        writeFactories.put(CONFIG_TRAILER, HttpBehaviorSystem::newWriteHttpTrailerHandler);
        this.writeFactories = writeFactories;
    }

    @Override
    public Set<StructuredTypeInfo> getConfigTypes()
    {
        return configTypes;
    }

    @Override
    public ReadBehaviorFactory readFactory(
        StructuredTypeInfo configType)
    {
        return readFactories.get(configType);
    }

    @Override
    public WriteBehaviorFactory writeFactory(
        StructuredTypeInfo configType)
    {
        return writeFactories.get(configType);
    }

    private static ReadHttpTrailersHandler newReadHttpTrailerHandler(
        AstReadConfigNode node,
        Function<AstValueMatcher, MessageDecoder> decoderFactory)
    {
        AstExactTextMatcher name = (AstExactTextMatcher) node.getMatcher("name");

        List<MessageDecoder> valueDecoders = new ArrayList<>();
        for (AstValueMatcher matcher : node.getMatchers()) {
            valueDecoders.add(decoderFactory.apply(matcher));
        }

        HttpTrailerDecoder decoder = new HttpTrailerDecoder(name.getValue(), valueDecoders);
        decoder.setRegionInfo(node.getRegionInfo());

        // Ideally we could use a ReadConfigHandler as follows, but the trailers come in-sync with
        // with the channel close, which completes the composite future of all handlers and checks
        // the ReadConfigHandler to see if it completed.
        // HttpTrailerDecoder decoder = new HttpTrailerDecoder(name.getValue(), valueDecoders);
        // decoder.setRegionInfo(node.getRegionInfo());
        // ReadConfigHandler handler = new ReadConfigHandler(decoder);

        ReadHttpTrailersHandler handler = new ReadHttpTrailersHandler(decoder);
        handler.setRegionInfo(node.getRegionInfo());
        return handler;
    }

    private static ReadConfigHandler newReadHttpStatusHandler(
        AstReadConfigNode node,
        Function<AstValueMatcher, MessageDecoder> decoderFactory)
    {
        AstValueMatcher code = node.getMatcher("code");
        AstValueMatcher reason = node.getMatcher("reason");

        MessageDecoder codeDecoder = decoderFactory.apply(code);
        MessageDecoder reasonDecoder = decoderFactory.apply(reason);

        HttpStatusDecoder decoder = new HttpStatusDecoder(codeDecoder, reasonDecoder);
        ReadConfigHandler handler = new ReadConfigHandler(decoder);
        handler.setRegionInfo(node.getRegionInfo());
        return handler;
    }

    private static ReadConfigHandler newReadHttpVersionHandler(
        AstReadConfigNode node,
        Function<AstValueMatcher, MessageDecoder> decoderFactory)
    {
        AstValueMatcher version = node.getMatcher();

        MessageDecoder versionDecoder = decoderFactory.apply(version);

        HttpVersionDecoder decoder = new HttpVersionDecoder(versionDecoder);
        ReadConfigHandler handler = new ReadConfigHandler(decoder);
        handler.setRegionInfo(node.getRegionInfo());
        return handler;
    }

    private static ReadConfigHandler newReadHttpParameterHandler(
        AstReadConfigNode node,
        Function<AstValueMatcher, MessageDecoder> decoderFactory)
    {
        AstExactTextMatcher name = (AstExactTextMatcher) node.getMatcher("name");
        requireNonNull(name);

        List<MessageDecoder> valueDecoders = new ArrayList<>();
        for (AstValueMatcher matcher : node.getMatchers()) {
            valueDecoders.add(decoderFactory.apply(matcher));
        }

        HttpParameterDecoder decoder = new HttpParameterDecoder(name.getValue(), valueDecoders);
        decoder.setRegionInfo(node.getRegionInfo());
        ReadConfigHandler handler = new ReadConfigHandler(decoder);
        handler.setRegionInfo(node.getRegionInfo());
        return handler;
    }

    private static ReadConfigHandler newReadHttpHeaderHandler(
        AstReadConfigNode node,
        Function<AstValueMatcher, MessageDecoder> decoderFactory)
    {
        AstExactTextMatcher name = (AstExactTextMatcher) node.getMatcher("name");
        requireNonNull(name);

        if (node.isMissing()) {
            HttpHeaderMissingDecoder decoder = new HttpHeaderMissingDecoder(name.getValue());
            decoder.setRegionInfo(node.getRegionInfo());
            ReadConfigHandler handler = new ReadConfigHandler(decoder);
            handler.setRegionInfo(node.getRegionInfo());
            return handler;
        }
        else {
            List<MessageDecoder> valueDecoders = new ArrayList<>();
            for (AstValueMatcher matcher : node.getMatchers()) {
                valueDecoders.add(decoderFactory.apply(matcher));
            }
    
            HttpHeaderDecoder decoder = new HttpHeaderDecoder(name.getValue(), valueDecoders);
            decoder.setRegionInfo(node.getRegionInfo());
            ReadConfigHandler handler = new ReadConfigHandler(decoder);
            handler.setRegionInfo(node.getRegionInfo());
            return handler;
        }
    }

    private static ReadConfigHandler newReadHttpMethodHandler(
        AstReadConfigNode node,
        Function<AstValueMatcher, MessageDecoder> decoderFactory)
    {
        AstValueMatcher methodName = node.getMatcher();
        requireNonNull(methodName);

        MessageDecoder methodValueDecoder = decoderFactory.apply(methodName);

        HttpMethodDecoder decoder = new HttpMethodDecoder(methodValueDecoder);
        ReadConfigHandler handler = new ReadConfigHandler(decoder);
        handler.setRegionInfo(node.getRegionInfo());
        return handler;
    }

    private static WriteConfigHandler newWriteHttpTrailerHandler(
        AstWriteConfigNode node,
        Function<AstValue<?>, MessageEncoder> encoderFactory)
    {
        AstValue<?> name = node.getValue("name");

        MessageEncoder nameEncoder = encoderFactory.apply(name);

        List<MessageEncoder> valueEncoders = new ArrayList<>();
        for (AstValue<?> value : node.getValues()) {
            valueEncoders.add(encoderFactory.apply(value));
        }

        WriteConfigHandler handler = new WriteConfigHandler(new HttpTrailerEncoder(nameEncoder, valueEncoders));
        handler.setRegionInfo(node.getRegionInfo());
        return handler;
    }

    private static WriteConfigHandler newWriteHttpStatusHandler(
        AstWriteConfigNode node,
        Function<AstValue<?>, MessageEncoder> encoderFactory)
    {
        AstValue<?> code = node.getValue("code");
        AstValue<?> reason = node.getValue("reason");

        MessageEncoder codeEncoder = encoderFactory.apply(code);
        MessageEncoder reasonEncoder = encoderFactory.apply(reason);

        WriteConfigHandler handler = new WriteConfigHandler(new HttpStatusEncoder(codeEncoder, reasonEncoder));

        handler.setRegionInfo(node.getRegionInfo());
        return handler;
    }

    private static WriteConfigHandler newWriteHttpVersionHandler(
        AstWriteConfigNode node,
        Function<AstValue<?>, MessageEncoder> encoderFactory)
    {
        AstValue<?> version = node.getValue();

        MessageEncoder versionEncoder = encoderFactory.apply(version);

        WriteConfigHandler handler = new WriteConfigHandler(new HttpVersionEncoder(versionEncoder));

        handler.setRegionInfo(node.getRegionInfo());
        return handler;
    }

    private static WriteConfigHandler newWriteHttpParameterHandler(
        AstWriteConfigNode node,
        Function<AstValue<?>, MessageEncoder> encoderFactory)
    {
        AstValue<?> name = node.getValue("name");
        MessageEncoder nameEncoder = encoderFactory.apply(name);

        List<MessageEncoder> valueEncoders = new ArrayList<>();
        for (AstValue<?> value : node.getValues()) {
            valueEncoders.add(encoderFactory.apply(value));
        }

        WriteConfigHandler handler = new WriteConfigHandler(new HttpParameterEncoder(nameEncoder, valueEncoders));

        handler.setRegionInfo(node.getRegionInfo());
        return handler;
    }

    private static WriteConfigHandler newWriteHttpMethodHandler(
        AstWriteConfigNode node,
        Function<AstValue<?>, MessageEncoder> encoderFactory)
    {
        AstValue<?> methodName = node.getValue();
        requireNonNull(methodName);

        MessageEncoder methodEncoder = encoderFactory.apply(methodName);

        WriteConfigHandler handler = new WriteConfigHandler(new HttpMethodEncoder(methodEncoder));
        handler.setRegionInfo(node.getRegionInfo());
        return handler;
    }

    private static WriteConfigHandler newWriteHttpHostHandler(
        AstWriteConfigNode node,
        Function<AstValue<?>, MessageEncoder> encoderFactory)
    {
        WriteConfigHandler handler = new WriteConfigHandler(new HttpHostEncoder());
        handler.setRegionInfo(node.getRegionInfo());
        return handler;
    }

    private static WriteConfigHandler newWriteHttpContentLengthHandler(
        AstWriteConfigNode node,
        Function<AstValue<?>, MessageEncoder> encoderFactory)
    {
        WriteConfigHandler handler = new WriteConfigHandler(new HttpContentLengthEncoder());
        handler.setRegionInfo(node.getRegionInfo());
        return handler;
    }

    private static WriteConfigHandler newWriteHttpHeaderHandler(
        AstWriteConfigNode node,
        Function<AstValue<?>, MessageEncoder> encoderFactory)
    {
        AstValue<?> name = node.getValue("name");
        MessageEncoder nameEncoder = encoderFactory.apply(name);

        List<MessageEncoder> valueEncoders = new ArrayList<>();
        for (AstValue<?> value : node.getValues()) {
            valueEncoders.add(encoderFactory.apply(value));
        }

        WriteConfigHandler handler = new WriteConfigHandler(new HttpHeaderEncoder(nameEncoder, valueEncoders));

        handler.setRegionInfo(node.getRegionInfo());
        return handler;
    }

    private static WriteConfigHandler newWriteHttpRequestHandler(
        AstWriteConfigNode node,
        Function<AstValue<?>, MessageEncoder> encoderFactory)
    {
        AstValue<?> form = (AstLiteralTextValue) node.getValue();
        MessageEncoder formEncoder = encoderFactory.apply(form);

        WriteConfigHandler handler = new WriteConfigHandler(new HttpRequestFormEncoder(formEncoder));

        handler.setRegionInfo(node.getRegionInfo());
        return handler;
    }
}
