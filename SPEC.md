# HTTP Extension and Emulation Protocol

## Abstract

This document specifies the behavior of HTTP emulated over HTTP.  It can be used to enable full HTTP capabilities for partially equipped HTTP clients while minimizing the syntactic differences to maintain consistent performance and bandwidth utilization profiles.

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
  * ...

## Introduction

### Background

_This section is non-normative._

Existing Javascript HTTP [RFC2616] clients deployed in HTML4 browsers and plug-in technologies such as Flash, Silverlight and Java often vary in their completeness of HTTP support.  For example, some clients do not support all HTTP methods, others do not support all HTTP status codes, the Web Origin Concept and Cross-Origin capability [CORS] is also not ubiquitously supported.  Given the request-driven nature of HTTP, many clients have problems initiating a text-based HTTP streaming response because of attempts to discover the content type by buffering the response.

This specification addresses these inconsistencies.

### Protocol Overview

_This section is non-normative._

[TODO]

### Design Philosophy

_This section is non-normative._

[TODO]

### Security Model

_This section is non-normative._

[TODO]

## Conformance Requirements
The key words "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL NOT", "SHOULD", "SHOULD NOT", "RECOMMENDED", "MAY", and "OPTIONAL" in this document are to be interpreted as described in RFC 2119 [RFC2119].

## Method Definitions
An HTTP request defined in [RFC2616] can support multiple different possible methods, such as GET, HEAD, POST, PUT, DELETE, OPTIONS, TRACE.  Some HTTP clients can only support GET and POST.  Therefore we use GET, and POST with either a query parameter or path encoding to indicate which method was intended.

_[Note: why do we need query parameter now that path encoding is supported?]_

### GET
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

_[Note: explicitly identifying the POST content-type as ignorable may be more appropriate here.]_

#### Request Path
The request path may also be used to encode the request method.  A path-encoded `GET` method MUST be represented by a `GET` 
method.

The following `GET` request
```
GET /;get/path HTTP/1.1
[headers]

[body content]
```
is treated equivalently to
```
GET /path HTTP/1.1
[headers]

[no body content]
```
by the server when `X-Origin-[origin]` header is present and matches `X-Origin` header - see Origin Security section.


### POST
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
by the server when `X-Origin-[origin]` header is present and matches `X-Origin` header - see Origin Security section.

### HEAD
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
by the server when `X-Origin-[origin]` header is present and matches `X-Origin header` - see Origin Security section.

Responses to this method MUST NOT include any content, therefore the emulated `POST` response MUST either return a 
`204 No Content` status code where the extension method request would have returned a `200 OK` status code, or a `Content-Length` 
header with a value `0`.

### PUT
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
by the server.

### DELETE
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
by the server when `X-Origin-[origin]` header is present and matches `X-Origin` header - see Origin Security section.

### OPTIONS
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
by the server when `X-Origin-[origin]` header is present and matches `X-Origin` header - see Origin Security section.

Responses to this method MUST NOT be cached, therefore the emulated `POST` response MUST NOT include any headers making the response cacheable.

### TRACE
The following `POST` request
```
POST /path?.km=T HTTP/1.1
```
is treated equivalently to
```
TRACE /path HTTP/1.1
```
by the server when `X-Origin-[origin]` header is present and matches `X-Origin` header - see Origin Security section.

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
by the server.

Responses to this method MUST NOT be cached, therefore the emulated `POST` response MUST NOT include any headers making the response cacheable.

### CONNECT
The `CONNECT` method is reserved for use with explicit proxies and therefore MUST NOT be supported as an emulated method.

### Extension Methods
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
by the server when `X-Origin-[origin]` header is present and matches `X-Origin` header - see Origin Security section.

If responses to the extension method MUST NOT be cached, then the emulated `POST` response MUST NOT include any headers making 
the response cacheable.

If responses to the extension method MUST NOT include any content, then the emulated `POST` response MUST either return a 
`204 No Content` status code where the extension method request would have returned a `200 OK` status code, or a `Content-Length`
header with a value `0`.

