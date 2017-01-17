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
import org.kaazing.specification.saml.internal.util.AuthnRequestConfiguration;
import org.kaazing.specification.saml.internal.util.OpenSAMLUtils;
import org.opensaml.core.config.InitializationException;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Response;

public final class Functions {

    @Function
    public static String generateEncryptedResponse(String realm, String authnType) throws Exception {

        GenerateResponse.initializeSAML();
        Response response = GenerateResponse.generateResponse(true);

        String encoded_response = OpenSAMLUtils.compressAndEncodeString(OpenSAMLUtils.SAMLObjectToString(response), false);
        return String.format("%s realm=\"%s\" saml-authn-request=\"%s\"", authnType, realm, encoded_response);
    }

    @Function
    public static String generateResponse(String realm, String authnType) throws Exception {

        GenerateResponse.initializeSAML();
        Response response = GenerateResponse.generateResponse(false);

        String encoded_response = OpenSAMLUtils.compressAndEncodeString(OpenSAMLUtils.SAMLObjectToString(response), false);
        return String.format("%s realm=\"%s\" saml-authn-request=\"%s\"", authnType, realm, encoded_response);
    }

    @Function
    public static String generateAuthnRequest(String realm, String authnType) throws Exception
    {
        GenerateSignedRequest.initializeSAML();
        AuthnRequestConfiguration conf = new AuthnRequestConfiguration();
        AuthnRequest request = GenerateSignedRequest.generateAuthnRequest(conf);

        String encoded_request = OpenSAMLUtils.compressAndEncodeString(OpenSAMLUtils.SAMLObjectToString(request), false);

        return String.format("%s realm=\"%s\" saml-authn-request=\"%s\"", authnType, realm, encoded_request);

    }

    /**
     * This function checks if the string received as parameter encodes a AuthnRequest
     * @param request - the challenge string received from the gateway
     * @throws Exception - throw Exception if any of the operation fails
     */
    @Function
    public static void checkAuthnRequest(String encodedRequest) throws Exception {
        GenerateSignedRequest.initializeSAML();
        String tokenData = getSamlAuthnRequest(encodedRequest);
        if (tokenData == null || tokenData.equals("")) {
            throw new Exception("No token received");
        }
        String decodedRequest = OpenSAMLUtils.decodeAndInflate(tokenData, false);
        OpenSAMLUtils.stringToObject(decodedRequest);
        throw new Exception();
    }

    private static String getSamlAuthnRequest(String rawTokenContent) {
        String result = null;
        if (rawTokenContent != null) {
            int startTokenIndex = rawTokenContent.indexOf("saml-authn-request=");
            if (startTokenIndex == -1) {
                return null; // no token was sent
            }

            startTokenIndex += "saml-authn-request=".length(); // the location of the data is after the
                                                               // 'saml-authn-request' string
            int endTokenIndex = rawTokenContent.indexOf(" ", startTokenIndex);

            // extract token
            if (endTokenIndex != -1) {
                result = rawTokenContent.substring(startTokenIndex, endTokenIndex);
            } else {
                result = rawTokenContent.substring(startTokenIndex);
            }
        }
        return result;
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



    private Functions() throws InitializationException {
        // utility
    }
}
