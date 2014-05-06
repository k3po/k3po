/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.codec.http;

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.util.CharsetUtil.UTF_8;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

import com.kaazing.robot.behavior.handler.codec.MessageDecoder;
import com.kaazing.robot.behavior.handler.codec.MessageMismatchException;

public class ReadHttpParameterDecoder implements HttpMessageContributingDecoder {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ReadHttpParameterDecoder.class);
    private String key;
    private MessageDecoder valueDecoder;

    public ReadHttpParameterDecoder(String key, MessageDecoder valueDecoder) {
        this.key = key;
        this.valueDecoder = valueDecoder;
    }

    /**
     * Will remove the first matching parameter, ie. if there are multiple parameters with same key, it will remove the
     * first one whose value also matches the expected value
     */
    @Override
    public void decode(HttpMessage message) throws Exception {
        HttpRequest request;
        if (message instanceof HttpRequest) {
            request = (HttpRequest) message;
        } else {
            throw new IllegalArgumentException("Can not match any parameter on a HttpResponse");
        }
        List<String> parameterValues = getParameters(request);
        if (parameterValues.isEmpty()) {
            new MessageMismatchException("Could not match non-existent parameter", key, null);
        }

        int firstMatchingParameter = -1;
        MessageMismatchException lastException = null;

        for (int i = 0; i < parameterValues.size(); i++) {
            try {
                String currentParameterValue = parameterValues.get(i);
                ChannelBuffer copiedBuffer = copiedBuffer(currentParameterValue, UTF_8);
                valueDecoder.decode(copiedBuffer);
                if (firstMatchingParameter > -1) {
                    LOGGER.warn(String.format(
                            "Multiple matching parameters for read parameter %s, will remove first matching paramter",
                            key));
                    // no need to throw this exception multiple times
                    break;
                } else {
                    firstMatchingParameter = i;
                }
            } catch (MessageMismatchException mme) {
                lastException = mme;
            }
        }

        if (firstMatchingParameter == -1) {
            assert lastException != null;
            throw lastException;
        }

        // Remove the matched query parameter
        URI uri = URI.create(request.getUri());
        String query = uri.getQuery();
        List<String> parameters = Arrays.asList(query.split("&"));
        String parameterToMatch = String.format("%s=%s", key, parameterValues.get(firstMatchingParameter));
        for (String parameter : parameters) {
            if (parameterToMatch.equals(parameter)) {
                parameters.remove(parameter);
                break;
            }
        }
        String result = null;
        for (String parameter : parameters) {
            if (result == null) {
                result = parameter;
            } else {
                result = String.format("%s&%s", result, parameter);
            }
        }

        request.setUri(uri.toString().replace(query, result));
    }

    /**
     * @param httpRequest
     * @return the queryParameter Value if it exists, or null
     */
    private List<String> getParameters(HttpRequest httpRequest) {
        List<String> matchingParameters = new ArrayList<String>();
        String uri = httpRequest.getUri();
        URI requestURI = URI.create(uri);
        String query = requestURI.getQuery();
        String[] parameters = query.split("&");
        for (int i = 0; i < parameters.length; i++) {
            String[] aParameter = parameters[i].split("=");
            String parameterKey = aParameter[0];
            if (key.equals(parameterKey)) {
                matchingParameters.add(aParameter[1]);
                break;
            }
        }
        return matchingParameters;
    }

    @Override
    public String toString() {
        return String.format("read http parameter decoder with: %s, %s", key, valueDecoder);
    }

}
