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

package org.kaazing.robot.lang.ast;

import org.kaazing.robot.lang.ast.value.AstValue;
import static org.kaazing.robot.lang.ast.util.AstUtil.equivalent;

public abstract class AstOptionNode extends AstStreamableNode {

    private String optionName;
    private AstValue optionValue;

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public AstValue getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(AstValue optionValue) {
        this.optionValue = optionValue;
    }

    protected int hashTo() {
        int hashCode = super.hashTo();

        if (optionName != null) {
            hashCode <<= 4;
            hashCode ^= optionName.hashCode();
        }

        if (optionValue != null) {
            hashCode <<= 4;
            hashCode ^= optionValue.hashCode();
        }

        return hashCode;
    }

    protected boolean equalTo(AstOptionNode that) {
        return equivalent(this.optionName, that.optionName) && equivalent(this.optionValue, that.optionValue);
    }
}