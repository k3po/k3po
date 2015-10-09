# <a name="ws-ext">WebSocket Extension: x-kaazing-idle-timeout</a>

## Abstract

This document specifies an extension layered on top of the WebSocket protocol [WSP](#references). It will work for both native and emulated web socket transports. Clients signal their support for this extension by negotiating a "x-kaazing-idle-timeout" extension during the WebSocket handshake. This allows the server to take advantage of it when so configured.

## Requirements

The key words "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL NOT", "SHOULD", "SHOULD NOT", "RECOMMENDED", "MAY", and "OPTIONAL" in this document are to be interpreted as described in [RFC 2119](https://tools.ietf.org/html/rfc2119).

* [WebSocket Extension: x-kaazing-idle-timeout](#ws-ext)
* [Abstract](#abstract)
* [Requirements](#requirements)
* [Introduction](#introduction)
* [Handshake Negotiation](#handshake-negotiation)
* [References](#references)

## Introduction

This extension applies only when the WebSocket connection is successfully established with the "x-kaazing-idle-timeout" extension being affirmatively negotiated. As part of that negotiation, the server passes a timeout value in milliseconds to the client. 

The intention of the extension is to cause the client to close the WebSocket connection if network connectivity is lost: that is, if no data is received for a period exceeding the specified timeout. In the absence of downstream application data, the server will send control messages (PONG) frequently enough to prevent the client from timing out.

## Handshake Negotiation

The "x-kaazing-idle-timeout" WebSocket extension is negotiated during the initial WebSocket handshake.

```
GET /transport HTTP/1.1\r\n
Upgrade: websocket\r\n

Sec-WebSocket-Extension: x-kaazing-idle-timeout

```

The extension name is specified on the initial connection handshake. The response includes a timeout parameter, to establish that the idle timeout is required.

```
HTTP/1.1 101 Switching Protocols\r\n
Upgrade: websocket\r\n

Sec-WebSocket-Extension: x-kaazing-idle-timeout;timeout=<timeout-millis>

```

When the extension is negotiated successfully, this indicates the server _requires the client to activate idle timeout_. The server indicates the timeout value in milliseconds, as an extension parameter with the name "timeout" and the value MUST be a positive, non-zero integer, which we will refer to as **\<timeout-millis\>**. The server MUST send a WebSocket frame every **\<timeout-millis\>** milliseconds. Such frames may either be normal application data, or, if the application is not sending data, either PING or PONG control frames. PING frames may be used if there is no application data flowing in either direction and the server is configured to check the client is still alive. PONG frames may be used if there is application data flowing from client to server but no application data is flowing from server to client. If the [x-kaazing-ping-pong](../../../../x-kaazing-ping-pong/specification/ws.extensions/x-kaazing-ping-pong/SPEC.md) extension is enabled, extension PING or PONG frames as defined by that extension MUST be used, otherwise, standard PING or PONG frames are used (as defined by the WebSocket protocol [WSP](http://tools.ietf.org/html/rfc6455)). The client MUST close the WebSocket connection if no downstream frames (of any nature) are received for a period equal to or exceeding **\<timeout-millis\>**.  The client MAY then attempt to reconnect using a new WebSocket connection as required by a higher level protocol (e.g. STOMP over WebSocket).

## References

[RFC2119]  Bradner, S., "Key words for use in RFCs to Indicate Requirement Levels", [BCP 14](https://tools.ietf.org/html/bcp14), [RFC 2119](https://tools.ietf.org/html/rfc2119), March 1997.

[WSP] The WebSocket Protocol, I.Fette, IETF, [WSP](http://tools.ietf.org/html/rfc6455)

[x-kaazing-ping-pong] WebSocket Extension: x-kaazing-ping-pong.
