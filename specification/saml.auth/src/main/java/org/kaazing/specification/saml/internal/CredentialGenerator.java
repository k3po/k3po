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

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.crypto.KeySupport;

public class CredentialGenerator {

    static BasicCredential singleKey;
    static {
        KeyPair keyPair = null;
        try {
            keyPair = KeySupport.generateKeyPair("RSA", 1024, null);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            System.out.println("ABORTING!!!");
            System.exit(1);
        }
        singleKey = CredentialSupport.getSimpleCredential(keyPair.getPublic(), keyPair.getPrivate());
    }
    public static BasicCredential getCredential() throws Exception {
        return singleKey;
    }

}
