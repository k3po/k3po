/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.websocket.functions;

import static org.jboss.netty.util.CharsetUtil.UTF_8;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.kaazing.el.Function;
import org.kaazing.el.spi.FunctionMapperSpi;


public class WsFunctionMapperSpi extends FunctionMapperSpi.Reflective {

    // See RFC-6455, section 1.3 Opening Handshake
    private static final byte[] WEBSOCKET_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11".getBytes(UTF_8);

    public static class Functions {

        @Function
        public static byte[] computeHashAsBase64(byte[] keyAsBase64) throws NoSuchAlgorithmException {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            sha1.update(keyAsBase64);
            byte[] digest = sha1.digest(WEBSOCKET_GUID);
            return Base64Util.encode(digest);
        }

    }

    public WsFunctionMapperSpi() {
        super(Functions.class);
    }

    @Override
    public String getPrefixName() {
        return "ws";
    }

}



