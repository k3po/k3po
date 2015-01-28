# WebSocket over RTMP Protocol (WSR)

## Abstract
This document specifies the behavior of WebSocket protocol emulated via RTMP.  It can be used to enable full WebSocket 
capabilities for HTTP clients while minimizing the syntactic differences to maintain consistent performance and bandwidth 
utilization profiles.

## Status of This Memo

This document specifies a protocol for the Internet community, and requests discussion and suggestions for improvement.
This memo is written in the style of an IETF RFC, but is _not_ an official IETF RFC.

## Copyright Notice

Copyright (c) 2008 Kaazing Corporation. All rights reserved.

## Table of Contents

  * [Introduction](#introduction)
    * [Background](#background)
    * [Protocol Overview](#protocol-overview)
    * [Design Philosophy](#design-philosophy)
    * [Security Model](#security-model)
    * [Relationship to TCP and HTTP](#relationship-to-tcp-and-http)
    * [Establishing a Connection](#establishing-a-connection)
    * [Subprotocols using the WebSocket Emulation Protocol](#subprotocols-using-the-websocket-emulation-protocol)
  * [Conformance Requirements](#conformance-requirements)
  * [WebSocket over RTMP URIs](#websocket-over-rtmp-uris)
  * [Opening Handshake](#opening-handshake)
    * [Client Handshake Requirements](#client-handshake-requirements)
    * [Server Handshake Requirements](#server-handshake-requirements)
  * ...
  * [References](#references)

## Introduction

### Background

_This section is non-normative._

The Adobe Flash runtime includes support for RTMP through the flash.net.NetConnection API. RTMP is a layered, multiplexed
protocol designed primarily to transport media streams. Flash has awareness of HTTP proxies and will use the HTTP CONNECT method
to establish a full-duplex tunnel for RTMP. This makes RTMP a viable protocol over which one can tunnel WebSocket connections.

### Protocol Overview

_This section is non-normative._

RTMP is a full-duplex multiplexing protocol developed by Macromedia (Adobe) that can be used to stream media over the Internet.
The semantics of the WebSocket protocol can map onto RTMP while preserving similar proxy-traversal characteristics and traffic
patterns. This document defines that mapping.

A WebSocket over RTMP session consists of an initial HTTP request and subsequent RTMP connection. The URLS used to connect over HTTP and RTMP are identical to the WebSocket URL in all ways other than the scheme. If the WebSocket session is secure, the addresses must have HTTPS and RTMPS schemes respectively.

### Design Philosophy

_This section is non-normative._

### Security Model

_This section is non-normative._

### Subprotocols using the WebSocket Emulation Protocol

_This section is non-normative._

## Conformance Requirements

The key words "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL NOT", "SHOULD", "SHOULD NOT", "RECOMMENDED", "MAY", and 
"OPTIONAL" in this document are to be interpreted as described in [[RFC 6455]][RFC6455].

## WebSocket over RTMP URIs

The WebSocket Emulation protocol uses WebSocket protocol URIs, as defined by
[RFC 6455, Section 3](https://tools.ietf.org/html/rfc6455#section-3).

## Opening Handshake

### Client Handshake Requirements

In order to approach the performance characteristics of the WebSocket protocol we need a full-duplex channel on which either the server or the client can send text and binary data with a minimum of overhead.

The HTTP `POST` allows the WSR session to begin with HTTP the same way native WebSocket does. This allows the server to respond with a redirect or inspect cookies. 

The connection is established as follows:

1. HTTP create request `/path/;e/cr`
2. Connect with RTMP TODO example
3. createStream & publish (upstream)
4. createStream & play (downstream)

#### Create Request
In order to establish a WSR connection, the client MUST begin by making an HTTP POST request for the target URL with the equivalent HTTP scheme. If the WSR session is secure, this request MUST use HTTPS. Otherwise, this request must use HTTP.

The create request includes X-* headers that equate to the Sec-* headers from the WebSocket protocol.
```
X-WebSocket-Version: wsr-1.0
X-WebSocket-Protocol: com.kaazing.echo
X-WebSocket-Extensions: x,y,z
```

#### Create Response
The server must return an HTTP response containing the RTMP URL as the text of the response body. The client uses this address to establish the RTMP session over which all subsequent communication occurs.

#### RTMP Connection
The client connects an RTMP session [SupRTMP]. The RTMP connect command MUST contain the objectEncoding property specifying that AMF3 serialized objects will appear in data messages.

#### Stream Creation
After completion of the RTMP connection handshake, the client MUST create eactly two RTMP streams. These two unidirectional streams represent the upstream and downstream halves of a full-duplex WebSocket session.

#### Downstream
The client MUST send a play command on the downstream stream. The server MUST send sample access, stream start, and meta data messages as detailed in the Supplemental RTMP Specification [SupRTMP]. After sending these messages, the server MAY send any data messages on this stream for the duration of the RTMP session.

#### Upstream
The client MUST send a play command on the downstream stream. In RTMP, a client calls publish on a stream in order to open that stream for client->server communication. After receiving publish confirmation from the server, the client MAY send any data messages on this stream for the duration of the RTMP session. 

The emulated WebSocket is now in the `CONNECTED` state.

### Server Handshake Requirements

...

## Messages
RTMP data events may contain serialized objects in Action Message Format version 0 or version 3 [AMF3]. WebSocket emulation messages must use AMF3 serialization. Both frame types from the WebSocket Protocol [WS] have analogs in WSR. The types are:

Text (AMF3 UTF-8 String)
Binary (AMF3 ByteArray)

WSR binary data messages must contain exactly one AMF3 encoded ByteArray and must be sent on a stream named `d`.

WSR text data messages must contain exactly one AMF3 encoded String and must be sent on a stream named `d`.

## Closing
The WebSocket session is considered closed if either the upstream or downstream stream is deleted.

The WebSocket is considered abruptly closed if the TCP session terminates.

## Chunk Tuning
The RTMP chunk stream protocol allows the sender to set the size of subsequent chunks. This chunk size affects all chunk streams in the session. A WebSocket over RTMP implementation MAY set the chunk size to the maximum allowed value, 65536.

## References

[[RFC 2616]][RFC2616]  "Hypertext Transfer Protocol -- HTTP/1.1"

[[RFC 6455]][RFC6455]  "The WebSocket Protocol"

[[RTMP]][RTMP] "RTMP Specification"

[RFC2616]: https://tools.ietf.org/html/rfc2616
[RFC6455]: https://tools.ietf.org/html/rfc6455
[RTMP]: http://www.adobe.com/devnet/rtmp.html
