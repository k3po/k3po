/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.parser;

import java.io.InputStream;

import org.kaazing.robot.lang.ast.AstScriptNode;

public interface ScriptParser {

    AstScriptNode parse(InputStream input) throws ScriptParseException;

}
