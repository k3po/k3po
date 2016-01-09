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
package org.kaazing.specification.socks5.internal;

import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import javax.security.auth.Subject;

import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.MessageProp;
import org.ietf.jgss.Oid;
import org.kaazing.k3po.lang.el.Function;
import org.kaazing.k3po.lang.el.spi.FunctionMapperSpi;

/**
 * Functions that represent calls to the GSS-API authentication scheme such
 * that robot scripts in this project can perform GSS-API authentication.
 */
public final class Functions {
    //
    // The OID of the Kerberos version 5 GSS-API mechanism as defined
    // in RFC 1964.  Used to tell GSS-API that it must use Kerberos.
    //
    private static final Oid krb5Oid;

    static {

        // set up System properties for use with these functions
        // FIXME: Is there a way to do a configuration that has all of these set properly?
        //        The bcsLogin.conf should have the data, but is not quite working as expected.
        System.setProperty("java.security.auth.login.config", "docker-kdc/bcsLogin.conf");
        System.setProperty("java.security.krb5.conf", "docker-kdc/krb5.conf");
        System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
        System.setProperty("sun.security.krb5.principal", "test1/kdc.km.test@KM.TEST");

        try {
            krb5Oid = new Oid("1.2.840.113554.1.2.2");
        } catch (GSSException ex) {
            throw new RuntimeException("Exception creating kerberos OID", ex);
        }
    }

    /**
     * Create a GSS Context from a clients point of view.
     * @param server the name of the host for which the GSS Context is being created
     * @return the GSS Context that a client can use to exchange security tokens for
     *         a secure channel, then wrap()/unpack() messages.
     */
    @Function
    public static GSSContext createClientGSSContext(String server) {
        try {
            GSSManager manager = GSSManager.getInstance();
            GSSName serverName = manager.createName(server, null);

            GSSContext context = manager.createContext(serverName,
                    krb5Oid,
                    null,
                    GSSContext.DEFAULT_LIFETIME);

            context.requestMutualAuth(true);  // Mutual authentication
            context.requestConf(true);  // Will use encryption later
            context.requestInteg(true); // Will use integrity later

            return context;
        } catch (GSSException ex) {
            throw new RuntimeException("Exception creating client GSSContext", ex);
        }
    }

    /**
     * Create a token, from a clients point of view, for establishing a secure
     * communication channel.  This is a client side token so it needs to bootstrap
     * the token creation.
     * @param context GSSContext for which a connection has been established to the remote peer
     * @return a byte[] that represents the token a client can send to a server for
     *         establishing a secure communication channel.
     */
    @Function
    public static byte[] getClientToken(GSSContext context) {
        byte[] initialToken = new byte[0];

        if (!context.isEstablished()) {

            try {
                // token is ignored on the first call
                initialToken = context.initSecContext(initialToken, 0, initialToken.length);

                return getTokenWithLengthPrefix(initialToken);
            } catch (GSSException ex) {
                throw new RuntimeException("Exception getting client token", ex);
            }
        }

        return null;
    }

    /**
     * Utility method for creating a MessageProp object using the default
     * Quality of Protection (0) and the given privacy setting.
     * @param usePrivacy whether or not the messages being sent using this MessageProp
     *        require privacy
     * @return the newly created MessageProp object with default QoP
     */
    @Function
    public static MessageProp createMessageProp(boolean usePrivacy) {
        // default Quality of Protection (0), use privacy (passed int)
        return new MessageProp(0, usePrivacy);
    }

    /**
     * Utility method to call GSSContext.wrap() on a message which will create a byte[]
     * that can be sent to a remote peer.
     * @param context GSSContext for which a connection has been established to the remote peer
     * @param prop the MessageProp object that is used to provide Quality of Protection of the message
     * @param message the bytes of the message to be sent to the remote peer
     * @return the protected bytes of the message that can be sent to and unpacked by the remote peer
     */
    @Function
    public static byte[] wrapMessage(GSSContext context, MessageProp prop, byte[] message) {
        try {
             // wrap the data and return the encrypted token
            byte[] initialToken = context.wrap(message, 0, message.length, prop);

            return getTokenWithLengthPrefix(initialToken);
        } catch (GSSException ex) {
            throw new RuntimeException("Exception wrapping message", ex);
        }
    }

