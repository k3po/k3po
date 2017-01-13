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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IDPCredentialsManager {
    private static Logger logger = LoggerFactory.getLogger(IDPCredentialsManager.class);

    private Credential decryptingCredential = null;
    private Credential signingCredential = null;

    public IDPCredentialsManager(String metaDataPath) {
        try {
            EntityDescriptor metaData = readMetaData(metaDataPath);
            SPSSODescriptor spssoDescriptor = metaData.getSPSSODescriptor(SAMLConstants.SAML20P_NS);
            IDPSSODescriptor idspssoDescriptor = metaData.getIDPSSODescriptor(SAMLConstants.SAML20P_NS);
            List<KeyDescriptor> keyDescriptors = new ArrayList<>();
            if (spssoDescriptor != null) {
                keyDescriptors.addAll(spssoDescriptor.getKeyDescriptors());
            }
            if (idspssoDescriptor != null) {
                keyDescriptors.addAll(idspssoDescriptor.getKeyDescriptors());
            }
            for (KeyDescriptor keyDescr : keyDescriptors) {
                KeyInfo keyInfo = keyDescr.getKeyInfo();
                List<java.security.cert.X509Certificate> certificates = KeyInfoSupport.getCertificates(keyInfo);
                if (certificates.size() != 1) {
                    logger.warn("More then certificate found! Descriptor:  " + keyDescr.toString());
                    continue;
                }
                BasicX509Credential cred = new BasicX509Credential(certificates.get(0));
                if (keyDescr.getUse().equals(UsageType.ENCRYPTION)) {
                    decryptingCredential = cred;
                } else if (keyDescr.getUse().equals(UsageType.SIGNING)) {
                    signingCredential = cred;
                } else {
                    logger.warn("Unkonwn use for the certificate! Descriptor:  " + keyDescr.toString());
                    continue;
                }
            }
            if (decryptingCredential == null) {
                throw new RuntimeException("No information about the decrypting credential!");
            }
            if (signingCredential == null) {
                throw new RuntimeException("No information about the signing credential!");
            }
        } catch (CertificateException e) {
            throw new RuntimeException("Something went wrong reading the certificate!", e);
        }
    }

    private EntityDescriptor readMetaData(String pathToMetaData) {
        BufferedReader br = null;
        logger.debug("Entering readMetaData( " + pathToMetaData + " )");
        InputStream inputStream;
        try {
            URL url = new URL(pathToMetaData);
            try {
                inputStream = url.openStream();
            } catch (IOException e) {
                throw new RuntimeException("Something went wrong reading metadata from the URL " + pathToMetaData, e);
            }
        } catch (MalformedURLException e) {
            try {
                if (pathToMetaData.startsWith("/")) {
                    // inputStream=IDPCredentialsManager.class.getResourceAsStream(pathToMetaData);
                    inputStream = new FileInputStream(new File(pathToMetaData));
                } else {
                    inputStream = new FileInputStream(pathToMetaData);
                }
            } catch (FileNotFoundException e1) {
                throw new RuntimeException("Something went wrong reading metadata from the file " + pathToMetaData, e1);
            }
        }
        try {
            br = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String metaDataString = sb.toString();
            return OpenSAMLUtils.stringToObject(metaDataString);

        } catch (Exception e) {
            throw new RuntimeException("Something went wrong reading metadata!", e);
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public Credential getDecryptingcredential() {
        return decryptingCredential;
    }

    public Credential getSigningcredential() {
        return signingCredential;
    }
}
