## WebSocket Extension: x-kaazing-ping-pong

### Abstract

This document specifies an extension layered on top of the WebSocket protocol [WSP]. It is only needed for native web socket transports. Clients request this extension by negotiating a “x-kaazing-ping-pong” extension during the WebSocket handshake. 

### Requirements

The key words "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL NOT", "SHOULD", "SHOULD NOT", "RECOMMENDED", "MAY", and "OPTIONAL" in this document are to be interpreted as described in [RFC 2119](https://tools.ietf.org/html/rfc2119).

## Table of Contents

  * [Introduction](#introduction)
  * [Handshake Negotiation](#handshake-negotiation)
  * [Protocol Definitions: extended PING and PONG](#ex-ping-pong)
  * [References](#references)

### Introduction

This extension applies only when the WebSocket connection is successfully established with the “x-kaazing-ping-pong” extension being affirmatively negotiated. 

The intention of the extension is to allow the Kaazing WebSocket javascript client library to send and receive PING and PONG control frames over native (Browser implemented) WebSockets, which do not expose the standard WebSocket PING and PONG frames to the consumer.

### Handshake Negotiation

The “x-kaazing-ping-pong” WebSocket extension is negotiated during the initial WebSocket handshake.


```
	GET /transport HTTP/1.1\r\n
	Upgrade: websocket\r\n
	…
	Sec-WebSocket-Extension: x-kaazing-ping-pong
	…
```

The extension name is specified on the initial connection handshake. 

```
	HTTP/1.1 101 Switching Protocols\r\n
	Upgrade: websocket\r\n
	…
	Sec-WebSocket-Extension:x-kaazing-ping-pong;<control_bytes>
	…
```
<`control_bytes`> designates four bytes represented in hexadecimal notation, as described in the [WebSocket Extension Control Frame Injection](../control-frame-injection/SPEC.md) specification.

Once the extension is negotiated successfully, the server MAY send extended PING and PONG control frames (as defined below) rather than the standard PING and PONG frames defined by the WebSocket Protocol [WSP], in order to make them visible to the Kaazing WebSocket client library. The server MUST respond to an extended PING frame with an extended PONG frame. But it MUST still respond to standard WebSocket PING frame with a standard PONG. The client MUST respond to an extended PING control frame with an extended PONG frame. The server may also assume that the client will respond to a standard WebSocket PING frame with a standard PONG (since the Browser native WebSocket implementation will take care of this).

### <a name="ex-ping-pong">Protocol Definitions: extended PING and PONG</a>

Once the extension is negotiated, all WebSocket frames are delivered unmodified, but WebSocket frames may include extended PING and PONG frames. These are websocket text frames distinguished by a server-negotiated control frame control byte sequence at the start of the payload, as defined by the [WebSocket Extension Control Frame Injection](../control-frame-injection/SPEC.md) specification. The format of the payload of these frames is the control bytes followed by a standard RFC 6455 PING or PONG frame with the fin bit unset (so it's a valid UTF) and no application data (zero data length):

ping:
	 
        <CONTROL> 0x09 0x00


pong:

        <CONTROL> 0x0a 0x00


where <`CONTROL`> is the four control bytes indicated by the server in the extensions header of the response during the WebSocket connection handshake.

### References

[RFC2119] Bradner, S., "Key words for use in RFCs to Indicate Requirement Levels", [BCP 14](https://tools.ietf.org/html/bcp14), [RFC 2119](https://tools.ietf.org/html/rfc2119), March 1997.

[WSP, RFC 6455] The WebSocket Protocol, I.Fette, IETF, [WSP](http://tools.ietf.org/html/rfc6455)
