----------------------------------------
TCPDUMP to PCAP Conversion

1) Capturing 
    
    a) Get capture via tcpdump
    
        tcpdump -i <interface> -s 0 -w <some-file>

    b) Get capture via wireshark

        capture and then save as /Wireshark/tcpdump/... - libcap

        note: You can save filtered wiresharks this way as well

2) tcpdump to pdml (Will be done automatically in program)

    tshark -T pdml -r <infile> -V | tee <outputfile.pdml> 

    -w <outfile> has the following man page note...

    NOTE:   -w provides raw packet data, not text. If you want text output you 
            need to redirect stdout (e.g. using '>'), don't use the -w option
            for this.

    and -W <file format option> does not show pdml as an option


    Helpful Hint:  Telling tshark to explicitly interpret traffic on ports as http will 
    add more info to the pdml and may allow the converter to create cleaner rpt scripts.  
    By default, tshark will only decode traffic as specific protocols if that port is 
    the default port for the protocol (for example tshark will decode traffic on 80 and 443 as if it was http).
    Example usage:
        tshark -T pdml -r inputfile.cap -V -d tcp.port==8000,http | tee outputfile.pdml
	
	    -T pdml,  says to save a pdml
	    -d tcp.port==8000,http   tells tshark to interpret tcp.port 8000 traffic as if it was http


-----------------------------------------
MVN GOALS

o convertTcpdumpToRupert:
    -parameters:
        tcpdumpFile:    Passed in via pom, -D, or set in stopTcpdump stage
        stream:         how do we identify this (tcpstream?)
                        Taken in pom, -D, (userinput ?)
