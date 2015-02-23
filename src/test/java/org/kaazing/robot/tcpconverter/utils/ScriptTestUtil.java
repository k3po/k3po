/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.tcpconverter.utils;

import java.util.List;

public class ScriptTestUtil {

    public final static boolean scriptHasNonCommentLineContaining(String script, String line) {
        for (String lines : script.split("\n")) {
            if ( lines.contains(line) && !isCommentLineOrWhitespace(line) ) {
                return true;
            }
        }
        return false;
    }

    public final static boolean scriptHasNonCommentLineContainingLineAndNoOtherCommandsInScript(String script,
            String line) {
        for (String currentLine : script.split("\n")) {
            if ( !currentLine.contains(line) && !isCommentLineOrWhitespace(currentLine) ) {
                return false;
            }
        }
        return true;
    }

    public final static boolean scriptIsInstanceOfScript(String script, List<String> scriptToMatch) {
        int lineInScriptToMatch = 0;
        for (String currentLine : script.split("\n")) {
            if ( lineInScriptToMatch >= scriptToMatch.size() ) {
                return false;
            }
            if ( currentLine.contains(scriptToMatch.get(lineInScriptToMatch)) ) {
                lineInScriptToMatch++;
            }
            else if ( !isCommentLineOrWhitespace(currentLine) ) {
                return false;
            }
        }
        return lineInScriptToMatch == scriptToMatch.size();
    }

    public final static boolean scriptHasNonCommentLineContainingLinesAndNoOtherCommandsInScript(String script,
            List<String> lines) {
        for (String currentLine : script.split("\n")) {
            if ( isCommentLineOrWhitespace(currentLine) ) {
                break;
            }
            else if ( lineIsPartOfLines(currentLine, lines) ) {
                break;
            }
            else {
                return false;
            }
        }
        return true;
    }

    private static boolean lineIsPartOfLines(String currentLine, List<String> lines) {
        for (String line : lines) {
            if ( currentLine.contains(line) ) {
                return true;
            }
        }
        return false;
    }

    private static boolean isCommentLineOrWhitespace(String line) {
        if ( line.trim().startsWith("#") || line.trim().equals("") ) {
            return true;
        }
        return false;
    }

}
