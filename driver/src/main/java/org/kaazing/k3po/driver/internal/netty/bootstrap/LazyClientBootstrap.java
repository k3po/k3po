/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
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

package org.kaazing.k3po.driver.internal.netty.bootstrap;

import java.net.URI;
import java.util.Map;

import org.jboss.netty.channel.ChannelPipelineFactory;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddressFactory;
import org.kaazing.k3po.lang.internal.ast.value.AstExpressionValue;
import org.kaazing.k3po.lang.internal.ast.value.AstLiteralBytesValue;
import org.kaazing.k3po.lang.internal.ast.value.AstLiteralTextValue;
import org.kaazing.k3po.lang.internal.ast.value.AstLocation;
import org.kaazing.k3po.lang.internal.ast.value.AstUriLiteralValue;
import org.kaazing.k3po.lang.internal.ast.value.AstValue;
import org.kaazing.k3po.lang.internal.el.ExpressionContext;

public class LazyClientBootstrap {

    private final BootstrapFactory bootstrapFactory;
    private final Map<String, Object> connectOptions;
    private final ChannelPipelineFactory pipelineFactory;
    private final ChannelAddressFactory addressFactory;

    private ClientBootstrap clientBootstrap;

    public LazyClientBootstrap(BootstrapFactory bootstrapFactory, ChannelAddressFactory addressFactory,
            ChannelPipelineFactory pipelineFactory, Map<String, Object> connectOptions) {
        this.bootstrapFactory = bootstrapFactory;
        this.addressFactory = addressFactory;
        this.pipelineFactory = pipelineFactory;
        this.connectOptions = connectOptions;
    }

    public ClientBootstrap getClientBootstrap() throws Exception {
        if (clientBootstrap == null) {
            AstValue location = (AstValue) connectOptions.remove("location");
            ExpressionContext environment = (ExpressionContext) connectOptions.remove("environment");
            URI connectUri = location.accept(new GenerateURIValueVisitor(), environment);
            ChannelAddress remoteAddress = addressFactory.newChannelAddress(connectUri);
            connectOptions.put("remoteAddress", remoteAddress);
            ClientBootstrap candidateClientBootstrap = bootstrapFactory.newClientBootstrap(connectUri.getScheme());
            candidateClientBootstrap.setPipelineFactory(pipelineFactory);
            candidateClientBootstrap.setOptions(connectOptions);
            clientBootstrap = candidateClientBootstrap;
        }
        return clientBootstrap;
    }

    public Map<String, Object> getOptions() {
        return this.connectOptions;
    }

    private static class GenerateURIValueVisitor implements AstLocation.LocationVisitor<URI, ExpressionContext> {

        @Override
        public URI visit(AstExpressionValue value, ExpressionContext environment) throws Exception {
            Object uriLiteralObj;

            synchronized (environment) {
                uriLiteralObj = value.getValue().getValue(environment);
            }

            if (uriLiteralObj == null) {
                throw new NullPointerException("Location expression result is null");
            }

            String uriLiteral = uriLiteralObj.toString();
            return URI.create(uriLiteral);
        }

        @Override
        public URI visit(AstUriLiteralValue value, ExpressionContext parameter) throws Exception {
            return value.getValue();
        }

        @Override
        public URI visit(AstLiteralTextValue value, ExpressionContext parameter) throws Exception {
            return null;
        }

        @Override
        public URI visit(AstLiteralBytesValue value, ExpressionContext parameter) throws Exception {
            return null;
        }
    }

}
