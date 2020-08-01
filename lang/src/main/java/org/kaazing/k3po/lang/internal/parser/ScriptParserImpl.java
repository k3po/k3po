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
package org.kaazing.k3po.lang.internal.parser;

import static java.lang.String.format;
import static org.kaazing.k3po.lang.internal.RegionInfo.newParallel;
import static org.kaazing.k3po.lang.internal.RegionInfo.newSequential;
import static org.kaazing.k3po.lang.internal.el.ExpressionFactoryUtils.newExpressionFactory;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.SCRIPT;

import java.util.List;

import javax.el.ExpressionFactory;

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.kaazing.k3po.lang.internal.RegionInfo;
import org.kaazing.k3po.lang.internal.ast.AstRegion;
import org.kaazing.k3po.lang.internal.ast.AstScriptNode;
import org.kaazing.k3po.lang.internal.el.ExpressionContext;
import org.kaazing.k3po.lang.parser.v2.RobotLexer;
import org.kaazing.k3po.lang.parser.v2.RobotParser;

public class ScriptParserImpl implements ScriptParser {

    private final ExpressionFactory factory;
    private final ExpressionContext context;

    public ScriptParserImpl() {
        this(newExpressionFactory(), new ExpressionContext());
    }

    public ScriptParserImpl(ExpressionFactory factory, ExpressionContext context) {
        this.factory = factory;
        this.context = context;
    }

    public ExpressionFactory getExpressionFactory() {
        return factory;
    }

    public ExpressionContext getExpressionContext() {
        return context;
    }

    @Override
    public AstScriptNode parse(String input) throws ScriptParseException {
        try {
            return parseWithStrategy(input, SCRIPT);
        } catch (Exception e) {
            throw new ScriptParseException(e);
        }
    }

    public <T extends AstRegion> T parseWithStrategy(String input, ScriptParseStrategy<T> strategy)
            throws ScriptParseException {
        T result = null;

        int newStart = 0;
        int newEnd = input.length();

        CharStream ais = CharStreams.fromString(input);
        RobotLexer lexer = new RobotLexer(ais);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        final RobotParser parser = new RobotParser(tokens);
        parser.setErrorHandler(new BailErrorStrategy());

        try {
            result = strategy.parse(parser, factory, context);
            RegionInfo regionInfo = result.getRegionInfo();
            List<RegionInfo> newChildren = regionInfo.children;
            switch (regionInfo.kind) {
            case SEQUENTIAL:
                result.setRegionInfo(newSequential(newChildren, newStart, newEnd));
                break;
            case PARALLEL:
                result.setRegionInfo(newParallel(newChildren, newStart, newEnd));
                break;
            }

        }
        catch (ParseCancellationException pce) {
            Throwable cause = pce.getCause();
            if (cause instanceof RecognitionException) {
                RecognitionException re = (RecognitionException) cause;
                throw createScriptParseException(parser, re);
            }

            throw pce;
        }
        catch (RecognitionException re) {
            throw createScriptParseException(parser, re);
        }

        return result;
    }

    private ScriptParseException createScriptParseException(RobotParser parser,
                                                            RecognitionException re) {

        if (re instanceof NoViableAltException) {
            return createScriptParseException(parser, (NoViableAltException) re);
        }
        else {
            Token token = re.getOffendingToken();
            String desc = format("line %d:%d: ", token.getLine(), token.getCharPositionInLine());

            String tokenText = token.getText();
            String msg = null;

            if (tokenText == null) {
                msg = "error: end of input";

            } else {
                desc = format("%s'%s'", desc, tokenText);

                @SuppressWarnings("unused")
                String unexpectedTokenName = token.getType() != -1 ? parser
                        .getTokenNames()[token.getType()] : parser
                        .getTokenNames()[0];

                msg = format("error: unexpected keyword '%s'", tokenText);
            }

            return new ScriptParseException(msg, re);
        }
    }

    private ScriptParseException createScriptParseException(RobotParser parser,
                                                            NoViableAltException nvae) {

        String desc = String.format("line %d:%d: ", nvae.getStartToken()
                .getLine(), nvae.getOffendingToken().getCharPositionInLine());
        String msg = String.format("%sunexpected character: '%s'", desc,
                escapeChar(nvae.getOffendingToken().getText().charAt(0)));

        return new ScriptParseException(msg);
    }

    private String escapeChar(char c) {
        switch (c) {
            case '\n':
                return "\\n";

            case '\r':
                return "\\r";

            case '\t':
                return "\\t";

            default:
                return Character.toString(c);
        }
    }
}
