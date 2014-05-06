/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.control;

public class StartMessage extends ControlMessage {

    @Override
    public Kind getKind() {
        return Kind.START;
    }

    @Override
    public int hashCode() {
        return super.hashTo();
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || (obj instanceof StartMessage) && equals((StartMessage) obj);
    }

    protected final boolean equals(StartMessage that) {
        return super.equalTo(that);
    }

}
