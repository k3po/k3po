/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
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

package org.kaazing.k3po.lang.internal.parser;

import static org.junit.Assert.assertEquals;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.ACCEPT;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.CLOSE;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.CLOSED;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.CONNECTED;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.EXACT_BYTES_MATCHER;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.EXPRESSION_MATCHER;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.FIXED_LENGTH_BYTES_MATCHER;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.LITERAL_BYTES_VALUE;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.LITERAL_TEXT_VALUE;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.PROPERTY_NODE;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.READ;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.READ_AWAIT;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.READ_NOTIFY;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.READ_OPTION;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.SCRIPT;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.VARIABLE_LENGTH_BYTES_MATCHER;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.WRITE;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.WRITE_AWAIT;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.WRITE_NOTIFY;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.WRITE_OPTION;
import static org.kaazing.k3po.lang.internal.regex.NamedGroupPattern.compile;
import static org.kaazing.k3po.lang.internal.test.junit.Assert.assertEquals;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import org.junit.Ignore;
import org.junit.Test;
import org.kaazing.k3po.lang.internal.ast.AstAcceptNode;
import org.kaazing.k3po.lang.internal.ast.AstCloseNode;
import org.kaazing.k3po.lang.internal.ast.AstClosedNode;
import org.kaazing.k3po.lang.internal.ast.AstConnectedNode;
import org.kaazing.k3po.lang.internal.ast.AstPropertyNode;
import org.kaazing.k3po.lang.internal.ast.AstReadAwaitNode;
import org.kaazing.k3po.lang.internal.ast.AstReadNotifyNode;
import org.kaazing.k3po.lang.internal.ast.AstReadOptionNode;
import org.kaazing.k3po.lang.internal.ast.AstReadValueNode;
import org.kaazing.k3po.lang.internal.ast.AstScriptNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteAwaitNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteNotifyNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteOptionNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteValueNode;
import org.kaazing.k3po.lang.internal.ast.builder.AstAcceptNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.builder.AstCloseNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.builder.AstClosedNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.builder.AstConnectedNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.builder.AstPropertyNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.builder.AstReadAwaitNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.builder.AstReadNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.builder.AstReadNotifyNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.builder.AstReadOptionNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.builder.AstScriptNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.builder.AstWriteAwaitNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.builder.AstWriteNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.builder.AstWriteNotifyNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.builder.AstWriteOptionNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.matcher.AstByteLengthBytesMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstExactBytesMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstExactTextMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstExpressionMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstFixedLengthBytesMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstIntLengthBytesMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstLongLengthBytesMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstRegexMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstShortLengthBytesMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstValueMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstVariableLengthBytesMatcher;
import org.kaazing.k3po.lang.internal.ast.value.AstExpressionValue;
import org.kaazing.k3po.lang.internal.ast.value.AstLiteralBytesValue;
import org.kaazing.k3po.lang.internal.ast.value.AstLiteralTextValue;
import org.kaazing.k3po.lang.internal.ast.value.AstLocation;
import org.kaazing.k3po.lang.internal.ast.value.AstLocationLiteral;
import org.kaazing.k3po.lang.internal.ast.value.AstValue;
import org.kaazing.k3po.lang.internal.el.ExpressionContext;

public class ScriptParserImplTest {

