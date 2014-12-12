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

package org.kaazing.k3po.driver.control.handler;

import static org.jboss.netty.util.CharsetUtil.UTF_8;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.kaazing.k3po.driver.control.AbortMessage;
import org.kaazing.k3po.driver.control.ControlMessage;
import org.kaazing.k3po.driver.control.ErrorMessage;
import org.kaazing.k3po.driver.control.FinishedMessage;
import org.kaazing.k3po.driver.control.PrepareMessage;
import org.kaazing.k3po.driver.control.PreparedMessage;
import org.kaazing.k3po.driver.control.StartMessage;

public class ControlDecoder extends ReplayingDecoder<ControlDecoder.State> {

    static enum State {
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
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer, State state)
            throws Exception {

        switch (state) {
        case READ_INITIAL: {
            String initialLine = readLine(buffer, maxInitialLineLength);
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

    private static String readLine(ChannelBuffer buffer, int maxLineLength) {

        int readableBytes = buffer.readableBytes();
        if (readableBytes == 0) {
            return null;
        }

        int readerIndex = buffer.readerIndex();

        int endOfLineAt = buffer.indexOf(readerIndex, Math.min(readableBytes, maxLineLength) + 1, (byte) 0x0a);

        // end-of-line not found
        if (endOfLineAt == -1) {
            if (readableBytes >= maxLineLength) {
                throw new IllegalArgumentException("Initial line too long");
            }
            return null;
        }

        // end-of-line found
        StringBuilder sb = new StringBuilder(endOfLineAt);
        for (int i = readerIndex; i < endOfLineAt; i++) {
            sb.append((char) buffer.readByte());
        }

        byte endOfLine = buffer.readByte();
        assert endOfLine == 0x0a;

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
        default:
            throw new IllegalArgumentException(String.format("Unrecognized message kind: %s", messageKind));
        }
    }

    private State readHeader(ChannelBuffer buffer) {

        int readableBytes = buffer.readableBytes();
        if (readableBytes == 0) {
            return null;
        }

        int endOfLineSearchFrom = buffer.readerIndex();
        // int endOfLineSearchTo = endOfLineSearchFrom + Math.min(readableBytes, maxHeaderLineLength);
        int endOfLineSearchTo = Math.min(readableBytes, maxHeaderLineLength);
        int endOfLineAt = buffer.indexOf(endOfLineSearchFrom, endOfLineSearchTo + 1, (byte) 0x0a);

        // end-of-line not found
        if (endOfLineAt == -1) {
            if (readableBytes >= maxHeaderLineLength) {
                throw new IllegalArgumentException("Header line too long");
            }
            return null;
        }

        if (endOfLineAt == endOfLineSearchFrom) {
            byte endOfLine = buffer.readByte();
            assert endOfLine == 0x0a;

            if (contentLength == 0) {
                return State.READ_INITIAL;
            }

            switch (message.getKind()) {
            case PREPARE:
            case FINISHED:
            case ERROR:
                // content for these message kinds
                return State.READ_CONTENT;
            default:
                return State.READ_INITIAL;
            }
        }

        // end-of-line found
        int colonSearchFrom = buffer.readerIndex();
        // int colonSearchTo = colonSearchFrom + Math.min(readableBytes, endOfLineAt);
        int colonSearchTo = Math.min(readableBytes, endOfLineAt);
        int colonAt = buffer.indexOf(colonSearchFrom, colonSearchTo + 1, (byte) 0x3a);

        // colon not found
        if (colonAt == -1) {
            throw new IllegalArgumentException("Colon not found in header line");
        }

        // colon found
        int headerNameLength = colonAt - colonSearchFrom;
        StringBuilder headerNameBuilder = new StringBuilder(headerNameLength);
        for (int i = 0; i < headerNameLength; i++) {
            headerNameBuilder.append((char) buffer.readByte());
        }
        String headerName = headerNameBuilder.toString();

        byte colon = buffer.readByte();
        assert colon == 0x3a;

        int headerValueLength = endOfLineAt - colonAt - 1;
        StringBuilder headerValueBuilder = new StringBuilder(headerValueLength);
        for (int i = 0; i < headerValueLength; i++) {
            headerValueBuilder.append((char) buffer.readByte());
        }
        String headerValue = headerValueBuilder.toString();

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
            }
            break;
        case PREPARED:
        case ERROR:
        case FINISHED:
            switch (headerName) {
            case "content-length":
                contentLength = Integer.parseInt(headerValue);
                if (contentLength > maxContentLength) {
                    throw new IllegalArgumentException("Content too long");
                }
                break;
            }
        default:
            break;
        }

        byte endOfLine = buffer.readByte();
        assert endOfLine == 0x0a;
        return State.READ_HEADER;
    }

    private State readContent(ChannelBuffer buffer) {

        assert contentLength > 0;

        if (buffer.readableBytes() < contentLength) {
            return State.READ_CONTENT;
        }

        String content = buffer.readBytes(contentLength).toString(UTF_8);
        switch (message.getKind()) {
        case PREPARED:
            PreparedMessage preparedMessage = (PreparedMessage) message;
            preparedMessage.setScript(content);
            break;
        case FINISHED:
            FinishedMessage finishedMessage = (FinishedMessage) message;
            finishedMessage.setScript(content);
            break;
        case ERROR:
            ErrorMessage errorMessage = (ErrorMessage) message;
            errorMessage.setDescription(content);
            break;
        default:
            throw new IllegalStateException("Unexpected message kind: " + message.getKind());
        }

        return State.READ_INITIAL;
    }
}
