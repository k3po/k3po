/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
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
import org.junit.Ignore;
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
