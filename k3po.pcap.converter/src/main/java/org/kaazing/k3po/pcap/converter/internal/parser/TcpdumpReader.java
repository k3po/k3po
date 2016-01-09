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

/**
 * Class that can read a tcpdump file and can maintain position in the tcpdump file It does so by having an internal
 * buffer of the last packet read, it can then return portions of that packet. This is needed to get the packet info
 * such as the payload data that is not in the pdml file. This is an alternative approach to using jpcap which has JNI
 * bindings
 * 
 */
public class TcpdumpReader {

    private InputStream tcpDumpReader;
    private int tcpDumpPos;

    public TcpdumpReader(InputStream tcpdumpInputStream) {
        tcpDumpReader = tcpdumpInputStream;
        tcpDumpPos = 0;
    }

    /*
     * Reads x bytes from tcpdump
     */
    private byte[] readXBytesFromTcpdump(int x) {

        byte[] buffer = new byte[x];

        try {
            int read = tcpDumpReader.read(buffer);
            if ( x == -1 ) {
                throw new ParserFailureException("Attempted to read pass the size of the tcpdump file, tcpDumpPos:"
                        + tcpDumpPos + ", attempted to read " + x + " bytes");
            }
            tcpDumpPos += read;
            if ( read != x )
                throw new ParserFailureException("Tried to read over the size of the file");
            return buffer;
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new ParserFailureException("Failed to read from tcpdump file: ");
        }

    }

    /*
     * Reads the header at the beginning of the file
     */
    private void readTcpDumpFileHeader() {
        readXBytesFromTcpdump(24);
    }

    /**
     * Updates the tcpdump read position over any headers that are included in the tcpdump file format
     */
    private void readTcpDumpHeader() {
        if ( tcpDumpPos == 0 )
            readTcpDumpFileHeader();
        readXBytesFromTcpdump(16);
    }

    private byte[] currentBuffer = null;

    /**
     * Reads a packet into an internal buffer
     * @param packetSize
     */
    public void readNewPacket(int packetSize) {
        readTcpDumpHeader();
        currentBuffer = readXBytesFromTcpdump(packetSize);
    }

    /**
     * Cleans the internal buffer
     */
    public void packetReadComplete() {
        currentBuffer = null;
    }

    /**
     * Returns a portion of the internal buffer
     * @param payloadStart
     * @param payloadSize
     * @return
     */
    public byte[] getPayload(int payloadStart, int payloadSize) {
        byte[] payloadBuffer = new byte[payloadSize];
        System.arraycopy(currentBuffer, payloadStart, payloadBuffer, 0, payloadSize);
        return payloadBuffer;
    }

}