    /**
     * Verify a message integrity check sent by a peer.  If the MIC correctly identifies the
     * message then the peer knows that the remote peer correctly received the message.
     * @param context GSSContext for which a connection has been established to the remote peer
     * @param prop the MessageProp that was used to wrap the original message
     * @param message the bytes of the original message
     * @param mic the bytes received from the remote peer that represent the MIC (like a checksum)
     * @return a boolean whether or not the MIC was correctly verified
     */
    @Function
    public static boolean verifyMIC(GSSContext context, MessageProp prop, byte[] message, byte[] mic) {
        try {
            context.verifyMIC(mic, 0, mic.length,
                    message, 0, message.length,
                    prop);

            return true;
        } catch (GSSException ex) {
            throw new RuntimeException("Exception verifying mic", ex);
        }
    }

    /**
     * Create a GSS Context not tied to any server name.  Peers acting as a server
     * create their context this way.
     * @return the newly created GSS Context
     */
    @Function
    public static GSSContext createServerGSSContext() {
        System.out.println("createServerGSSContext()...");
        try {
            final GSSManager manager = GSSManager.getInstance();

            //
            // Create server credentials to accept kerberos tokens.  This should
            // make use of the sun.security.krb5.principal system property to
            // authenticate with the KDC.
            //
            GSSCredential serverCreds;
            try {
                serverCreds = Subject.doAs(new Subject(), new PrivilegedExceptionAction<GSSCredential>() {
                    public GSSCredential run() throws GSSException {
                        return manager.createCredential(null, GSSCredential.INDEFINITE_LIFETIME, krb5Oid,
                                GSSCredential.ACCEPT_ONLY);
                    }
                });
            } catch (PrivilegedActionException e) {
                throw new RuntimeException("Exception creating server credentials", e);
            }

            //
            // Create the GSSContext used to process requests from clients.  The client
            // requets should use Kerberos since the server credentials are Kerberos
            // based.
            //
            GSSContext retVal = manager.createContext(serverCreds);
            System.out.println("createServerGSSContext(), context: " + retVal);
            return retVal;
        } catch (GSSException ex) {
            System.out.println("createServerGSSContext(), finished with exception");
            throw new RuntimeException("Exception creating server GSSContext", ex);
        }
    }

    /**
     * Accept a client token to establish a secure communication channel.
     * @param context GSSContext for which a connection has been established to the remote peer
     * @param token the client side token (client side, as in the token had
     *        to be bootstrapped by the client and this peer uses that token
     *        to update the GSSContext)
     * @return a boolean to indicate whether the token was used to successfully
     *         establish a communication channel
     */
    @Function
    public static boolean acceptClientToken(GSSContext context, byte[] token) {
        try {
            if (!context.isEstablished()) {
                byte[] nextToken = context.acceptSecContext(token, 0, token.length);
                return nextToken == null;
            }
            return true;
        } catch (GSSException ex) {
            throw new RuntimeException("Exception accepting client token", ex);
        }
    }

    /**
     * Generate a message integrity check for a given received message.
     * @param context GSSContext for which a connection has been established to the remote peer
     * @param prop the MessageProp used for exchanging messages
     * @param message the bytes of the received message
     * @return the bytes of the message integrity check (like a checksum) that is
     *         sent to a peer for verifying that the message was received correctly
     */
    @Function
    public static byte[] generateMIC(GSSContext context, MessageProp prop, byte[] message) {
        try {
            // Ensure the default Quality-of-Protection is applied.
            prop.setQOP(0);

            byte[] initialToken = context.getMIC(message, 0, message.length, prop);

            return getTokenWithLengthPrefix(initialToken);
        } catch (GSSException ex) {
            throw new RuntimeException("Exception generating MIC for message", ex);
        }
    }

    private static byte[] getTokenWithLengthPrefix(byte[] initialToken) {
        byte[] token = new byte[initialToken.length + 4];

        token[0] = (byte) ((initialToken.length >>> 24) & 0XFF);
        token[1] = (byte) ((initialToken.length >>> 16) & 0XFF);
        token[2] = (byte) ((initialToken.length >>> 8) & 0XFF);
        token[3] = (byte) ((initialToken.length >>> 0) & 0XFF);

        System.arraycopy(initialToken, 0, token, 4, initialToken.length);

        return token;
    }

    /**
     * Internal class that maps the functions to the socks5 namespace.
     */
    public static class Mapper extends FunctionMapperSpi.Reflective {

        /**
         * Default constructor.
         */
        public Mapper() {
            super(Functions.class);
        }

        @Override
        public String getPrefixName() {
            return "socks5";
        }

    }

    private Functions() {
        // utility
    }
}
