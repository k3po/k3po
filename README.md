# K3po PCAP Converter

A utility for converting [PCAP (Packet Captures)](http://en.wikipedia.org/wiki/Pcap) into k3po scripts

### Steps 
1. Obtain a PCAP (There are many ways to do this)

From Linux/Mac/Windows use wireshark

`capture and then save as /Wireshark/tcpdump/... - libcap`

From Linux/Mac machine use tcpdump

`tcpdump -i <interface> -s 0 -w <some-file>`

2. Generate a pdml (Packet Details Markup Language) file from the pcap
run  `tshark -T pdml -r <infile> -V | tee <outputfile.pdml> `
tshark comes as a utility installed via wireshark

    NOTE:   -w provides raw packet data, not text. If you want text output you 
            need to redirect stdout (e.g. using '>'), don't use the -w option
            for this. And -W <file format option> does not show pdml as an option


    Helpful Hint:  Telling tshark to explicitly interpret traffic on ports as http will 
    add more info to the pdml and may allow the converter to create cleaner rpt scripts.  
    By default, tshark will only decode traffic as specific protocols if that port is 
    the default port for the protocol (for example tshark will decode traffic on 80 and 443 as if it was http).
    Example usage:
        `tshark -T pdml -r inputfile.cap -V -d tcp.port==8000,http | tee outputfile.pdml`
	-T pdml,  says to save a pdml
	-d tcp.port==8000,http   tells tshark to interpret tcp.port 8000 traffic as if it was http

3) Generate the K3po Script from the pdml and pcap.

First build this project (mvn clean verify).  Then run

`java -jar target/com.kaazing.k3po.pcap.converter-develop-SNAPSHOT-jar-with-dependencies.jar <tcpDumpFile> <pdmlFile>`
