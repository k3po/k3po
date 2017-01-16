/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
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
package org.kaazing.specification.saml.internal;

import org.kaazing.k3po.lang.el.Function;
import org.kaazing.k3po.lang.el.spi.FunctionMapperSpi;
import org.kaazing.specification.saml.internal.util.OpenSAMLUtils;
import org.opensaml.saml.saml2.core.Response;

public final class Functions {

    @Function
    public static String generateResponse() throws Exception {

        Response response = GenerateResponse.generateAuthnResponse();

        String encoded_response = OpenSAMLUtils.compressAndEncodeString(OpenSAMLUtils.SAMLObjectToString(response), false);
        return encoded_response;

    }

    @Function
    public static String generateResponse(String realm, String authnType) throws Exception {

        Response response = GenerateResponse.generateAuthnResponse();

        String encoded_response = OpenSAMLUtils.compressAndEncodeString(OpenSAMLUtils.SAMLObjectToString(response), false);
        return encoded_response;

    }

    public static class Mapper extends FunctionMapperSpi.Reflective {
        public Mapper() {
            super(Functions.class);
        }

        @Override
        public String getPrefixName() {
            return "saml";
        }
    }



    private Functions() {
        // utility
    }
}
