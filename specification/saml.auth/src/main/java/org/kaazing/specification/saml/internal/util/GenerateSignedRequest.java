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
package org.kaazing.specification.saml.internal.util;

import org.joda.time.DateTime;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameIDPolicy;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;
import org.opensaml.xmlsec.config.JavaCryptoValidationInitializer;

public class GenerateSignedRequest {

    public static void main(String[] args) throws MarshallingException, InitializationException {
        AuthnRequestConfiguration conf = new AuthnRequestConfiguration();
        intializeSAML();
        AuthnRequest authnRequest = generateAuthnRequest(conf);
        System.out.println(OpenSAMLUtils.SAMLObjectToString(authnRequest));
    }

    public static AuthnRequest generateAuthnRequest(AuthnRequestConfiguration conf) {
        AuthnRequest authnRequest = OpenSAMLUtils.buildSAMLObject(AuthnRequest.class);
        authnRequest.setIssueInstant(new DateTime());
        authnRequest.setProtocolBinding(SAMLConstants.SAML2_PAOS_BINDING_URI);
        authnRequest.setDestination(conf.getDestination());
        authnRequest.setAssertionConsumerServiceURL(conf.getAssertionConsumerServiceURL());

        authnRequest.setID(OpenSAMLUtils.generateSecureRandomId());
        Issuer issuer = createIssuer(conf);
        authnRequest.setIssuer(issuer);

        NameIDPolicy nameIDPolicy = createNameIDPolicy(conf);
        authnRequest.setNameIDPolicy(nameIDPolicy);

        RequestedAuthnContext requestedAuthnContext = createRequestedAuthnContext(conf);
        authnRequest.setRequestedAuthnContext(requestedAuthnContext);
        return authnRequest;
    }

    private static Issuer createIssuer(AuthnRequestConfiguration conf) {
        Issuer issuer = OpenSAMLUtils.buildSAMLObject(Issuer.class);
        issuer.setValue(conf.getIssuerValue());
        return issuer;
    }

    private static RequestedAuthnContext createRequestedAuthnContext(AuthnRequestConfiguration conf) {
        RequestedAuthnContext requestedAuthnContext = OpenSAMLUtils.buildSAMLObject(RequestedAuthnContext.class);
        AuthnContextClassRef authnContextClassRef = OpenSAMLUtils.buildSAMLObject(AuthnContextClassRef.class);
        authnContextClassRef.setAuthnContextClassRef(conf.getAuthnContextClassRef());
        requestedAuthnContext.getAuthnContextClassRefs().add(authnContextClassRef);
        requestedAuthnContext.setComparison(AuthnContextComparisonTypeEnumeration.MINIMUM);
        return requestedAuthnContext;
    }

    private static NameIDPolicy createNameIDPolicy(AuthnRequestConfiguration conf) {
        // The NameID is the IdP identifier for the user. The NameID policy is a
        // specification from the SP on how it wants the NameID to be created. The format
        // indicates what type of identifier the SP wants for the user. The flag AllowCreate
        // indicates if the receiving IdP is allowed to create a user account if one does not
        // already exist.

        NameIDPolicy nameIDPolicy = OpenSAMLUtils.buildSAMLObject(NameIDPolicy.class);
        nameIDPolicy.setFormat(conf.getNameIDPolicyFormat());
        nameIDPolicy.setAllowCreate(conf.isNameIDPolicyAllowCreate());
        return nameIDPolicy;
    }

    private static void intializeSAML() throws InitializationException {
        JavaCryptoValidationInitializer javaCryptoValidationInitializer = new JavaCryptoValidationInitializer();
        try {
            javaCryptoValidationInitializer.init();
        } catch (InitializationException e) {
            RuntimeException exc = new RuntimeException("Java Crytpo Initialization failed");
            exc.initCause(e);
            throw exc;
        }

        InitializationService.initialize();
    }

}
