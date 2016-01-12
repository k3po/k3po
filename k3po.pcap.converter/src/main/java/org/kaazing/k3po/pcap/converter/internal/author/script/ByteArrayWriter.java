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
package org.kaazing.k3po.pcap.converter.internal.author.script;

import java.nio.charset.Charset;
import java.util.StringTokenizer;

import org.kaazing.k3po.pcap.converter.internal.author.emitter.Emitter;

public class ByteArrayWriter {
    private static final int MAX_HEX_LINE_SIZE = 130;
    public static final int MAX_TEXT_LINE_SIZE = 130;
    private static final int ASSUME_TEXT_AFTER = 5;
    private static final Charset UTF8 = Charset.forName("UTF8");
    
    public enum Type {
        WRITE, READ;
        
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
    
    private enum State {
        BINARY,
        CANDIDATE_TEXT,
        TEXT,
        END;
    }
    
    private State state;

    final String textStart, textEnd, binaryStart, binaryEnd;
    final int maxBinaryBytesPerLine;
    final int maxTextBytesPerLine;
    final Emitter emitter;
    
    int start; // position up to which bytes have been written
    int startCandidateText; // possible start of text bytes (CANDIDATE_TEXT state only)
    int position; // position of the byte which is being processed
    byte[] bytes;
    
    public ByteArrayWriter(Type type, Emitter emitter) {
        this(emitter, type + " \"", "\"", type + " [", "]");
    }
    
    private ByteArrayWriter(Emitter emitter, String textStart, String textEnd, String hexStart, String hexEnd) {
        this.emitter = emitter;
        this.textStart = textStart;
        this.textEnd = textEnd;
        this.binaryStart = hexStart;
        this.binaryEnd = hexEnd;
        // each output binary byte (0xhh) takes 5 characters including a space after
        maxBinaryBytesPerLine = (MAX_HEX_LINE_SIZE - textStart.length() - textEnd.length() + 1) / 5;
        maxTextBytesPerLine = (MAX_TEXT_LINE_SIZE - textStart.length() - textEnd.length());
    }
    
    public void write(byte[] bytes) {
        this.bytes = bytes;
        start = 0;
        state = State.BINARY;
        for (position=0; position < bytes.length; position++) {
            int codePoint = bytes[position] & 0xFF;
            if (shouldTreatAsText(codePoint)) {
                switch(state) {
                case BINARY:
                    transitionBinaryToCandidateText();
                    break;
                case CANDIDATE_TEXT:
                    if (position + 1 - startCandidateText == ASSUME_TEXT_AFTER) {
                        transitionCandidateTextToText();
                    }
                    break;
                default:
                    break;
                }
            }
            else {
                switch(state) {
                case CANDIDATE_TEXT:
                    transitionCandidateTextToBinary();
                    break;
                case TEXT:
                    transitionTextToBinary();
                    break;
                default:
                    break;
                }
            }
        }
        transitionToEnd();
    }
    
    /**
     * Determine if the given data byte should be written out as text or binary (hexadecimal).
     * In order to ensure the generated Robot script can be read and edited in all environments we
     * only treat 7 bit characters (ascii) as text. This method returns true for all printable ascii
     * characters (digits, letters or punctuation), space, linefeed and newline.
     */
    private boolean shouldTreatAsText(int codePoint) {
        return codePoint == '\r' || codePoint == '\n' ||
               (codePoint >= 0x20 // space
                && codePoint <= 0x7E) // tilde, max printable ascii character
                ; 
    }
    
    private void transitionBinaryToCandidateText() {
        startCandidateText = position;
        state = State.CANDIDATE_TEXT;
    }
    
    private void transitionCandidateTextToBinary() {
        state = State.BINARY;
    }
    
    private void transitionCandidateTextToText() {
        writeBinary(start, startCandidateText);
        start = startCandidateText;
        state = State.TEXT;
    }
    
    private void transitionTextToBinary() {
        writeText(start, position);
        start = position;
        state = State.BINARY;
    }
    
    private void transitionToEnd() {
        switch(state) {
        case BINARY:
        case CANDIDATE_TEXT:
            writeBinary(start, position);
            break;
        case TEXT:
            writeText(start, position);
            break;
        default:
            break;
        }
        state = State.END;
    }
    
    private void writeBinary(int fromInclusive, int toExclusive) {
        if (toExclusive == fromInclusive) {
            return;
        }
        int end = Math.min(toExclusive, fromInclusive + maxBinaryBytesPerLine);
        int outputLength = binaryStart.length() + binaryEnd.length()
                + 5 * (end - fromInclusive) - 1;
        StringBuilder sb = new StringBuilder(outputLength);
        sb.append(binaryStart);
        int i;
        for (i=fromInclusive; i < end; i++) {
            sb.append(String.format("0x%02X ", bytes[i]));
        }
        sb.replace(sb.length() - 1, sb.length(),  binaryEnd);
        emitter.add(sb.toString());
        emitter.add("\n");
        if (end < toExclusive) {
            writeBinary(end, toExclusive);
        }
    }
    
    private void writeText(int fromInclusive, int toExclusive) {
        String text = new String(bytes, fromInclusive, toExclusive - fromInclusive, UTF8);
        text = text.replace("\r", "\\r");
        text = text.replace("\n", "\\n\n");
        StringTokenizer tokenizer = new StringTokenizer(text, "\n");
        while (tokenizer.hasMoreTokens()) {
            writeTextLine(tokenizer.nextToken(), 0); 
        }
    }
    
    private void writeTextLine(String line, int from) {
        int end = Math.min(line.length() - from, maxTextBytesPerLine) + from;
        emitter.add(textStart);
        emitter.add(line.substring(from, end));
        emitter.add(textEnd);
        emitter.add("\n");
        if (end < line.length()) {
            writeTextLine(line, end);
        }
    }

}
