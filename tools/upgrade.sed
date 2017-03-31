#
# HOWTO Upgrade
#
# find . -name "*.rpt" | xargs -L 1 sed -i "~" -e $'s/\r//'
# find . -name "*.rpt" | xargs -L 1 sed -i "~" -f tools/upgrade.sed
#

#
# Tidy
#
s/^[[:space:]][[:space:]]*connect/connect/
s/^[[:space:]][[:space:]]*accept/accept/
s/^[[:space:]][[:space:]]*read/read/
s/^[[:space:]][[:space:]]*write/write/

s/\([^\"\'#]*\)connect[[:space:]][[:space:]]*/\1connect /
s/\([^\"\'#]*\)accept[[:space:]][[:space:]]*/\1accept /
s/\([^\"\'#]*\)read[[:space:]][[:space:]]*/\1read /
s/\([^\"\'#]*\)write[[:space:]][[:space:]]*/\1write /
s/\([^\"\'#]*\)option[[:space:]][[:space:]]*/\1option /

#
# URI literals
#
s/\(^[^\"\'#]*[[:space:]][[:space:]]*\)\([a-z]*:[^[:space:]]*\/[^[:space:]]*\)/\1\"\2\"/

#
# HTTP accept and connect options
#
s/option transport/option http:transport/

#
# HTTP read configs
#
s/read method/read http:method/
s/read header/read http:header/
s/read parameter/read http:parameter/
s/read status/read http:status/
s/read version/read http:version/
s/read trailer/read http:trailer/
s/read option chunkExtension/read option http:chunkExtension/

#
# HTTP write configs
#
s/write request/write http:request/
s/write method/write http:method/
s/write header host/write http:host/
s/write header content-length/write http:content-length/
s/write header/write http:header/
s/write parameter/write http:parameter/
s/write status/write http:status/
s/write version/write http:version/
s/write trailer/write http:trailer/

#
# file accept and connect options
#
s/option mode r/option file:mode "r"/
s/option mode rw/option file:mode "rw"/
s/option size (\d+)/option file:size \\1L/

#
# file read options
#
s/read option offset/read option file:offset/

#
# file write options
#
s/write option offset/write option file:offset/

#
# agrona accept and connect options
#
s/option reader/option agrona:reader/
s/option writer/option agrona:writer/

#
# UDP accept and connect options
#
s/option timeout/option udp:timeout/

#
# connect await BARRIER
#         location
#
1h;2,$H;$!d;g; s/connect await \([a-zA-Z0-9_]*\)\nconnect /connect await \1\
        /g
