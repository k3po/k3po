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
package org.kaazing.k3po.pcap.converter.internal.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Stack;
import java.util.logging.Logger;

import org.gjt.xpp.XmlPullParser;
import org.gjt.xpp.XmlPullParserException;
import org.gjt.xpp.XmlPullParserFactory;
import org.gjt.xpp.XmlStartTag;
import org.kaazing.k3po.pcap.converter.internal.packet.Packet;

/**
 * PdmlParser will parse tcpdumps by converting them into a pdml, It will then return a Packet that states the relevant
 * properties of what was just read
 *
 */
public class Parser {

    private final static Logger LOG = Logger.getLogger(Parser.class.getName());
    private final XmlPullParser parser;
    private final TcpdumpReader tcpdumpReader;
    private final Stack<String> protoStack = new Stack<>();
    private final Stack<String> fieldStack = new Stack<>();

    private static XmlPullParserFactory xppFactory;

    private int packetsCnted = 0;

    /**
     * Initializes a PdmlParser Object from a tcpdump file location, during the construction of the object a pdml file
     * will be generated
     * @param tcpDumpFileLocation an absolutePath to the tcpdump file
     * @param pdmlFileDestination an absolute path to where a pdml file will be generated
     * @param tsharkPath an absolute path to where tshark can be called from the runtime
     */
    public Parser(InputStream tcpdumpInputStream, InputStream pdmlInputStream, String tsharkPath) {
        try {
            xppFactory = XmlPullParserFactory.newInstance();
            xppFactory.setNamespaceAware(false);
            parser = xppFactory.newPullParser();
            parser.setInput(new InputStreamReader(pdmlInputStream));
        }
        catch (XmlPullParserException e) {
            e.printStackTrace();
            throw new ParserFailureException("Failed to init parser: " + e.getMessage());
        }
        tcpdumpReader = new TcpdumpReader(tcpdumpInputStream);
    }
    
    /**
     * Constructor that will default the tshark location to tshark (ie assuming started with that set as an environment
     * variable, which will not be true if running from eclipse)
     * @param tcpDumpFileLocation
     * @param pdmlFileDestination
     */
    public Parser(InputStream tcpdumpInputStream, InputStream pdmlInputStream) {
        this(tcpdumpInputStream, pdmlInputStream, "tshark");
    }

    /**
     * Returns the next Packet that can be parsed, or null if all packets have been read
     * @return Packet
     */
    public Packet getNextPacket() {
        Packet parsedPacket = parseNextPacketFromPdml();
        
        if ( parsedPacket == null ) {       // All packets have been read
            return null;
        }
        
        parsedPacket = addTcpdumpInfoToPacket(parsedPacket);
        
        return parsedPacket;
    }

    /**
     * Adds tcpdump info onto packet parsed from pdml i.e. adds the packet payload for various protocols
     * @param parsedPacket
     * @return
     */
    private Packet addTcpdumpInfoToPacket(Packet parsedPacket){ 

        int packetSize = parsedPacket.getPacketSize();
        
        tcpdumpReader.readNewPacket(packetSize);

        // Get Tcp Payload
        if ( parsedPacket.isTcp() ) {       
            int payloadSize = parsedPacket.getTcpPayloadSize();
            int payloadStart = parsedPacket.getTcpPayloadStart();
            parsedPacket.setTcpPayload(tcpdumpReader.getPayload(payloadStart, payloadSize));
        }
        
        tcpdumpReader.packetReadComplete();
        
        return parsedPacket;
    }
    
