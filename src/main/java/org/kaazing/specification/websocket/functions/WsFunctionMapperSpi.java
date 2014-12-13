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

package org.kaazing.specification.websocket.functions;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.kaazing.k3po.lang.el.Function;
import org.kaazing.k3po.lang.el.spi.FunctionMapperSpi;

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



