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

package org.kaazing.k3po.lang.internal.parser;

import static org.junit.Assert.assertSame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kaazing.k3po.lang.parser.v2.RobotLexer;

public class RobotScriptLexerTest {
    private File scriptFile;
    private RobotLexer lexer;

    @Before
    public void setUp() throws Exception {

        scriptFile = File.createTempFile("lexer", ".tmp");
    }

    @After
    public void tearDown() throws Exception {

        if (scriptFile != null) {
            scriptFile.delete();
            scriptFile = null;
        }
    }

    @Test
    public void shouldScanEmptyScript() throws Exception {

        InputStream is = new FileInputStream(scriptFile);
        ANTLRInputStream ais = new ANTLRInputStream(is);

        lexer = new RobotLexer(ais);
        Token token = lexer.nextToken();

        assertSame(String.format("Expected EOF token, got %d (%s)", token.getType(), token), RobotLexer.EOF,
                token.getType());
    }

    @Test
    public void shouldSkipLineComment() throws Exception {

        String text = "# tcp.test\n";
        FileWriter writer = new FileWriter(scriptFile);
        writer.write(String.format("%s", text));
        writer.close();

        InputStream is = new FileInputStream(scriptFile);
        ANTLRInputStream ais = new ANTLRInputStream(is);

        lexer = new RobotLexer(ais);
        Token token = lexer.nextToken();

        assertSame(String.format("Expected token type %d, got %d (%s)", Token.EOF, token.getType(), token.getText()),
                Token.EOF, token.getType());
    }

    @Test
    public void shouldScanKeyword() throws Exception {

        String text = "close";
        FileWriter writer = new FileWriter(scriptFile);
        writer.write(String.format("%s\n", text));
        writer.close();

        InputStream is = new FileInputStream(scriptFile);
        ANTLRInputStream ais = new ANTLRInputStream(is);

        lexer = new RobotLexer(ais);
        Token token = lexer.nextToken();

        assertSame(String.format("Expected keyword token, got type %d: %s", token.getType(), token.getText()),
                RobotLexer.CloseKeyword, token.getType());
    }
}
