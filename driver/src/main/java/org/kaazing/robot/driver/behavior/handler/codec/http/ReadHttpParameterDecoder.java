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

package org.kaazing.robot.driver.behavior.handler.codec.http;

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.util.CharsetUtil.UTF_8;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.robot.driver.behavior.handler.codec.MessageDecoder;
import org.kaazing.robot.driver.behavior.handler.codec.MessageMismatchException;
import org.kaazing.robot.lang.ast.matcher.AstExactTextMatcher;
import org.kaazing.robot.lang.ast.matcher.AstRegexMatcher;
import org.kaazing.robot.lang.ast.matcher.AstValueMatcher;
import org.kaazing.robot.lang.regex.NamedGroupMatcher;
import org.kaazing.robot.lang.regex.NamedGroupPattern;

public class ReadHttpParameterDecoder implements HttpMessageContributingDecoder {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ReadHttpParameterDecoder.class);
    private String key;
    private AstValueMatcher valueMatcher;
    private MessageDecoder valueDecoder;

    public ReadHttpParameterDecoder(String key, AstValueMatcher valueMatcher, MessageDecoder valueDecoder) {
        this.key = key;
        this.valueMatcher = valueMatcher;
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

        List<String> parameters = toList(query.split("&"));
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

        if (result != null) {
            request.setUri(uri.toString().replace(query, result));
        } else {
            request.setUri(uri.toString().replace(query, ""));
        }
    }

    private static List<String> toList(String [] arr) {
        List<String> list = new ArrayList<String>(arr.length);
        for (int i = 0; i < arr.length; i++) {
            list.add(arr[i]);
        }
        return list;
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
            String parameterValue = aParameter[1];
            if (key.equals(parameterKey)) {
                if (valueMatcher instanceof AstExactTextMatcher) {
                    if (((AstExactTextMatcher) valueMatcher).equals(new AstExactTextMatcher(parameterValue))) {
                        matchingParameters.add(parameterValue);
                        break;
                    }
                } else if (valueMatcher instanceof AstRegexMatcher) {
                    NamedGroupPattern pattern = ((AstRegexMatcher) valueMatcher).getValue();
                    NamedGroupMatcher matcher = pattern.matcher(parameterValue);
                    if (matcher.matches()) {
                        matchingParameters.add(parameterValue);
                        break;
                    }
                } else {
                    throw new IllegalStateException(String.format("Unexpected matcher type on valueMatcher: %s", valueMatcher));
                }
            }
        }
        return matchingParameters;
    }

    @Override
    public String toString() {
        return String.format("read http parameter decoder with: %s, %s", key, valueDecoder);
    }

}
