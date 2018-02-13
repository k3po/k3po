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
package org.kaazing.k3po.driver.internal.control.handler;

import static java.lang.String.format;
import static org.jboss.netty.util.CharsetUtil.UTF_8;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.PROPERTY_NODE;

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.kaazing.k3po.driver.internal.control.AbortMessage;
import org.kaazing.k3po.driver.internal.control.AwaitMessage;
import org.kaazing.k3po.driver.internal.control.CloseMessage;
import org.kaazing.k3po.driver.internal.control.ControlMessage;
import org.kaazing.k3po.driver.internal.control.ControlMessage.Kind;
import org.kaazing.k3po.driver.internal.control.NotifyMessage;
import org.kaazing.k3po.driver.internal.control.PrepareMessage;
import org.kaazing.k3po.driver.internal.control.StartMessage;
import org.kaazing.k3po.lang.internal.parser.ScriptParseException;
import org.kaazing.k3po.lang.internal.parser.ScriptParserImpl;

public class ControlDecoder extends ReplayingDecoder<ControlDecoder.State> {

    enum State {
        READ_INITIAL, READ_HEADER, READ_CONTENT
    }

    private final int maxInitialLineLength;
    private final int maxHeaderLineLength;
    private final int maxContentLength;

    private ControlMessage message;
    private int contentLength;

    public ControlDecoder() {
        this(1024, 1024, 32768);
    }

    public ControlDecoder(int maxInitialLineLength, int maxHeaderLineLength, int maxContentLength) {
        super(false);

        this.maxInitialLineLength = maxInitialLineLength;
        this.maxHeaderLineLength = maxHeaderLineLength;
        this.maxContentLength = maxContentLength;

        setState(State.READ_INITIAL);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer, State state) throws Exception {

        switch (state) {
        case READ_INITIAL: {
            String initialLine = readLine(buffer, maxInitialLineLength, "Initial line too long");
            if (initialLine != null) {
                message = createMessage(initialLine);
                contentLength = 0;
                checkpoint(State.READ_HEADER);
            }
            return null;
        }
        case READ_HEADER: {
            State nextState = readHeader(buffer);
            checkpoint(nextState);
            if (nextState == State.READ_INITIAL) {
                return message;
            }
            return null;
        }
        case READ_CONTENT: {
            State nextState = readContent(buffer);
            checkpoint(nextState);
            if (nextState != State.READ_CONTENT) {
                return message;
            }
            return null;
        }
        default:
            throw new IllegalArgumentException(String.format("Unrecognized decoder state: %s", state));
        }
    }

    private String readLine(ChannelBuffer buffer, int maxLength, String errorMessage) {
        StringBuilder sb = new StringBuilder();
        byte b = buffer.readByte();
        while (b != (byte) 0x0a && sb.length() < maxLength) {
            sb.append((char) b);
            b = buffer.readByte();
        }

        if (sb.length() >= maxLength)
            throw new IllegalArgumentException(errorMessage);

        return sb.toString();
    }

    private ControlMessage createMessage(String initialLine) {

        ControlMessage.Kind messageKind = ControlMessage.Kind.valueOf(initialLine);
        switch (messageKind) {
        case PREPARE:
            return new PrepareMessage();
        case START:
            return new StartMessage();
        case ABORT:
            return new AbortMessage();
        case NOTIFY:
            return new NotifyMessage();
        case AWAIT:
            return new AwaitMessage();
        case CLOSE:
            return new CloseMessage();
         default:
            throw new IllegalArgumentException(format("Unrecognized message kind: %s", messageKind));
        }
    }

    private State readHeader(ChannelBuffer buffer) {
        String line = readLine(buffer, maxHeaderLineLength, "Header line too long");

        if (line.length() == 0) {
            if (contentLength == 0) {
                return State.READ_INITIAL;
            }

            return Kind.PREPARE.equals(message.getKind()) ? State.READ_CONTENT : State.READ_INITIAL;
        }

        int colonAt = line.indexOf(':');

        // colon not found
        if (colonAt == -1) {
            throw new IllegalArgumentException("Colon not found in header line");
        }

        // colon found
        String headerName = line.substring(0, colonAt);

        String headerValue = line.substring(colonAt + 1);

        // add kind-specific headers
        switch (message.getKind()) {
        case PREPARE:
            PrepareMessage prepareMessage = (PrepareMessage) message;
            switch (headerName) {
            case "version":
                prepareMessage.setVersion(headerValue);
                break;
            case "name":
                prepareMessage.getNames().add(headerValue);
                break;
            case "origin":
                prepareMessage.setOrigin(headerValue);
                break;
            case "content-length":
                contentLength = Integer.parseInt(headerValue);
                if (contentLength > maxContentLength) {
                    throw new IllegalArgumentException("Content too long");
                }
                break;
            }
            break;
        case NOTIFY:
            NotifyMessage notifyMessage = (NotifyMessage) message;
            switch (headerName) {
            case "barrier":
                notifyMessage.setBarrier(headerValue);
                break;
            }
            break;
        case AWAIT:
            AwaitMessage awaitMessage = (AwaitMessage) message;
            switch (headerName) {
            case "barrier":
                awaitMessage.setBarrier(headerValue);
                break;
            }
            break;
        default:
            break;
        }

        return State.READ_HEADER;
    }

    private State readContent(ChannelBuffer buffer) throws ScriptParseException {

        assert contentLength > 0;

        String content = buffer.readBytes(contentLength).toString(UTF_8);

        switch (message.getKind()) {
        case PREPARE:
            PrepareMessage prepareMessage = (PrepareMessage) message;
            ScriptParserImpl parser = new ScriptParserImpl();
            List<String> properties = new ArrayList<>();
            for (String scriptFragment : content.split("\\r?\\n")) {
                // confirm parse-able
                parser.parseWithStrategy(scriptFragment, PROPERTY_NODE);
                properties.add(scriptFragment);
            }
            prepareMessage.setProperties(properties);
            break;
        default:
            throw new IllegalStateException("Unexpected message kind: " + message.getKind());
        }

        return State.READ_INITIAL;
    }
}
