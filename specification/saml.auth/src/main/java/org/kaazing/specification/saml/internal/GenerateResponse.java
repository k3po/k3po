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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.xml.security.utils.EncryptionConstants;
import org.joda.time.DateTime;
import org.kaazing.specification.saml.internal.util.OpenSAMLUtils;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.core.xml.schema.impl.XSStringBuilder;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.AttributeValue;
import org.opensaml.saml.saml2.core.Audience;
import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.StatusMessage;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml.saml2.core.impl.AssertionBuilder;
import org.opensaml.saml.saml2.core.impl.AttributeBuilder;
import org.opensaml.saml.saml2.core.impl.AttributeStatementBuilder;
import org.opensaml.saml.saml2.core.impl.AudienceBuilder;
import org.opensaml.saml.saml2.core.impl.AudienceRestrictionBuilder;
import org.opensaml.saml.saml2.core.impl.AuthnContextBuilder;
import org.opensaml.saml.saml2.core.impl.AuthnContextClassRefBuilder;
import org.opensaml.saml.saml2.core.impl.AuthnStatementBuilder;
import org.opensaml.saml.saml2.core.impl.ConditionsBuilder;
import org.opensaml.saml.saml2.core.impl.NameIDBuilder;
import org.opensaml.saml.saml2.core.impl.ResponseBuilder;
import org.opensaml.saml.saml2.core.impl.StatusBuilder;
import org.opensaml.saml.saml2.core.impl.StatusCodeBuilder;
import org.opensaml.saml.saml2.core.impl.StatusMessageBuilder;
import org.opensaml.saml.saml2.core.impl.SubjectBuilder;
import org.opensaml.saml.saml2.core.impl.SubjectConfirmationBuilder;
import org.opensaml.saml.saml2.core.impl.SubjectConfirmationDataBuilder;
import org.opensaml.xmlsec.config.JavaCryptoValidationInitializer;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;

public class GenerateResponse {

    // private static boolean isBootStrapped = false;

    public static void main(String[] args) throws Exception {

        Response response = generateAuthnResponse();

        String encoded_response = OpenSAMLUtils.compressAndEncodeString(OpenSAMLUtils.SAMLObjectToString(response), false);
        System.out.println(OpenSAMLUtils.SAMLObjectToString(response));
        System.out.println(encoded_response);
    }

    public static Response generateAuthnResponse() throws Exception {
        intializeSAML();
        Response response = new ResponseBuilder().buildObject();
        response.setIssuer(createIssuer());
        response.setID(OpenSAMLUtils.generateSecureRandomId());
        response.setDestination("http://kaazing.example.com");
        response.setStatus(buildStatus("Success", null));
        response.setVersion(SAMLVersion.VERSION_20);
        DateTime issueInstant = new DateTime();
        DateTime notOnOrAfter = new DateTime(issueInstant.getMillis() + getSAMLResponseValidityPeriod() * 60 * 1000);
        response.setIssueInstant(issueInstant);
        Assertion assertion = buildSAMLAssertion(notOnOrAfter, createSampleUserName());
        response.getAssertions().add(assertion);

        signMessage(response);

        return response;
    }

    private static void signMessage(SignableSAMLObject response) throws Exception, MarshallingException, SignatureException {
        Signature signature = OpenSAMLUtils.buildSAMLObject(Signature.class);
        signature.setSigningCredential(CredentialGenerator.getCredential());
        signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
        signature.setCanonicalizationAlgorithm(EncryptionConstants.ALGO_ID_C14N_OMITCOMMENTS);

        response.setSignature(signature);
        XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(response).marshall(response);
        Signer.signObject(signature);
    }

    private static String createSampleUserName() {
        return "Standard Kaazing User"; // Probably can be cryptographic key
    }

    private static int getSAMLResponseValidityPeriod() {
        return 3000;
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

        System.out.println("Initializing OpenSAML");
        InitializationService.initialize();
    }

    private static Issuer createIssuer()
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {

        XMLObjectBuilderFactory builderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();
        QName defaultElementName = (QName) Issuer.class.getDeclaredField("DEFAULT_ELEMENT_NAME").get(null);
        Issuer issuer = (Issuer) builderFactory.getBuilder(defaultElementName).buildObject(defaultElementName);

        issuer.setValue("http://idp.example.com");
        return issuer;
    }

    private static Status buildStatus(String status, String statMsg) {

        Status stat = new StatusBuilder().buildObject();

        // Set the status code
        StatusCode statCode = new StatusCodeBuilder().buildObject();
        statCode.setValue(status);
        stat.setStatusCode(statCode);

        // Set the status Message
        if (statMsg != null) {
            StatusMessage statMesssage = new StatusMessageBuilder().buildObject();
            statMesssage.setMessage(statMsg);
            stat.setStatusMessage(statMesssage);
        }

        return stat;
    }

