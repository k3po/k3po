package org.kaazing.k3po.driver.behavior.handler.codec;

import static org.jboss.netty.util.CharsetUtil.UTF_8;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.kaazing.k3po.driver.behavior.ScriptProgressException;
import org.kaazing.k3po.driver.behavior.handler.codec.http.HttpMethodDecoder;
import org.kaazing.k3po.driver.netty.bootstrap.http.HttpChannelConfig;

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