## Request Headers
The Flash client technology HTTP runtime has limited support for large HTTP headers, such that the aggregate length of all 
HTTP headers cannot exceed 8192 bytes.  This triggered an issue for Kerberos authorization when the number of groups described 
by a service ticket is on the order of `200` because the `Authentication` header causes the limit to be exceeded.

Requests can be enveloped to support custom headers as part of the body much like responses described below.
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

The request path is required to match the enveloped path exactly.  Enveloped headers defined in the request body are added to the
effective request headers, and currently only the `Authorization`, `Content-Type` and `Content-Length` are supported.

## Content-Type
IE8+ XDomainRequest does not support explicitly setting the `POST` body content type, so we use a query parameter to let the 
server know exactly what content-type to use.  This let’s us trigger request enveloping as necessary.
```
POST /path?.kct=application/x-message-http HTTP/1.1
Content-Length: ...

POST /path HTTP/1.1
Authorization: [large-header-value]
Content-Type: text/plain
Content-Length: 6

[body]
```
The server removes the `.kct` query parameter during processing to avoid exposing its presence unnecessarily.

## Status Code Definitions
An HTTP client may not be able to represent status codes other than `200 OK`, `304 Not Modified` or `404 Not Found`, so status 
codes may need to wrapped as the first part of the response body content.

### Informational 1xx
These informational status codes MUST NOT be changed in the response.

### Successful 2xx
These successful status codes MUST be represented by the `200 OK` status code, with the actual status code wrapped in the 
response body content.

If the status code MUST NOT include a message body, such as `204 No Content`, `205 Reset Content`, `206 Partial Content`, then 
the wrapped status code and headers MUST NOT include any further body content.

### Redirection 3xx
These redirection status codes except `304 Not Modified` MAY be represented by the `200 OK` status code, with the actual status 
code wrapped in the response body content.  The client SHOULD detect infinite redirection loops, since such loops generate 
network traffic for each redirection.

The `304 Not Modified` redirection status code MUST NOT be changed in the response for accurate interaction with the client cache.

### Client Error 4xx
These client error status codes except `404 Not Modified` MAY be represented by the `200 OK` status code, with the actual status 
code wrapped in the response body content.  

The `404 Not Modified` client error status code MUST NOT be changed in the response for accurate interaction with the client 
cache.

### Server Error 5xx
These server error status codes MUST NOT be changed in the response.

## Response Headers
The following headers MUST NOT be moved into the response body; `Cache-Control`, `Connection`, `Content-Encoding`, `Date`, 
`ETag`, `Last-Modified`, `Pragma`, `Server`, `Set-Cookie`, `Transfer-Encoding`, `X-Content-Type-Options`.

_[Note: explicitly indicating whether the response is enveloped via a header would let the client make an informed choice about how to parse the response rather than having to assume when enveloping has occurred.  Not sure if this is possible on all platforms, such as Flash.]_

## Response Bodies
An HTTP client may not be able to receive specific content types, such as `text/xml`.  The response envelope content type 
describes both the wrapped status code and headers in ASCII character set, and the original response body if present.

A text-based content type, such as `text/xml;charset=UTF-8`, MUST be canonicalized as `text/plain;charset=UTF-8` for the wrapped 
response, while the original content-type MUST be retained in the wrapped headers.
```
200 OK HTTP/1.1
...
Content-Type: text/plain;charset=UTF-8
Content-Length: 67

201 Created HTTP/1.1
Content-Type: text/xml;charset=UTF-8
Content-Length: 33

<?xml version=”1.1”?>
<document/>
```
A binary content type, such as `application/octet-stream`, MUST be retained unmodified.
```
200 OK HTTP/1.1
...
Content-Type: application/octet-stream
Content-Length: 67

201 Created HTTP/1.1
Content-Type: application/octet-stream
Content-Length: 33

<?xml version=”1.1”?>
<document/>
```

