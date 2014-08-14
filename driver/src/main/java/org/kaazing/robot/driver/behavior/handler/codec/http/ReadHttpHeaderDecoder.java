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

import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.robot.driver.behavior.handler.codec.MessageDecoder;
import org.kaazing.robot.driver.behavior.handler.codec.MessageMismatchException;
import org.kaazing.robot.lang.ast.matcher.AstExactTextMatcher;
import org.kaazing.robot.lang.ast.matcher.AstRegexMatcher;
import org.kaazing.robot.lang.ast.matcher.AstValueMatcher;
import org.kaazing.robot.lang.regex.NamedGroupMatcher;
import org.kaazing.robot.lang.regex.NamedGroupPattern;

public class ReadHttpHeaderDecoder implements HttpMessageContributingDecoder {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ReadHttpHeaderDecoder.class);
    private String name;
    private AstValueMatcher valueMatcher;
    private MessageDecoder valueDecoder;

    public ReadHttpHeaderDecoder(String name, AstValueMatcher valueMatcher, MessageDecoder valueDecoder) {
        this.name = name;
        this.valueMatcher = valueMatcher;
        this.valueDecoder = valueDecoder;
    }

    /**
     * Will remove the first matching header, ie. if there are multiple headers with same key, it will remove the first
     * one whose value also matches the expected value
     */
    @Override
    public void decode(HttpMessage message) throws Exception {
        HttpHeaders headers = message.headers();
        if (headers.isEmpty()) {
            new MessageMismatchException("Could not match non-existent header", name, null);
        }

        int firstMatchingHeader = -1;
        MessageMismatchException lastException = null;

        List<String> headerValues = headers.getAll(name);

        for (int i = 0; i < headerValues.size(); i++) {
            String currentHeaderValue = headerValues.get(i);
            if (valueMatcher instanceof AstExactTextMatcher) {
                if (((AstExactTextMatcher) valueMatcher).equals(new AstExactTextMatcher(currentHeaderValue))) {
                    try {
                        ChannelBuffer copiedBuffer = copiedBuffer(currentHeaderValue, UTF_8);
                        valueDecoder.decode(copiedBuffer);
                    } catch (MessageMismatchException mme) {
                        lastException = mme;
                    }
                    firstMatchingHeader = i;
                    break;
                }
            } else if (valueMatcher instanceof AstRegexMatcher) {
                NamedGroupPattern pattern = ((AstRegexMatcher) valueMatcher).getValue();
                NamedGroupMatcher matcher = pattern.matcher(currentHeaderValue);
                if (matcher.matches()) {
                    try {
                        ChannelBuffer copiedBuffer = copiedBuffer(currentHeaderValue, UTF_8);
                        valueDecoder.decode(copiedBuffer);
                    } catch (MessageMismatchException mme) {
                        lastException = mme;
                    }
                    firstMatchingHeader = i;
                    break;
                }
            } else {
                throw new IllegalStateException(String.format("Unexpected matcher type on valueMatcher: %s", valueMatcher));
            }
        }

        if (firstMatchingHeader == -1) {
            // Last Exception cannot be null because there is a non empty list of headers
            throw lastException;
        }

        // remove all the headers
        message.headers().remove(name);

        // add all of them back in except the matching one
        for (int i = 0; i < headerValues.size(); i++) {
            if (i != firstMatchingHeader) {
                message.headers().add(name, headerValues.get(i));
            }
        }
    }

    @Override
    public String toString() {
        return String.format("read http header decoder with: %s, %s", name, valueDecoder);
    }
}
