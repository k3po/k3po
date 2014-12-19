/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kaazing.specification.ws.functions;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.kaazing.k3po.lang.el.Function;
import org.kaazing.k3po.lang.el.spi.FunctionMapperSpi;

public class StringFunctionMapperSpi extends FunctionMapperSpi.Reflective {

    public static class Functions {

        @Function
        public static byte[] asBytes(String utf8) {
            return utf8.getBytes(UTF_8);
        }

        @Function
        public static String fromBytes(byte[] bytes) {
            return new String(bytes, UTF_8);
        }

    }

    public StringFunctionMapperSpi() {
        super(Functions.class);
    }

    @Override
    public String getPrefixName() {
        return "string";
    }

}
