# WebSocket Emulated Binary (WSEB) Protocol

## Abstract
This document specifies the behavior of WebSocket wire protocol emulated over HTTP.  It can be used to enable full 
WebSocket capabilities for HTTP clients while minimizing the syntactic differences to maintain consistent performance 
and bandwidth utilization profiles.

## Requirements
The key words "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL NOT", "SHOULD", "SHOULD NOT", "RECOMMENDED", "MAY", and 
"OPTIONAL" in this document are to be interpreted as described in [RFC 2119](https://tools.ietf.org/html/rfc2119).

## Introduction
Although all major browser vendors have endorsed the WebSocket specification, deployment of Web applications requires a 
solution for older browsers that have yet to be upgraded.  In addition, plug-in technologies such as Flash, Silverlight 
and Java may not support WebSocket yet.  In future, it is anticipated that native support will be made available for all
of these technologies.  This situation presents adoption problems for WebSocket until all end-users have upgraded accordingly.

The WSEB protocol addresses these adoption issues.

## Full Duplex
The native WebSocket handshake consists of an HTTP-compliant handshake round trip followed by WebSocket framing protocol on 
the same TCP connection.

The emulated WebSocket protocol is constructed from 3 distinct HTTP usages.
* the “create” request
* the “downstream” request
* the “upstream” request

## Create Request

The purpose of the “create” request is to establish server-side knowledge of a new emulated WebSocket connection.  An HTTP 
`POST` request is sent to a location derived from the WebSocket location `ws://host.example.com:8000/path` by extending the path
as shown below.
```
POST /path/;e/cb HTTP/1.1
Host: host.example.com:8000
Origin: [source-origin]
Content-Length: 0
X-WebSocket-Protocol: amqp;version=0.9.1, amqp;version=1.0
X-WebSocket-Extensions: x,y,z
X-WebSocket-Version: wseb-1.1
```
Note: an additional header may also be present, as follows:
```
X-Accept-Commands: ping
```
This is sent by clients which support `PING` and `PONG` control frames (see Emulation Frames below).

A successful response has status code 201 and the response body describes the locations to be used for upstream and downstream.
Any response status code other than 201 will fail the emulated WebSocket connection.
```
HTTP/1.1 201 Created
X-WebSocket-Protocol: amqp;version=1.0
X-WebSocket-Extensions: x,z
X-WebSocket-Version: 3.3
Content-Type: text/plain;charset=utf-8
Content-Length: 116

http://host.example.com:8000/path/;e/ub?.kz=uofdbnreiodfkb
https://host.example.com:9000/path/;e/db?.kz=uofdbnreiodfkb
```
In this example, the emulated WebSocket instance is identified by the value of the `.kz` query string parameter.
Note that it is possible to provide a different scheme, and host name for each of the upstream and downstream locations.

The WebSocket location MUST match the upstream and downstream derived locations subdomain and initial path, otherwise fail the 
emulated WebSocket connection.

## Downstream Request

The downstream request associates a continuous HTTP response to the emulated WebSocket connection.  Any WebSocket frames that 
would have been sent to the client over a native WebSocket connection are instead sent over the continuous downstream HTTP 
response for an emulated WebSocket connection.
```
GET /path/;e/db?.kz=uofdbnreiodfkb HTTP/1.1
Host: host.example.com:9000
Origin: [source-origin]
```
A downstream response status code of 200 with complete headers indicates that the emulated WebSocket connection is open and 
ready to send and receive messages.  Any downstream response status code other than `200` will fail the emulated WebSocket 
connection.
```
HTTP/1.1 200 OK
Content-Type: application/octet-stream
Connection: close

[emulated websocket frames]
```
Emulated WebSocket frames are the same as native WebSocket frames as described by Draft-76 [Draft-76], with the addition of 
control frames needed to handle content-type sniffing and client garbage collection.

## Binary as Text
The JavaScript execution environment provided by browsers did not historically have support for binary data types, so all HTTP 
responses accessible to JavaScript were described as strings.  For clients such as these, the initial handshake uses a different
derived location path.
```
POST /path/;e/ct HTTP/1.1
Host: host.example.com:8000
Origin: [source-origin]
Content-Length: 0
```
Here, the "create" request location path uses `/;e/ct` instead, and the corresponding upstream and downstream locations use 
`/;e/ut` and `/;e/dt` respectively.

The `/;e/dt` downstream HTTP response must be provided using a text-based content-type.
```
HTTP/1.1 200 OK
Content-Type: text/plain;charset=windows-1252
Connection: close

[emulated websocket frames]
```
The charset `windows-1252` [Windows-1252](http://en.wikipedia.org/wiki/Windows-1252) allows bytes to be transferred by the server
without modification because each character is represented by a single byte and exactly 256 distinct character codes are 
available, and 224 of those character codes match their byte representations when processed by the client.  The remaining 
character codes can be mapped to the corresponding binary representations accordingly.

## Escaped text encoding (/cte)

Some browsers have difficulty representing a few specific character codes in the HTTP response text.  For example, IE6+ will 
interpret character code zero as end-of-response, truncating any body content following that character.  IE6+ also automatically
canonicalises carriage return and linefeed characters as all carriage returns.  Conversely, IE9 strict document mode 
canonicalises carriage return and linefeed characters as all linefeeds.  Therefore, these characters must all be escaped as 
follows:

| Byte Value | Character | Escaped Byte Sequence | Escaped Characters |
|------------|-----------|-----------------------|--------------------|
| 0x00       | \0        | 0x7f 0x3f             | DEL 0              |
| 0x0d       | \r        | 0x7f 0x72             | DEL r              |
| 0x0a       | \n        | 0x7f 0x63             | DEL n              |
| 0x7f       | DEL       | 0x7f 0x7f             | DEL DEL            |

For clients requiring escaped text responses, the initial handshake uses a different derived location path.
```
POST /path/;e/cte HTTP/1.1
Host: host.example.com:8000
Origin: [source-origin]
Content-Length: 0
```
Here, the create request location path uses `/;e/cte` instead, and the corresponding upstream and downstream locations use 
`/;e/ute` and `/;e/dte` respectively.

## Mixed text encoding (/ctm)

IE8+ `XDomainRequest` can handle all 256 byte-as-character-code values, including those above, for response body with 
content-type `text/plain;charset=windows-1252`.  However, the `XDomainRequest` POST request cannot specify content-type, so 
`text/plain;charset=UTF-8` is assumed, and a bug remains where the NUL character cannot be included in the POST body.  Rather 
than escaping the NUL byte-as-character-code, we can instead use character code `256` to represent zero and then drop the high 
bit when decoding at the server. So we require upstream to send `\u0100` for `\u0000` since otherwise the text is clipped at the
`\u0000`. On the server we decode the character codepoint and `mod 0x0100` to get the intended byte value.

Since UTF-8 encoding is used, bytes with the leading bit set get represented on the wire as two bytes. For example, the following
BUMP CONNECT frame:
```
0x0b 0x07 0x01 0x60 0x00 0x00 0x01 0x00 0x00
```
gets encoded as follows
```
c2 80 09 0b 07 01 60 c4  80 c4 80 01 c4 80 c4 80
```
`c2 80` is `0x80`, `draft-76` frame type binary. `09` is the WebSocket frame length. `c4 80` is the UTF-8 representation of 
`\u0100` which indicates `0x00`.

For clients requiring mixed text responses, the initial handshake uses a different derived location path.
```
POST /path/;e/ctm HTTP/1.1
Host: host.example.com:8000
Origin: [source-origin]
Content-Length: 0
```
This approach remains untested for IE6+ downstream scenario as an alternative to the escaping strategy above for `\0`, `\r` and 
`\n`, but may very well be more efficient than what we have today.

## Content-Type Sniffing
The programmatic HTTP client runtime provided by many browser environments will attempt to determine the “real” content type of
a response served with explicit content type `text/plain`.  

The browsers achieve this by waiting for a certain amount of content to arrive for content type analysis before exposing any of
the content to the client runtime.  This prevents delivery of any emulated WebSocket frames that happen to be smaller than the
buffer size.  The exact buffer size can be determined by experimentation for different browser implementations.  In some cases 
however it is not necessary.

Padding the beginning of the response with additional control frames to exceed the content sniffing buffer size allows immediate
delivery of emulated WebSocket frames delivered to such clients.  The maximum amount of padding required is communicated in the
request via the `.kp` query parameter.
```
GET /path/;e/dt?.kp=256 HTTP/1.1
Host: host.example.com:8000
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
`application/octet-stream`, preventing programmatic access to the response text from JavaScript.

This issue only comes up in a special case of HTTP request emulation [HTTPE], where response headers are promoted to the 
beginning of the response body anyway, so setting a sufficiently long header with text value fills the content type sniffing 
buffer at the client side, allowing the text-based content type to be properly detected and proving programmatic access to the 
response text from IE7’s JavaScript runtime. 
```
GET /path/;e/dt?.kns=1 HTTP/1.1
Host: host.example.com:8000
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

## Garbage Collection
Most HTTP client runtimes such as Flash, Silverlight and Java have a streaming capable HTTP response implementation, meaning that
fragments of the response can be consumed without waiting for the entire document to be transferred.

The JavaScript HTTP client runtime provided by most browsers is a little different.  A notification is provided each time new 
data arrives in the response body, but the aggregated response text still builds up.  When a certain amount of data builds up on 
the client, it is beneficial to let that response complete normally, letting the browser reclaim all memory associated with the 
aggregated response, and then reconnect the downstream with a new HTTP request.

The amount of data that the client is willing to build up between garbage collecting reconnects is defined in kilobytes by the 
`.kb` query parameter in the corresponding request.
```
GET /path/;e/dt?.kb=512 HTTP/1.1
Host: host.example.com:8000
Origin: [source-origin]
```
When the limit is exceeded, a “reconnect” command frame is sent by the server to complete the response.  The “reconnect” frame 
MUST be the last frame on the downstream response during a reconnect, otherwise undetectable loss could occur for subsequent 
frames.

## Garbage Collection enhancement
Client may choose not to supply the .kb parameter. Instead, client establishes a new downstream connection when it decides it 
needs to (based on how much data has already been buffered on the current connection, and the network connect latency). When the
server detects the new downstream connection, it will send a RECONNECT command on the current downstream connection, then close 
it, and write all further data messages to the new downstream connection. 

Note: no data frames, in fact no frames at all, will be sent after the RECONNECT frame.

Note: the `.kb` parameter is still supported for backward compatibility.

## Buffering Proxies
Some HTTP intermediaries, such as transparent and explicit proxies, prevent HTTP responses from being successfully streamed to 
the client.  Instead, such intermediaries may buffer the response introducing undesirable latency.  Once detected, there are two
possible solutions; long-polling, and secure streaming.

We detect the presence of a buffering proxy by timing out the arrival of the status code and complete headers from the streaming 
downstream HTTP response.  In this case some emulated WebSocket frames may be blocked at the proxy, so to avoid data loss, we 
send a second overlapping downstream HTTP request, with the `.ki=p` query parameter to indicate the interaction mode is “proxy”.

In reaction to receiving this overlapping proxy mode downstream request for the same emulated WebSocket connection, the initial 
downstream response is completed normally, automatically flushing the entire response through any buffering proxy.  Then, the 
response to the second downstream request is to either redirect to an encrypted location for secure streaming, or fall back to 
long-polling as a last resort.

When long-polling, any frame sent to the client triggers a controlled reconnect in the same way as garbage collection.

## Destructive Proxies
Some HTTP intermediaries, such as transparent and explicit proxies, attempt to reclaim resources for idle HTTP responses that are
still incomplete after a timeout period.  A heartbeat command frame must be sent to the client at regular intervals to prevent 
the downstream from being severed by the proxy.

When long-polling, the heartbeat frame triggers a controlled reconnect.

Clients MAY request a shorter heartbeat interval by setting the `.kkt` query parameter. For example, `?.kkt=20` requests a 20 
second interval. This is necessary on user agents that shut down HTTP requests after only 30 seconds.

## Upstream Request
The upstream request associates a transient HTTP request to the emulated WebSocket connection.  Any WebSocket frames that would 
have been sent from the client over a native WebSocket connection are instead sent over the transient upstream HTTP request for 
an emulated WebSocket connection.
```
POST /path/;e/ub?.kz=uofdbnreiodfkb HTTP/1.1
Host: host.example.com:9000
Origin: [source-origin]
Content-Type: application/octet-stream
Content-Length: [size]

[emulated WebSocket frames]
```
An upstream response status code of `200` indicates that the frames were received successfully by the emulated WebSocket 
connection.  Any upstream response status code other than `200` will fail the emulated WebSocket connection.

## Emulation Frames
The frames used by an emulated WebSocket connection for upstream and downstream communication extend those defined by 
[Draft-76](http://tools.ietf.org/html/draft-hixie-thewebsocketprotocol-76), plus `PING` and `PONG` similar to RFC 6455, as 
defined below.

The text-based command frame has frame type `0x01`, which masks to `0x00` indicating text content.  The content of each command 
frame is a sequence of bytes encoded as hex.  For example, the bytes `5` `B` (`0x35 0x42`) decode to the hypothetical command 
hex code `0x5B`. 

Command hex code `0x00` (`0x30 0x30`) indicates padding and heartbeat and is therefore ignored.
Command hex code `0x01` (`0x30 0x31`) is the reconnect command used for controlled reconnect.
Command hex code `0x02` (`0x30 0x32`) is the close command.

PING: this is frame code (or type) `0x89`
PONG: this is frame code `0x8A`

We use the same codes as RFC 6455. These have the leading bit set, which in [Draft-76](http://tools.ietf.org/html/draft-hixie-thewebsocketprotocol-76)
indicates binary. The following bytes indicate the length of the payload (length encoded following the rules for binary message 
length in [Draft-76](http://tools.ietf.org/html/draft-hixie-thewebsocketprotocol-76) as implied by section 5.3 Data framing).

Unlike RFC 6455, `PING` and `PONG` frames may not contain any content (this is to reduce implementation cost and bandwidth usage). So the following byte must always be 0x00 (indicating zero length payload).

This implies that the wire representations for `PING` and `PONG` frames are as follows:

PING: `0x89 0x00`
PONG: `0x8A 0x00`

For backwards compatibility reasons, clients which support PING (that is, will respond to a PING frame with a PONG frame, 
and tolerate receiving an unsolicited PONG frame on the downstream) MUST advertise that support by sending the following header 
on the create request:
```
X-Accept-Commands: ping
```

## Connection Termination

### Client-initiated Close
Client sends a text-based `CLOSE` command frame on the upstream and closes the stream. Client’s readyState is set to `CLOSING`.

The Gateway responds by closing the downstream.  The Gateway transmits an emulated text-based command frame on the downstream 
with a `CLOSE` command (`0x02`) followed by a `RECONNECT` (`0x01`), then ends the stream.

The client verifies that the Gateway has responded with `CLOSE` followed by `RECONNECT` followed by termination of the stream. 
Finally, the client fires a close event as defined by the WebSocket API.  Otherwise, it is a protocol violation, and an error 
event is fired.

Note that this section is extended by close code and reason, as described in the Close Handshake section below.

### Client-initiated Error
If the Client detects a protocol violation, then it will shutdown all outstanding requests, and fire events as required by 
WebSocket API.

### Server-initiated Close
Server transmits a `CLOSE` command (`0x02`) followed by a `RECONNECT` (`0x01`), then ends the stream.

The client verifies that the downstream `CLOSE` command followed by the `RECONNECT` command and end of stream, then fires a 
close event.  Otherwise, it is a protocol violation, and an error event is fired.


### Gateway-initiated Error
Gateway terminates the downstream with no `RECONNECT` or `CLOSE` frame.

Alternatively, the Gateway can respond with an HTTP error-code on any HTTP create, upstream or downstream request.

## Connection Loss
The semantics of connection loss for emulated WebSocket match those of native WebSocket.  The lifetime of the downstream 
indicates the lifetime of the emulated WebSocket.  Note that the downstream can span more than one HTTP request due to either 
a garbage collecting reconnect or buffering proxy detection.  If the downstream response completes without a “reconnect” command
frame, then the WebSocket connection is closed.

### Native WebSocket Protocol Close Frame Sample

The following close handshake frame samples are captured with Chrome WebSocket

#### Case #1: 

Client called Close() without parameter, the frame is `0x88 0x80 0x16 0x8f 0x0c 0x0a`
`0x88` - close frame
`0x80` - mask bit (first bit = 1) + data length (equals to 0)
`0x16 0x8f 0x0c 0x0a` - mask key (4 bytes random generated by client)

Server responded with the same data without mask, the frame is `0x88 0x00`
`0x88` - close frame
`0x00` - mask bit (first bit = 0) + data length (equals to 0)

Client fired CLOSE Event
```javascript
evt.wasClean = true;
evt.code = 1005
evt.reason = “”
```

#### Case #2: 
Client called close with parameters, such as Close(1000, “abc”), the frame is `0x88 0x85 0x78 0x20 0xef 0x1c 0x7b 0xc8c 0x8e 0x7e 0xeb`

`0x88` - close frame
`0x85` - mask bit (first bit = 1) + data length (equals to 5)
`0x78 0x20 0xef 0x1c` - mask key (4 bytes)
`0x7b 0xc8` - first parameter [code] (unsigned short, equals to 1000 or range from 3000 to 4999)
`0x8e 0x7e 0xeb` - second parameter [reason] (UTF-8 encoded, no longer than 123 bytes)

Server responded with same data without mask, the frame is  `0x88 0x05 0x03 0xe8 0x61 0x62 0x63`

`0x88` - close frame
`0x05` - mask bit (first bit = 0, no mask) + data length (equals to 5)
`0x03 0xe8` - code (value equals to 1000)
`0x61 0x62 0x63` - reason (value equals to “abc”)

Clent fired CLOSE event
```javascript
evt.wasClean = true
evt.code = 1000
evt.reason = “abc”
```
