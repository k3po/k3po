package org.kaazing.robot.behavior.handler.codec;

import org.jboss.netty.buffer.ChannelBuffer;

public abstract class MaskingDecoder {

    public abstract ChannelBuffer applyMask(ChannelBuffer buffer) throws Exception;

    public abstract ChannelBuffer undoMask(ChannelBuffer buffer) throws Exception;
}
