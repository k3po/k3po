/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.parser.v2;

import static org.kaazing.robot.lang.el.ExpressionFactoryUtils.newExpressionFactory;
import static org.kaazing.robot.lang.parser.v2.ScriptParseStrategy.SCRIPT;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.el.ExpressionFactory;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;

import org.kaazing.robot.lang.ast.AstScriptNode;
import org.kaazing.robot.lang.el.ExpressionContext;
import org.kaazing.robot.lang.parser.ScriptParseException;
import org.kaazing.robot.lang.parser.ScriptParser;

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
        }
        catch (Exception e) {
            throw new ScriptParseException(e);
        }
    }

    // package-private for unit testing
    <T> T parseWithStrategy(String input, ScriptParseStrategy<T> strategy) throws ScriptParseException {

        return parseWithStrategy(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)), strategy);
    }

    <T> T parseWithStrategy(InputStream input, ScriptParseStrategy<T> strategy) throws ScriptParseException {

        try {
            ANTLRInputStream ais = new ANTLRInputStream(input);
            RobotLexer lexer = new RobotLexer(ais);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            RobotParser parser = new RobotParser(tokens);
            parser.setExpressionFactory(factory);
            parser.setExpressionContext(context);

            try {
                return strategy.parse(parser);

            }
            catch (IllegalArgumentException iae) {
                Throwable cause = iae.getCause();
                if (cause != null && cause instanceof RecognitionException) {
                    throw createScriptParseException(parser, (RecognitionException) cause);
                }
                else {
                    throw iae;
                }

            }
            catch (RecognitionException re) {
                throw createScriptParseException(parser, re);
            }

        }
        catch (IOException e) {
            throw new ScriptParseException(e);
        }
    }

    void lex(InputStream input) {
        try {
            ANTLRInputStream ais = new ANTLRInputStream(input);
            RobotLexer lexer = new RobotLexer(ais);
            Token token;
            while ((token = lexer.nextToken()).getType() != Token.EOF) {
                System.out.println("Token: " + token);
            }

        }
        catch (Throwable t) {
            System.out.println("Exception: " + t);
            t.printStackTrace();
        }
    }

    private ScriptParseException createScriptParseException(RobotParser parser, RecognitionException re) {

        if (re instanceof MismatchedSetException) {
            return createScriptParseException(parser, (MismatchedSetException) re);

        }
        else if (re instanceof MismatchedTokenException) {
            return createScriptParseException(parser, (MismatchedTokenException) re);

        }
        else if (re instanceof NoViableAltException) {
            return createScriptParseException(parser, (NoViableAltException) re);

        }
        else {
            Token token = re.token;
            String desc = String.format("line %d:%d: ", token.getLine(), token.getCharPositionInLine());

            String tokenText = token.getText();
            String msg = null;

            if (tokenText == null) {
                msg = "error: end of input";

            }
            else {
                desc = String.format("%s'%s'", desc, tokenText);

                @SuppressWarnings("unused")
                String unexpectedTokenName =
                        re.getUnexpectedType() != -1 ? parser.getTokenNames()[re.getUnexpectedType()]
                                : parser.getTokenNames()[0];

                msg = String.format("error: unexpected keyword '%s'", tokenText);
            }

            return new ScriptParseException(msg, re);
        }
    }

    private ScriptParseException createScriptParseException(RobotParser parser, MismatchedSetException mse) {

        String desc = String.format("line %d:%d: ", mse.line, mse.charPositionInLine);

        /*
         * Unfortunately we can't do much with a MismatchedSetException. We
         * COULD -- but only if the mse.expecting BitSet was properly filled in
         * by the auto-generated parser. It isn't. Apparently it's a known ANTLR
         * bug:
         *
         * http://www.antlr.org/pipermail/antlr-interest/2007-September/023590.html
         * but not yet fixed. Sigh.
         */

        String msg = String.format("%sunexpected character: '%s'", desc, escapeChar((char) mse.c));

        return new ScriptParseException(msg, mse);
    }

    private ScriptParseException createScriptParseException(RobotParser parser, MismatchedTokenException mte) {

        String desc = String.format("line %d:%d: %s", mte.line, mte.charPositionInLine, mte.toString());

        return new ScriptParseException(desc);
        /*
         * This didn't really give us a meaningful error. toString seems to do a
         * better job
         *
         * char currChar = (char) mte.c;
         *
         * @SuppressWarnings("unused") String desc =
         * String.format("line %d:%d: '%s'", mte.line, mte.charPositionInLine,
         * escapeChar(currChar));
         *
         * char expectedChar = (char) mte.expecting;
         *
         * String msg =
         * String.format("mismatched character: saw '%s', expected '%s'",
         * escapeChar(currChar), escapeChar(expectedChar));
         *
         * return new ScriptParseException( msg );
         */
    }

    private ScriptParseException createScriptParseException(RobotParser parser, NoViableAltException nvae) {

        String desc = String.format("line %d:%d: ", nvae.line, nvae.charPositionInLine);
        String msg = String.format("%sunexpected character: '%s'", desc, escapeChar((char) nvae.c));

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