    @Test
    public void shouldParseLiteralText() throws Exception {

        String scriptFragment = "\"012345 test, here!!\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstLiteralTextValue actual = parser.parseWithStrategy(scriptFragment, LITERAL_TEXT_VALUE);

        AstLiteralTextValue expected = new AstLiteralTextValue("012345 test, here!!");

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseComplexLiteralText() throws Exception {

        String scriptFragment =
                "\"GET / HTTP/1.1\\r\\nHost: localhost:8000\\r\\nUser-Agent: Mozilla/5.0 "
                        + "(Macintosh; Intel Mac OS X 10.6; rv:8.0) Gecko/20100101 Firefox/8.0\\r\\nAccept: text/html,"
                        + "application/xhtml+xml, application/xml;q=0.9,*/*;q=0.8\\r\\n\\r\\n\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstLiteralTextValue actual = parser.parseWithStrategy(scriptFragment, LITERAL_TEXT_VALUE);

        AstLiteralTextValue expected =
                new AstLiteralTextValue("GET / HTTP/1.1\r\nHost: localhost:8000\r\nUser-Agent: Mozilla/5.0 "
                        + "(Macintosh; Intel Mac OS X 10.6; rv:8.0) Gecko/20100101 Firefox/8.0\r\nAccept: text/html,"
                        + "application/xhtml+xml, application/xml;q=0.9,*/*;q=0.8\r\n\r\n");

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseComplexLiteralText2() throws Exception {

        String scriptFragment =
                "\"POST /index.html HTTP/1.1\\r\\nHost: localhost:8000\\r\\nUser-Agent: "
                        + "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:8.0) Gecko/20100101 Firefox/8.0\\r\\n"
                        + "Accept: text/html, application/xhtml+xml, application/xml;q=0.9,*/*;q=0.8\\r\\n"
                        + "Content-Length: 43\\r\\n\\r\\nfirst_name=John&last_name=Doe&action=Submit\\r\\n\\r\\n\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstLiteralTextValue actual = parser.parseWithStrategy(scriptFragment, LITERAL_TEXT_VALUE);

        AstLiteralTextValue expected =
                new AstLiteralTextValue("POST /index.html HTTP/1.1\r\nHost: "
                        + "localhost:8000\r\nUser-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:8.0) "
                        + "Gecko/20100101 Firefox/8.0\r\nAccept: text/html, application/xhtml+xml, "
                        + "application/xml;q=0.9,*/*;q=0.8\r\nContent-Length: 43\r\n\r\nfirst_name=John"
                        + "&last_name=Doe&action=Submit\r\n\r\n");

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseLiteralBytesValue() throws Exception {

        String scriptFragment = "[0x01 0xff 0XFA]";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstLiteralBytesValue actual = parser.parseWithStrategy(scriptFragment, LITERAL_BYTES_VALUE);

        AstLiteralBytesValue expected = new AstLiteralBytesValue(new byte[]{0x01, (byte) 0xff, (byte) 0xfa});

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseShortLiteral() throws Exception {

        String scriptFragment = "0x0005";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstExactBytesMatcher actual = parser.parseWithStrategy(scriptFragment, EXACT_BYTES_MATCHER);

        byte[] arr = {0x00, 0x05};

        AstExactBytesMatcher expected = new AstExactBytesMatcher(arr, parser.getExpressionContext());

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseShortNegativeLiteral() throws Exception {

        String scriptFragment = "0xFFFB";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstExactBytesMatcher actual = parser.parseWithStrategy(scriptFragment, EXACT_BYTES_MATCHER);

        byte[] arr = ByteBuffer.allocate(2).putShort((short) -5).array();

        AstExactBytesMatcher expected = new AstExactBytesMatcher(arr, parser.getExpressionContext());

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseNegativeByteLiteral() throws Exception {

        String scriptFragment = "0xFB";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstExactBytesMatcher actual = parser.parseWithStrategy(scriptFragment, EXACT_BYTES_MATCHER);

        byte[] arr = {(byte) -5};

        AstExactBytesMatcher expected = new AstExactBytesMatcher(arr, parser.getExpressionContext());

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseByteLiteral() throws Exception {

        String scriptFragment = "0x05";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstExactBytesMatcher actual = parser.parseWithStrategy(scriptFragment, EXACT_BYTES_MATCHER);

        byte[] arr = {0x05};

        AstExactBytesMatcher expected = new AstExactBytesMatcher(arr, parser.getExpressionContext());

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseIntLiteral() throws Exception {

        String scriptFragment = "5";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstExactBytesMatcher actual = parser.parseWithStrategy(scriptFragment, EXACT_BYTES_MATCHER);

        byte[] arr = {0x00, 0x00, 0x00, 0x05};

        AstExactBytesMatcher expected = new AstExactBytesMatcher(arr, parser.getExpressionContext());

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseNegativeIntLiteral() throws Exception {

        String scriptFragment = "-5";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstExactBytesMatcher actual = parser.parseWithStrategy(scriptFragment, EXACT_BYTES_MATCHER);

        byte[] arr = ByteBuffer.allocate(4).putInt(-5).array();

        AstExactBytesMatcher expected = new AstExactBytesMatcher(arr, parser.getExpressionContext());

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseLongLiteral() throws Exception {

        String scriptFragment = "5L";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstExactBytesMatcher actual = parser.parseWithStrategy(scriptFragment, EXACT_BYTES_MATCHER);

        byte[] arr = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05};

        AstExactBytesMatcher expected = new AstExactBytesMatcher(arr, parser.getExpressionContext());

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseNegativeLongLiteral() throws Exception {

        String scriptFragment = "-5L";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstExactBytesMatcher actual = parser.parseWithStrategy(scriptFragment, EXACT_BYTES_MATCHER);

        byte[] arr = ByteBuffer.allocate(8).putLong(-5).array();

        AstExactBytesMatcher expected = new AstExactBytesMatcher(arr, parser.getExpressionContext());
        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseFixedLengthBytesMatcher() throws Exception {

        String scriptFragment = "[0..25]";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstFixedLengthBytesMatcher actual = parser.parseWithStrategy(scriptFragment, FIXED_LENGTH_BYTES_MATCHER);

        AstFixedLengthBytesMatcher expected = new AstFixedLengthBytesMatcher(25);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseVariableLengthBytesMatcher() throws Exception {

        String scriptFragment = "[0..${len+2}]";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstVariableLengthBytesMatcher actual = parser.parseWithStrategy(scriptFragment, VARIABLE_LENGTH_BYTES_MATCHER);

        ExpressionFactory factory = parser.getExpressionFactory();
        ExpressionContext context = parser.getExpressionContext();
        ValueExpression length = factory.createValueExpression(context, "${len+2}", Integer.class);
        ExpressionContext environment = parser.getExpressionContext();
        AstVariableLengthBytesMatcher expected = new AstVariableLengthBytesMatcher(length, environment);

        assertEquals(expected, actual);
    }

    // @Ignore("not yet supported")
    @Test
    public void shouldParsePrefixedLengthBytesMatcher() throws Exception {

        // String scriptFragment = "[(...){2+}]";
        //
        // ScriptParserImpl parser = new ScriptParserImpl();
        // AstPrefixedLengthBytesMatcher actual =
        // parser.parseWithStrategy(scriptFragment,
        // PREFIXED_LENGTH_BYTES_MATCHER);
        //
        // AstPrefixedLengthBytesMatcher expected = new
        // AstPrefixedLengthBytesMatcher(2);
        //
        // assertEquals(expected, actual);
    }

    @Test
    public void shouldParseExpressionMatcher() throws Exception {

        String scriptFragment = "${ byteArray }";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstExpressionMatcher actual = parser.parseWithStrategy(scriptFragment, EXPRESSION_MATCHER);

        ExpressionFactory factory = parser.getExpressionFactory();
        ExpressionContext context = parser.getExpressionContext();
        ValueExpression value = factory.createValueExpression(context, "${ byteArray }", byte[].class);
        AstExpressionMatcher expected = new AstExpressionMatcher(value, parser.getExpressionContext());

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseCapturingFixedLengthBytesMatcher() throws Exception {

        String scriptFragment = "([0..1]:capture)";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstFixedLengthBytesMatcher actual = parser.parseWithStrategy(scriptFragment, FIXED_LENGTH_BYTES_MATCHER);

        AstFixedLengthBytesMatcher expected = new AstFixedLengthBytesMatcher(1, "capture", parser.getExpressionContext());

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseCapturingByteLengthMatcher() throws Exception {

        String scriptFragment = "(byte:capture)";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstFixedLengthBytesMatcher actual = parser.parseWithStrategy(scriptFragment, FIXED_LENGTH_BYTES_MATCHER);

        AstByteLengthBytesMatcher expected = new AstByteLengthBytesMatcher("capture", parser.getExpressionContext());

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseCapturingShortLengthMatcher() throws Exception {

        String scriptFragment = "(short:capture)";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstFixedLengthBytesMatcher actual = parser.parseWithStrategy(scriptFragment, FIXED_LENGTH_BYTES_MATCHER);

        AstShortLengthBytesMatcher expected = new AstShortLengthBytesMatcher("capture", parser.getExpressionContext());

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseCapturingIntLengthMatcher() throws Exception {

        String scriptFragment = "(int:capture)";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstFixedLengthBytesMatcher actual = parser.parseWithStrategy(scriptFragment, FIXED_LENGTH_BYTES_MATCHER);

        AstIntLengthBytesMatcher expected = new AstIntLengthBytesMatcher("capture", parser.getExpressionContext());

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseCapturingLongLengthMatcher() throws Exception {

        String scriptFragment = "(long:capture)";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstFixedLengthBytesMatcher actual = parser.parseWithStrategy(scriptFragment, FIXED_LENGTH_BYTES_MATCHER);

        AstLongLengthBytesMatcher expected = new AstLongLengthBytesMatcher("capture", parser.getExpressionContext());

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseCapturingVariableLengthBytesMatcher() throws Exception {

        String scriptFragment = "([0..${len-45}]:capture)";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstVariableLengthBytesMatcher actual = parser.parseWithStrategy(scriptFragment, VARIABLE_LENGTH_BYTES_MATCHER);

        ExpressionFactory factory = parser.getExpressionFactory();
        ExpressionContext context = parser.getExpressionContext();
        ValueExpression length = factory.createValueExpression(context, "${len-45}", Integer.class);
        AstVariableLengthBytesMatcher expected =
                new AstVariableLengthBytesMatcher(length, "capture", parser.getExpressionContext());

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseExactTextWithQuote() throws Exception {
        String scriptFragment = "\"He\\\"llo\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstLiteralTextValue actual = parser.parseWithStrategy(scriptFragment, LITERAL_TEXT_VALUE);

        AstLiteralTextValue expected = new AstLiteralTextValue("He\"llo");

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseEscapedQuoteAndNewline() throws Exception {

        String scriptFragment = "read \"say \\\"hello\\n\\\"\"";
        String expectedValue = "say \"hello\n\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadValueNode actual = parser.parseWithStrategy(scriptFragment, READ);

        AstReadValueNode expected = new AstReadValueNode();
        expected.setMatchers(Arrays.<AstValueMatcher>asList(new AstExactTextMatcher(expectedValue)));
        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseEscapedBrackets() throws Exception {
        String scriptFragment = "read \"say [HAHA]\"";
        String expectedValue = "say [HAHA]";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadValueNode actual = parser.parseWithStrategy(scriptFragment, READ);

        AstReadValueNode expected = new AstReadValueNode();
        expected.setMatchers(Arrays.<AstValueMatcher>asList(new AstExactTextMatcher(expectedValue)));
        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseMultiCapturingByteLengthMatcher() throws Exception {

        String scriptFragment = "read (byte:capture) (byte:capture2)";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadValueNode actual = parser.parseWithStrategy(scriptFragment, READ);

        AstReadValueNode expected = new AstReadValueNode();
        expected.setMatchers(Arrays.<AstValueMatcher>asList(
                new AstByteLengthBytesMatcher("capture", parser.getExpressionContext()), new AstByteLengthBytesMatcher(
                        "capture2", parser.getExpressionContext())));
        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseMultiCapturingShortLengthMatcher() throws Exception {

        String scriptFragment = "read (short:capture) (short:capture2)";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadValueNode actual = parser.parseWithStrategy(scriptFragment, READ);

        AstReadValueNode expected = new AstReadValueNode();
        expected.setMatchers(Arrays.<AstValueMatcher>asList(
                new AstShortLengthBytesMatcher("capture", parser.getExpressionContext()), new AstShortLengthBytesMatcher(
                        "capture2", parser.getExpressionContext())));

        assertEquals(expected, actual);
        assertEquals(2, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseMultiCapturingIntLengthMatcher() throws Exception {

        String scriptFragment = "read (int:capture) (int:capture2)";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadValueNode actual = parser.parseWithStrategy(scriptFragment, READ);

        AstReadValueNode expected = new AstReadValueNode();
        expected.setMatchers(Arrays.<AstValueMatcher>asList(
                new AstIntLengthBytesMatcher("capture", parser.getExpressionContext()), new AstIntLengthBytesMatcher("capture2",
                        parser.getExpressionContext())));
        assertEquals(expected, actual);
        assertEquals(2, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseMultiCapturingLongLengthMatcher() throws Exception {

        String scriptFragment = "read (long:capture) (long:capture2)";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadValueNode actual = parser.parseWithStrategy(scriptFragment, READ);

        AstReadValueNode expected = new AstReadValueNode();
        expected.setMatchers(Arrays.<AstValueMatcher>asList(
                new AstLongLengthBytesMatcher("capture", parser.getExpressionContext()), new AstLongLengthBytesMatcher(
                        "capture2", parser.getExpressionContext())));
        assertEquals(expected, actual);
        assertEquals(2, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseMultiExactText() throws Exception {
        String scriptFragment = "read \"Hello\" \"World\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadValueNode actual = parser.parseWithStrategy(scriptFragment, READ);

        AstReadValueNode expected = new AstReadValueNode();
        expected.setMatchers(Arrays.<AstValueMatcher>asList(new AstExactTextMatcher("Hello"), new AstExactTextMatcher("World")));
        assertEquals(expected, actual);
        assertEquals(2, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseMultiExactBytes() throws Exception {

        String scriptFragment = "read [0x01 0xff 0XFA] [0x00 0xF0 0x03 0x05 0x08 0x04]";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadValueNode actual = parser.parseWithStrategy(scriptFragment, READ);

        AstReadValueNode expected = new AstReadValueNode();
        expected.setMatchers(Arrays.<AstValueMatcher>asList(new AstExactBytesMatcher(new byte[]{0x01, (byte) 0xff, (byte) 0xfa},
                parser.getExpressionContext()), new AstExactBytesMatcher(new byte[]{0x00, (byte) 0xf0, (byte) 0x03, (byte) 0x05,
                (byte) 0x08, (byte) 0x04}, parser.getExpressionContext())));

        assertEquals(expected, actual);
        assertEquals(2, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseMultiExactBytesWithMultipleSpaces() throws Exception {

        String scriptFragment = "read [0x01  0xff    0XFA]  [0x000xF0 0x03 0x05 0x080x04]";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadValueNode actual = parser.parseWithStrategy(scriptFragment, READ);

        AstReadValueNode expected = new AstReadValueNode();
        expected.setMatchers(Arrays.<AstValueMatcher>asList(new AstExactBytesMatcher(new byte[]{0x01, (byte) 0xff, (byte) 0xfa},
                parser.getExpressionContext()), new AstExactBytesMatcher(new byte[]{0x00, (byte) 0xf0, (byte) 0x03, (byte) 0x05,
                (byte) 0x08, (byte) 0x04}, parser.getExpressionContext())));

        assertEquals(expected, actual);
        assertEquals(2, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseMultiRegex() throws Exception {
        String scriptFragment = "read /.*\\n/ /.+\\r/";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadValueNode actual = parser.parseWithStrategy(scriptFragment, READ);

        AstReadValueNode expected = new AstReadValueNode();
        ExpressionContext environment = parser.getExpressionContext();
        expected.setMatchers(Arrays.<AstValueMatcher>asList(new AstRegexMatcher(compile(".*\\n"), environment),
                new AstRegexMatcher(compile(".+\\r"), environment)));

        assertEquals(expected, actual);
        assertEquals(2, actual.getRegionInfo().children.size());
        assertEquals(2, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseMultExpression() throws Exception {
        String scriptFragment = "read ${var} ${var2}";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadValueNode actual = parser.parseWithStrategy(scriptFragment, READ);

        ExpressionFactory factory = parser.getExpressionFactory();
        ExpressionContext context = parser.getExpressionContext();
        ValueExpression value = factory.createValueExpression(context, "${var}", byte[].class);
        ValueExpression value2 = factory.createValueExpression(context, "${var2}", byte[].class);

        AstReadValueNode expected = new AstReadValueNode();
        ExpressionContext environment = parser.getExpressionContext();
        expected.setMatchers(Arrays.<AstValueMatcher>asList(new AstExpressionMatcher(value, environment),
                new AstExpressionMatcher(value2, environment)));

        assertEquals(expected, actual);
        assertEquals(2, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseMultiFixedLengthBytes() throws Exception {
        String scriptFragment = "read [0..1024] [0..4096]";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadValueNode actual = parser.parseWithStrategy(scriptFragment, READ);

        AstReadValueNode expected = new AstReadValueNode();
        expected.setMatchers(Arrays.<AstValueMatcher>asList(new AstFixedLengthBytesMatcher(1024),
                new AstFixedLengthBytesMatcher(4096)));
        assertEquals(expected, actual);
        assertEquals(2, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseMultiFixedLengthBytesWithCaptures() throws Exception {
        String scriptFragment = "read [0..1024] ([0..64]:var1) [0..4096] ([0..64]:var2)";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadValueNode actual = parser.parseWithStrategy(scriptFragment, READ);

        AstReadValueNode expected = new AstReadValueNode();
        ExpressionContext environment = parser.getExpressionContext();
        expected.setMatchers(Arrays.<AstValueMatcher>asList(new AstFixedLengthBytesMatcher(1024),
                new AstFixedLengthBytesMatcher(64, "var1", environment), new AstFixedLengthBytesMatcher(4096),
                new AstFixedLengthBytesMatcher(64, "var2", environment)));

        assertEquals(expected, actual);
        assertEquals(4, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseMultVariableLengthBytes() throws Exception {
        String scriptFragment = "read [0..${len1}] [0..${len2}]";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadValueNode actual = parser.parseWithStrategy(scriptFragment, READ);

        ExpressionFactory factory = parser.getExpressionFactory();
        ExpressionContext context = parser.getExpressionContext();
        ValueExpression value = factory.createValueExpression(context, "${len1}", Integer.class);
        ValueExpression value2 = factory.createValueExpression(context, "${len2}", Integer.class);

        AstReadValueNode expected = new AstReadValueNode();
        ExpressionContext environment = parser.getExpressionContext();
        expected.setMatchers(Arrays.<AstValueMatcher>asList(new AstVariableLengthBytesMatcher(value, environment),
                new AstVariableLengthBytesMatcher(value2, environment)));

        assertEquals(expected, actual);
        assertEquals(2, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseMultVariableLengthBytesWithCapture() throws Exception {
        String scriptFragment = "read ([0..${len1}]:var1) ([0..${len2}]:var2)";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadValueNode actual = parser.parseWithStrategy(scriptFragment, READ);

        ExpressionFactory factory = parser.getExpressionFactory();
        ExpressionContext context = parser.getExpressionContext();
        ValueExpression value = factory.createValueExpression(context, "${len1}", Integer.class);
        ValueExpression value2 = factory.createValueExpression(context, "${len2}", Integer.class);

        AstReadValueNode expected = new AstReadValueNode();
        ExpressionContext environment = parser.getExpressionContext();
        expected.setMatchers(Arrays.<AstValueMatcher>asList(new AstVariableLengthBytesMatcher(value, "var1", environment),
                new AstVariableLengthBytesMatcher(value2, "var2", environment)));

        assertEquals(expected, actual);
        assertEquals(2, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseMultLiteralTextValue() throws Exception {
        String scriptFragment = "write \"Hello\" \"World\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteValueNode actual = parser.parseWithStrategy(scriptFragment, WRITE);

        AstWriteValueNode expected = new AstWriteValueNode();
        expected.setValues(Arrays.<AstValue>asList(new AstLiteralTextValue("Hello"), new AstLiteralTextValue("World")));
        assertEquals(expected, actual);
        assertEquals(2, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseMultLiteralBytesValue() throws Exception {
        String scriptFragment = "write [0x01 0x02] [0x03 0x04]";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteValueNode actual = parser.parseWithStrategy(scriptFragment, WRITE);

        AstWriteValueNode expected = new AstWriteValueNode();
        expected.setValues(Arrays.<AstValue>asList(new AstLiteralBytesValue(new byte[]{(byte) 0x01, (byte) 0x02}),
                new AstLiteralBytesValue(new byte[]{(byte) 0x03, (byte) 0x04})));
        assertEquals(expected, actual);
        assertEquals(2, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseMultExpressionValue() throws Exception {
        String scriptFragment = "write ${var1} ${var2}";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteValueNode actual = parser.parseWithStrategy(scriptFragment, WRITE);

        ExpressionFactory factory = parser.getExpressionFactory();
        ExpressionContext context = parser.getExpressionContext();
        ValueExpression value1 = factory.createValueExpression(context, "${var1}", byte[].class);
        ValueExpression value2 = factory.createValueExpression(context, "${var2}", byte[].class);

        AstWriteValueNode expected = new AstWriteValueNode();
        expected.setValues(Arrays.<AstValue>asList(new AstExpressionValue(value1, parser.getExpressionContext()),
                new AstExpressionValue(value2, parser.getExpressionContext())));

        assertEquals(expected, actual);
        assertEquals(2, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseMultAllValue() throws Exception {
        String scriptFragment = "write \"Hello\" [0x01 0x02] ${var1}";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteValueNode actual = parser.parseWithStrategy(scriptFragment, WRITE);

        ExpressionFactory factory = parser.getExpressionFactory();
        ExpressionContext context = parser.getExpressionContext();
        ValueExpression value1 = factory.createValueExpression(context, "${var1}", byte[].class);

        AstWriteValueNode expected = new AstWriteValueNode();
        expected.setValues(Arrays.<AstValue>asList(new AstLiteralTextValue("Hello"), new AstLiteralBytesValue(new byte[]{
                (byte) 0x01, (byte) 0x02}), new AstExpressionValue(value1, parser.getExpressionContext())));

        assertEquals(expected, actual);
        assertEquals(3, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseWriteMultAllValue() throws Exception {
        String scriptFragment = "write \"Hello\" [0x01 0x02] ${var1}";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteValueNode actual = parser.parseWithStrategy(scriptFragment, WRITE);

        ExpressionFactory factory = parser.getExpressionFactory();
        ExpressionContext context = parser.getExpressionContext();

        AstWriteValueNode expected =
                new AstWriteNodeBuilder()
                        .addExactText("Hello")
                        .addExactBytes(new byte[]{0x01, (byte) 0x02})
                        .addExpression(factory.createValueExpression(context, "${var1}", byte[].class),
                                parser.getExpressionContext()).done();

        assertEquals(expected, actual);
        assertEquals(3, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseAccept() throws Exception {

        String scriptFragment = "accept http://localhost:8001/echo";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstAcceptNode actual = parser.parseWithStrategy(scriptFragment, ACCEPT);

        AstAcceptNode expected = new AstAcceptNodeBuilder().setLocation(
                new AstLocationLiteral(URI.create("http://localhost:8001/echo"))).done();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseAcceptWithQueryString() throws Exception {

        String scriptFragment = "accept http://localhost:8001/echo?param=value";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstAcceptNode actual = parser.parseWithStrategy(scriptFragment, ACCEPT);

        AstAcceptNode expected = new AstAcceptNodeBuilder().setLocation(
                new AstLocationLiteral(URI.create("http://localhost:8001/echo?param=value"))).done();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseAcceptWithQueryStringAndPathSegmentParameter() throws Exception {

        String scriptFragment = "accept http://localhost:8001/echo/;e/ct?param=value";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstAcceptNode actual = parser.parseWithStrategy(scriptFragment, ACCEPT);

        AstAcceptNode expected = new AstAcceptNodeBuilder().setLocation(
                new AstLocationLiteral(URI.create("http://localhost:8001/echo/;e/ct?param=value"))).done();

        assertEquals(expected, actual);
    }

    @Test(
        expected = ScriptParseException.class)
    public void shouldNotParseAcceptedWithoutBehavior() throws Exception {

        String script = "accepted";

        ScriptParserImpl parser = new ScriptParserImpl();
        parser.parseWithStrategy(script, SCRIPT);
    }

    @Test
    public void shouldParseClose() throws Exception {

        String scriptFragment = "close";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstCloseNode actual = parser.parseWithStrategy(scriptFragment, CLOSE);

        AstCloseNode expected = new AstCloseNodeBuilder().done();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseClosed() throws Exception {

        String scriptFragment = "closed";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstClosedNode actual = parser.parseWithStrategy(scriptFragment, CLOSED);

        AstClosedNode expected = new AstClosedNodeBuilder().done();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseConnected() throws Exception {

        String scriptFragment = "connected";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstConnectedNode actual = parser.parseWithStrategy(scriptFragment, CONNECTED);

        AstConnectedNode expected = new AstConnectedNodeBuilder().done();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseReadLiteralText() throws Exception {

        String scriptFragment = "read \"Hello\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadValueNode actual = parser.parseWithStrategy(scriptFragment, READ);

        AstReadValueNode expected = new AstReadNodeBuilder().addExactText("Hello").done();

        assertEquals(expected, actual);
        assertEquals(1, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseReadExactByte() throws Exception {

        String scriptFragment = "read 0x05";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadValueNode actual = parser.parseWithStrategy(scriptFragment, READ);

        AstReadValueNode expected =
                new AstReadNodeBuilder().addExactBytes(new byte[]{0x05}, parser.getExpressionContext()).done();

        assertEquals(expected, actual);
        assertEquals(1, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseReadExactShort() throws Exception {

        String scriptFragment = "read 0x0005";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadValueNode actual = parser.parseWithStrategy(scriptFragment, READ);

        AstReadValueNode expected =
                new AstReadNodeBuilder().addExactBytes(new byte[]{0x00, 0x05}, parser.getExpressionContext()).done();

        assertEquals(expected, actual);
        assertEquals(1, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseReadExactInt() throws Exception {

        String scriptFragment = "read 5";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadValueNode actual = parser.parseWithStrategy(scriptFragment, READ);

        AstReadValueNode expected = new AstReadNodeBuilder()

        .addExactBytes(new byte[]{0x00, 0x00, 0x00, 0x05}, parser.getExpressionContext()).done();

        assertEquals(expected, actual);
        assertEquals(1, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseReadExactLong() throws Exception {

        String scriptFragment = "read 5L";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadValueNode actual = parser.parseWithStrategy(scriptFragment, READ);

        AstReadValueNode expected = new AstReadNodeBuilder()

        .addExactBytes(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05}, parser.getExpressionContext()).done();

        assertEquals(expected, actual);
        assertEquals(1, actual.getRegionInfo().children.size());
    }

    @Test
    // see http://jira.kaazing.wan/browse/NR-12
    public void shouldParseReadLiteralTextWithMuchPunctuation() throws Exception {

        String scriptFragment =
                "read \"HTTP/1.1 404 Not Found\\r\\nServer: Kaazing Gateway\\r\\n"
                        + "Date: Thu, 03 May 2012 20:41:24 GMT\\r\\n\\r\\nContent-Type: text/html\\r\\n"
                        + "Content-length: 61 <html><head></head><body><h1>404 Not Found</h1></body></html>\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadValueNode actual = parser.parseWithStrategy(scriptFragment, READ);

        AstReadValueNode expected =
                new AstReadNodeBuilder()

                .addExactText(
                        "HTTP/1.1 404 Not Found\r\nServer: Kaazing Gateway\r\n"
                                + "Date: Thu, 03 May 2012 20:41:24 GMT\r\n\r\nContent-Type: text/html\r\n"
                                + "Content-length: 61 <html><head></head><body><h1>404 Not Found</h1></body></html>").done();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseReadLiteralBytes() throws Exception {

        String scriptFragment = "read [0x01 0x02 0xFF]";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadValueNode actual = parser.parseWithStrategy(scriptFragment, READ);

        AstReadValueNode expected = new AstReadNodeBuilder()

        .addExactBytes(new byte[]{0x01, 0x02, (byte) 0xff}, parser.getExpressionContext()).done();

        assertEquals(expected, actual);
        assertEquals(1, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseReadExpression() throws Exception {

        String scriptFragment = "read ${hello}";

        ScriptParserImpl parser = new ScriptParserImpl();
        ExpressionFactory factory = parser.getExpressionFactory();
        ExpressionContext context = parser.getExpressionContext();

        AstReadValueNode actual = parser.parseWithStrategy(scriptFragment, READ);

        AstReadValueNode expected = new AstReadNodeBuilder()

        .addExpression(factory.createValueExpression(context, "${hello}", byte[].class), parser.getExpressionContext()).done();

        assertEquals(expected, actual);
        assertEquals(1, actual.getRegionInfo().children.size());
    }

    @Test
    // see http://jira.kaazing.wan/browse/NR-10
    public void shouldParseWriteLiteralTextWithSlash() throws Exception {

        String scriptFragment = "write \"GET /index.html blah\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteValueNode actual = parser.parseWithStrategy(scriptFragment, WRITE);

        AstWriteValueNode expected = new AstWriteNodeBuilder().addExactText("GET /index.html blah").done();

        assertEquals(expected, actual);
        assertEquals(1, actual.getRegionInfo().children.size());
    }

    @Test
    // see http://jira.kaazing.wan/browse/NR-10
    public void shouldParseWriteLiteralTextWithAsterisk() throws Exception {

        String scriptFragment = "write \"GET /index.html blah*\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteValueNode actual = parser.parseWithStrategy(scriptFragment, WRITE);

        AstWriteValueNode expected = new AstWriteNodeBuilder().addExactText("GET /index.html blah*").done();

        assertEquals(expected, actual);
        assertEquals(1, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseWriteLiteralTextWithDollarSign() throws Exception {

        String scriptFragment = "write \"GET $foo\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteValueNode actual = parser.parseWithStrategy(scriptFragment, WRITE);

        AstWriteValueNode expected = new AstWriteNodeBuilder().addExactText("GET $foo").done();

        assertEquals(expected, actual);
        assertEquals(1, actual.getRegionInfo().children.size());
    }

    @Test
    // see http://jira.kaazing.wan/browse/NR-12
    public void shouldParseWriteLiteralTextWithMuchPunctuation() throws Exception {

        String scriptFragment =
                "write \"GET / HTTP/1.1\\r\\nHost: localhost:8000\\r\\n"
                        + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:8.0) Gecko/20100101 Firefox/8.0\\r\\n"
                        + "Accept: text/html, application/xhtml+xml, application/xml;q=0.9,*/*;q=0.8\\r\\n\\r\\n\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteValueNode actual = parser.parseWithStrategy(scriptFragment, WRITE);

        AstWriteValueNode expected =
                new AstWriteNodeBuilder().addExactText(
                        "GET / HTTP/1.1\r\nHost: localhost:8000\r\n"
                                + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:8.0) "
                                + "Gecko/20100101 Firefox/8.0\r\n"
                                + "Accept: text/html, application/xhtml+xml, application/xml;q=0.9,*/*;q=0.8\r\n\r\n").done();

        assertEquals(expected, actual);
    }

    @Test
    // see http://jira.kaazing.wan/NR-34
    public void shouldParseWriteLiteralTextWithSingleQuote() throws Exception {

        String scriptFragment = "write \"DON'T WORK\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteValueNode actual = parser.parseWithStrategy(scriptFragment, WRITE);

        AstWriteValueNode expected = new AstWriteNodeBuilder().addExactText("DON'T WORK").done();

        assertEquals(expected, actual);
        assertEquals(1, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseWriteLongLiteralText() throws Exception {

        StringBuilder longLiteralTextBuilder = new StringBuilder();
        longLiteralTextBuilder.append("POST /index.html HTTP/1.1\\r\\nHost: localhost:8000\\r\\n"
                + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:8.0) Gecko/20100101 Firefox/8.0\\r\\n"
                + "Accept: text/html, application/xhtml+xml, application/xml;q=0.9,*/*;q=0.8\\r\\n"
                + "Content-Length: 99860\\r\\n\\r\\nfirst_name=Johnlast_nameDoeactionSubmitLoremipsumdolorsitametconsectetur");
        for (int i = 0; i < 3030; i++) {
            longLiteralTextBuilder.append("Loremipsumdolorsitametconsectetur");
        }
        String longLiteralText = longLiteralTextBuilder.toString();

        String scriptFragment = String.format("write \"%s\"", longLiteralText);

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteValueNode actual = parser.parseWithStrategy(scriptFragment, WRITE);

        longLiteralTextBuilder = new StringBuilder();
        longLiteralTextBuilder.append("POST /index.html HTTP/1.1\r\nHost: localhost:8000\r\n"
                + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:8.0) Gecko/20100101 Firefox/8.0\r\n"
                + "Accept: text/html, application/xhtml+xml, application/xml;q=0.9,*/*;q=0.8\r\n"
                + "Content-Length: 99860\r\n\r\nfirst_name=Johnlast_nameDoeactionSubmitLoremipsumdolorsitametconsectetur");
        for (int i = 0; i < 3030; i++) {
            longLiteralTextBuilder.append("Loremipsumdolorsitametconsectetur");
        }
        longLiteralText = longLiteralTextBuilder.toString();

        AstWriteValueNode expected = new AstWriteNodeBuilder().addExactText(longLiteralText).done();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseReadAwaitBarrier() throws Exception {

        String scriptFragment = "read await BARRIER";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadAwaitNode actual = parser.parseWithStrategy(scriptFragment, READ_AWAIT);

        AstReadAwaitNode expected = new AstReadAwaitNodeBuilder().setBarrierName("BARRIER").done();

        assertEquals(expected, actual);
        assertEquals(0, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseReadNotifyBarrier() throws Exception {

        String scriptFragment = "read notify BARRIER";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadNotifyNode actual = parser.parseWithStrategy(scriptFragment, READ_NOTIFY);

        AstReadNotifyNode expected = new AstReadNotifyNodeBuilder().setBarrierName("BARRIER").done();

        assertEquals(expected, actual);
        assertEquals(0, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseWriteAwaitBarrier() throws Exception {

        String scriptFragment = "write await BARRIER";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteAwaitNode actual = parser.parseWithStrategy(scriptFragment, WRITE_AWAIT);

        AstWriteAwaitNode expected = new AstWriteAwaitNodeBuilder().setBarrierName("BARRIER").done();

        assertEquals(expected, actual);
        assertEquals(0, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseWriteNotifyBarrier() throws Exception {

        String scriptFragment = "write notify BARRIER";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteNotifyNode actual = parser.parseWithStrategy(scriptFragment, WRITE_NOTIFY);

        AstWriteNotifyNode expected = new AstWriteNotifyNodeBuilder().setBarrierName("BARRIER").done();

        assertEquals(expected, actual);
        assertEquals(0, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseConnectScript() throws Exception {

        String script =
                "# tcp.client.connect-then-close\n" + "connect http://localhost:8080/path?p1=v1&p2=v2\n" + "connected\n"
                        + "close\n" + "closed\n";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstScriptNode actual = parser.parseWithStrategy(script, SCRIPT);
        AstLocation location = new AstLocationLiteral(URI.create("http://localhost:8080/path?p1=v1&p2=v2"));
        AstScriptNode expected =
                new AstScriptNodeBuilder().addConnectStream().setLocation(location)
                        .addConnectedEvent().done().addCloseCommand().done().addClosedEvent().done().done().done();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseConnectWhenScript() throws Exception {

        // @formatter:off
        String script =
                "# tcp.client.connect-then-close\n" +
                "connect await BARRIER\r\n" +
                "connect http://localhost:8080/path?p1=v1&p2=v2\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on

        ScriptParserImpl parser = new ScriptParserImpl();
        AstScriptNode actual = parser.parseWithStrategy(script, SCRIPT);
        AstLocation location = new AstLocationLiteral(URI.create("http://localhost:8080/path?p1=v1&p2=v2"));

        // @formatter:off
         AstScriptNode expected = new AstScriptNodeBuilder()
                 .addConnectStream()
                     .setLocation(location)
                     .setBarrier("BARRIER")
                     .addConnectedEvent()
                     .done()
                     .addCloseCommand()
                     .done()
                     .addClosedEvent()
                     .done()
                 .done()
             .done();

        assertEquals(expected, actual);
        // @formatter:on
    }

    @Test
    public void shouldParseConnectScriptWithComments() throws Exception {

        String script =
                "# tcp.client.connect-then-close\n" + "connect tcp://localhost:7788 # Comment 1\n" + "\t\t # Comment 2\n"
                        + "connected\n" + "close\n" + "closed\n";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstScriptNode actual = parser.parseWithStrategy(script, SCRIPT);
        AstLocation location = new AstLocationLiteral(URI.create("tcp://localhost:7788"));

        AstScriptNode expected =
                new AstScriptNodeBuilder().addConnectStream().setLocation(location)
                        .addConnectedEvent().done().addCloseCommand().done().addClosedEvent().done().done().done();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseConnectScriptWithOptions() throws Exception {

        String script =
                "connect http://localhost:7788\n" +
                "        option transport tcp://localhost:8888\n" +
                "connected\n" +
                "close\n" +
                "closed\n";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstScriptNode actual = parser.parseWithStrategy(script, SCRIPT);
        AstLocation location = new AstLocationLiteral(URI.create("http://localhost:7788"));
        AstLocation transport = new AstLocationLiteral(URI.create("tcp://localhost:8888"));

        AstScriptNode expected = new AstScriptNodeBuilder()
                .addConnectStream()
                    .setLocation(location)
                    .setTransport(transport)
                    .addConnectedEvent().done()
                    .addCloseCommand().done()
                    .addClosedEvent().done()
                .done()
        .done();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseAcceptScript() throws Exception {

        String script =
                "# tcp.client.accept-then-close\n" + "accept tcp://localhost:7788\n" + "accepted\n" + "connected\n" + "close\n"
                        + "closed\n";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstScriptNode actual = parser.parseWithStrategy(script, SCRIPT);

        AstScriptNode expected = new AstScriptNodeBuilder().addAcceptStream()
                .setLocation(new AstLocationLiteral(URI.create("tcp://localhost:7788"))).done().addAcceptedStream()
                .addConnectedEvent().done().addCloseCommand().done().addClosedEvent().done().done().done();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseAcceptScriptWithOptions() throws Exception {

        String script =
                "# tcp.client.accept-then-close\n" +
                "accept http://localhost:7788\n" +
                "       option transport tcp://localhost:8000\n" +
                "accepted\n" +
                "connected\n" +
                "close\n" +
                "closed\n";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstScriptNode actual = parser.parseWithStrategy(script, SCRIPT);
        AstLocation location = new AstLocationLiteral(URI.create("http://localhost:7788"));
        AstLocation transport = new AstLocationLiteral(URI.create("tcp://localhost:8000"));

        AstScriptNode expected = new AstScriptNodeBuilder()
                .addAcceptStream()
                    .setLocation(location)
                    .setTransport(transport)
                .done()
                .addAcceptedStream()
                    .addConnectedEvent().done()
                    .addCloseCommand().done()
                    .addClosedEvent().done()
                .done()
        .done();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseMultiConnectScript() throws Exception {

        String script =
                "# tcp.client.echo-multi-conn.upstream\n" + "connect tcp://localhost:8785\n" + "connected\n"
                        + "write \"Hello, world!\"\n" + "write notify BARRIER\n" + "close\n" + "closed\n"
                        + "# tcp.client.echo-multi-conn.downstream\n" + "connect tcp://localhost:8783\n" + "connected\n"
                        + "read await BARRIER\n" + "read \"Hello, world!\"\n" + "close\n" + "closed\n";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstScriptNode actual = parser.parseWithStrategy(script, SCRIPT);
        AstLocation location8785 = new AstLocationLiteral(URI.create("tcp://localhost:8785"));
        AstLocation location8783 = new AstLocationLiteral(URI.create("tcp://localhost:8783"));

        AstScriptNode expected =
                new AstScriptNodeBuilder().addConnectStream().setLocation(location8785)
                        .addConnectedEvent().done().addWriteCommand().addExactText("Hello, world!").done()
                        .addWriteNotifyBarrier().setBarrierName("BARRIER").done().addCloseCommand().done().addClosedEvent()
                        .done().done().addConnectStream().setLocation(location8783).addConnectedEvent()
                        .done().addReadAwaitBarrier().setBarrierName("BARRIER").done().addReadEvent()
                        .addExactText("Hello, world!").done().addCloseCommand().done().addClosedEvent().done().done().done();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseMultiAcceptScript() throws Exception {

        String script =
                "# tcp.server.echo-multi-conn.upstream\n" + "accept tcp://localhost:8783\n" + "accepted\n" + "connected\n"
                        + "read await BARRIER\n" + "read \"Hello, world!\"\n" + "close\n" + "closed\n"
                        + "# tcp.server.echo-multi-conn.downstream\n" + "accept tcp://localhost:8785\n" + "accepted\n"
                        + "connected\n" + "write \"Hello, world!\"\n" + "write notify BARRIER\n" + "close\n" + "closed\n";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstScriptNode actual = parser.parseWithStrategy(script, SCRIPT);

        AstScriptNode expected = new AstScriptNodeBuilder().addAcceptStream()
                .setLocation(new AstLocationLiteral(URI.create("tcp://localhost:8783"))).done().addAcceptedStream()
                .addConnectedEvent().done().addReadAwaitBarrier().setBarrierName("BARRIER").done().addReadEvent()
                .addExactText("Hello, world!").done().addCloseCommand().done().addClosedEvent().done().done()
                .addAcceptStream().setLocation(new AstLocationLiteral(URI.create("tcp://localhost:8785"))).done()
                .addAcceptedStream().addConnectedEvent().done().addWriteCommand().addExactText("Hello, world!").done()
                .addWriteNotifyBarrier().setBarrierName("BARRIER").done().addCloseCommand().done().addClosedEvent()
                .done().done().done();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseAcceptAndConnectScript() throws Exception {

        String script =
                "# tcp.server.accept-then-close\n" + "accept tcp://localhost:7788\n" + "accepted\n" + "connected\n" + "closed\n"
                        + "# tcp.client.connect-then-close\n" + "connect tcp://localhost:7788\n" + "connected\n" + "close\n"
                        + "closed\n";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstLocation location7788 = new AstLocationLiteral(URI.create("tcp://localhost:7788"));
        AstScriptNode actual = parser.parseWithStrategy(script, SCRIPT);
        AstScriptNode expected = new AstScriptNodeBuilder().addAcceptStream()
                .setLocation(new AstLocationLiteral(URI.create("tcp://localhost:7788"))).done().addAcceptedStream()
                .addConnectedEvent().done().addClosedEvent().done().done().addConnectStream().setLocation(location7788)
                .addConnectedEvent().done().addCloseCommand().done().addClosedEvent().done().done().done();
        assertEquals(expected, actual);
    }

    @Test
    // see http://jira.kaazing.wan/NR-35
    public void shouldParseNonClosingConnectScript() throws Exception {

        String script =
                "# tcp.client.non-closing\n" + "connect tcp://localhost:7788\n" + "connected\n" + "read \"foo\"\n"
                        + "write [0x01 0x02 0xff]\n" + "closed\n";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstScriptNode actual = parser.parseWithStrategy(script, SCRIPT);
        AstLocation location7788 = new AstLocationLiteral(URI.create("tcp://localhost:7788"));
        AstScriptNode expected =
                new AstScriptNodeBuilder().addConnectStream().setLocation(location7788)
                        .addConnectedEvent().done().addReadEvent().addExactText("foo").done().addWriteCommand()
                        .addExactBytes(new byte[]{0x01, 0x02, (byte) 0xff}).done().addClosedEvent().done().done().done();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseEmptyScript() throws Exception {

        String script = "";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstScriptNode actual = parser.parseWithStrategy(script, SCRIPT);

        AstScriptNode expected = new AstScriptNodeBuilder().done();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseScriptWithCommentsOnly() throws Exception {

        String script = "# Comment 1\n" + "# Comment 2\n" + "# Comment 3\n";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstScriptNode actual = parser.parseWithStrategy(script, SCRIPT);

        AstScriptNode expected = new AstScriptNode();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseScriptWithCommentsAndWhitespace() throws Exception {

        String script = "# Comment 1\n" + "\t\n" + " # Comment 2\n" + "\r\n" + "# Comment 3\n";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstScriptNode actual = parser.parseWithStrategy(script, SCRIPT);

        AstScriptNode expected = new AstScriptNode();

        assertEquals(expected, actual);
    }

    @Ignore("Not implemented and perhaps not designed correctly.  "
            + "Need to access the use and proper syntax for child channels")
    @Test
    public void shouldParseScript() throws Exception {

        String script =
                "#\n" + "# server\n" + "#\n" + "accept tcp://localhost:8000 as ACCEPT\n" + "opened\n" + "bound\n"
                        + "child opened\n" + "child closed\n" + "unbound\n" + "closed\n" + "#\n" + "# child\n" + "#\n"
                        + " accepted ACCEPT\n" + "opened\n" + " bound\n" + "connected\n" + " read ([0..32]:input)\n"
                        + "read notify BARRIER\n" + "write await BARRIER\n" + "write [ 0x01 0xfe ]\n" + "close\n"
                        + "disconnected\n" + "unbound\n" + "closed\n" + "#\n" + "# client\n" + "#\n"
                        + "connect tcp://localhost:8000\n" + " opened\n" + "bound\n" + " connected\n" + "write ${input}\n"
                        + " read [ 0x00 0xff ]\n" + "close\n" + "disconnected\n" + "unbound\n" + "closed";

        ExpressionFactory factory = ExpressionFactory.newInstance();
        ExpressionContext context = new ExpressionContext();

        ScriptParserImpl parser = new ScriptParserImpl(factory, context);
        // parser.lex(new ByteArrayInputStream(script.getBytes(UTF_8)));
        AstScriptNode actual = parser.parseWithStrategy(script, SCRIPT);
        AstLocation location8000 = new AstLocationLiteral(URI.create("tcp://localhost:8000"));

        AstScriptNode expected = new AstScriptNodeBuilder().addAcceptStream()
                .setLocation(new AstLocationLiteral(URI.create("tcp://localhost:8000"))).setAcceptName("ACCEPT")
                .addOpenedEvent()
                .done().addBoundEvent()
                .done().addChildOpenedEvent()
                .done().addChildClosedEvent()
                .done().addUnboundEvent()
                .done().addClosedEvent()
                .done().done().addAcceptedStream().setAcceptName("ACCEPT").addOpenedEvent()
                .done().addBoundEvent().done().addConnectedEvent()
                .done().addReadEvent().addFixedLengthBytes(32, "input", parser.getExpressionContext()).done()
                .addReadNotifyBarrier()
                .setBarrierName("BARRIER").done().addWriteAwaitBarrier()
                .setBarrierName("BARRIER").done().addWriteCommand()
                .addExactBytes(new byte[] { 0x01, -0x02 }).done().addCloseCommand()
                .done().addDisconnectedEvent()
                .done().addUnboundEvent()
                .done().addClosedEvent()
                .done().done().addConnectStream().setLocation(location8000).addOpenedEvent().done().addBoundEvent()
                .done().addConnectedEvent().done().addWriteCommand()
                .addExpression(factory.createValueExpression(context, "${input}", byte[].class), context).done()
                .addReadEvent().addExactBytes(new byte[] { 0x00, -0x01 }, context).done().addCloseCommand().done()
                .addDisconnectedEvent().done().addUnboundEvent().done().addClosedEvent().done().done().done();

        assertEquals(expected, actual);
    }

    @Test(
        expected = ScriptParseException.class)
    public void shouldNotParseScriptWithUnknownKeyword() throws Exception {

        String script = "written\n";

        ScriptParserImpl parser = new ScriptParserImpl();
        parser.parseWithStrategy(script, SCRIPT);
    }

    @Test(
        expected = ScriptParseException.class)
    public void shouldNotParseScriptWithReadBeforeConnect() throws Exception {

        String script =
                "# tcp.client.connect-then-close\n" + "read [0x01 0x02 0x03]\n" + "connect tcp://localhost:7788\n"
                        + "connected\n" + "close\n" + "closed\n";

        ScriptParserImpl parser = new ScriptParserImpl();
        parser.parseWithStrategy(script, SCRIPT);
    }

    @Test
    public void shouldParseReadOptionMask() throws Exception {

        String scriptFragment = "read option mask [0x01 0x02 0x03 0x04]";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadOptionNode actual = parser.parseWithStrategy(scriptFragment, READ_OPTION);

        AstReadOptionNode expected =
                new AstReadOptionNodeBuilder().setOptionName("mask").setOptionValue(new byte[]{0x01, 0x02, 0x03, 0x04}).done();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseReadOptionMaskExpression() throws Exception {

        String scriptFragment = "read option mask ${maskingKey}";

        ScriptParserImpl parser = new ScriptParserImpl();
        ExpressionFactory factory = parser.getExpressionFactory();
        ExpressionContext context = parser.getExpressionContext();

        AstReadOptionNode actual = parser.parseWithStrategy(scriptFragment, READ_OPTION);

        AstReadOptionNode expected =
                new AstReadOptionNodeBuilder()
                        .setOptionName("mask")
                        .setOptionValue(factory.createValueExpression(context, "${maskingKey}", byte[].class),
                                parser.getExpressionContext()).done();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseWriteOptionMask() throws Exception {

        String scriptFragment = "write option mask [0x01 0x02 0x03 0x04]";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteOptionNode actual = parser.parseWithStrategy(scriptFragment, WRITE_OPTION);

        AstWriteOptionNode expected =
                new AstWriteOptionNodeBuilder().setOptionName("mask").setOptionValue(new byte[]{0x01, 0x02, 0x03, 0x04}).done();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseWriteOptionMaskExpression() throws Exception {

        String scriptFragment = "write option mask ${maskingKey}";

        ScriptParserImpl parser = new ScriptParserImpl();
        ExpressionFactory factory = parser.getExpressionFactory();
        ExpressionContext context = parser.getExpressionContext();

        AstWriteOptionNode actual = parser.parseWithStrategy(scriptFragment, WRITE_OPTION);

        AstWriteOptionNode expected =
                new AstWriteOptionNodeBuilder()
                        .setOptionName("mask")
                        .setOptionValue(factory.createValueExpression(context, "${maskingKey}", byte[].class),
                                parser.getExpressionContext()).done();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseCapturingFixedLengthBytesMatcher2() throws Exception {

        String scriptFragment = "[(:capture){5}]";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstFixedLengthBytesMatcher actual = parser.parseWithStrategy(scriptFragment, FIXED_LENGTH_BYTES_MATCHER);

        AstFixedLengthBytesMatcher expected = new AstFixedLengthBytesMatcher(5, "capture", parser.getExpressionContext());

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseNamedPropertyWithLiteralText() throws Exception {

        String scriptFragment = "property location \"tcp://localhost:8000\"";

        ScriptParserImpl parser = new ScriptParserImpl();

        AstPropertyNode actual = parser.parseWithStrategy(scriptFragment, PROPERTY_NODE);

        AstPropertyNode expected =
                new AstPropertyNodeBuilder().setPropertyName("location").setPropertyValue("tcp://localhost:8000").done();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseNamedPropertyWithLiteralBytes() throws Exception {

        String scriptFragment = "property location [0x00 0x01 0x02 0x03]";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstPropertyNode actual = parser.parseWithStrategy(scriptFragment, PROPERTY_NODE);

        AstPropertyNode expected =
                new AstPropertyNodeBuilder().setPropertyName("location").setPropertyValue(new byte[]{0x00, 0x01, 0x02, 0x03})
                        .done();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseNamedPropertyWithExpression() throws Exception {

        String scriptFragment = "property location ${expression}";

        ScriptParserImpl parser = new ScriptParserImpl();

        ExpressionFactory factory = parser.getExpressionFactory();
        ExpressionContext context = parser.getExpressionContext();

        AstPropertyNode actual = parser.parseWithStrategy(scriptFragment, PROPERTY_NODE);

        ValueExpression expression = factory.createValueExpression(context, "${expression}", Object.class);

        AstPropertyNode expected =
                new AstPropertyNodeBuilder().setPropertyName("location")
                        .setPropertyValue(expression, parser.getExpressionContext()).done();

        assertEquals(expected, actual);
    }

}