    private static Assertion buildSAMLAssertion(DateTime notOnOrAfter, String userName) throws Exception {
        DateTime currentTime = new DateTime();
        Assertion samlAssertion = new AssertionBuilder().buildObject();
        samlAssertion.setID(OpenSAMLUtils.generateSecureRandomId());
        samlAssertion.setVersion(SAMLVersion.VERSION_20);
        samlAssertion.setIssuer(createIssuer());
        samlAssertion.setIssueInstant(currentTime);

        Subject subject = buildSubject(notOnOrAfter, userName);
        samlAssertion.setSubject(subject);

        AuthnStatement authStmt = buildAuthnStmt();
        samlAssertion.getAuthnStatements().add(authStmt);

        Conditions conditions = buildConditions(notOnOrAfter, currentTime);
        samlAssertion.setConditions(conditions);

        Map<String, String> claims = new HashMap<>();
        claims.put("uid", "uid0001231");
        samlAssertion.getAttributeStatements().add(buildAttributeStatement(claims));
        signMessage(samlAssertion);
        return samlAssertion;
    }

    private static AttributeStatement buildAttributeStatement(Map<String, String> claims) {
        AttributeStatement attStmt = null;
        if (claims != null) {
            attStmt = new AttributeStatementBuilder().buildObject();
            Iterator<String> ite = claims.keySet().iterator();

            for (int i = 0; i < claims.size(); i++) {
                Attribute attrib = new AttributeBuilder().buildObject();
                String claimUri = ite.next();
                attrib.setName(claimUri);
                attrib.setFriendlyName(claimUri);
                // look
                // https://wiki.shibboleth.net/confluence/display/OpenSAML/OSTwoUsrManJavaAnyTypes
                XSStringBuilder stringBuilder =
                        (XSStringBuilder) XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(XSString.TYPE_NAME);
                XSString stringValue = stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
                stringValue.setValue(claims.get(claimUri));
                attrib.getAttributeValues().add(stringValue);
                attStmt.getAttributes().add(attrib);
            }
        }
        return attStmt;
    }

    private static Subject buildSubject(DateTime notOnOrAfter, String userName) {
        Subject subject = new SubjectBuilder().buildObject();
        NameID nameId = new NameIDBuilder().buildObject();
        String claimValue = null;

        if (claimValue == null) {
            nameId.setValue(userName);
        }

        nameId.setFormat(NameID.EMAIL);

        subject.setNameID(nameId);

        SubjectConfirmation subjectConfirmation = new SubjectConfirmationBuilder().buildObject();
        subjectConfirmation.setMethod("urn:oasis:names:tc:SAML:2.0:cm:bearer");

        SubjectConfirmationData subjectConfirmationData = new SubjectConfirmationDataBuilder().buildObject();
        subjectConfirmationData.setRecipient(getServiceProviderURL());
        subjectConfirmationData.setNotOnOrAfter(notOnOrAfter);

        subjectConfirmation.setSubjectConfirmationData(subjectConfirmationData);
        subject.getSubjectConfirmations().add(subjectConfirmation);
        return subject;
    }

    private static Conditions buildConditions(DateTime notOnOrAfter, DateTime currentTime) {
        AudienceRestriction audienceRestriction = buildAudienceRestriction();

        Conditions conditions = new ConditionsBuilder().buildObject();
        conditions.setNotBefore(currentTime);
        conditions.setNotOnOrAfter(notOnOrAfter);
        conditions.getAudienceRestrictions().add(audienceRestriction);
        return conditions;
    }

    private static AudienceRestriction buildAudienceRestriction() {
        AudienceRestriction audienceRestriction = new AudienceRestrictionBuilder().buildObject();
        Audience issuerAudience = new AudienceBuilder().buildObject();
        issuerAudience.setAudienceURI(getServiceProviderURL());
        audienceRestriction.getAudiences().add(issuerAudience);
        return audienceRestriction;
    }

    private static AuthnStatement buildAuthnStmt() {
        AuthnStatement authStmt = new AuthnStatementBuilder().buildObject();
        authStmt.setAuthnInstant(new DateTime());
        AuthnContext authContext = new AuthnContextBuilder().buildObject();
        AuthnContextClassRef authCtxClassRef = new AuthnContextClassRefBuilder().buildObject();
        authCtxClassRef.setAuthnContextClassRef(AuthnContext.PASSWORD_AUTHN_CTX);
        authContext.setAuthnContextClassRef(authCtxClassRef);
        authStmt.setAuthnContext(authContext);
        return authStmt;
    }

    private static String getServiceProviderURL() {
        return "http://kaazing.example.com";
    }

    // public static void doBootstrap() {
    // if (!isBootStrapped) {
    // try {
    // DefaultBootstrap.bootstrap();
    // isBootStrapped = true;
    // } catch (ConfigurationException e) {
    // System.out.println("Error in bootstrapping the OpenSAML2 library" + e);
    // }
    // }
    // }
}
