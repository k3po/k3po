/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.control;

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
