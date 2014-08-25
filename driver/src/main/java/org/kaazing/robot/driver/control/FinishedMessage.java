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

package org.kaazing.robot.driver.control;

import java.util.Objects;

public class FinishedMessage extends ControlMessage {

    private String observedScript = "";
    private String expectedScript = "";

    public String getExpectedScript() {
        return expectedScript;
    }

    public void setExpectedScript(String expectedScript) {
        this.expectedScript = expectedScript;
    }

    public String getObservedScript() {
        return observedScript;
    }

    public void setObservedScript(String observedScript) {
        this.observedScript = observedScript;
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashTo();

        hashCode <<= 8;
        hashCode ^= (observedScript != null) ? observedScript.hashCode() : 0;

        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || (obj instanceof FinishedMessage) && equals((FinishedMessage) obj);
    }

    protected final boolean equals(FinishedMessage that) {
        return super.equalTo(that) && Objects.equals(this.observedScript, that.observedScript)
                && Objects.equals(this.expectedScript, that.expectedScript);
    }

    @Override
    public Kind getKind() {
        return Kind.FINISHED;
    }

}
