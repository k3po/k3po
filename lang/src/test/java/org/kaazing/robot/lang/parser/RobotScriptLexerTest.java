/*
 * Copyright (c) 2014 "Kaazing Corporation," (www.kaazing.com)
 *
 * This file is part of Robot.
 *
 * Robot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.kaazing.robot.lang.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kaazing.robot.lang.parser.v2.RobotLexer;

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

        Assert.assertTrue(String.format("Expected EOF token, got %d (%s)", token.getType(), token),
                token.getType() == RobotLexer.EOF);
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
        Assert.assertTrue(String.format("Expected token type %d, got %d (%s)", Token.EOF, token.getType(), token.getText()),
                token.getType() == Token.EOF);
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

        Assert.assertTrue(String.format("Expected keyword token, got type %d: %s", token.getType(), token.getText()),
                token.getType() == RobotLexer.CloseKeyword);
    }
}
