/**
 * Copyright (c) 2007-2014 Kaazing Corporation. All rights reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.kaazing.specification.http.internal;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kaazing.k3po.lang.el.Function;
import org.kaazing.k3po.lang.el.spi.FunctionMapperSpi;

public final class Functions {

    private static final Random RANDOM = new Random();

    @Function
    public static String randomInvalidVersion() {
        String randomVersion = null;
        Pattern validVersionPattern = Pattern.compile("HTTP/1\\.(\\d)+");
        Matcher validVersionMatcher = null;
        do {
            int randomSize = RANDOM.nextInt(30) + 1;
            randomVersion = randomString(randomSize);
            randomVersion.replaceAll("\\s+", "");
            validVersionMatcher = validVersionPattern.matcher(randomVersion);
        } while (randomVersion.length() > 1 && validVersionMatcher.matches());
        return randomVersion;
    }

    private static String randomString(int length) {
        StringBuffer sb = new StringBuffer(length);
        while (sb.length() < length) {
            char c = (char) (RANDOM.nextInt() & Character.MAX_VALUE);
            if (Character.isDefined(c)) {
                sb.append(c);
                // DEBUG
                System.out.println(Integer.toHexString(c));
            }
        }
        return sb.toString();
    }

    public static class Mapper extends FunctionMapperSpi.Reflective {

        public Mapper() {
            super(Functions.class);
        }

        @Override
        public String getPrefixName() {
            return "http";
        }
    }

    private Functions() {
        // utility
    }
}
