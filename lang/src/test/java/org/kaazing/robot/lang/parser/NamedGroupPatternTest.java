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

package org.kaazing.robot.lang.parser;

import static java.lang.String.format;
import static org.junit.Assert.assertTrue;
import static org.kaazing.robot.lang.parser.ScriptParseStrategy.READ;
import static org.kaazing.robot.lang.parser.ScriptParseStrategy.REGEX_MATCHER;
import static org.kaazing.robot.lang.test.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import org.junit.Test;
import org.kaazing.robot.lang.ast.AstReadValueNode;
import org.kaazing.robot.lang.ast.builder.AstReadNodeBuilder;
import org.kaazing.robot.lang.ast.matcher.AstByteLengthBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstExactBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstExactTextMatcher;
import org.kaazing.robot.lang.ast.matcher.AstExpressionMatcher;
import org.kaazing.robot.lang.ast.matcher.AstFixedLengthBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstIntLengthBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstLongLengthBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstRegexMatcher;
import org.kaazing.robot.lang.ast.matcher.AstShortLengthBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstValueMatcher;
import org.kaazing.robot.lang.ast.matcher.AstVariableLengthBytesMatcher;
import org.kaazing.robot.lang.el.ExpressionContext;
import org.kaazing.robot.lang.regex.NamedGroupMatcher;
import org.kaazing.robot.lang.regex.NamedGroupPattern;

public class NamedGroupPatternTest {

    @Test
    public void shouldCompileDotStar() {
        NamedGroupPattern.compile("/what(.*)/");
    }

    @Test
    public void shouldCompileNamedGroup() {
        NamedGroupPattern.compile("/(?<groupA>.*)/");
    }

    @Test
    public void shouldCompilePlainText() {
        NamedGroupPattern.compile("/plainText/");
    }

    @Test
    public void shouldParseDigitCharacterClass() {
        NamedGroupPattern pattern = NamedGroupPattern.compile("/[0-9]/");
        assertTrue(pattern.matcher("0").matches());

    }

    @Test
    public void shouldParseSuccessfully() {
        String scriptText = format("/^The quick brown fox (?<verb>[a-z]+) over the lazy dog$/");
        String inputText = "The quick brown fox jumps over the lazy dog";

        Pattern jPattern = Pattern.compile(scriptText.substring(1, scriptText.length() - 1));
        Matcher jMatcher = jPattern.matcher(inputText);
        assertTrue(jMatcher.matches());
        
        NamedGroupPattern pattern = NamedGroupPattern.compile(scriptText);
        NamedGroupMatcher matcher = pattern.matcher(inputText);

        assertTrue(matcher.matches());
        assertEquals("jumps", matcher.group("verb"));
    }

    @Test
    public void shouldParseNestedGroups() {
        String scriptText = "/(?<name>[a-f\\d]{8}(?:-[a-f\\d]{4}){3}-[a-f\\d]{12})/";
        String inputText = "f1b77305-8980-4d1c-b3d4-bb71256e11e9";

        Pattern jPattern = Pattern.compile(scriptText.substring(1, scriptText.length() - 1));
        Matcher jMatcher = jPattern.matcher(inputText);
        assertTrue(jMatcher.matches());

        NamedGroupPattern pattern = NamedGroupPattern.compile(scriptText);
        NamedGroupMatcher matcher = pattern.matcher(inputText);

        assertTrue(matcher.matches());
        assertEquals("f1b77305-8980-4d1c-b3d4-bb71256e11e9", matcher.group("name"));
    }

    @Test
    public void shouldParseRegex2() throws Exception {

        String scriptText = format("/(?<hello>\\p{javaWhitespace}{1,6})hello/");
        String inputText = "      hello";

        NamedGroupPattern pattern = NamedGroupPattern.compile(scriptText);
        NamedGroupMatcher matcher = pattern.matcher(inputText);

        assertTrue(matcher.matches());
        assertEquals("      ", matcher.group("hello"));
    }

    @Test
    public void shouldParseRegex3() throws Exception {

        String scriptText = format("/(?<hello>\\d+)/");
        String inputText = "12345";

        NamedGroupPattern pattern = NamedGroupPattern.compile(scriptText);
        NamedGroupMatcher matcher = pattern.matcher(inputText);

        assertTrue(matcher.matches());
        assertEquals("12345", matcher.group("hello"));
    }

    @Test
    public void shouldParseRegex4() throws Exception {
        ArrayList<String> groupNames = new ArrayList<String>(1);
        groupNames.add("hello");
        groupNames.add("reason");
        String scriptText = format("/(?<hello>HTTP\\/1.1\\s401\\s(?<reason>.*)\\r\\n\\r\\n)/");
        String inputText = "HTTP/1.1 401 Unauthorized\r\n\r\n";

        Pattern jPattern = Pattern.compile(scriptText.substring(1, scriptText.length() - 1));
        Matcher jMatcher = jPattern.matcher(inputText);
        assertTrue(jMatcher.matches());
        assertEquals("Unauthorized", jMatcher.group(2));
        
        NamedGroupPattern pattern = NamedGroupPattern.compile(scriptText);
        NamedGroupMatcher matcher = pattern.matcher(inputText);

        assertTrue(matcher.matches());
        assertEquals("Unauthorized", matcher.group("reason"));
    }

