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

package org.kaazing.k3po.lang.parser;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.kaazing.k3po.lang.RegionInfo.newParallel;
import static org.kaazing.k3po.lang.RegionInfo.newSequential;
import static org.kaazing.k3po.lang.el.ExpressionFactoryUtils.newExpressionFactory;
import static org.kaazing.k3po.lang.parser.ScriptParseStrategy.SCRIPT;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.el.ExpressionFactory;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.kaazing.k3po.lang.RegionInfo;
import org.kaazing.k3po.lang.ast.AstRegion;
import org.kaazing.k3po.lang.ast.AstScriptNode;
import org.kaazing.k3po.lang.el.ExpressionContext;
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
    public AstScriptNode parse(InputStream input) throws ScriptParseException {
        try {
            return parseWithStrategy(input, SCRIPT);
        } catch (Exception e) {
            throw new ScriptParseException(e);
        }
    }

    // package-private for unit testing
    <T extends AstRegion> T parseWithStrategy(String input, ScriptParseStrategy<T> strategy)
            throws ScriptParseException {
        return parseWithStrategy(
                new ByteArrayInputStream(input.getBytes(UTF_8)), strategy);
    }

    <T extends AstRegion> T parseWithStrategy(String input, ScriptParseStrategy<T> strategy,
                            final List<ScriptParseException> parseErrors)
            throws ScriptParseException {
        return parseWithStrategy(
                new ByteArrayInputStream(input.getBytes(UTF_8)), strategy);
    }

    <T extends AstRegion> T parseWithStrategy(InputStream input, ScriptParseStrategy<T> strategy)
            throws ScriptParseException {
        final List<ScriptParseException> parseErrors = new ArrayList<ScriptParseException>();
        T result = null;
        try {
            int newStart = 0;
            int newEnd = input.available();

            ANTLRInputStream ais = new ANTLRInputStream(input);
            RobotLexer lexer = new RobotLexer(ais);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            RobotParser parser = new RobotParser(tokens);

            parser.addErrorListener(new BaseErrorListener() {

                @Override
                public void syntaxError(Recognizer<?, ?> recognizer,
                                        Object offendingSymbol, int line,
                                        int charPositionInLine, String msg,
                                        RecognitionException e) {
                    parseErrors.add(new ScriptParseException(
                            "Syntax error while parsing: ", e));
                }
            });

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

            } catch (IllegalArgumentException iae) {
                Throwable cause = iae.getCause();
                if (cause != null && cause instanceof RecognitionException) {
                    throw createScriptParseException(parser,
                            (RecognitionException) cause);
                } else {
                    throw iae;
                }

            } catch (RecognitionException re) {
                throw createScriptParseException(parser, re);
            }

        } catch (IOException e) {
            throw new ScriptParseException(e);
        }
        if (parseErrors.size() > 0) {
            throw parseErrors.get(0);
        }
        return result;
    }

    private ScriptParseException createScriptParseException(RobotParser parser,
                                                            RecognitionException re) {

        if (re instanceof InputMismatchException) {
            return createScriptParseException(parser,
                    (InputMismatchException) re);

        } else if (re instanceof NoViableAltException) {
            return createScriptParseException(parser, (NoViableAltException) re);

        } else {
            Token token = re.getOffendingToken();
            String desc = String.format("line %d:%d: ", token.getLine(),
                    token.getCharPositionInLine());

            String tokenText = token.getText();
            String msg = null;

            if (tokenText == null) {
                msg = "error: end of input";

            } else {
                desc = String.format("%s'%s'", desc, tokenText);

                @SuppressWarnings("unused")
                String unexpectedTokenName = token.getType() != -1 ? parser
                        .getTokenNames()[token.getType()] : parser
                        .getTokenNames()[0];

                msg = String
                        .format("error: unexpected keyword '%s'", tokenText);
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
