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

import java.util.Map;

import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.NameIDType;

public class AuthnRequestConfiguration {
    public static final String AUTHN_REQ_ASSERTION_CONSUMER_SERVICE = "authn.req.assertion.consumer.service";
    private static final String AUTHN_REQ_DESTINATION = "authn.req.destination";
    public static final String AUTHN_REQ_NAME_ID_POLICY_ALLOW_CREATE = "authn.req.nameID.policy.allow.create";
    public static final String AUTHN_REQ_NAME_ID_POLICY_FORMAT = "authn.req.nameID.policy.format";
    public static final String AUTHN_REQ_CONTEXT_CLASS_REF = "authn.req.context.class.ref";
    public static final String AUTHN_REQ_ISSUER_VALUE = "authn.req.issuer.value";

    private String issuerValue = "https://sp.idptestbed/shibboleth";
    private String authnContextClassRef = AuthnContext.PASSWORD_AUTHN_CTX;
    private String nameIDPolicyFormat = NameIDType.TRANSIENT;
    private boolean nameIDPolicyAllowCreate = true;
    private String destination = "http://idp.example.com";
    private String assertionConsumerServiceURL = "https://idptestbed/Shibboleth.sso/SAML2/ECP";

    public AuthnRequestConfiguration(Map<String, ?> options) {
        super();
        if (options.get(AUTHN_REQ_ISSUER_VALUE) != null) {
            this.issuerValue = (String) options.get(AUTHN_REQ_ISSUER_VALUE);
        }
        if (options.get(AUTHN_REQ_CONTEXT_CLASS_REF) != null) {
            this.authnContextClassRef = (String) options.get(AUTHN_REQ_CONTEXT_CLASS_REF);
        }
        if (options.get(AUTHN_REQ_NAME_ID_POLICY_FORMAT) != null) {
            this.nameIDPolicyFormat = (String) options.get(AUTHN_REQ_NAME_ID_POLICY_FORMAT);
        }
        if (options.get(AUTHN_REQ_NAME_ID_POLICY_ALLOW_CREATE) != null) {
            this.nameIDPolicyAllowCreate = Boolean.valueOf((String) options.get(AUTHN_REQ_NAME_ID_POLICY_ALLOW_CREATE));
        }
        if (options.get(AUTHN_REQ_DESTINATION) != null) {
            this.destination = (String) options.get(AUTHN_REQ_DESTINATION);
        }
        if (options.get(AUTHN_REQ_ASSERTION_CONSUMER_SERVICE) != null) {
            this.assertionConsumerServiceURL = (String) options.get(AUTHN_REQ_ASSERTION_CONSUMER_SERVICE);
        }
    }

    public AuthnRequestConfiguration() {
        super();
    }

    public String getIssuerValue() {
        return issuerValue;
    }

    public String getAuthnContextClassRef() {
        return authnContextClassRef;
    }

    public String getNameIDPolicyFormat() {
        return nameIDPolicyFormat;
    }

    public boolean isNameIDPolicyAllowCreate() {
        return nameIDPolicyAllowCreate;
    }

    public String getDestination() {
        return destination;
    }

    public String getAssertionConsumerServiceURL() {
        return assertionConsumerServiceURL;
    }

}