    @Test
    public void shouldParseRegex5() throws Exception {

        String scriptText = format("/(?<hello>.*)/");
        String inputText = "foo";

        Pattern jPattern = Pattern.compile(scriptText.substring(1, scriptText.length() - 1));
        Matcher jMatcher = jPattern.matcher(inputText);
        assertTrue(jMatcher.matches());
        
        NamedGroupPattern pattern = NamedGroupPattern.compile(scriptText);
        NamedGroupMatcher matcher = pattern.matcher(inputText);

        assertTrue(matcher.matches());
        assertEquals("foo", matcher.group("hello"));
    }

    @Test
    public void shouldParseRegex6() throws Exception {

        String scriptText = format("/(?<hello>\\D+)(?<goodbye>\\d+)/");
        String inputText = "foo123";

        NamedGroupPattern pattern = NamedGroupPattern.compile(scriptText);
        NamedGroupMatcher matcher = pattern.matcher(inputText);

        assertTrue(matcher.matches());
        assertEquals("foo", matcher.group("hello"));
        assertEquals("123", matcher.group("goodbye"));
    }

    @Test
    public void shouldParseRegexWithColon() throws Exception {

        String scriptText = format("/(?<left>.*):(?<right>.*)/");
        String inputText = "foo:bar";

        NamedGroupPattern pattern = NamedGroupPattern.compile(scriptText);
        NamedGroupMatcher matcher = pattern.matcher(inputText);

        assertTrue(matcher.matches());
        assertEquals("foo", matcher.group("left"));
        assertEquals("bar", matcher.group("right"));
    }

    @Test
    public void shouldParseRegexWithLeftParen() throws Exception {

        String scriptText = format("/(?<left>.*)\\((?<right>.*)/");
        String inputText = "foo(bar";

        NamedGroupPattern pattern = NamedGroupPattern.compile(scriptText);
        NamedGroupMatcher matcher = pattern.matcher(inputText);

        assertTrue(matcher.matches());
        assertEquals("foo", matcher.group("left"));
        assertEquals("bar", matcher.group("right"));
    }

    @Test
    public void shouldParseRegexWithEscapedChars() throws Exception {

        String scriptText = format("/HTTP\\/1.1\\s401\\sAuthorization Required/");
        String inputText = "HTTP/1.1 401 Authorization Required";

        NamedGroupPattern pattern = NamedGroupPattern.compile(scriptText);
        NamedGroupMatcher matcher = pattern.matcher(inputText);

        assertTrue(matcher.matches());
    }

    @Test
    public void shouldParseRegexWithLeftParenAndColon() throws Exception {

        String scriptText = format("/(?<left>.*)\\(:(?<right>.*)/");
        String inputText = "foo(:bar";

        NamedGroupPattern pattern = NamedGroupPattern.compile(scriptText);
        NamedGroupMatcher matcher = pattern.matcher(inputText);

        assertTrue(matcher.matches());
        assertEquals("foo", matcher.group("left"));
        assertEquals("bar", matcher.group("right"));
    }

    @Test(expected = PatternSyntaxException.class)
    public void shouldFailGroupNamesMismatch() throws Exception {
        String scriptText = format("/(?<left>.*)\\(:(.*)/");
        NamedGroupPattern.compile(scriptText);
    }

    @Test
    public void shouldNotFailGroupsWithZeroNames() throws Exception {
        String scriptText = format("/(.*)/");
        NamedGroupPattern.compile(scriptText);
    }

    @Test
    public void shouldNotFailNonCaptureGroup() throws Exception {
        String scriptText = format("/(?:.*)/");
        NamedGroupPattern.compile(scriptText);
    }
    
	@Test
	public void shouldParseReadRegexLiteral() throws Exception {

		String scriptFragment = "read /hello\\:^foo.*\\n/";

		ScriptParserImpl parser = new ScriptParserImpl();

		AstReadValueNode actual = parser
				.parseWithStrategy(scriptFragment, READ);

		// @formatter:off
		AstReadValueNode expected = new AstReadNodeBuilder()
				.addRegex(NamedGroupPattern.compile("/hello\\:^foo.*\\n/"))
				.done();
		// @formatter:on

		System.out.println(expected);
		System.out.println(actual);
		assertEquals(expected, actual);
	}
	
