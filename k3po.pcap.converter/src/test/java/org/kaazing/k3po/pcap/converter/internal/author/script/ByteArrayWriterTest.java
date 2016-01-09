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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.Emitter;
import org.kaazing.k3po.pcap.converter.internal.author.script.ByteArrayWriter.Type;
import org.kaazing.k3po.pcap.converter.internal.utils.Util;

public class ByteArrayWriterTest {
    private static final Charset UTF8 = Charset.forName("UTF8");

    @Test
    public void shouldFormat5TextBytesAsText() throws Exception {
        Mockery context = new Mockery();
        final Emitter emitter = context.mock(Emitter.class);
        ByteArrayWriter writer = new ByteArrayWriter(Type.READ, emitter);
        
        context.checking(new Expectations() {
            {
                oneOf(emitter).add("read \"");
                oneOf(emitter).add("abcde");
                oneOf(emitter).add("\"");
                oneOf(emitter).add("\n");
            }
        });
        
        writer.write("abcde".getBytes(UTF8));
        context.assertIsSatisfied();
    }
    
    @Test
    public void shouldFormatLessThan5TextBytesAsBinary() throws Exception {
        Mockery context = new Mockery();
        final Emitter emitter = context.mock(Emitter.class);
        ByteArrayWriter writer = new ByteArrayWriter(Type.READ, emitter);
        
        context.checking(new Expectations() {
            {
                oneOf(emitter).add("read [0x61 0x62 0x63 0x64]");
                oneOf(emitter).add("\n");
            }
        });
        
        writer.write("abcd".getBytes(UTF8));
        context.assertIsSatisfied();
    }
    
    @Test
    public void shouldFormatTextWithNewLinesAsMultipleTextLines() throws Exception {
        Mockery context = new Mockery();
        final Emitter emitter = context.mock(Emitter.class);
        ByteArrayWriter writer = new ByteArrayWriter(Type.WRITE, emitter);
        
        context.checking(new Expectations() {
            {
                oneOf(emitter).add("write \"");
                oneOf(emitter).add("GET /jms/;e/ctm?.kn=057899653881306246 HTTP/1.1\\r\\n");
                oneOf(emitter).add("\"");
                oneOf(emitter).add("\n");
                oneOf(emitter).add("write \"");
                oneOf(emitter).add("Accept: */*\\r\\n");
                oneOf(emitter).add("\"");
                oneOf(emitter).add("\n");
                oneOf(emitter).add("write \"");
                oneOf(emitter).add("\\r\\n");
                oneOf(emitter).add("\"");
                oneOf(emitter).add("\n");
            }
        });
        
        StringBuffer sb = new StringBuffer();
        sb.append("GET /jms/;e/ctm?.kn=057899653881306246 HTTP/1.1\r\n");
        sb.append("Accept: */*\r\n");
        sb.append("\r\n");
        
        writer.write(sb.toString().getBytes(UTF8));
        context.assertIsSatisfied();
    }
    
    @Test
    public void shouldFormatBinaryAsSingleLine() throws Exception {
        Mockery context = new Mockery();
        final Emitter emitter = context.mock(Emitter.class);
        ByteArrayWriter writer = new ByteArrayWriter(Type.READ, emitter);
        
        final byte[] bytes = new byte[] {
                0x01, 0x30, 0x30, (byte) 0xFF, 
                (byte) 0x80, 0x0F, 0x0C, 0x0D, 0x01, 0x00, 0x05, 0x00, 0x00, 0x01,
                0x00, 0x02, 0x00, 0x03, 0x00, 0x05, 0x00
        };
        
        context.checking(new Expectations() {
            {
                oneOf(emitter).add("read [" + Util.getHexFromBytes(bytes) + "]");
                oneOf(emitter).add("\n");
            }
        });
        writer.write(bytes);
    }
    
    @Test
    public void shouldFormatMultibyteUTF8CharactersAsBinary() throws Exception {
        Mockery context = new Mockery();
        final Emitter emitter = context.mock(Emitter.class);
        ByteArrayWriter writer = new ByteArrayWriter(Type.READ, emitter);

        final byte[] bytes = new byte[] {
                (byte) 0xBD, (byte) 0xF3, 0x2B, 0x2C, (byte) 0x9F, 0x1B, (byte) 0xB2, 0x0B, (byte) 0xA5,
                0x4A, (byte) 0x90, 0x7A, (byte) 0xA0, 0x20, (byte) 0xD4, (byte) 0xAA, 0x4A, 0x55, 0x11, 
                (byte) 0xD2, 0x1E, 0x4A, 0x0F 
        };
        
        context.checking(new Expectations() {
            {
                oneOf(emitter).add("read [" + Util.getHexFromBytes(bytes) + "]");
                oneOf(emitter).add("\n");
            }
        });

        writer.write(bytes);
    }
    
