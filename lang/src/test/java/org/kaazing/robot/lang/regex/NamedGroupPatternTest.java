/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.regex;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.junit.Test;

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
}