	@Test
	public void shouldParseRegexMatcher() throws Exception {

		String scriptFragment = "/[a-f\\d]{8}(-[a-f\\d]{4}){3}-[a-f\\d]{12}/";

		ScriptParserImpl parser = new ScriptParserImpl();
		AstRegexMatcher actual = parser.parseWithStrategy(scriptFragment,
				REGEX_MATCHER);

		NamedGroupPattern regex = NamedGroupPattern
				.compile("/[a-f\\d]{8}(-[a-f\\d]{4}){3}-[a-f\\d]{12}/");
		AstRegexMatcher expected = new AstRegexMatcher(regex);

		assertEquals(expected, actual);
	}
	
	@Test
	public void shouldParseReadMult() throws Exception {
		String scriptFragment = "read \"Hello\" [0x01 0x02 0x03] /.*\\n/ /(?<cap1>.*)\\n/ ${var}  [0..64] ([0..64]:cap2)"
				+ "[0..${var}] [0..${var-1}] ([0..${var}]:cap3) ([0..${var-1}]:cap4) (byte:b) (short:s) (int:i) (long:l)";

		ScriptParserImpl parser = new ScriptParserImpl();
		AstReadValueNode actual = parser
				.parseWithStrategy(scriptFragment, READ);

		ExpressionFactory factory = parser.getExpressionFactory();
		ExpressionContext context = parser.getExpressionContext();

		// @formatter:off
		AstReadValueNode expected = new AstReadNodeBuilder()
				.addExactText("Hello")
				.addExactBytes(new byte[] { 0x01, (byte) 0x02, (byte) 0x03 })
				.addRegex(NamedGroupPattern.compile("/.*\\n/"))
				.addRegex(NamedGroupPattern.compile("/(?<cap1>.*)\\n/"))
				.addExpression(
						factory.createValueExpression(context, "${var}",
								byte[].class))
				.addFixedLengthBytes(64)
				.addFixedLengthBytes(64, "cap2")
				.addVariableLengthBytes(
						factory.createValueExpression(context, "${var}",
								Integer.class))
				.addVariableLengthBytes(
						factory.createValueExpression(context, "${var-1}",
								Integer.class))
				.addVariableLengthBytes(
						factory.createValueExpression(context, "${var}",
								Integer.class), "cap3")
				.addVariableLengthBytes(
						factory.createValueExpression(context, "${var-1}",
								Integer.class), "cap4")
				.addFixedLengthBytes(1, "b").addFixedLengthBytes(2, "s")
				.addFixedLengthBytes(4, "i").addFixedLengthBytes(8, "l").done();
		// @formatter:on

		assertEquals(expected, actual);
	}
	
	@Test
	public void shouldParseMultAllMatcher() throws Exception {
		String scriptFragment = "read \"Hello\" [0x01 0x02 0x03] /.*\\n/ /(?<cap1>.*)\\n/ ${var}  [0..64] ([0..64]:cap2)"
				+ "[0..${var}] [0..${var-1}] ([0..${var}]:cap3) ([0..${var-1}]:cap4)"
				+ "(byte:b) (short:s) (int:i) (long:l)";

		ScriptParserImpl parser = new ScriptParserImpl();
		AstReadValueNode actual = parser
				.parseWithStrategy(scriptFragment, READ);

		ExpressionFactory factory = parser.getExpressionFactory();
		ExpressionContext context = parser.getExpressionContext();
		ValueExpression value = factory.createValueExpression(context,
				"${var}", byte[].class);
		ValueExpression value2 = factory.createValueExpression(context,
				"${var}", Integer.class);
		ValueExpression value3 = factory.createValueExpression(context,
				"${var-1}", Integer.class);
		ValueExpression value4 = factory.createValueExpression(context,
				"${var}", Integer.class);
		ValueExpression value5 = factory.createValueExpression(context,
				"${var-1}", Integer.class);

		AstReadValueNode expected = new AstReadValueNode();
		expected.setMatchers(Arrays.<AstValueMatcher> asList(
				new AstExactTextMatcher("Hello"),
				new AstExactBytesMatcher(new byte[] { 0x01, (byte) 0x02,
						(byte) 0x03 }),
				new AstRegexMatcher(NamedGroupPattern.compile("/.*\\n/")),
				new AstRegexMatcher(NamedGroupPattern
						.compile("/(?<cap1>.*)\\n/")),
				new AstExpressionMatcher(value),
				new AstFixedLengthBytesMatcher(64),
				new AstFixedLengthBytesMatcher(64, "cap2"),
				new AstVariableLengthBytesMatcher(value2),
				new AstVariableLengthBytesMatcher(value3),
				new AstVariableLengthBytesMatcher(value4, "cap3"),
				new AstVariableLengthBytesMatcher(value5, "cap4"),
				new AstByteLengthBytesMatcher("b"),
				new AstShortLengthBytesMatcher("s"),
				new AstIntLengthBytesMatcher("i"),
				new AstLongLengthBytesMatcher("l")));

		assertEquals(expected, actual);
	}
	
}
