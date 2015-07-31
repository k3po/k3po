# WebSocket Emulation Protocol (WSE)

## Abstract
This document specifies the behavior of WebSocket protocol emulated via HTTP.  It can be used to enable full WebSocket 
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
  * [WebSocket Emulation URIs](#websocket-emulation)
  * [Opening Handshake](#opening-handshake)
    * [Client Handshake Requirements](#client-handshake-requirements)
    * [Server Handshake Requirements](#server-handshake-requirements)
  * [Attaching the Downstream Response](#attaching-the-downstream-response)
    * [Client Downstream Requirements](#client-downstream-requirements)
    * [Server Downstream Requirements](#server-downstream-requirements)
  * [Sending the Upstream Request](#sending-the-upstream-request)
    * [Client Upstream Requirements](#client-upstream-requirements)
    * [Server Upstream Requirements](#server-upstream-requirements)
  * [Data Frames](#data-frames)
  * [Command Frames](#command-frames)
  * [Control Frames](#control-frames)
  * [Closing Handshake](#closing-handshake)
    * [Client Close Requirements](#client-close-requirements)
    * [Server Close Requirements](#server-close-requirements)
  * [Browser Considerations](#browser-considerations)
    * [Content Type Sniffing](#content-type-sniffing)
    * [Binary as Text](#binary-as-text)
    * [Binary as Escaped Text](#binary-as-escaped-text)
    * [Binary as Mixed Text](#binary-as-mixed-text)
    * [Garbage Collection](#garbage-collection)
  * [Proxy Considerations](#proxy-considerations)
    * [Buffering Proxies](#buffering-proxies)
    * [Destructive Proxies](#destructive-proxies)
  * [Request Sequencing](#request-sequencing)
  * [References](#references)

## Introduction

### Background

_This section is non-normative._

When the W3C WebSocket API and corresponding network protocol was first defined by W3C in the HTML5 specification in 2008, even 
though all major browser vendors endorsed the specification, practical deployment of WebSocket applications required a 
strategy to support all browsers, whether or not those browsers already supported the WebSocket specification.

In addition, plug-in technologies such as Flash, Silverlight and Java did not initially support WebSocket either.

The WebSocket Emulation protocol addresses practical WebSocket deployment issues by defining a protocol that depends only on 
typical application usage of HTTP, with solutions for the various limitations found in many commonly deployed HTTP client 
implementations.

### Protocol Overview

_This section is non-normative._

The WebSocket Emulation protocol is layered on top of HTTP and has three parts: an HTTP handshake, an HTTP client-to-server 
(upstream) data transfer, and an HTTP server-to-client (downstream) data transfer.

### Design Philosophy

_This section is non-normative._

The WebSocket Emulation Protocol (WSE) is designed to support a client-side WebSocket API with identical semantics
when compared to a WebSocket API using the WebSocket protocol defined by [[RFC 6455]][RFC6455] while
only making use of (perhaps limited) HTTP APIs at the client, and a corresponding WebSocket Emulation server implementation.

For example, it should be possible to provide a compatible [[W3C WebSocket API][W3C WebSocket API]] in JavaScript
using only an HTML4 browser's HTTP APIs to implement the WebSocket Emulation protocol. Notably, the end-user should not be 
required to install any browser plug-ins, instead the emulated WebSocket API should be delivered as part of the Web application 
JavaScript, perhaps using an HTML `<script>` tag.

The WebSocket Emulation protocol overhead is kept to a minimum such that performance and scalability can be considered 
approximately equivalent when compared to the WebSocket protocol defined by [[RFC 6455]][RFC6455], especially when the 
majority of data transfer flows from server-to-client.

### Security Model

_This section is non-normative._

WebSocket Emulation protocol is layered on top of HTTP and therefore inherits the [CORS](http://www.w3.org/TR/cors) origin model 
when used from browser clients.

### Subprotocols using the WebSocket Emulation Protocol

_This section is non-normative._

The client can request that the server use a specific subprotocol by including the `X-WebSocket-Protocol` HTTP header in its 
handshake. If the `X-WebSocket-Protocol` HTTP header is specified by the client, the server needs to include the same HTTP
header with one of the selected subprotocol values in the handshake response for the emulated WebSocket connection to be 
established.

These subprotocol names should follow the guidelines described by the WebSocket protocol in 
[RFC 6455, Section 1.9, paragraphs 2 and 3](https://tools.ietf.org/html/rfc6455#section-1.9). 

## Conformance Requirements

The key words "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL NOT", "SHOULD", "SHOULD NOT", "RECOMMENDED", "MAY", and 
"OPTIONAL" in this document are to be interpreted as described in [[RFC 6455]][RFC6455].

## WebSocket Emulatation URIs

The WebSocket Emulation protocol uses WebSocket protocol URIs, as defined by
[RFC 6455, Section 3](https://tools.ietf.org/html/rfc6455#section-3).

## Opening Handshake

### Client Handshake Requirements

To establish an emulated WebSocket connection, a client makes an HTTP handshake request.

* the HTTP handshake request uri scheme MUST be derived from the WebSocket URL by changing `ws` to `http`, or `wss` to `https`
* the HTTP handshake request method MUST be `POST` 
* the HTTP handshake request uri path MUST be derived from the WebSocket URL by appending a suitable handshake encoding path 
  suffix
  * `/;e/cb` for binary encoding
  * `/;e/ct` for text encoding  (see [Binary as Text](#binary-as-text))
  * `/;e/cte` for escaped text encoding  (see [Binary as Escaped Text](#binary-as-escaped-text)) 
  * `/;e/ctm` for mixed text encoding  (see [Binary as Mixed Text](#binary-as-mixed-text))
* the HTTP handshake request path query parameters MUST include all query parameters from the WebSocket URL

Browser clients MUST send the `Origin` HTTP header with the source origin.

Clients MUST send the `X-WebSocket-Version` HTTP header with the value `wseb-1.0`.

Clients MUST send the `X-Sequence-No` HTTP header. Please see [Request Sequencing](#request-sequencing) for details.

Clients MAY send the `X-Websocket-Protocol` HTTP header with a list of alternative subprotocols to use over the emulated 
WebSocket connection.

Clients MAY send the `X-Websocket-Extensions` HTTP header with a list of extensions supported by the client for this emulated 
WebSocket connection.

Clients SHOULD send the `X-Accept-Commands` HTTP header with the value `ping` to indicate that both `PING` and `PONG` frames 
are understood by the client.

Clients MUST send an empty handshake request body.

For example, given the WebSocket Emulation URL `ws://host.example.com:8080/path?query` and binary encoding.

```
POST /path/;e/cb?query HTTP/1.1
Host: host.example.com:8080
Origin: [source-origin]
Content-Length: 0
X-WebSocket-Version: wseb-1.0
X-WebSocket-Protocol: x,y,z
X-Accept-Commands: ping
X-Sequence-Number: 5
```

When the handshake request is sent, the emulated WebSocket is in the `CONNECTING` state.

__Note:__ the HTTP client runtime MAY have automatically processed an HTTP redirect (status code `3xx`) or an HTTP authorization
challenge (status code `401`) before the application sees the effective HTTP handshake response.  Processing these status codes
is considered outside the scope of this specification.

A successful WebSocket Emulation handshake response has status code `201` and the response body with content type 
`text/plain;charset=utf-8` consisting of two lines each followed by a `\n` linefeed character.

The first line is an `http` or `https` URL for upstream data transfer of the emulated WebSocket connection.

The second line is an `http` or `https`  URL for downstream data transfer of the emulated WebSocket connection.

The upstream and downstream data transfer URLs MAY use different ports than the original WebSocket URL, and they MAY each 
optionally include query parameters.

For any handshake response status code other than `201`, the client MUST fail the emulated WebSocket connection.

For any handshake response content type other than `text/plain;charset=utf-8`, the client MUST fail the emulated WebSocket 
connection.

For any handshake response `X-WebSocket-Version` HTTP header value not matching the value sent in the handshake request, 
clients MUST fail the emulated WebSocket connection.

For any handshake response `X-WebSocket-Protocol` HTTP header value not matching one of the values sent in the handshake
request, the client MUST fail the emulated WebSocket connection.

For any handshake response `X-WebSocket-Extensions` HTTP header value indicating the use of an extension not matching one of 
the extensions sent in the handshake request, the client MUST fail the emulated WebSocket connection.

For an upstream data transfer URL scheme other than `http` or `https`, the client MUST fail the emulated WebSocket 
connection.

For an upstream data transfer URL scheme other than `https` when the original handshake URL uses `https`, the client MUST fail 
the emulated WebSocket connection.

For an upstream data transfer URL host not matching the host of the original WebSocket URL, the client MUST fail the emulated 
WebSocket connection.

For an upstream data transfer URL path not prefixed by the path of the original WebSocket URL, the client MUST fail the 
emulated WebSocket connection.

For a downstream data transfer URL scheme other than `http` or `https`, the client MUST fail the emulated WebSocket 
connection.

For a downstream data transfer URL scheme other than `https` when the original handshake URL uses `https`, the client MUST fail
the emulated WebSocket connection.

For a downstream data transfer URL host not matching the host of the original WebSocket URL, the client MUST fail the emulated 
WebSocket connection.

For a downstream data transfer URL path not prefixed by the path of the original WebSocket URL, the client MUST fail the 
emulated WebSocket connection.

```
HTTP/1.1 201 Created
X-WebSocket-Version: wseb-1.0
X-WebSocket-Protocol: x
Content-Type: text/plain;charset=utf-8
Content-Length: 105

http://host.example.com:8080/path/uofdbnreiodfkbqi
https://host.example.com:8443/path/kwebfbkjwehkdsfa
```

The emulated WebSocket is now in the `CONNECTED` state.

### Server Handshake Requirements

When a client starts an emulated WebSocket connection, it sends an HTTP handshake request.  The server must parse and and 
process the handshake request to generate a handshake response.

If the server determines that any of the following conditions are not met by the HTTP handshake request, then the server MUST
send an HTTP response with a `4xx` status code, such as `400 Bad Request`.

* the HTTP handshake request method MUST be `POST` 
* the HTTP handshake request header `X-WebSocket-Version` MUST have the value `wseb-1.0`
* the HTTP handshake request header `X-Sequence-No` MUST be a valid sequence number. Please see [Request Sequencing](#request-sequencing) for details.
* the HTTP handshake request header `X-WebSocket-Protocol` is OPTIONAL, and when present indicates a list of alternative 
  protocols to speak in client preference order
* the HTTP handshake request header `X-WebSocket-Extensions` is OPTIONAL, and when present indicates a list of extensions
  supported by the client
* the HTTP handshake request header `X-Accept-Commands` is OPTIONAL, and when present MUST have the value `ping`

If the `X-Accept-Commands` HTTP header is present, then the server MAY send `PING` and `PONG` frames to the client.

The server SHOULD ignore any request body and MAY choose to enforce a maximum handshake request body length.

If any of the above conditions are not met, the server MUST reject the handshake request with a `400 Bad Request` status code.

Otherwise, the server processes the HTTP handshake request and generates an HTTP handshake response as follows.

__Note:__ the server MAY send an HTTP redirect (status code `3xx`) or an HTTP authorization challenge (status code `401`) before the generating the final HTTP handshake response.  Responding with these status codes is considered outside the scope of this 
specification.

* the HTTP handshake response status MUST be `201`
* the HTTP handshake response header `X-WebSocket-Version` MUST have the value `wseb-1.0`
* the HTTP handshake response header `Content-Type` MUST have the value `text/plain;charset=utf-8`
* the HTTP handshake response header `X-WebSocket-Protocol` MUST have one of the values negotiated by the client, if any were
sent by the client
* the HTTP handshake response header `X-WebSocket-Extensions` is OPTIONAL, with a list of client-supported extensions that are
enabled by the server for this emulated WebSocket connection
* the HTTP handshake response body must contain two URLs each by a `\n` (LF) character

The first URL in the response body is the upstream data transfer URL.

The second URL in the response body is the downstream data transfer URL.

Each URL MAY change the handshake HTTP request scheme from `http` to `https` and MAY select a different port number, but the 
host MUST remain the same as the host for the HTTP handshake request.

If a `;` is present in the original handshake request URL path then each URL MUST consist of a unique path prefixed by the 
original handshake request URL path, up to but not including the `;`.

If no `;` is present in the original handshake request URL path then each URL MUST consist of a unique path prefixed by the 
original handshake request URL path.

## Attaching the Downstream Response

### Client Downstream Requirements

Once the emulated WebSocket connection is established, the client MUST send an HTTP request for downstream data
transfer.
* the HTTP downstream request method MUST be `GET` 
* the HTTP downstream request `Origin` header MUST be present with the source origin for browser clients
* Clients MUST send the `X-Sequence-No` HTTP header. Please see [Request Sequencing](#request-sequencing) for details.

The downstream request associates a continuously streaming HTTP response to the emulated WebSocket connection.

For example, with a downstream data transfer URL `https://host.example.com:8443/path/kwebfbkjwehkdsfa`.

```
GET /path/kwebfbkjwehkdsfa HTTP/1.1
Host: host.example.com:8443
Origin: [source-origin]
X-Sequence-No: 6
```

When the receives a downstream HTTP response status code of `200`, complete with all HTTP headers, this indicates to the client
that the downstream HTTP response is ready to deliver emulated WebSocket frames to the client.

For any downstream HTTP response status code other than `200`, the client MUST fail the emulated WebSocket connection.

For any binary downstream response content type other than `application/octet-stream`, the client MUST fail the emulated 
WebSocket connection.

```
HTTP/1.1 200 OK
Content-Type: application/octet-stream
Connection: close

[emulated websocket frames]
```

The downstream response MAY use `Connection: close` or `Transfer-Encoding: chunked` to provide a continuously streaming response
to the client.

See [Buffering Proxies](#buffering-proxies) for further client requirements when attaching the downstream.

If the downstream HTTP response transfer is complete, or else complete but does not end in a `RECONNECT` command frame, then the 
client should consider this as unexpected connection loss for the emulated WebSocket connection.

If the client receives any frames on the HTTP downstream response after the `RECONNECT` frame, the client should fail the
emulated WebSocket connection.

### Server Downstream Requirements

When processing a binary HTTP downstream request the server generates an HTTP downstream response for the emulated WebSocket.

See [Binary as Text](#binary-as-text) for details of processing a text HTTP downstream request.

See [Binary as Escaped Text](#binary-as-escaped-text) for details of processing an escaped text HTTP downstream request.

See [Binary as Mixed Text](#binary-as-mixed-text) for details of processing a mixed text HTTP downstream request.

If the emulated WebSocket cannot be located for the HTTP downstream request path, then the server MUST generate an HTTP response
with a `404 Not Found` status code.

If `X-Sequence-No` header is missing in downstream request, then the server MUST generate an HTTP response with a `400 Bad Request` status code and fail the WSE connection.

If the sequence number received in `X-Sequence-No` header is out of order or invalid, the server MUST generate an HTTP response with a `400 Bad Request` status code and fail the WSE connection. Please see [Request Sequencing](#request-sequencing) for details.

If the `.ki` query parameter is present with value `p`, see [Buffering Proxies](#buffering-proxies) for further server 
requirements when attaching the downstream.

Otherwise, if `.ki` query parameter is _not_ present with the value `p`, the server generates an HTTP downstream response as
follows.
* the downstream HTTP response status code MUST have the value `200`
* the downstream HTTP `Content-Type` header MUST have the value `application/octet-stream` 
* the downstream HTTP `Connection` header MUST have the value `close` 

The server MUST flush these HTTP response headers to the client even before data is available to send to the client.

If the `.kp` parameter is present, see [Content-Type Sniffing](#content-type-sniffing) for further server requirements when 
attaching the downstream.

## Sending the Upstream Request

### Client Upstream Requirements

Any upstream data frames are sent in the payload of a transient HTTP upstream request.
* the HTTP upstream request method MUST be `POST`
* the HTTP upstream request `Content-Type` HTTP header MUST be `application/octet-stream`
* Clients MUST send the `X-Sequence-No` HTTP header. Please see [Request Sequencing](#request-sequencing) for details.

For example, with an upstream data transfer URL `http://host.example.com:8080/path/uofdbnreiodfkbqi`.
```
POST /path/uofdbnreiodfkbqi HTTP/1.1
Host: host.example.com:8080
Origin: [source-origin]
X-Sequence-No: 6
Content-Type: application/octet-stream
Content-Length: [size]

[emulated websocket frames]
```

An upstream response status code of `200` indicates that the frames were received successfully by the emulated WebSocket 
connection.

For any upstream response status code other than `200`, the client MUST fail the emulated WebSocket connection.

The client MUST NOT send another upstream request before the previous upstream response has completed.

### Server Upstream Requirements

When processing a binary HTTP upstream request the server generates an HTTP upstream response for the emulated WebSocket.

See [Binary as Text](#binary-as-text) for details of processing a text HTTP upstream request.

See [Binary as Escaped Text](#binary-as-escaped-text) for details of processing an escaped text HTTP upstream request.

See [Binary as Mixed Text](#binary-as-mixed-text) for details of processing a mixed text HTTP upstream request.

If the emulated WebSocket cannot be located for the HTTP upstream request path, then the server MUST generate an HTTP response
with a `404 Not Found` status code.

If the emulated WebSocket is already processing an HTTP upstream request, then the server MUST generate an HTTP response
with a `400 Bad Request` status code and fail the emulated WebSocket connection.

If `X-Sequence-No` header is missing in upstream request, then the server MUST generate an HTTP response with a `400 Bad Request` status code and fail the WSE connection.

If the sequence number received via `X-Sequence-No` header is out of order or invalid, the server MUST generate an HTTP response with a `400 Bad Request` status code and fail the WSE connection. Please see [Request Sequencing](#request-sequencing) for details.

Otherwise, the server decodes the emulated WebSocket frames from the upstream request body and generates an HTTP upstream 
response as follows.
* the upstream HTTP response body MUST have status code `200` 
* the upstream HTTP response body MUST be empty with a `Content-Length` header value of `0` 

The server MAY choose to delay the HTTP upstream response until some or all of the emulated WebSocket frames from the upstream
request body have been processed to throttle the emulated WebSocket upstream from the client.

If the upstream HTTP request transfer is either incomplete or else complete but does not end in a `RECONNECT` command frame, 
then the server should consider this as unexpected connection loss for the emulated WebSocket connection.

## Data Frames

The client requirements for data frame syntax are defined by 
[Draft-76, Section 4.2](http://tools.ietf.org/html/draft-hixie-thewebsocketprotocol-76#section-4.2).

The server requirements for data frame syntax are defined by 
[Draft-76, Section 5.3](http://tools.ietf.org/html/draft-hixie-thewebsocketprotocol-76#section-5.3).

## Command Frames

The frames used by an emulated WebSocket connection for upstream and downstream data transfer extend those defined by 
[Draft-76](http://tools.ietf.org/html/draft-hixie-thewebsocketprotocol-76), by adding a command frame.

The text-based command frame has frame type `0x01`, which masks to `0x00` indicating text content.

The content of each command frame is a sequence of bytes encoded as hex.  For example, the bytes `5` `B` (`0x35 0x42`) decode to 
the hypothetical command hex code `0x5B`. 

| Hex Code | Text Payload | Text as Bytes | Description                 |
|----------|--------------|---------------|-----------------------------|
| 0x00     | "00"         | 0x30 0x30     | NOP (padding and heartbeat) |
| 0x01     | "01"         | 0x30 0x31     | RECONNECT                   |
| 0x02     | "02"         | 0x30 0x32     | CLOSE                       |

## Control Frames

The frames used by an emulated WebSocket connection for upstream and downstream communication extend those defined by 
[Draft-76](http://tools.ietf.org/html/draft-hixie-thewebsocketprotocol-76), by adding `PING` and `PONG` control frames.

These control frames have the leading bit set, which in [Draft-76](http://tools.ietf.org/html/draft-hixie-thewebsocketprotocol-76)
indicates a binary frame payload.

The `PING` control frame code is `0x89`. This is the same frame code as `PING` in [[RFC 6455]][RFC6455] but only supports a
zero length payload.

The `PONG` control frame code is `0x8A`. This is the same frame code as `PONG` in [[RFC 6455]][RFC6455] but only supports a 
zero length payload.

If a client does not include the HTTP `X-Accept-Commands` header with a value of `ping` in the handshake request, then the 
server MUST NOT send a `PING` or `PONG` frame to the client.

If a client does not include the HTTP `X-Accept-Commands` header with a value of `ping` in the handshake request, then the 
server SHOULD fail the emulated WebSocket connection if it receives a `PING` or `PONG` frame from the client.

## Closing Handshake

### Client Close Requirements

To start the closing handshake, the client MUST send a `CLOSE` command frame on the upstream, followed by a `RECONNECT` frame
to mark the end of the upstream request body.

```
0x01 "02"
0x01 "01"
```

The client's emulated WebSocket connection is now in the `CLOSING` state.

When the client receives a `CLOSE` command frame from the server on the HTTP downstream response, the client's emulated 
WebSocket connection transitions to the `CLOSED` state.

The client SHOULD ignore any data frames received on the downstream HTTP response after the `CLOSE` command frame and before 
the `RECONNECT` command frame.

When the client receives a `CLOSE` command frame followed by a `RECONNECT` command frame, the client SHOULD abort the 
downstream HTTP response if the downstream HTTP response has not already completed.

### Server Close Requirements

To start the closing handshake, the server MUST send a `CLOSE` command frame on the downstream HTTP response, followed by a 
`RECONNECT` frame and then complete the downstream HTTP response.  When the downstream HTTP response uses HTTP header
`Connection` with a value of `close`, the downstream HTTP response is completed by terminating the underlying transport.

```
0x01 "02"
0x01 "01"
```

When the server receives a `CLOSE` command frame followed by a `RECONNECT` command frame, the server MUST consider the emulated
WebSocketd connection to be closed.

## Proxy Considerations

### Buffering Proxies
Some HTTP intermediaries, such as transparent and explicit proxies, prevent HTTP responses from being successfully streamed to 
the client.  Instead, such intermediaries may buffer the response introducing undesirable latency for the downstream HTTP 
response of an emulated WebSocket connection.  Once such downstream buffering is detected, there are two possible solutions; 
long-polling, and secure streaming.

The client detects the presence of a buffering proxy by timing out the arrival of the status code and complete headers from the
streaming downstream HTTP response.  When such buffering is detected, some emulated WebSocket frames may be blocked at the 
proxy, so to avoid data loss, the client sends a second overlapping downstream HTTP request, with the `.ki=p` query parameter 
to indicate to the server that the `interaction mode` is `proxy`.

In reaction to receiving this overlapping `proxy` mode downstream HTTP request for the same emulated WebSocket connection, the 
initial downstream HTTP response is completed normally with a `RECONNECT` command frame, automatically flushing the entire 
response body through any buffering proxy.

Then, the response to the second downstream request either redirects the client to an encrypted location for secure streaming, 
or fall back to long-polling as a last resort.

When long-polling, any frame sent to the client triggers a controlled reconnect in the same way as
[Garbage Collection](#garbage-collection).  In this case, the `Content-Length` downstream HTTP response header SHOULD be
calculated and used instead of the `Connection` downstream HTTP response header with value `close`, so that the long-polling
TCP connection can be reused. 

In extremely rare situations, where an SSL-terminating load balancer sitting in front of the emulated WebSocket server is 
acting as a buffering intermediary, long-polling can still be used over SSL-terminated `https`.

### Destructive Proxies
Some HTTP intermediaries, such as transparent and explicit proxies, attempt to reclaim resources for idle HTTP responses that 
are still incomplete after a timeout period.  A heartbeat command frame must be sent to the client at regular intervals to 
prevent the downstream from being severed by the proxy.

When long-polling, the heartbeat frame triggers the completion of the current downstream HTTP response, and the client then 
performs an immediate reconnect of the downstream with another downstream HTTP request.

Clients MAY request a shorter heartbeat interval by setting the `.kkt` query parameter. For example, a query parameter of 
`.kkt=20` requests a 20 second interval. This is necessary for either user agents or HTTP intermediaries that shut down 
in-flight HTTP requests after only 30 seconds.

## Browser Considerations

### Content-Type Sniffing
During the early days of the Web when HTML files were served with the wrong content type, `text/plain`.
Some browsers, such as Internet Explorer, determined it was best to infer the content type intended by the author.  Other
browsers then followed the same approach, so as not to appear _broken_ when compared with Internet Explorer.

For HTTP responses served with content type `text/plain`, browsers can infer the implied response content type by buffering a 
certain amount of the response body for analysis before exposing any of the response body to the application.  This prevents 
delivery of any emulated WebSocket frames until the buffer size is exceeded. The exact buffer size can be determined by 
experimentation for different browser implementations. In some cases however it is not necessary.

Padding the beginning of the downstream HTTP response with additional control frames to exceed the content sniffing buffer size
allows immediate delivery of emulated WebSocket frames to be delivered to such clients.  The maximum amount of padding required
is communicated in the request via the `.kp` query parameter.
```
GET /path/kwebfbkjwehkdsfa?.kp=256 HTTP/1.1
Host: host.example.com:8443
Origin: [source-origin]
```
IE8+ supports a special HTTP response header that can be used to switch off content type sniffing, thus eliminating the need to
send any extra padding at the beginning of the response.
```
HTTP/1.1 200 OK
Content-Type: text/plain;charset=windows-1252
Connection: close
X-Content-Type-Options: nosniff

[emulated websocket frames]
```
Even when padding the body with command frames, IE7 determines the downstream response content type to be 
`application/octet-stream`, preventing programmatic access to the downstream HTTP response text from JavaScript.

This issue only comes up in a special case of HTTP request emulation [HTTPE], where response headers are promoted to the 
beginning of the response body anyway, so setting a sufficiently long header with text value fills the content type sniffing 
buffer at the client side, allowing the text-based content type to be properly detected and proving programmatic access to the 
response text from IE7’s JavaScript runtime. 
```
GET /path/kwebfbkjwehkdsfa?.kns=1 HTTP/1.1
Host: host.example.com:8443
Origin: [source-origin]
```
The behavior to inject a long text header is triggered by the presence of the `.kns=1` query parameter above.
```
HTTP/1.1 200 OK
Content-Type: text/plain;charset=windows-1252
Connection: close
X-Content-Type-Options: nosniff

HTTP/1.1 200 OK
Content-Type: text/plain;charset=windows-1252
X-Content-Type-Nosniff: [long text string]

[emulated websocket frames]
```

### Binary as Text
The JavaScript execution environment provided by browsers did not historically have support for binary data types, so all HTTP 
responses accessible to JavaScript were described as strings.

```
POST /path/;e/ct HTTP/1.1
Host: host.example.com:8080
Origin: [source-origin]
Content-Length: 0
```
Here, the handshake request location path uses `/;e/ct` instead

```
HTTP/1.1 201 Created
X-WebSocket-Version: wseb-1.1
Content-Type: text/plain;charset=utf-8
Content-Length: 105

http://host.example.com:8080/path/uofdbnreiodfkbqi
https://host.example.com:8443/path/kwebfbkjwehkdsfa
```

The binary-as-text downstream HTTP response MUST have content type `text/plain;charset=windows-1252`.
```
GET /path/kwebfbkjwehkdsfa HTTP/1.1
Host: host.example.com:8443
Origin: [source-origin]
```
```
HTTP/1.1 200 OK
Content-Type: text/plain;charset=windows-1252
Connection: close

[emulated websocket frames]
```
The charset `windows-1252` [[Windows-1252][Windows-1252]] allows bytes to be transferred by the server without modification
because each character is represented by a single byte and exactly 256 distinct character codes are available, and 224 of those
character codes match their byte representations when processed by the client.  The remaining character codes can be mapped to
the corresponding binary representations accordingly.

### Binary as Escaped Text

Some browsers have difficulty representing a few specific character codes in the HTTP response text.  For example, IE6+ will 
interpret character code zero as end-of-response, truncating any body content following that character.  IE6+ also automatically
canonicalizes carriage return and linefeed characters as all carriage returns.  Conversely, IE9 strict document mode 
canonicalizes carriage return and linefeed characters as all linefeeds.

Therefore, the server MUST escape these characters must as follows:

| Byte Value | Character | Escaped Byte Sequence | Escaped Characters |
|------------|-----------|-----------------------|--------------------|
| 0x00       | \0        | 0x7f 0x3f             | DEL 0              |
| 0x0d       | \r        | 0x7f 0x72             | DEL r              |
| 0x0a       | \n        | 0x7f 0x63             | DEL n              |
| 0x7f       | DEL       | 0x7f 0x7f             | DEL DEL            |

For clients requiring escaped text responses, the initial handshake uses a different derived location path.
```
POST /path/;e/cte HTTP/1.1
Host: host.example.com:8080
Origin: [source-origin]
Content-Length: 0
```
Here, the handshake request location path uses `/;e/cte` instead
```
HTTP/1.1 201 Created
X-WebSocket-Version: wseb-1.0
Content-Type: text/plain;charset=utf-8
Content-Length: 105

http://host.example.com:8080/path/uofdbnreiodfkbqi
https://host.example.com:8443/path/kwebfbkjwehkdsfa
```

The binary-as-escaped-text downstream HTTP response MUST have content type `text/plain;charset=windows-1252`.
```
GET /path/kwebfbkjwehkdsfa HTTP/1.1
Host: host.example.com:8443
Origin: [source-origin]
```
```
HTTP/1.1 200 OK
Content-Type: text/plain;charset=windows-1252
Connection: close

[emulated websocket frames]
```

However, the client MUST also unescape the `DEL 0`, `DEL r`, `DEL n` and `DEL DEL` escaped character sequences to
get the logical bytes transfered.

### Binary as Mixed Text

For clients requiring mixed text responses, the initial handshake uses a different derived location path.
```
POST /path/;e/ctm HTTP/1.1
Host: host.example.com:8080
Origin: [source-origin]
Content-Length: 0
```
Here, the handshake request location path uses `/;e/ctm` instead
```
HTTP/1.1 201 Created
X-WebSocket-Version: wseb-1.0
Content-Type: text/plain;charset=utf-8
Content-Length: 105

http://host.example.com:8080/path/uofdbnreiodfkbqi
https://host.example.com:8443/path/kwebfbkjwehkdsfa
```

The binary-as-mixed-text downstream HTTP response MUST have content type `text/plain;charset=windows-1252`.
```
GET /path/kwebfbkjwehkdsfa HTTP/1.1
Host: host.example.com:8443
Origin: [source-origin]
```
```
HTTP/1.1 200 OK
Content-Type: text/plain;charset=windows-1252
Connection: close

[emulated websocket frames]
```

IE8+ `XDomainRequest` can distinguish all 256 byte-as-character-code values, for a downstream response body with content-type 
`text/plain;charset=windows-1252`.

However, the `XDomainRequest` `POST` request cannot specify content-type, and so `text/plain;charset=UTF-8` is assumed, but a
bug remains where the `\0` (NUL) character cannot be included in the `POST` body since the text is then clipped at the `\0`.

Rather than escaping the `\0` (NUL) byte-as-character-code, the client MUST use character code `256` to represent zero, so the
client MUST send a `\u0100` character for each `\u0000` character.

The server MUST decode the binary-as-mixed-text upstream such that each character codepoint is computed `mod 0x0100` to determine 
each originally intended byte value.

### Garbage Collection
Most HTTP client runtimes such as Flash, Silverlight and Java have a streaming capable HTTP response implementation, meaning that
fragments of the response can be consumed without waiting for the entire document to be transferred.

The JavaScript HTTP client runtime provided by most browsers is a little different.  A notification is provided each time new 
data arrives in the response body, but the aggregated response text still builds up.  When a certain amount of data builds up on 
the client, it is beneficial to let that response complete normally, letting the browser reclaim all memory associated with the 
aggregated response, and then reconnect the downstream with a new HTTP request.

The client MAY send the `.kb` downstream HTTP request query parameter indicated the amount of data (in kilobytes) that the client 
is willing to build up between garbage collecting reconnects.
```
GET /path/kwebfbkjwehkdsfa?.kb=512 HTTP/1.1
Host: host.example.com:8443
Origin: [source-origin]
```
When the limit is exceeded, the server MUST send a `RECONNECT` command frame to complete the downstream HTTP response. 

However, the client MAY choose not to supply the `.kb` parameter. Instead, the client establishes a new HTTP downstream request
when it decides it needs to (based on how much data has already been buffered on the current HTTP downstream response, and the 
network connect latency). When the server detects the new downstream HTTP request, it will send a `RECONNECT` command on the 
current downstream HTTP response, then complete it normally, and write all further data messages to the new downstream 
HTTP response.

## Request Sequencing
Every request (Create, Upstream & Downstream) in WSE connection MUST be annotated with sequence number. This is done via `X-Sequence-No` header. For platform which does not provide API support to add custom header to the HTTP request, sequencing can be achieved by using the query parameter `.ksn`. The sequence number must be a `Nonnegative integer` and it cannot exceed 2 ^ 53 - 1. The sequence number of the subsequent request MUST be `one` greater than previous request. The sequence number for Upstream and Downstream requests diverge to increment independently once the WSE connection is established.

A sequence number is **valid** if it is a `Nonnegative integer` AND it is less than or equal to `2 ^ 53 - 1`. Otherwise, the sequence number is **invalid**.

A request is regarded `In Order` if the sequence number of the request is `one` greater than previous request. Otherwise, the request is treated as `Out of Order` request.

For example, if the sequence number of the Create request is 10, the sequence number of subsequent upstream and downstream request MUST be `11`. During data transfer, the sequence number of subsequent upstream and downstream request increment independently. The sequence number of a Downstream request MUST be `one` greater than the sequence number of the previous Create or Downstream request. Likewise, the sequence number of an Upstream request MUST be `one` greater than the sequence number of the previous Create or Upstream request.

## References

[[RFC 2616]][RFC2616]  "Hypertext Transfer Protocol -- HTTP/1.1"

[[RFC 6455]][RFC6455]  "The WebSocket Protocol"

[[W3C WebSocket API][W3C WebSocket API]] "The WebSocket API"

[[Windows-1252][Windows-1252]] "The Windows-1252 Character Set"

[RFC2616]: https://tools.ietf.org/html/rfc2616
[RFC6455]: https://tools.ietf.org/html/rfc6455
[W3C WebSocket API]: https://www.w3.org/TR/websockets/
[Windows-1252]: http://en.wikipedia.org/wiki/Windows-1252