## Origin Security
The source origin sent by the client is trusted to be authoritative by the HTTP server, and is typically the responsibility of 
the trusted HTTP client implementation rather than the untrusted application code using the HTTP client.  For example, in browser
systems the `Origin` header is populated by the browser HTTP client implementation and not by the calling JavaScript code.

HTTP emulation specifies the source origin using a number of different strategies, each of which is verified to ensure that the 
value can be trusted.

### Standard Header
When the `Origin` header is present, it MUST be trusted as the authoritative value for source origin.

### Extension Headers
When the `Origin` header is not present, the `X-Origin` header is present , and the method is not path-encoded, then the 
`X-Origin` header MUST be trusted as the authoritative source origin. 
```
POST /path HTTP/1.1
X-Origin: http://source.origin.net:80
```
is treated equivalently to
```
POST /path HTTP/1.1
Origin: http://source.origin.net:80
```
by the server.

When the `Origin` header is not present, the `X-Origin` header is present and the method is path-encoded, then the value of the 
`X-Origin-[ascii-escaped-source-origin-value]` header MUST exactly match the value of the `X-Origin` header to be trusted as the
authoritative source origin.
```
POST /;post/path HTTP/1.1
X-Origin: http://source.origin.net:80
X-Origin-http%3A//source.origin.net%3A80: http://source.origin.net:80
```
is treated equivalently to
```
POST /path HTTP/1.1
Origin: http://source.origin.net:80
```
by the server.

_[Note: if same origin requests include Origin header, could the X-Origin then be ignored and cause an incorrect Origin to be used?]_

### Query Parameter
If the `.ko` query parameter is present and both the `Origin` and `X-Origin` headers are not present then the `.ko` header MUST be
trusted as the authoritative source origin value IFF the `Referer` header matches the host and port of the `Host` header.
```
GET /path?.ko=http%3A//source.origin.net%3A80 HTTP/1.1
Host: target.origin.net:80
Referer: http://target.origin.net:80/any/path?query=any
```
is treated equivalently to
```
GET /path HTTP/1.1
Origin: http://source.origin.net:80
```
by the server.

### Access Control Headers
IE8+ `XDomainRequest` requires the presence of `Access-Control-*` headers in the response, even for same origin requests.

Therefore, we supply a `.kac=ex` query parameter from the client to override our default behavior and explicitly include the 
`Access-Control-*` headers, even if the request is same-origin.

### Target Origin Resources
Each client platform, such as JavaScript, Flash, Silverlight need additional resources to reach the target origin.  These fall 
into one of two categories; bridge code executing in the target origin security sandbox, and out-of-band resources that grant 
the client runtime access to the target origin.

The following URLs describe bridge code resources:
* http://target.origin.net/?.kr=xs    JavaScript bridge
* http://target.origin.net/?.kr=xsa   Flash bridge
* http://target.origin.net/?.kr=xsj   Java bridge

_[Note: these URLs are not scoped by the path of the target request, so they can be cached for all connections to the same target origin, but an HTTP-aware intermediary could be scoping access to specific paths, preventing these URLs from being accessed]_

The following URLs describe client runtime authorization resources:
* http://target.origin.net/clientaccesspolicy.xml Silverlight client access policy

Note: we explicitly reject requests for Flash authorization resources, such as:
* http://target.origin.net/crossdomain.xml
because it grants access at a coarse grained level, preventing enforcement of the finer-grained policy described by cross origin semantics.

## References

[RFC2119]  Bradner, S., "Key words for use in RFCs to Indicate Requirement Levels", BCP 14, RFC 2119, March 1997.

[RFC2616]  Fielding, R., Gettys, J., Mogul, J., Frystyk, H., Masinter, L., Leach, P., and T. Berners-Lee, "Hypertext Transfer Protocol -- HTTP/1.1", RFC 2616, June 1999.

[Web Origin Concept] Barth, A., “The Web Origin Concept”, Internet Draft, November 2010.

[CORS] A. van Kesteren, “Cross Origin Resource Sharing”, CORS, W3C Draft, July, 2010.
