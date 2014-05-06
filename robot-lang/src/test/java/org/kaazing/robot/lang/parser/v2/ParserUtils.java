/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.parser.v2;

import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;

public final class ParserUtils {
    private static String getEscapedChar(char c) {
        if (c == '\n') {
            return "\\n";

        }
        else if (c == '\r') {
            return "\\r";

        }
        else if (c == '\t') {
            return "\\t";
        }

        return Character.toString(c);
    }

    private static void handleMismatchedSetException(MismatchedSetException mse, RobotParser recognizer) {
        Token token = mse.token;
        String tokenText = null;

        if (token != null) {
            tokenText = token.getText();

        }
        else {
            System.err.println(String.format("Caught mismatched set error: line %d:%d: unexpected character: %s", mse.line,
                    mse.charPositionInLine, Character.toString((char) mse.c)));
        }

        if (tokenText == null) {
            System.err.println(String.format("Caught mismatched set error: line %d:%d: end of input", mse.line,
                    mse.charPositionInLine));

        }
        else {
            String unexpectedTokenName =
                    mse.getUnexpectedType() != -1 ? recognizer.getTokenNames()[mse.getUnexpectedType()] : recognizer
                            .getTokenNames()[0];

            System.err.println(String.format("Caught mismatched set error: line %d:%d: unexpected token %s", token.getLine(),
                    token.getCharPositionInLine(), unexpectedTokenName));
        }
    }

    private static void handleMismatchedTokenException(MismatchedTokenException mte, RobotParser recognizer) {
        Token token = mte.token;

        int tokenNamesCount = -1;
        String[] tokenNames = recognizer.getTokenNames();
        if (tokenNames != null) {
            tokenNamesCount = tokenNames.length;
        }

        String expectedTokenName = null;
        if (mte.expecting != -1) {
            if (mte.expecting >= tokenNamesCount) {
                expectedTokenName = "<unknown>";

            }
            else {
                expectedTokenName = tokenNames[mte.expecting];
            }

        }
        else {
            expectedTokenName = tokenNames[0];
        }

        String foundTokenName = null;

        if (mte.getUnexpectedType() != -1) {
            foundTokenName = getEscapedChar((char) mte.getUnexpectedType());

        }
        else {
            foundTokenName = "EOF";
        }

        System.err.println(String.format("Caught mismatched token error: line %d:%d: unexpected token: %s (%d)", mte.line,
                mte.charPositionInLine, foundTokenName, mte.getUnexpectedType()));

        if (token == null) {
            System.err.println(String.format("Caught mismatched token error: line %d:%d: expected %s, got %s", mte.line,
                    mte.charPositionInLine + 1, expectedTokenName, foundTokenName));
            return;
        }

        String tokenText = token.getText();
        if (tokenText == null) {
            System.err.println(String.format("Caught mismatched token error: line %d:%d: end of input when expecting %s",
                    token.getLine(), token.getCharPositionInLine(), recognizer.getTokenNames()[mte.expecting]));

        }
        else {
            System.err.println(String.format("Caught mismatched token error: line %d:%d: expected %s, got %s", token.getLine(),
                    token.getCharPositionInLine(), expectedTokenName, foundTokenName));
        }
    }

    private static void handleNoViableAltException(NoViableAltException nvae, RobotParser recognizer) {

        System.err.println(String.format("Caught no viable alternative error: line %d:%d: unexpected character: %s (hex %x)",
                nvae.line, nvae.charPositionInLine, getEscapedChar((char) nvae.c), nvae.c));
    }

    public static void handleRecognitionException(RecognitionException re, RobotParser recognizer) throws Exception {

        if (re instanceof MismatchedTokenException) {
            handleMismatchedTokenException((MismatchedTokenException) re, recognizer);

        }
        else if (re instanceof MismatchedSetException) {
            handleMismatchedSetException((MismatchedSetException) re, recognizer);

        }
        else if (re instanceof NoViableAltException) {
            handleNoViableAltException((NoViableAltException) re, recognizer);

        }
        else {
            Token token = re.token;

            System.err.println(String.format("handling re: %s", re));

            int tokenNamesCount = -1;
            String[] tokenNames = recognizer.getTokenNames();
            System.err.println(String.format("token names: %s", (Object) tokenNames));
            if (tokenNames != null) {
                tokenNamesCount = tokenNames.length;
            }

            String foundTokenName = "EOF";
            if (re.getUnexpectedType() != -1) {
                System.err.println(String.format("unexpected type: %d", re.getUnexpectedType()));

                if (re.getUnexpectedType() >= tokenNamesCount) {
                    foundTokenName = getEscapedChar((char) re.getUnexpectedType());
                }
                else {
                    foundTokenName = tokenNames[re.getUnexpectedType()];
                }
            }

            if (token == null) {
                System.err.println(String.format("Caught recognition error: line %d:%d: saw unexpected token %s", re.line,
                        re.charPositionInLine + 1, foundTokenName));
                throw re;
            }

            String tokenText = token.getText();
            if (tokenText == null) {
                System.err.println(String.format("Caught recognition error: line %d:%d: end of input", token.getLine(),
                        token.getCharPositionInLine()));

            }
            else {
                String unexpectedTokenName = re.getUnexpectedType() != -1 ? tokenNames[re.getUnexpectedType()] : tokenNames[0];

                System.err.println(String.format("Caught recognition error: line %d:%d: unexpected token %s", token.getLine(),
                        token.getCharPositionInLine(), unexpectedTokenName));
            }
        }

        throw re;
    }

    private ParserUtils() {
        // utility class
    }
}
