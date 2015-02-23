/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.tcpconverter.parser;

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
