/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.control;

import org.jboss.netty.channel.ChannelFuture;

public class AbortMessage extends ControlMessage {

    private transient ChannelFuture abortFuture;

    public ChannelFuture getAbortFuture() {
        return abortFuture;
    }

    public void setAbortFuture(ChannelFuture abortFuture) {
        this.abortFuture = abortFuture;
    }

    @Override
    public int hashCode() {
        return hashTo();
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || (obj instanceof AbortMessage) && equalTo((AbortMessage) obj);
    }

    @Override
    public Kind getKind() {
        return Kind.ABORT;
    }
}
