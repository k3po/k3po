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

package org.kaazing.robot.lang.el;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.el.ValueExpression;

public class VariableMapper
    extends javax.el.VariableMapper {

    protected final ConcurrentMap<String, ValueExpression> variables;

    public VariableMapper() {
        variables = new ConcurrentHashMap<String, ValueExpression>();
    }

    @Override
    public ValueExpression resolveVariable(String name) {
        return variables.get(name);
    }

    @Override
    public ValueExpression setVariable(String name,
                                       ValueExpression expr) {
        return expr == null ? variables.remove(name) : variables.put(name, expr);
    }
}
