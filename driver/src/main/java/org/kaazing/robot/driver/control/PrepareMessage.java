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

public class PrepareMessage extends ControlMessage {

    private Kind compatibilityKind = Kind.PREPARE;
    private String expectedScript = "";
    private String scriptFormatOverride;

    public boolean hasFormatOverride() {
        return scriptFormatOverride != null;
    }

    public String getScriptFormatOverride() {
        if (!hasFormatOverride()) {
            throw new IllegalStateException("There is no script format override");
        }
        return scriptFormatOverride;
    }

    public void setScriptFormatOverride(String scriptFormat) {
        this.scriptFormatOverride = scriptFormat;
    }

    public String getExpectedScript() {
        return expectedScript;
    }

    public void setExpectedScript(String expectedScript) {
        this.expectedScript = expectedScript;
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashTo();

        hashCode <<= 4;
        hashCode ^= (expectedScript != null) ? expectedScript.hashCode() : 0;

        hashCode <<= 4;
        hashCode += (scriptFormatOverride != null) ? scriptFormatOverride.hashCode() : 0;
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || (obj instanceof PrepareMessage) && equals((PrepareMessage) obj);
    }

    protected final boolean equals(PrepareMessage that) {
        // @formatter:off
        return super.equalTo(that)
                && (this.compatibilityKind == that.compatibilityKind)
                && (this.expectedScript == that.expectedScript
                    || (this.expectedScript != null && this.expectedScript.equals(that.expectedScript))
                && (this.scriptFormatOverride == that.scriptFormatOverride));
        // @formatter:on
    }

    @Override
    public Kind getKind() {
        return Kind.PREPARE;
    }

    public Kind getCompatibilityKind() {
        return compatibilityKind;
    }

    public void setCompatibilityKind(Kind compatibilityKind) {
        this.compatibilityKind = compatibilityKind;
    }

}
