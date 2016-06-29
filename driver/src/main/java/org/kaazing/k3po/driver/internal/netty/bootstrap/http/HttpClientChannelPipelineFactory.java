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
package org.kaazing.k3po.driver.internal.netty.bootstrap.http;

import static org.jboss.netty.channel.Channels.pipeline;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;

import java.lang.reflect.Field;

final class HttpClientChannelPipelineFactory implements ChannelPipelineFactory {
    private static final Field encoderField;
    static {
        Field tmpField;
        try {
            tmpField = HttpClientCodec.class.getDeclaredField("encoder");
            tmpField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            tmpField = null;
        }
        encoderField = tmpField;
    }

    private static final Field decoderField;
    static {
        Field tmpField;
        try {
            tmpField = HttpClientCodec.class.getDeclaredField("decoder");
            tmpField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            tmpField = null;
        }
        decoderField = tmpField;
    }


    @Override
    public ChannelPipeline getPipeline() throws Exception {
        // HttpClientCodec's HttpResponseDecoder, HttpRequestEncoder fields are private in netty 3.9.x
        // (there are accessor methods in 4.x)
        HttpClientCodec codec = new HttpClientCodec();
        HttpResponseDecoder decoder = (HttpResponseDecoder) decoderField.get(codec);
        HttpRequestEncoder encoder = (HttpRequestEncoder) encoderField.get(codec);
        return pipeline(decoder, encoder, new HttpClientChannelSource());
    }

}
