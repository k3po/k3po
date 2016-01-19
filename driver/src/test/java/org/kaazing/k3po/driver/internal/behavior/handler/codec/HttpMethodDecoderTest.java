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
package org.kaazing.k3po.driver.internal.behavior.handler.codec;

import static org.jboss.netty.util.CharsetUtil.UTF_8;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.kaazing.k3po.driver.internal.behavior.ScriptProgressException;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.http.HttpMethodDecoder;
import org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpChannelConfig;

public class HttpMethodDecoderTest {

    @Test(expected = ScriptProgressException.class)
    public void testHttpMethodDecoderFails() throws Exception {
        MessageDecoder methodValueDecoder = new ReadExactTextDecoder("GET", UTF_8);

        Mockery context = new Mockery();
        final Channel channel = context.mock(Channel.class);
        final HttpChannelConfig httpConfig = context.mock(HttpChannelConfig.class);

        context.checking(new Expectations() {
            {
                oneOf(channel).getConfig();
                will(returnValue(httpConfig));
                oneOf(httpConfig).getMethod();
                will(returnValue(new HttpMethod("POST")));
            }
        });
        HttpMethodDecoder decoder = new HttpMethodDecoder(methodValueDecoder);
        decoder.decode(channel);
        context.assertIsSatisfied();
    }

    @Test
    public void testHttpMethodDecoderPass() throws Exception {
        MessageDecoder methodValueDecoder = new ReadExactTextDecoder("GET", UTF_8);

        Mockery context = new Mockery();
        final Channel channel = context.mock(Channel.class);
        final HttpChannelConfig httpConfig = context.mock(HttpChannelConfig.class);

        context.checking(new Expectations() {
            {
                oneOf(channel).getConfig();
                will(returnValue(httpConfig));
                oneOf(httpConfig).getMethod();
                will(returnValue(new HttpMethod("GET")));
            }
        });
        HttpMethodDecoder decoder = new HttpMethodDecoder(methodValueDecoder);
        decoder.decode(channel);
        context.assertIsSatisfied();
    }
}
