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

import static java.util.ServiceLoader.load;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import org.jboss.netty.channel.ChannelHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.MessageDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.MessageEncoder;
import org.kaazing.k3po.lang.internal.ast.AstReadConfigNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteConfigNode;
import org.kaazing.k3po.lang.internal.ast.matcher.AstValueMatcher;
import org.kaazing.k3po.lang.internal.ast.value.AstValue;
import org.kaazing.k3po.lang.types.StructuredTypeInfo;

public final class BehaviorSystem {

    private final Map<StructuredTypeInfo, ReadBehaviorFactory> readBehaviors;
    private final Map<StructuredTypeInfo, WriteBehaviorFactory> writeBehaviors;

    private BehaviorSystem(Iterable<BehaviorSystemSpi> behaviorSystems) {

        Map<StructuredTypeInfo, ReadBehaviorFactory> readBehaviors = new LinkedHashMap<>();
        Map<StructuredTypeInfo, WriteBehaviorFactory> writeBehaviors = new LinkedHashMap<>();

        for (BehaviorSystemSpi behaviorSystem : behaviorSystems) {
            for (StructuredTypeInfo configType : behaviorSystem.getConfigTypes()) {
                readBehaviors.put(configType, behaviorSystem.readFactory(configType));
                writeBehaviors.put(configType, behaviorSystem.writeFactory(configType));
            }
        }

        this.readBehaviors = readBehaviors;
        this.writeBehaviors = writeBehaviors;
    }

    public ChannelHandler newReadHandler(
        AstReadConfigNode node,
        Function<AstValueMatcher, MessageDecoder> decoderFactory) {

        StructuredTypeInfo type = node.getType();
        ReadBehaviorFactory factory = readBehaviors.getOrDefault(type, (n, f) -> null);
        return factory.newHandler(node, decoderFactory);
    }

    public ChannelHandler newWriteHandler(
        AstWriteConfigNode node,
        Function<AstValue<?>, MessageEncoder> encoderFactory) {

        StructuredTypeInfo type = node.getType();
        WriteBehaviorFactory factory = writeBehaviors.getOrDefault(type, (n, f) -> null);
        return factory.newHandler(node, encoderFactory);
    }

    public static final BehaviorSystem newInstance() {
        return new BehaviorSystem(load(BehaviorSystemSpi.class));
    }
}
