/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.regex;

import static org.kaazing.robot.lang.ast.util.AstUtil.equivalent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;

public class NamedGroupPattern {

    @Deprecated
    public static NamedGroupPattern compile(String regex, List<String> groupNames) {
        return new NamedGroupPattern(Pattern.compile(regex), groupNames);
    }

    public static NamedGroupPattern compile(String regexWithGroupNames) {
        try {
            ByteArrayInputStream input = new ByteArrayInputStream(regexWithGroupNames.getBytes(StandardCharsets.UTF_8));
            CharStream ais = new ANTLRInputStream(input);
            Lexer lexer = new NamedGroupPatternLexer(ais);
            TokenStream tokens = new CommonTokenStream(lexer);
            NamedGroupPatternParser parser = new NamedGroupPatternParser(tokens);
            NamedGroupPattern pattern = parser.namedGroupPattern();
            if (pattern == null) {
                int position = lexer.getCharPositionInLine();
                throw new PatternSyntaxException("Invalid named group pattern", regexWithGroupNames, position);
            }
            return pattern;
        }
        catch (IOException e) {
            PatternSyntaxException pse = new PatternSyntaxException("I/O exception", regexWithGroupNames, 0);
            pse.initCause(e);
            throw pse;
        }
        catch (RecognitionException e) {
            PatternSyntaxException pse = new PatternSyntaxException("Unexpected type", regexWithGroupNames, e.index);
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
        // To bad we cant generate a string with the proper nesting of parens here
        return groupNames.isEmpty() ? String.format("/%s/", pattern) : String.format("/%s/%s/", pattern, groupNames);
    }
}
