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

package org.kaazing.robot.lang.ast.builder;

import org.kaazing.robot.lang.ast.AstNode;

public abstract class AbstractAstNodeBuilder<N extends AstNode, R> {

    protected final N node;
    protected final R result;

    protected AbstractAstNodeBuilder(N node, R result) {
        this.node = node;
        this.result = result;
    }

    public abstract AbstractAstNodeBuilder<N, R> setNextLineInfo(int linesToSkip, int column);

    public abstract AbstractAstNodeBuilder<N, R> setLocationInfo(int line, int column);

    public abstract R done();

    protected abstract int line();

    protected abstract int line(int line);

    protected void internalSetLineInfo(int line) {
        line(line);
    }

    protected void internalSetNextLineInfo(int linesToSkip, int column) {
        setLocationInfo(line() + linesToSkip, column);
    }
}