    /**
     * Returns the Packet set with all properties that can be read from the pdml
     * @return
     */
    private Packet parseNextPacketFromPdml() {
        Packet currentPacket = null;
        int eventType;
        fieldStack.empty(); 
        try {
            eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                case XmlPullParser.START_TAG:
                    if ( parser.getRawName().contains("packet") ) { // At packet tag
                        currentPacket = setPacketProperties(xppFactory.newStartTag());
                    }
                    else if ( parser.getRawName().contains("proto") ) {
                        currentPacket = setProtoProperties(xppFactory.newStartTag(), currentPacket);
                    }
                    else if ( parser.getRawName().contains("field") ) {
                        fieldStack.push("field");
                        // Added http after all others 
                        if (protoStack.peek().equals("http") && currentPacket.isTcp()){
                            currentPacket = setHttpFieldProperties(xppFactory.newStartTag(), currentPacket);
                        }
                        else{
                        	currentPacket = setFieldProperties(xppFactory.newStartTag(), currentPacket);
                        }
                    }
                    else {
                        ;
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ( parser.getRawName().contains("packet") ) {
                        eventType = parser.next();
                        return currentPacket;
                    }
                    else if ( parser.getRawName().contains("proto") ) {
                        protoStack.pop();
                    }
                    else if ( parser.getRawName().contains("field") ) {
                        fieldStack.pop();
                    }
                default:
                }
                eventType = parser.next();
            }
            return null; // Returned if at end of pdml
        }
        catch (XmlPullParserException e) {
            e.printStackTrace();
            throw new ParserFailureException("Failed parsing pmdl " + e);
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new ParserFailureException("Failed reading parser.next " + e);
        }
    }

    private Packet setPacketProperties(XmlStartTag st) throws XmlPullParserException {
        LOG.fine("Reading new packet");
        Packet pc = new Packet();
        pc.setPacketNumber(packetsCnted++);
        return pc;
    }

    private Packet setProtoProperties(XmlStartTag st, Packet currentPacket) throws XmlPullParserException {
        parser.readStartTag(st);
        XmlAttributesHashMap<String, String> attributes = new XmlAttributesHashMap<>();
        for (int i = 0; i < st.getAttributeCount(); i++) {
            attributes.put(st.getAttributeLocalName(i).trim(), st.getAttributeValue(i).trim());
        }

        protoStack.push(attributes.get("name"));
        //tcp
        if ( attributes.checkIfEqual("name", "geninfo") ) {
            currentPacket.setPacketSize(Integer.parseInt(attributes.get("size")));
        }
        //geninfo
        else if ( attributes.checkIfEqual("name", "tcp") ) {
            currentPacket.setTcp(true);
            currentPacket.setTcpPayloadStart(Integer.parseInt(attributes.get("pos"))
                    + (Integer.parseInt(attributes.get("size"))));
        } 
        else if ( attributes.checkIfEqual("name", "fake-field-wrapper") ) {
            currentPacket.setFragmented(true);
        } 
        else if ( attributes.checkIfEqual("name", "http")){
            // don't set http if this packet is the last fragment of a fragmented HTTP request/response
            // because we have no logic to reassemble the packet content, and anyway we want the script 
            // to maintain the original (fragmented) nature of the traffic
            if (!currentPacket.isFragmented()) {
                currentPacket.setHttp(true);
            }
        }
        return currentPacket;
    }

    private Packet setFieldProperties(XmlStartTag st, Packet currentPacket) throws XmlPullParserException {
        parser.readStartTag(st);
        XmlAttributesHashMap<String, String> attributes = new XmlAttributesHashMap<>();
        for (int i = 0; i < st.getAttributeCount(); i++) {
            attributes.put(st.getAttributeLocalName(i).trim(), st.getAttributeValue(i).trim());
        }
        //tcp
        if ( attributes.checkIfEqual("name", "tcp.srcport") ) {
            currentPacket.setTcpSrcPort(Integer.parseInt(attributes.get("show")));
        }
        if ( attributes.checkIfEqual("name", "tcp.dstport") ) {
            currentPacket.setTcpDestPort(Integer.parseInt(attributes.get("show")));
        }
        if ( attributes.checkIfEqual("name", "tcp.len") ) {
            currentPacket.setTcpPayloadLength(Integer.parseInt(attributes.get("show")));
            currentPacket.setTcpLen(Integer.parseInt(attributes.get("show")));
        }
        if ( attributes.checkIfEqual("name", "tcp.flags")){
            String show = attributes.get("show");
            // Wireshark 1.8.5 pdml format had 0x... hex value in show. In 1.12.1 it has just the decimal value.
            int flags = show.startsWith("0x") ? Integer.parseInt(show.substring(2), 16)
                                              : Integer.parseInt(show);
            currentPacket.setTcpFlags(flags);
        }
        if ( attributes.checkIfEqual("name", "tcp.seq") ) {
            currentPacket.setRelativeTcpSeqNum(Integer.parseInt(attributes.get("show")));
        }
        if ( attributes.checkIfEqual("name", "tcp.nxtseq") ) {
            currentPacket.setTcpNextRelativeSeqNum(Integer.parseInt(attributes.get("show")));
        }
        if ( attributes.checkIfEqual("name", "tcp.flags.ack") ) {
            String value = attributes.get("value");
            // Wireshark 1.99.1 pdml format had FFF... for tcp.flag value. 
            int flagV = value.startsWith("FFF") ? 1
                                                : Integer.parseInt(value);
            currentPacket.setTcpFlagsAck(1 == flagV);
        }
        if ( attributes.checkIfEqual("name", "tcp.flags.syn") ) {
            String value = attributes.get("value");
            // Wireshark 1.99.1 pdml format had FFF... for tcp.flag value. 
            int flagV = value.startsWith("FFF") ? 1
                    : Integer.parseInt(value);
            currentPacket.setTcpFlagsSyn(1 == flagV);
        }
        if ( attributes.checkIfEqual("name", "tcp.flags.fin") ) {
            String value = attributes.get("value");
            // Wireshark 1.99.1 pdml format had FFF... for tcp.flag value. 
            int flagV = value.startsWith("FFF") ? 1
                    : Integer.parseInt(value);
            currentPacket.setTcpFlagsFin(1 == flagV);
        }
        if ( attributes.checkIfEqual("name", "tcp.flags.reset") ) {
            String value = attributes.get("value");
            // Wireshark 1.99.1 pdml format had FFF... for tcp.flag value. 
            int flagV = value.startsWith("FFF") ? 1
                    : Integer.parseInt(value);
            currentPacket.setTcpFlagsReset(1 == flagV);
        }
        if ( attributes.checkIfEqual("name", "ip.src_host") ){
            currentPacket.setIp(true);
            currentPacket.setSrcIpAddr(attributes.get("show"));
        }
        if ( attributes.checkIfEqual("name", "ip.dst_host") ){
            currentPacket.setDestIpAddr(attributes.get("show"));
        }
        if ( attributes.checkIfEqual("name", "ipv6.host") ){
            currentPacket.setIp(true);
            currentPacket.setSrcIpAddr(attributes.get("show"));
        }
        if ( attributes.checkIfEqual("name", "ipv6.dst") ){
            currentPacket.setDestIpAddr(attributes.get("show"));
        }
        if ( attributes.checkIfEqual("name", "tcp.seq")){
            currentPacket.setTcpSequenceNumber(attributes.get("value"));
        }
        if ( attributes.checkIfEqual("name", "tcp.ack")){
            currentPacket.setTcpAcknowledgementNumber(attributes.get("value"));
        }
        if ( attributes.checkIfEqual("name", "tcp.stream")){
            currentPacket.setTcpStream(Integer.parseInt(attributes.get("show")));
        }
        
        //geninfo
        if ( attributes.checkIfEqual("name", "timestamp") ){
            try {
                currentPacket.setTimeStamp(new SimpleDateFormat("MMM dd, yyyy HH:mm:ss.SSSSSSSSS", Locale.ENGLISH)
                        .parse(attributes.get("show")));
            }
            catch (ParseException e) {
                e.printStackTrace();
                throw new ParserFailureException("Failed to parse simple date: " + attributes.get("show"));
            }
            currentPacket.setTimeInSecondsFromEpoch(Double.parseDouble(attributes.get("value")));
        }
        
        return currentPacket;
    }

    private Packet setHttpFieldProperties(XmlStartTag st, Packet currentPacket) throws XmlPullParserException {

        parser.readStartTag(st);

        XmlAttributesHashMap<String, String> attributes = new XmlAttributesHashMap<>();

        for (int i = 0; i < st.getAttributeCount(); i++) {
            attributes.put(st.getAttributeLocalName(i).trim(), st.getAttributeValue(i).trim());
        }

        // used for id
        if ( attributes.checkIfEqual("name", "http.request.method") ) {
            currentPacket.setHttpRequestType(Packet.RequestType.valueOf(attributes.get("show")));
        }
        else if ( attributes.checkIfEqual("name", "http.request.uri") ) {
            currentPacket.setHttpRequestURI(attributes.get("show"));
        }
        
        // used for formatting of payload
        if(fieldStack.size() == 1){
            String pos = attributes.get("pos");
            String size = attributes.get("size");
            // Some fields don't have pos and size, e.g. data field in http proto packet
            if (pos != null && size != null) {
                currentPacket.addHttpField(attributes.get("name"), attributes.get("show"),
                    Integer.parseInt(pos) - currentPacket.getTcpPayloadStart(), Integer.parseInt(size));
            }
        }
        return currentPacket;
    }

    /**
     * 
     * Miscellaneous class to do safe value comparisons regardless of nonexistent keys
     * 
     * @param <K> This was designed/tested for strings only
     * @param <V> This was designed/tested for strings only
     */
    private class XmlAttributesHashMap<K, V> extends HashMap<K, V> {

        private static final long serialVersionUID = 1L;

        public XmlAttributesHashMap() {
            super();
        }

        public boolean checkIfEqual(K key, V value) {
            if ( this.containsKey(key) && this.get(key).equals(value) )
                return true;
            return false;
        }
    }

}
