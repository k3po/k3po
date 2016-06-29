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
package org.kaazing.k3po.lang.internal.regex;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.kaazing.k3po.lang.internal.ast.util.AstUtil.equivalent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.kaazing.k3po.lang.regex.RegexBaseListener;
import org.kaazing.k3po.lang.regex.RegexLexer;
import org.kaazing.k3po.lang.regex.RegexParser;
import org.kaazing.k3po.lang.regex.RegexParser.GroupNContext;
import org.kaazing.k3po.lang.regex.RegexParser.LiteralContext;

public class NamedGroupPattern {

    public static NamedGroupPattern compile(final String regexWithGroupNames) {
        try {
            ByteArrayInputStream input = new ByteArrayInputStream(regexWithGroupNames.getBytes(UTF_8));
            CharStream ais = new ANTLRInputStream(input);
            Lexer lexer = new RegexLexer(ais);
            TokenStream tokens = new CommonTokenStream(lexer);
            RegexParser parser = new RegexParser(tokens);
            parser.setErrorHandler(new BailErrorStrategy());
            final List<String> groupNames = new ArrayList<>();
            parser.addParseListener(new RegexBaseListener() {
                @Override
                public void exitGroupN(GroupNContext ctx) {
                    Token captureVar = ctx.capture;
                    // Not every entry in groupN populates groupNames
                    if (captureVar != null) {
                        String capture = captureVar.getText();
                        String groupName = capture.substring(2, capture.length() - 1);
                        groupNames.add(groupName);
                    }
                }
            });
            LiteralContext literal = parser.literal();
            String regex = literal.regex.getText();
            return new NamedGroupPattern(Pattern.compile(regex), groupNames);
        }
        catch (IOException ioe) {
            PatternSyntaxException pse = new PatternSyntaxException("I/O exception", regexWithGroupNames, 0);
            pse.initCause(ioe);
            throw pse;
        }
        catch (ParseCancellationException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RecognitionException) {
                RecognitionException re = (RecognitionException) cause;
                PatternSyntaxException pse =
                    new PatternSyntaxException("Unexpected type", regexWithGroupNames, re.getInputStream().index());
                pse.initCause(re);
                throw pse;
            }
            throw e;
        }
        catch (RecognitionException re) {
            PatternSyntaxException pse =
                new PatternSyntaxException("Unexpected type", regexWithGroupNames, re.getInputStream().index());
            pse.initCause(re);
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
        return pattern.toString();
    }
}
