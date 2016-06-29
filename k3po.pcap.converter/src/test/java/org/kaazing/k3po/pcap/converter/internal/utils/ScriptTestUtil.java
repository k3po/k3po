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
package org.kaazing.k3po.pcap.converter.internal.utils;

import java.util.List;

public class ScriptTestUtil {

    public static boolean scriptHasNonCommentLineContaining(String script, String line) {
        for (String lines : script.split("\n")) {
            if ( lines.contains(line) && !isCommentLineOrWhitespace(line) ) {
                return true;
            }
        }
        return false;
    }

    public static boolean scriptHasNonCommentLineContainingLineAndNoOtherCommandsInScript(String script,
            String line) {
        for (String currentLine : script.split("\n")) {
            if ( !currentLine.contains(line) && !isCommentLineOrWhitespace(currentLine) ) {
                return false;
            }
        }
        return true;
    }

    public static boolean scriptIsInstanceOfScript(String script, List<String> scriptToMatch) {
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

    public static boolean scriptHasNonCommentLineContainingLinesAndNoOtherCommandsInScript(String script,
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
