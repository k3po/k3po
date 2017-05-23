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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;

import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.SignableSAMLObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.security.RandomIdentifierGenerationStrategy;
import net.shibboleth.utilities.java.support.xml.BasicParserPool;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

public class OpenSAMLUtils {
    private static Logger logger = LoggerFactory.getLogger(OpenSAMLUtils.class);
    private static RandomIdentifierGenerationStrategy secureRandomIdGenerator;

    static {
        secureRandomIdGenerator = new RandomIdentifierGenerationStrategy();
    }

    @SuppressWarnings("unchecked")
    public static <T> T buildSAMLObject(final Class<T> clazz) {
        T object;
        try {
            XMLObjectBuilderFactory builderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();
            QName defaultElementName = (QName) clazz.getDeclaredField("DEFAULT_ELEMENT_NAME").get(null);
            object = (T) builderFactory.getBuilder(defaultElementName).buildObject(defaultElementName);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new IllegalArgumentException("Could not create SAML object");
        }

        return object;
    }

    public static String generateSecureRandomId() {
        return secureRandomIdGenerator.generateIdentifier();
    }

    public static void logSAMLObject(final XMLObject object) {
        logger.info(SAMLObjectToString(object));
    }

    public static String SAMLObjectToString(final XMLObject object) {
        Element element;

        if (object instanceof SignableSAMLObject && ((SignableSAMLObject) object).isSigned() && object.getDOM() != null) {
            element = object.getDOM();
        } else {
            try {
                Marshaller out = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(object);
                if (out != null) {
                    out.marshall(object);
                }
                element = object.getDOM();

            } catch (MarshallingException e) {
                logger.error(e.getMessage(), e);
                return null;
            }
        }

        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(element);

            transformer.transform(source, result);

            return result.getWriter().toString();
        } catch (TransformerException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static <T> T stringToObject(String source)
            throws ComponentInitializationException, XMLParserException, UnmarshallingException {
        BasicParserPool pool = new BasicParserPool();
        pool.initialize();
        Document inCommonMDoc = pool.parse(new StringReader(source));

        Element metadataRoot = inCommonMDoc.getDocumentElement();
        @SuppressWarnings("unchecked") T ret = (T) XMLObjectProviderRegistrySupport.getUnmarshallerFactory()
                .getUnmarshaller(metadataRoot).unmarshall(metadataRoot);
        return ret;

    }

    public static String compressAndEncodeString(String str, boolean compress) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Encoder encoder = Base64.getEncoder();
        byte[] base64EncodedArray, arrayToEncode;
        if (compress) {
            DeflaterOutputStream def;
            // create deflater without header
            def = new DeflaterOutputStream(out, new Deflater(Deflater.BEST_COMPRESSION, true));
            def.write(str.getBytes());
            def.close();
            arrayToEncode = out.toByteArray();
        } else {
            arrayToEncode = str.getBytes();
        }
        base64EncodedArray = encoder.encode(arrayToEncode);
        return new String(base64EncodedArray, 0, base64EncodedArray.length, "UTF-8");
    }

    public static String decodeAndInflate(String encodedData, boolean inflate) throws UnsupportedEncodingException {

        byte[] base64DecodedByteArray = Base64.getMimeDecoder().decode(encodedData);
        byte[] xmlMessageBytes;
        int resultLength;
        if (inflate) {
            Inflater inflater = new Inflater(true);
            inflater.setInput(base64DecodedByteArray);
            xmlMessageBytes = new byte[15000];
            try {
                resultLength = inflater.inflate(xmlMessageBytes);
            } catch (DataFormatException e) {
                RuntimeException r = new RuntimeException("Inflation error!", e);
                throw r;
            }
            if (!inflater.finished()) {
                throw new RuntimeException("didn't allocate enough space to hold " + "decompressed data");
            }
            inflater.end();
        } else {
            xmlMessageBytes = base64DecodedByteArray;
            resultLength = xmlMessageBytes.length;
        }

        return new String(xmlMessageBytes, 0, resultLength, "UTF-8");

    }
}
