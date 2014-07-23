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

package org.kaazing.robot.driver.behavior.handler;

import java.util.concurrent.ConcurrentHashMap;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import org.kaazing.robot.lang.LocationInfo;

public class LogLastEventHandler extends SimpleChannelHandler {

    // @formatter:off
    private static final ConcurrentHashMap<LocationInfo, String> EVENTS =
            new ConcurrentHashMap<LocationInfo, String>();
    // @formatter:on

    public static String getLastEvent(LocationInfo startLocForStream) {
        return EVENTS.get(startLocForStream);
    }

    private final LocationInfo streamLocStart;
    private boolean            isDone;

    // Another hack so we can close channels ... after completion without them
    // showing up in the log. Again going away soon.
    public void setDone() {
        isDone = true;
    }

    public LogLastEventHandler(LocationInfo location) {
        super();
        streamLocStart = location;
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (!isDone) {
            if (e instanceof ExceptionEvent) {
                // Another really bad hack. We don't care about CLOSED. ... this was racing against EXCEPTION, on connection
                // failures. Again ... this is all going to go away soon.
                setDone();

            }
            EVENTS.put(streamLocStart, eventToString(e));
        }
        super.handleUpstream(ctx, e);
    }

    @Override
    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (!isDone) {
            EVENTS.put(streamLocStart, eventToString(e));
        }
        super.handleDownstream(ctx, e);
    }

    public static void clear() {
        EVENTS.clear();
    }

    private static String eventToString(ChannelEvent e) {

        // Append hex dump if necessary. ... Copied from
        // org.jboss.netty.handler.logging.LoggingHandler.log

        String msg = e.toString();

        // Append hex dump if necessary. ... Copied from
        // org.jboss.netty.handler.logging.LoggingHandler
        if (e instanceof MessageEvent) {
            MessageEvent me = (MessageEvent) e;
            if (me.getMessage() instanceof ChannelBuffer) {
                msg += formatBuffer((ChannelBuffer) me.getMessage());
            }
        }
        return msg;

    }

    private static final String NEWLINE = String.format("%n");

    private static final String[] BYTE2HEX = new String[256];
    private static final String[] HEXPADDING = new String[16];
    private static final String[] BYTEPADDING = new String[16];
    private static final char[] BYTE2CHAR = new char[256];

    // Copied From org.jboss.netty.handler.logging.LoggingHandler
    static {
        int i;

        // Generate the lookup table for byte-to-hex-dump conversion
        for (i = 0; i < 10; i++) {
            StringBuilder buf = new StringBuilder(3);
            buf.append(" 0");
            buf.append(i);
            BYTE2HEX[i] = buf.toString();
        }
        for (; i < 16; i++) {
            StringBuilder buf = new StringBuilder(3);
            buf.append(" 0");
            buf.append((char) ('a' + i - 10));
            BYTE2HEX[i] = buf.toString();
        }
        for (; i < BYTE2HEX.length; i++) {
            StringBuilder buf = new StringBuilder(3);
            buf.append(' ');
            buf.append(Integer.toHexString(i));
            BYTE2HEX[i] = buf.toString();
        }

        // Generate the lookup table for hex dump paddings
        for (i = 0; i < HEXPADDING.length; i++) {
            int padding = HEXPADDING.length - i;
            StringBuilder buf = new StringBuilder(padding * 3);
            for (int j = 0; j < padding; j++) {
                buf.append("   ");
            }
            HEXPADDING[i] = buf.toString();
        }

        // Generate the lookup table for byte dump paddings
        for (i = 0; i < BYTEPADDING.length; i++) {
            int padding = BYTEPADDING.length - i;
            StringBuilder buf = new StringBuilder(padding);
            for (int j = 0; j < padding; j++) {
                buf.append(' ');
            }
            BYTEPADDING[i] = buf.toString();
        }

        // Generate the lookup table for byte-to-char conversion
        for (i = 0; i < BYTE2CHAR.length; i++) {
            if (i <= 0x1f || i >= 0x7f) {
                BYTE2CHAR[i] = '.';
            } else {
                BYTE2CHAR[i] = (char) i;
            }
        }
    }

    // Copied from org.jboss.netty.handler.logging.LoggingHandler
    private static String formatBuffer(ChannelBuffer buf) {
        int length = buf.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder dump = new StringBuilder(rows * 80);

        dump.append(NEWLINE + "         +-------------------------------------------------+" + NEWLINE
                + "         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |" + NEWLINE
                + "+--------+-------------------------------------------------+----------------+");

        final int startIndex = buf.readerIndex();
        final int endIndex = buf.writerIndex();

        int i;
        for (i = startIndex; i < endIndex; i++) {
            int relIdx = i - startIndex;
            int relIdxMod16 = relIdx & 15;
            if (relIdxMod16 == 0) {
                dump.append(NEWLINE);
                dump.append(Long.toHexString(relIdx & 0xFFFFFFFFL | 0x100000000L));
                dump.setCharAt(dump.length() - 9, '|');
                dump.append('|');
            }
            dump.append(BYTE2HEX[buf.getUnsignedByte(i)]);
            if (relIdxMod16 == 15) {
                dump.append(" |");
                for (int j = i - 15; j <= i; j++) {
                    dump.append(BYTE2CHAR[buf.getUnsignedByte(j)]);
                }
                dump.append('|');
            }
        }

        if ((i - startIndex & 15) != 0) {
            int remainder = length & 15;
            dump.append(HEXPADDING[remainder]);
            dump.append(" |");
            for (int j = i - remainder; j < i; j++) {
                dump.append(BYTE2CHAR[buf.getUnsignedByte(j)]);
            }
            dump.append(BYTEPADDING[remainder]);
            dump.append('|');
        }

        dump.append(NEWLINE + "+--------+-------------------------------------------------+----------------+");

        return dump.toString();
    }

}
