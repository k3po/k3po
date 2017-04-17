# Kaazing SOCKS 5+ protocol

## Abstract

This document specifies an extended form of the standard SOCKS 5 protocol used for Enterprise Shield (a.k.a. reverse connectivity). This is sometimes referred to as SOCKS 5+.

## Requirements
The key words "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL NOT", "SHOULD", "SHOULD NOT", "RECOMMENDED", "MAY", and "OPTIONAL" in this document are to be interpreted as described in [RFC2119][].

## The Protocol
Kaazing SOCKS 5+ protocol conforms to [SOCKS 5] except that an extra ATYP (address type) is supported, as follows:

URI address: X'FE'

When this ATYP is used, the desired destination address (DST.ADDR), which immediately follows, MUST take the following form:
* 4 byte integer length, in network octet order
* The URI string value
* NUL byte: X'00'

The DST.PORT is a 2 byte port value in network octet order (as for the other address types). For URIs where the scheme has no notion of the port number, for example, "pipe://foo", DST.PORT SHOULD be specified as zero (two NUL octets). For URIs including a port number, DST.PORT MUST be sent to that value.

## References
* SOCKS Protocol Version 5, [RFC1928][]
* Bradner, S., "Key words for use in RFCs to Indicate Requirement Levels", [BCP14][]

[RFC2119]: https://tools.ietf.org/html/rfc2119 "RFC 2119"
[RFC1928]: https://tools.ietf.org/html/rfc1928 "RFC 1928"
[BCP14]: https://tools.ietf.org/html/bcp14 "BCP 14"