# HTTP Extension and Emulation Protocol

## Abstract

This document specifies the behavior of HTTP emulated over HTTP.  It can be used to enable full HTTP capabilities for partially 
equipped HTTP clients while minimizing the syntactic differences to maintain consistent performance and bandwidth utilization 
profiles.

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
  * [Conformance Requirements](#conformance-requirements)
  * [Request Requirements](#request-requirements)
    * [Method Definitions](#method-definitions)
    * [Request Headers](#request-headers)
  * [Response Requirements](#response-requirements)
    * [Response Status Codes](#response-status-codes)
    * [Response Headers](#response-headers)
  * [Origin Security](#origin-security)
    * [Origin Request Headers](#origin-request-headers)
    * [Origin Query Parameter](#origin-query-parameter)
    * [Same-Origin Access Control Headers](#same-origin-access-control-headers)
    * [Target-Origin Resources](#target-origin-resources)
  * [References](#references)

## Introduction

### Background

_This section is non-normative._

JavaScript HTTP [[RFC7230]] clients deployed in HTML4 browsers and plug-in technologies such as Flash, Silverlight and Java often 
vary in their completeness of HTTP support.  For example, some clients do not support all HTTP methods, others do not support all 
HTTP status codes. The Web Origin Concept and Cross-Origin capability [[W3C CORS]] are also not ubiquitously supported.
Given the request-driven nature of HTTP, many clients have problems initiating a text-based HTTP streaming response because of 
attempts to discover the content type by buffering the response.

This specification aims to address these inconsistencies.

### Protocol Overview

_This section is non-normative._

The HTTP Extension and Emulation protocol is layered on top of HTTP.  The content of an HTTPXE request or response is split into 
two parts, the emulated start line plus headers, and the emulated content for the request or response.


### Design Philosophy

_This section is non-normative._

The HTTP Extension and Emulation Protocol (HTTPXE) is designed to support a client-side HTTP API with identical semantics when 
compared to an HTTP API using the HTTP protocol defined by [[RFC 7230]][RFC7230] while only making use of (perhaps limited) HTTP 
APIs at the client, and a corresponding HTTP Extension and Emulation server implementation.

For example, it should be possible to provide a compatible [[W3C XMLHttpRequest API][W3C XMLHttpRequest]] in JavaScript
using only an HTML4 browser's HTTP APIs to implement the HTTP Emulation protocol. Notably, the end-user should not be 
required to install any browser plug-ins, instead the emulated XMLHTTPRequest API should be delivered as part of the Web 
application JavaScript, perhaps using an HTML `<script>` tag.

### Security Model

_This section is non-normative._

The HTTP Extension and Emulation protocol is layered on top of HTTP and therefore inherits the [[W3C CORS][W3C CORS]] origin 
model when used from browser clients.

## Conformance Requirements
The key words "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL NOT", "SHOULD", "SHOULD NOT", "RECOMMENDED", "MAY", and "OPTIONAL" 
in this document are to be interpreted as described in [[RFC 2119] [RFC2119]].

## Request Requirements

### Method Definitions
An HTTP request defined in [[RFC 7230]][RFC7230] can support multiple different possible methods, such as `GET`, `HEAD`, `POST`, 
`PUT`, `DELETE`, `OPTIONS`, and `TRACE`.  Some HTTP clients can only support `GET` and `POST`.

Therefore we use `GET`, and `POST` with either a query parameter or path encoding to indicate which method was intended.

_[Note: why do we need query parameter now that path encoding is supported?]_

#### GET
The `.km` query parameter MUST NOT be present on a `GET` request intended to be processed as a `GET` request.

An HTTP client may choose to automatically convert a `POST` request into a `GET` request if no body content is specified in the 
`POST`.  This automatic conversion may also result in loss of headers specified to be included with the `GET` request.

The following `POST` request
```
POST /path?.km=G HTTP/1.1
[headers]

[body content]
```
is treated equivalently to
```
GET /path HTTP/1.1
[headers]

[no body content]
```
by the server.

The request path may also be used to encode the request method.  A path-encoded `GET` method MUST be represented by a `POST` 
method.

The following `POST` request
```
POST /;get/path HTTP/1.1
[headers]

[body content]
```
is treated equivalently to
```
GET /path HTTP/1.1
[headers]

[no body content]
```
by the server when `X-Origin-[origin]` header is present and matches `X-Origin` header - see [Origin Security](#origin-security).


#### POST
The `.km` query parameter MUST NOT be present on a `POST` request intended to be processed as a `POST` request.
```
POST /path HTTP/1.1
```
The request path may also be used to encode the request method.  A path-encoded `POST` method MUST be represented by a `POST` 
method.

The following `POST` request
```
POST /;post/path HTTP/1.1
```
is treated equivalently to
```
POST /path HTTP/1.1
```
by the server when `X-Origin-[origin]` header is present and matches `X-Origin` header - see [Origin Security](#origin-security).

#### HEAD
The following `POST` request
```
POST /path?.km=H HTTP/1.1
```
is treated equivalently to
```
HEAD /path HTTP/1.1
```
by the server.

The request path may also be used to encode the request method.  A path-encoded `HEAD` method MUST be represented by a `POST` 
method.

The following `POST` request
```
POST /;head/path HTTP/1.1
```
is treated equivalently to
```
HEAD /path HTTP/1.1
```
by the server when `X-Origin-[origin]` header is present and matches `X-Origin header` - see [Origin Security](#origin-security).

Responses to this method MUST NOT include any content, therefore the emulated `POST` response MUST either return a 
`204 No Content` status code where the extension method request would have returned a `200 OK` status code, or a `Content-Length` 
header with a value `0`.

#### PUT
The following `POST` request
```
POST /path?.km=P HTTP/1.1
```
is treated equivalently to
```
PUT /path HTTP/1.1
```
by the server.

The request path may also be used to encode the request method.  A path-encoded `PUT` method MUST be represented by a `POST` 
method.

The following `POST` request
```
POST /;put/path HTTP/1.1
```
is treated equivalently to
```
PUT /path HTTP/1.1
```
by the server when `X-Origin-[origin]` header is present and matches `X-Origin header` - see [Origin Security](#origin-security).

#### DELETE
The following `POST` request
```
POST /path?.km=D HTTP/1.1
```
is treated equivalently to
```
DELETE /path HTTP/1.1
```
by the server.

The request path may also be used to encode the request method.  A path-encoded `DELETE` method MUST be represented by a `POST` 
method.

The following `POST` request
```
POST /;delete/path HTTP/1.1
```
is treated equivalently to
```
DELETE /path HTTP/1.1
```
by the server when `X-Origin-[origin]` header is present and matches `X-Origin` header - see [Origin Security](#origin-security).

#### OPTIONS
The following `POST` request
```
POST /path?.km=O HTTP/1.1
```
is treated equivalently to
```
OPTIONS /path HTTP/1.1
```
by the server.

The request path may also be used to encode the request method.  A path-encoded `OPTIONS` method MUST be represented by a `POST` 
method.

The following `POST` request
```
POST /;options/path HTTP/1.1
```
is treated equivalently to
```
OPTIONS /path HTTP/1.1
```
by the server when `X-Origin-[origin]` header is present and matches `X-Origin` header - see [Origin Security](#origin-security).

Responses to this method MUST NOT be cached, therefore the emulated `POST` response MUST NOT include any headers making the response cacheable.

#### TRACE
The following `POST` request
```
POST /path?.km=T HTTP/1.1
```
is treated equivalently to
```
TRACE /path HTTP/1.1
```
by the server.

The request path may also be used to encode the request method.  A path-encoded `TRACE` method MUST be represented by a `POST` 
method.

The following `POST` request
```
POST /;trace/path HTTP/1.1
```
is treated equivalently to
```
TRACE /path HTTP/1.1
```
by the server when `X-Origin-[origin]` header is present and matches `X-Origin header` - see [Origin Security](#origin-security).

Responses to this method MUST NOT be cached, therefore the emulated `POST` response MUST NOT include any headers making the response cacheable.

#### CONNECT
The `CONNECT` method is reserved for use with explicit proxies and therefore MUST NOT be supported as an emulated method.

#### Extension Methods
The HTTP specification defines the HTTP method to also support custom extension methods, which are tokens that allow any US ASCII character (0 - 127) except “()<>@,;:\"/[]?={} \t”.  An extension method is specified inside () to disambiguate from other well-known methods defined here.

The following `POST` request
```
POST /path?.km=(CUSTOM) HTTP/1.1
```
is treated equivalently to
```
CUSTOM /path HTTP/1.1
```
by the server.

The request path may also be used to encode the request method.  A path-encoded custom method MUST be represented by a `POST` 
method.

The following `POST` request
```
POST /;custom/path HTTP/1.1
```
is treated equivalently to
```
CUSTOM /path HTTP/1.1
```
by the server when `X-Origin-[origin]` header is present and matches `X-Origin` header - see [Origin Security](#origin-security).

If responses to the extension method MUST NOT be cached, then the emulated `POST` response MUST NOT include any headers making 
the response cacheable.

If responses to the extension method MUST NOT include any content, then the emulated `POST` response MUST either return a 
`204 No Content` status code where the extension method request would have returned a `200 OK` status code, or a `Content-Length`
header with a value `0`.

### Request Headers
The HTTP `X-Next-Protocol` header value for an HTTPXE request SHOULD be `httpxe/1.1`.  If the client HTTP runtime cannot specify
such an HTTP header, then the `.knp` query parameter MUST be present with with value `httpxe/1.1`.

**Note** for backwards compatibility, the next protocol may be inferred when neither `X-Next-Protocol` header nor `.knp` query
parameter are present.  Clients relying on such inference are _not_ considered compliant with this specification.

The HTTP content-type for an HTTPXE request MUST be `application/x-message-http`. This MUST either specified using the 
`Content-Type` header, or in the value of the `.kct` query parameter instead.

Some client HTTP runtimes may have limited support for large HTTP headers, such that the aggregate length of all HTTP headers 
cannot exceed a maximum number of bytes.

HTTPXE requests are enveloped, thus addressing this problem by supporting HTTPXE headers as part of the HTTP request body.
```
POST /path HTTP/1.1
Content-Type: application/x-message-http
Content-Length: ...

POST /path HTTP/1.1
Authorization: [large-header-value]
Content-Type: text/plain
Content-Length: 6

[body]
```
is treated equivalently to
```
POST /path HTTP/1.1
Authorization: [large-header-value]
Content-Type: text/plain
Content-Length: 6

[body]
```

The HTTP request path is required to match the HTTPXE path exactly. HTTPXE headers defined in the HTTP request body are 
combined with the HTTP headers to determine the effective request headers.

HTTPXE headers other than `Authorization`, `Content-Type` and `Content-Length` MUST NOT be combined.

If an HTTP client does not support explicitly setting the `POST` body `Content-Type` header, then the client MUST use the 
`.kct` query parameter to let the server know exactly what content-type to expect when processing the request body.
```
POST /path?.kct=application/x-message-http HTTP/1.1
Content-Length: ...

POST /path HTTP/1.1
Authorization: [large-header-value]
Content-Type: text/plain
Content-Length: 6

[body]
```
is treated equivalently to
```
POST /path HTTP/1.1
Content-Type: application/x-message-http
Content-Length: ...

POST /path HTTP/1.1
Authorization: [large-header-value]
Content-Type: text/plain
Content-Length: 6

[body]
```
which is treated equivalently to
```
POST /path HTTP/1.1
Authorization: [large-header-value]
Content-Type: text/plain
Content-Length: 6

[body]
```

## Response Requirements

Responses are generally enveloped as shown below: 
```
HTTP/1.1 200 OK
Content-Type: application/x-message-http
Content-Length: ...

HTTP/1.1 201 Created
Content-Type: ...
Content-Length: 33

```
is treated equivalently to
```
HTTP/1.1 201 Created
Content-Type: ...
Content-Length: 33

```

However, some status codes require that the responses are _not_ enveloped - 
see [Response Status Codes](response-status-codes) and different content-types may be required in the response to handle
the limitations of some HTTP clients - see [Response Headers](#response-headers).

### Response Status Codes
An HTTP client may not be able to represent status codes other than `200 OK`, `304 Not Modified` or `404 Not Found`, so status 
codes may need to wrapped as the first part of the response body content.

#### Informational 1xx
These informational status codes MUST NOT be changed in the response.

#### Successful 2xx
These successful status codes MUST be represented by the `200 OK` status code, with the actual status code wrapped in the 
response body content.

If the status code MUST NOT include a message body, such as `204 No Content`, `205 Reset Content`, `206 Partial Content`, then 
the wrapped status code and headers MUST NOT include any further body content.

#### Redirection 3xx
These redirection status codes except `304 Not Modified` MAY be represented by the `200 OK` status code, with the actual status 
code wrapped in the response body content.  The client SHOULD detect infinite redirection loops, since such loops generate 
network traffic for each redirection.

The `304 Not Modified` redirection status code MUST NOT be changed in the response for accurate interaction with the client cache.

#### Client Error 4xx
These client error status codes except `404 Not Modified` MAY be represented by the `200 OK` status code, with the actual status 
code wrapped in the response body content.  

The `404 Not Modified` client error status code MUST NOT be changed in the response for accurate interaction with the client 
cache.

#### Server Error 5xx
These server error status codes MUST NOT be changed in the response.

### Response Headers
The following headers MUST NOT be moved into the response body; `Cache-Control`, `Connection`, `Content-Encoding`, `Date`, 
`ETag`, `Last-Modified`, `Pragma`, `Server`, `Set-Cookie`, `Transfer-Encoding`, `X-Content-Type-Options`, `Sec-*`.

_[Note: explicitly indicating whether the response is enveloped via a header would let the client make an informed choice about 
how to parse the response rather than having to assume when enveloping has occurred.  Not sure if this is possible on all 
platforms, such as Flash.]_

An HTTP client may not be able to receive specific content types, such as `text/xml`.  The response envelope content type 
describes both the wrapped status code and headers in ASCII character set, and the original response body if present.

A text-based content type, such as `text/xml;charset=UTF-8`, MUST be canonicalized as `text/plain;charset=UTF-8` for the wrapped 
response, while the original content-type MUST be retained in the wrapped headers.
```
HTTP/1.1 200 OK
...
Content-Type: text/plain;charset=UTF-8
Content-Length: 67

HTTP/1.1 201 Created
Content-Type: text/xml;charset=UTF-8
Content-Length: 33

<?xml version=”1.1”?>
<document/>
```
A binary content type, such as `application/octet-stream`, MUST be retained unmodified.
```
HTTP/1.1 200 OK
...
Content-Type: application/octet-stream
Content-Length: 67

HTTP/1.1 201 Created
Content-Type: application/octet-stream
Content-Length: 33

<?xml version=”1.1”?>
<document/>
```

## Origin Security
The source origin sent by the client is trusted to be authoritative by the HTTP server, and is typically the responsibility of 
the trusted HTTP client implementation rather than the untrusted application code using the HTTP client.  For example, in browser
systems the `Origin` header is populated by the browser HTTP client implementation and not by the calling JavaScript code.

HTTPXE specifies the source origin using a number of different strategies, each of which is verified to ensure that the 
value can be trusted.

### Origin Request Headers
When the `Origin` header is present and `X-Origin` is not present, then the value of the `Origin` header MUST be trusted as the
authoritative value for source origin.
```
POST /path HTTP/1.1
Host: target.example.com:80
Origin: http://source.example.com:80
```
is treated equivalently to
```
POST /path HTTP/1.1
Host: target.example.com:80
Origin: http://source.example.com:80
```
When the `X-Origin` header is present and identical to the `Origin` header, then the value of the `Origin` header MUST be trusted 
as the authoritative value for source origin.
```
POST /path HTTP/1.1
Host: target.example.com:80
Origin: http://source.example.com:80
X-Origin: http://source.example.com:80
```
is treated equivalently to
```
POST /path HTTP/1.1
Host: target.example.com:80
Origin: http://source.example.com:80
```
When the `Origin` header is not present, and the `X-Origin` header is present, and the request method is path-encoded, and the 
value of the `X-Origin-[ascii-escaped-source-origin-value]` header is identical to the value of the `X-Origin` header, then the
value of the `X-Origin` header MUST be trusted as the authoritative value for source origin.
```
POST /;post/path HTTP/1.1
Host: target.example.com:80
X-Origin: http://source.example.com:80
X-Origin-http%3A%2F%2Fsource.example.com%3A80: http://source.example.com:80
```
is treated equivalently to
```
POST /path HTTP/1.1
Host: target.example.com:80
Origin: http://source.example.com:80
```
When the `X-Origin` header is present and differs from the `Origin` header, and the request is _not_ path-encoded, and the 
request is determined to be a same-origin request by comparing value of the `Origin` header with the `Host` header, then the 
value of the `X-Origin` header MUST be trusted  as the authoritative value for source origin.
```
POST /path HTTP/1.1
Host: target.example.com:80
Origin: http://target.example.com:80
X-Origin: http://source.example.com:80
```
is treated equivalently to
```
POST /path HTTP/1.1
Host: target.example.com:80
Origin: http://source.example.com:80
```
When the `X-Origin` header is present and the the `Origin` header is not present, and the request is _not_ path-encoded, and the 
request is determined to be a same-origin request by comparing value of the `Referer` header with the `Host` header, then the 
value of the `X-Origin` header MUST be trusted  as the authoritative value for source origin.
```
POST /path HTTP/1.1
Host: target.example.com:80
Referer: http://target.example.com:80/different/path
X-Origin: http://source.example.com:80
```
is treated equivalently to
```
POST /path HTTP/1.1
Host: target.example.com:80
Origin: http://source.example.com:80
```
If the authoritative source origin cannot be determined, then the server MUST responsd with 4xx status code (either `403 Forbidden` or `400 Bad Request`).

### Origin Query Parameter
If the `.ko` query parameter is present and both the `Origin` and `X-Origin` headers are not present and the `Referer` header 
matches the host and port of the `Host` header, then the value of the `.ko` query parameter MUST be trusted as the authoritative 
value for source origin.
```
GET /path?.ko=http%3A//source.example.com%3A80 HTTP/1.1
Host: target.example.com:80
Referer: http://target.example.com:80/any/path?query=any
```
is treated equivalently to
```
GET /path HTTP/1.1
Origin: http://source.example.com:80
```
by the server.

### Same-Origin Access Control Headers
If an HTTP client runtime requires the presence of `Access-Control-*` headers in the response, even for same-origin requests, 
then the client MUST include a `.kac=ex` query parameter to override the standard behavior and explicitly include the
`Access-Control-*` headers, even if the request is same-origin.

### Target-Origin Resources
Each client platform, such as JavaScript, Flash, Silverlight need additional resources to reach the target origin.  These fall 
into one of two categories; bridge code executing in the target origin security sandbox, and out-of-band resources that grant 
the client runtime access to the target origin.

The contents of bridge code resources are beyond the scope of this specification, but MAY be of the form
`http://target.example.com/;resource/bridge-name/version`.

_Note: such bridge code resource URLs are not scoped by the path of the request target, so they can be cached for all requests
to the same target origin, but an HTTP-aware intermediary could be scoping access to specific paths, preventing these URLs from 
being accessed._

The following URLs describe client runtime authorization resources:

| Client Technology        | Target-Origin URL                                |
|--------------------------|--------------------------------------------------|
| Silverlight              |`http://target.example.com/clientaccesspolicy.xml`|
| Flash, Java, Silverlight |`http://target.example.com/crossdomain.xml`       |

Note: servers MUST reject requests for `crossdomain.xml` authorization resources, because it grants access at a coarse-grained
level, preventing enforcement of the finer-grained policy described by cross-origin semantics.

## References

[[RFC 2119][RFC2119]]  "Key words for use in RFCs to Indicate Requirement Levels"

[[RFC 7230][RFC7230]]  "Hypertext Transfer Protocol"

[[RFC 6454][RFC6454]] “The Web Origin Concept”

[[W3C CORS][W3C CORS]] “Cross Origin Resource Sharing”

[RFC2119]: https://tools.ietf.org/html/rfc2119
[RFC7230]: https://tools.ietf.org/html/rfc7230
[RFC6454]: https://tools.ietf.org/html/rfc6454
[W3C XMLHttpRequest]: https://www.w3.org/TR/XMLHttpRequest/
[W3C CORS]: https://www.w3.org/TR/cors
