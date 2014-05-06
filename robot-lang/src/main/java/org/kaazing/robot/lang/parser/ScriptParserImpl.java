/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.parser;

import java.io.InputStream;

import javax.el.ExpressionFactory;

import org.kaazing.robot.lang.ast.AstScriptNode;
import org.kaazing.robot.lang.el.ExpressionContext;

public class ScriptParserImpl implements ScriptParser {

    public static final String MIME_TYPE_PREFIX = "text/x-robot";
    public static final String LATEST_SUPPORTED_FORMAT = MIME_TYPE_PREFIX + "-2";
    public static final String EARLIEST_SUPPORTED_FORMAT = MIME_TYPE_PREFIX;

    private final String format;
    private int formatVersion;
    private ScriptParser parser;

    public ScriptParserImpl(String format) {
        this.format = format;
        setFormatVersion();
        switch (formatVersion) {
            case 1:
                parser = new org.kaazing.robot.lang.parser.v1.ScriptParserImpl();
                break;
            case 2:
                parser = new org.kaazing.robot.lang.parser.v2.ScriptParserImpl();
                break;
        }
    }

    public ScriptParserImpl(String format, ExpressionFactory factory, ExpressionContext context) {
        this.format = format;
        setFormatVersion();
        switch (formatVersion) {
            case 1:
                parser = new org.kaazing.robot.lang.parser.v1.ScriptParserImpl(factory, context);
                break;
            case 2:
                parser = new org.kaazing.robot.lang.parser.v2.ScriptParserImpl(factory, context);
                break;
        }
    }

    public String getFormat() {
        return format;
    }

    @Override
    public AstScriptNode parse(InputStream input) throws ScriptParseException {
        return parser.parse(input);
    }

    private void setFormatVersion() {
        if (format.equals(MIME_TYPE_PREFIX)) {
            formatVersion = 1;
            return;
        }

        int lastDashAt = format.lastIndexOf('-');
        if (lastDashAt == -1) {
                throw new IllegalArgumentException("Invalid Mime Type: " + format);
        }

        if (!format.substring(0, lastDashAt).equals(MIME_TYPE_PREFIX)) {
            throw new IllegalArgumentException("Invalid Mime Type: " + format);
        }

        formatVersion = Integer.parseInt(format.substring(lastDashAt + 1));

        switch (formatVersion) {
            case 1:
            case 2:
                break;
            default:
                throw new IllegalArgumentException("Mime Type not supported: " + format);
        }
    }
}
