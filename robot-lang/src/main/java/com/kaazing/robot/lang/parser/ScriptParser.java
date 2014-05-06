/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.parser;

import java.io.InputStream;

import com.kaazing.robot.lang.ast.AstScriptNode;

public interface ScriptParser {

    AstScriptNode parse(InputStream input) throws ScriptParseException;

}
