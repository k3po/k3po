/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.regex;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.kaazing.robot.lang.ast.util.AstUtil.equivalent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.TokenStream;
import org.kaazing.robot.lang.regex.RegexParser.GroupNContext;
import org.kaazing.robot.lang.regex.RegexParser.LiteralContext;

public class NamedGroupPattern {

    @Deprecated
    public static NamedGroupPattern compile(String regex, List<String> groupNames) {
        return new NamedGroupPattern(Pattern.compile(regex), groupNames);
    }

    public static NamedGroupPattern compile(String regexWithGroupNames) {
        try {
            ByteArrayInputStream input = new ByteArrayInputStream(regexWithGroupNames.getBytes(UTF_8));
            CharStream ais = new ANTLRInputStream(input);
            Lexer lexer = new RegexLexer(ais);
            TokenStream tokens = new CommonTokenStream(lexer);
            RegexParser parser = new RegexParser(tokens);
            final List<String> groupNames = new ArrayList<String>();
            parser.addParseListener(new RegexBaseListener() {
                @Override
                public void exitGroupN(GroupNContext ctx) {
                    String capture = ctx.capture.getText();
                    String groupName = capture.substring(2, capture.length() - 1);
                    groupNames.add(groupName);
                }
            });
            LiteralContext literal = parser.literal();
            String regex = literal.regex.getText();
            return new NamedGroupPattern(Pattern.compile(regex), groupNames);
        }
        catch (IOException e) {
            PatternSyntaxException pse = new PatternSyntaxException("I/O exception", regexWithGroupNames, 0);
            pse.initCause(e);
            throw pse;
        }
        catch (RecognitionException e) {
            PatternSyntaxException pse =
                new PatternSyntaxException("Unexpected type", regexWithGroupNames, e.getInputStream().index());
            pse.initCause(e);
            throw pse;
        }
    }

    private final Pattern pattern;
    private final List<String> groupNames;

    NamedGroupPattern(Pattern pattern, List<String> groupNames) {
        this.pattern = pattern;
        this.groupNames = groupNames;
        int groupNamesSize = groupNames.size();
        if (groupNamesSize != 0 && (pattern.matcher("").groupCount() != groupNamesSize)) {
            throw new PatternSyntaxException(
                    "Inconsistant named group count. The number of named groups must match the number of groups in the pattern.",
                    pattern.toString(), -1);
        }
    }

    public NamedGroupMatcher matcher(CharSequence input) {
        return new NamedGroupMatcher(pattern.matcher(input), groupNames);
    }

    @Override
    public int hashCode() {
        return pattern.hashCode() ^ groupNames.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || (obj instanceof NamedGroupPattern) && equals((NamedGroupPattern) obj);
    }

    protected boolean equals(NamedGroupPattern that) {
        return equivalent(this.pattern, that.pattern)
                && equivalent(this.groupNames, that.groupNames);
    }

    public String pattern() {
        return pattern.pattern();
    }

    @Override
    public String toString() {
        return String.format("/%s/", pattern);
    }
}