    @Test
    public void shouldFormatLongBinaryAsMultipleLines() throws Exception {
        Mockery context = new Mockery();
        final Emitter emitter = context.mock(Emitter.class);
        ByteArrayWriter writer = new ByteArrayWriter(Type.READ, emitter);
        
        context.checking(new Expectations() {
            {
                oneOf(emitter).add("read [0x01 0x30 0x30 0xFF "
                        + "0x80 0x0F 0x0C 0x0D 0x01 0x00 0x05 0x00 0x00 0x01 "
                        + "0x00 0x02 0x00 0x03 0x00 0x05 0x00 "
                        + "0x01 0x30 0x30]");
                oneOf(emitter).add("\n");
                oneOf(emitter).add("read [0xFF "
                        + "0x80 0x0F 0x0C 0x0D 0x01 0x00 0x05 0x00 0x00 0x01 "
                        + "0x00 0x02 0x00 0x03 0x00 0x05 0x00]");
                oneOf(emitter).add("\n");
            }
        });

        byte[] bytes = new byte[] {
                0x01, 0x30, 0x30, (byte) 0xFF, 
                (byte) 0x80, 0x0F, 0x0C, 0x0D, 0x01, 0x00, 0x05, 0x00, 0x00, 0x01,
                0x00, 0x02, 0x00, 0x03, 0x00, 0x05, 0x00,
                0x01, 0x30, 0x30, (byte) 0xFF, 
                (byte) 0x80, 0x0F, 0x0C, 0x0D, 0x01, 0x00, 0x05, 0x00, 0x00, 0x01,
                0x00, 0x02, 0x00, 0x03, 0x00, 0x05, 0x00
        };
        writer.write(bytes);
    }
    
    @Test
    public void shouldFormatTextFollowedByBinary() throws Exception {
        Mockery context = new Mockery();
        final Emitter emitter = context.mock(Emitter.class);
        ByteArrayWriter writer = new ByteArrayWriter(Type.WRITE, emitter);
        
        context.checking(new Expectations() {
            {
                oneOf(emitter).add("write \"");
                oneOf(emitter).add("ABCDE");
                oneOf(emitter).add("\"");
                oneOf(emitter).add("\n");
                oneOf(emitter).add("write [0x01 0x30 0x30 0xFF]");
                oneOf(emitter).add("\n");
            }
        });

        byte[] bytes = new byte[] {
                0x41, 0x42, 0x43, 0x44, 0x45, // ABCDE 
                0x01, 0x30, 0x30, (byte) 0xFF
        };
        writer.write(bytes);
    }
    
    @Test
    public void shouldFormatBinaryFollowedByText() throws Exception {
        Mockery context = new Mockery();
        final Emitter emitter = context.mock(Emitter.class);
        ByteArrayWriter writer = new ByteArrayWriter(Type.WRITE, emitter);
        
        context.checking(new Expectations() {
            {
                oneOf(emitter).add("write [0x01 0x30 0x30 0xFF]");
                oneOf(emitter).add("\n");
                oneOf(emitter).add("write \"");
                oneOf(emitter).add("ABCDE");
                oneOf(emitter).add("\"");
                oneOf(emitter).add("\n");
            }
        });

        byte[] bytes = new byte[] {
                0x01, 0x30, 0x30, (byte) 0xFF,
                0x41, 0x42, 0x43, 0x44, 0x45 // ABCDE 
        };
        writer.write(bytes);
    }
    
    @Test
    public void shouldFormatLongPaddingAsMultipleLines() throws Exception {
        Mockery context = new Mockery();
        final Emitter emitter = context.mock(Emitter.class);
        ByteArrayWriter writer = new ByteArrayWriter(Type.READ, emitter);
        
        final byte[] bytes = new byte[] {
                0x01, 0x30, 0x30, (byte) 0xFF, 
                0x01, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                (byte) 0xFF
        };

        context.checking(new Expectations() {
            {
                oneOf(emitter).add("read [0x01 0x30 0x30 0xFF 0x01]");
                oneOf(emitter).add("\n");

                int expectedMaxChars = ByteArrayWriter.MAX_TEXT_LINE_SIZE - 7;
                StringBuffer maxLine = new StringBuffer(expectedMaxChars);
                for (int i=0; i<expectedMaxChars; i++) {
                    maxLine.append("0");
                }
                int textCharsToWrite = bytes.length - 6; 
                int expectedMaxLines = textCharsToWrite / expectedMaxChars;
                int expectedRemainingChars = textCharsToWrite % expectedMaxChars;
                StringBuffer remainderLine = new StringBuffer(expectedRemainingChars);
                for (int i=0; i<expectedRemainingChars; i++) {
                    remainderLine.append("0");
                }
                exactly(expectedMaxLines).of(emitter).add("read \"");
                exactly(expectedMaxLines).of(emitter).add(maxLine.toString());
                exactly(expectedMaxLines).of(emitter).add("\"");
                exactly(expectedMaxLines).of(emitter).add("\n");
                
                oneOf(emitter).add("read \"");
                oneOf(emitter).add(remainderLine.toString());
                oneOf(emitter).add("\"");
                oneOf(emitter).add("\n");

                oneOf(emitter).add("read [0xFF]");
                oneOf(emitter).add("\n");
            }
        });
        
        writer.write(bytes);
    }
    
    // TODO: add more tests, including:
    // - mixed binary and text
    // - mixed binary and short text (LT 5 chars)
    
}