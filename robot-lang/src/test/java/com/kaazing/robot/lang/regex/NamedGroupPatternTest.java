/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.regex;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;

import org.junit.Ignore;
import org.junit.Test;

public class NamedGroupPatternTest {

    @Ignore("KG-7535 not complete")
    @Test
    public void shouldParseSuccessfully() {
        String scriptText = format("/^The quick brown fox (?<verb>[a-z]+) over the lazy dog$/");
        String inputText = "The quick brown fox jumps over the lazy dog";

        NamedGroupPattern pattern = NamedGroupPattern.compile(scriptText);
        NamedGroupMatcher matcher = pattern.matcher(inputText);

        assertTrue(matcher.matches());
        assertEquals(1, matcher.groupCount());
        assertEquals("verb", matcher.groupName(0));
        assertEquals("jumps", matcher.group(0));
    }

    @Ignore("KG-7535 not complete")
    @Test
    public void shouldParseRegex2() throws Exception {

        String scriptText = format("/(?<hello>\\p{javaWhitespace}{1,6})hello/");
        String inputText = "      hello";

        NamedGroupPattern pattern = NamedGroupPattern.compile(scriptText);
        NamedGroupMatcher matcher = pattern.matcher(inputText);

        assertTrue(matcher.matches());
        assertEquals(1, matcher.groupCount());
        assertEquals("hello", matcher.groupName(0));
        assertEquals("      ", matcher.group(0));
    }

    @Ignore("KG-7535 not complete")
    @Test
    public void shouldParseRegex3() throws Exception {

        String scriptText = format("/(?<hello>\\d+)/");
        String inputText = "12345";

        NamedGroupPattern pattern = NamedGroupPattern.compile(scriptText);
        NamedGroupMatcher matcher = pattern.matcher(inputText);

        assertTrue(matcher.matches());
        assertEquals(1, matcher.groupCount());
        assertEquals("hello", matcher.groupName(0));
        assertEquals("12345", matcher.group(0));
    }

    @Ignore("KG-7535 not complete")
    @Test
    public void shouldParseRegex4() throws Exception {

        ArrayList<String> groupNames = new ArrayList<String>(1);
        groupNames.add("hello");
        groupNames.add("reason");
        String scriptText = format("/(?<hello>HTTP\\/1.1\\s401\\s(?<reason>.*)\\r\\n\\r\\n)/");
        String inputText = "HTTP/1.1 401 Unauthorized\r\n\r\n";

        NamedGroupPattern pattern = NamedGroupPattern.compile(scriptText);
        NamedGroupMatcher matcher = pattern.matcher(inputText);

        assertTrue(matcher.matches());
        assertEquals(2, matcher.groupCount());
        assertEquals("hello", matcher.groupName(0));
        assertEquals("reason", matcher.groupName(1));
        assertEquals("Unauthorized", matcher.group(1));
    }

    @Ignore("KG-7535 not complete")
    @Test
    public void shouldParseRegex5() throws Exception {

        String scriptText = format("/(?<hello>)/");
        String inputText = "foo";

        NamedGroupPattern pattern = NamedGroupPattern.compile(scriptText);
        NamedGroupMatcher matcher = pattern.matcher(inputText);

        assertTrue(matcher.matches());
        assertEquals(1, matcher.groupCount());
        assertEquals("hello", matcher.groupName(0));
        assertEquals("foo", matcher.group(0));
    }

    @Ignore("KG-7535 not complete")
    @Test
    public void shouldParseRegex6() throws Exception {

        String scriptText = format("/(?<hello>\\D+)(?<goodbye>\\d+)/");
        String inputText = "foo123";

        NamedGroupPattern pattern = NamedGroupPattern.compile(scriptText);
        NamedGroupMatcher matcher = pattern.matcher(inputText);

        assertTrue(matcher.matches());
        assertEquals(2, matcher.groupCount());
        assertEquals("hello", matcher.groupName(0));
        assertEquals("foo", matcher.group(0));
        assertEquals("goodbye", matcher.groupName(1));
        assertEquals("123", matcher.group(1));
    }

    @Ignore("KG-7535 not complete")
    @Test
    public void shouldParseRegexWithColon() throws Exception {

        String scriptText = format("/(?<left>.*):(?<right>.*)/");
        String inputText = "foo:bar";

        NamedGroupPattern pattern = NamedGroupPattern.compile(scriptText);
        NamedGroupMatcher matcher = pattern.matcher(inputText);

        assertTrue(matcher.matches());
        assertEquals(2, matcher.groupCount());
        assertEquals("left", matcher.groupName(0));
        assertEquals("foo", matcher.group(0));
        assertEquals("right", matcher.groupName(1));
        assertEquals("bar", matcher.group(1));
    }

    @Ignore("KG-7535 not complete")
    @Test
    public void shouldParseRegexWithLeftParen() throws Exception {

        String scriptText = format("/(?<left>.*)\\((?<right>.*)/");
        String inputText = "foo(bar";

        NamedGroupPattern pattern = NamedGroupPattern.compile(scriptText);
        NamedGroupMatcher matcher = pattern.matcher(inputText);

        assertTrue(matcher.matches());
        assertEquals(2, matcher.groupCount());
        assertEquals("left", matcher.groupName(0));
        assertEquals("foo", matcher.group(0));
        assertEquals("right", matcher.groupName(1));
        assertEquals("bar", matcher.group(1));
    }

    @Ignore("KG-7535 not complete")
    @Test
    public void shouldParseRegexWithLeftParenAndColon() throws Exception {

        String scriptText = format("/(?<left>.*)\\(:(?<right>.*)/");
        String inputText = "foo(:bar";

        NamedGroupPattern pattern = NamedGroupPattern.compile(scriptText);
        NamedGroupMatcher matcher = pattern.matcher(inputText);

        assertTrue(matcher.matches());
        assertEquals(2, matcher.groupCount());
        assertEquals("left", matcher.groupName(0));
        assertEquals("foo", matcher.group(0));
        assertEquals("right", matcher.groupName(1));
        assertEquals("bar", matcher.group(1));
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
