/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.k3po.pcap.converter.tcpconverter;

import java.io.FileInputStream;
import java.net.URL;
import java.util.Enumeration;

import org.junit.Assert;
import org.junit.Test;

public class ConverterFullRunTest {
    
    @Test
    public void testCreator1() throws Exception {
        try {
            // get capture file
            Enumeration<URL> captureFileEnum = getClass().getClassLoader()
                    .getResources("tcpdumps/ServerHelloRobot.cap");
            String captureFileLoc = captureFileEnum.nextElement().getPath();
            Assert.assertEquals(captureFileLoc != null, true);

            Enumeration<URL> pdmlFiles = getClass().getClassLoader()
                    .getResources("pdmls/ServerHelloRobot.pdml");
            String pdmlFile = pdmlFiles.nextElement().getPath();
            Assert.assertEquals(pdmlFile != null, true);

            TcpdumpConverter converter = new TcpdumpConverter(new FileInputStream(captureFileLoc), new FileInputStream(pdmlFile));
            converter.convertTcpDumpToRpt();
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testCreator2() throws Exception {
        try {
            // get capture file
            Enumeration<URL> captureFileEnum = getClass().getClassLoader()
                    .getResources("tcpdumps/httpRequestDpwspoon.cap");
            String captureFileLoc = captureFileEnum.nextElement().getPath();
            Assert.assertEquals(captureFileLoc != null, true);

            Enumeration<URL> pdmlFiles = getClass().getClassLoader()
                    .getResources("pdmls/httpRequestDpwspoon.pdml");
            String pdmlFile = pdmlFiles.nextElement().getPath();
            Assert.assertEquals(pdmlFile != null, true);

            TcpdumpConverter converter = new TcpdumpConverter(new FileInputStream(captureFileLoc), new FileInputStream(pdmlFile));
            converter.convertTcpDumpToRpt();
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testCreator3() throws Exception {
        try {
            // get capture file
            Enumeration<URL> captureFileEnum = getClass().getClassLoader()
                    .getResources("tcpdumps/echoorg.cap");
            String captureFileLoc = captureFileEnum.nextElement().getPath();
            Assert.assertEquals(captureFileLoc != null, true);

            Enumeration<URL> pdmlFiles = getClass().getClassLoader()
                    .getResources("pdmls/echoorg.pdml");
            String pdmlFile = pdmlFiles.nextElement().getPath();
            Assert.assertEquals(pdmlFile != null, true);

            TcpdumpConverter converter = new TcpdumpConverter(new FileInputStream(captureFileLoc), new FileInputStream(pdmlFile));
            converter.convertTcpDumpToRpt();
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    
    @Test
    public void testCreator4() throws Exception {
        try {
            // get capture file
            Enumeration<URL> captureFileEnum = getClass().getClassLoader()
                    .getResources("tcpdumps/output.tcpdump.cap");
            String captureFileLoc = captureFileEnum.nextElement().getPath();
            Assert.assertEquals(captureFileLoc != null, true);

            Enumeration<URL> pdmlFiles = getClass().getClassLoader()
                    .getResources("pdmls/output.tcpdump.pdml");
            String pdmlFile = pdmlFiles.nextElement().getPath();
            Assert.assertEquals(pdmlFile != null, true);

            TcpdumpConverter converter = new TcpdumpConverter(new FileInputStream(captureFileLoc), new FileInputStream(pdmlFile));
            converter.convertTcpDumpToRpt();
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    
    @Test
    public void testCreator5() throws Exception {
        try {
            // get capture file
            Enumeration<URL> captureFileEnum = getClass().getClassLoader()
                    .getResources("tcpdumps/verifyClientSSL.pcap");
            String captureFileLoc = captureFileEnum.nextElement().getPath();
            Assert.assertEquals(captureFileLoc != null, true);

            Enumeration<URL> pdmlFiles = getClass().getClassLoader()
                    .getResources("pdmls/ssl.pdml");
            String pdmlFile = pdmlFiles.nextElement().getPath();
            Assert.assertEquals(pdmlFile != null, true);

            TcpdumpConverter converter = new TcpdumpConverter(new FileInputStream(captureFileLoc), new FileInputStream(pdmlFile));
            converter.convertTcpDumpToRpt();
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    
    @Test
    public void testCreatorWireshare1_12_1() throws Exception {
        // get capture file
        Enumeration<URL> captureFileEnum = getClass().getClassLoader()
                .getResources("tcpdumps/amqp091ConnectAndCloseWs.cap");
        String captureFileLoc = captureFileEnum.nextElement().getPath();
        Assert.assertEquals(captureFileLoc != null, true);

        Enumeration<URL> pdmlFiles = getClass().getClassLoader()
                .getResources("pdmls/amqp091ConnectAndCloseWs.pdml");
        String pdmlFile = pdmlFiles.nextElement().getPath();
        Assert.assertEquals(pdmlFile != null, true);

        TcpdumpConverter converter = new TcpdumpConverter(new FileInputStream(captureFileLoc), new FileInputStream(pdmlFile));
        converter.convertTcpDumpToRpt();
    }
    
    @Test
    // Test case for fragmented http request
    public void testCreatorReassembledPackets() throws Exception {
        // get capture file
        Enumeration<URL> captureFileEnum = getClass().getClassLoader()
                .getResources("tcpdumps/ie11_wse_BasicAuth8001_reassembled_packets.cap");
        String captureFileLoc = captureFileEnum.nextElement().getPath();
        Assert.assertEquals(captureFileLoc != null, true);

        Enumeration<URL> pdmlFiles = getClass().getClassLoader()
                .getResources("pdmls/ie11_wse_BasicAuth8001_reassembled_packets.pdml");
        String pdmlFile = pdmlFiles.nextElement().getPath();
        Assert.assertEquals(pdmlFile != null, true);

        TcpdumpConverter converter = new TcpdumpConverter(new FileInputStream(captureFileLoc), new FileInputStream(pdmlFile));
        converter.convertTcpDumpToRpt();
    }
    
    @Test
    public void shouldConvertDotNetClientRevalidatePackets() throws Exception {
        // get capture file
        Enumeration<URL> captureFileEnum = getClass().getClassLoader()
                .getResources("tcpdumps/dotnet-revalidate.cap");
        String captureFileLoc = captureFileEnum.nextElement().getPath();
        Assert.assertEquals(captureFileLoc != null, true);

        Enumeration<URL> pdmlFiles = getClass().getClassLoader()
                .getResources("pdmls/dotnet-revalidate.pdml");
        String pdmlFile = pdmlFiles.nextElement().getPath();
        Assert.assertEquals(pdmlFile != null, true);

        TcpdumpConverter converter = new TcpdumpConverter(new FileInputStream(captureFileLoc), new FileInputStream(pdmlFile));
        converter.convertTcpDumpToRpt();
    }
    
//    @Test
//    public void testHelloRobotCap() throws Exception {
//        try {
//            // get capture file
//            Enumeration<URL> captureFileEnum = getClass().getClassLoader()
//                    .getResources("tcpdumps/ClientHelloRobot.cap");
//            String captureFileLoc = captureFileEnum.nextElement().getPath();
//            Assert.assertEquals(captureFileLoc != null, true);
//
//            TcpdumpConverter converter = new TcpdumpConverter(captureFileLoc, "target/output.pdml", tsharkPath);
//            converter.convertTcpDumpToRpt();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Failed testCreator4");
//            Assert.fail();
//        }
//    }
//
//    @Test
//    public void testCreator3() throws Exception {
//        try {
//            // get capture file
//            Enumeration<URL> captureFileEnum = getClass().getClassLoader().getResources(
//                    "tcpdumps/gatewaylocalhost8001.cap");
//            String captureFileLoc = captureFileEnum.nextElement().getPath();
//            Assert.assertEquals(captureFileLoc != null, true);
//
//            TcpdumpConverter converter = new TcpdumpConverter(captureFileLoc, "target/output.pdml", tsharkPath);
//            converter.convertTcpDumpToRpt();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Failed testCreator4");
//            Assert.fail();
//        }
//    }
//
//    @Test
//    public void testCreator4() throws Exception {
//        try {
//            // get capture file
//            Enumeration<URL> captureFileEnum = getClass().getClassLoader().getResources("tcpdumps/echo.cap");
//            String captureFileLoc = captureFileEnum.nextElement().getPath();
//            Assert.assertEquals(captureFileLoc != null, true);
//
//            TcpdumpConverter converter = new TcpdumpConverter(captureFileLoc, "target/output.pdml", tsharkPath);
//            converter.convertTcpDumpToRpt();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Failed testCreator4");
//            Assert.fail();
//        }
//    }
//
//    @Test
//    public void testCreator5() throws Exception {
//        try {
//            // get capture file
//            Enumeration<URL> captureFileEnum = getClass().getClassLoader().getResources(
//                    "tcpdumps/core.server.rtests.cap");
//            String captureFileLoc = captureFileEnum.nextElement().getPath();
//            Assert.assertEquals(captureFileLoc != null, true);
//
//            TcpdumpConverter converter = new TcpdumpConverter(captureFileLoc, "target/output.pdml", tsharkPath);
//            converter.convertTcpDumpToRpt();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Failed testCreator4");
//            Assert.fail();
//        }
//    }
//
//    @Test
//    public void testCreator6() throws Exception {
//        try {
//            // get capture file
//            Enumeration<URL> captureFileEnum = getClass().getClassLoader().getResources("tcpdumps/echoorg.cap");
//            String captureFileLoc = captureFileEnum.nextElement().getPath();
//            Assert.assertEquals(captureFileLoc != null, true);
//            
//            TcpdumpConverter converter = new TcpdumpConverter(captureFileLoc, "target/output.pdml", tsharkPath);
//            converter.convertTcpDumpToRpt();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Failed testCreator4");
//            Assert.fail();
//        }
//    }
}

