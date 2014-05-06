/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.ast.builder;

import org.kaazing.robot.lang.ast.AstScriptNode;

public class AstScriptNodeBuilder extends AbstractAstNodeBuilder<AstScriptNode, AstScriptNode> {

    private int line;

    public AstScriptNodeBuilder() {
        this(new AstScriptNode());
    }

    private AstScriptNodeBuilder(AstScriptNode node) {
        super(node, node);
    }

    @Override
    public int line() {
        return line;
    }

    @Override
    public int line(int line) {
        this.line = line;
        return line;
    }

    @Override
    public AstScriptNodeBuilder setLocationInfo(int line, int column) {
        node.setLocationInfo(line, column);
        internalSetLineInfo(line);
        return this;
    }

    @Override
    public AstScriptNodeBuilder setNextLineInfo(int linesToSkip, int column) {
        internalSetNextLineInfo(linesToSkip, column);
        return this;
    }

    public AstAcceptNodeBuilder.ScriptNested<AstScriptNodeBuilder> addAcceptStream() {
        return new AstAcceptNodeBuilder.ScriptNested<AstScriptNodeBuilder>(this);
    }

    public AstAcceptableNodeBuilder.ScriptNested<AstScriptNodeBuilder> addAcceptedStream() {
        return new AstAcceptableNodeBuilder.ScriptNested<AstScriptNodeBuilder>(this);
    }

    public AstConnectNodeBuilder.ScriptNested<AstScriptNodeBuilder> addConnectStream() {
        return new AstConnectNodeBuilder.ScriptNested<AstScriptNodeBuilder>(this);
    }

    @Override
    public AstScriptNode done() {
        return node;
    }

}
