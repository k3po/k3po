## WebSocket Protocol: x-kaazing-handshake

### Abstract

This document specifies the behavior of a WebSocket protocol called the “Kaazing WebSocket Handshake Protocol” (KHP).  It can be used to enable extended WebSocket capabilities for WebSocket clients. It is intended for use with native WebSocket implementations.

### Requirements

The key words "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL NOT", "SHOULD", "SHOULD NOT", "RECOMMENDED", "MAY", and "OPTIONAL" in this document are to be interpreted as described in [RFC 2119](https://tools.ietf.org/html/rfc2119).

## Table of Contents

  * [Introduction](#introduction)
  * [Protocol Handshake Requirements](#protocol-handshake-requirements)
    * [Handshake Requirements for the Opening WebSocket Handshake](#handshake-requirements-for-the-opening-websocket-handshake)
    * [Handshake Requirements for the Extended WebSocket Handshake](#handshake-requirements-for-the-extended-websocket-handshake)
  * [Diagrams](#diagrams)
  * [Kaazing Extended Handshake Response Codes](#kaazing-extended-handshake-response-codes)
    * [1xx Informational](#1xx-informational)
    * [2xx success](#2xx-success)
    * [3xx Redirection](#3xx-redirection)
    * [4xx Client Error](#4xx-client-error)
    * [5xx Server Error](#5xx-server-error)
  * [References](#references)

### Introduction

The WebSocket protocol [[WSP](http://tools.ietf.org/html/draft-ietf-hybi-thewebsocketprotocol-17)] defines notions of extension and sub-protocols. [Section 1.9](http://tools.ietf.org/html/draft-ietf-hybi-thewebsocketprotocol-17#section-1.9) of [WSP] specifies that “WebSocket subprotocols” can be negotiated at time of initial handshake. This document describes a new Kaazing-specific sub-protocol whose role is to define an additional handshake, allowing the client to negotiate additional capabilities, loosely called “extensions”, or “application-class extensions” to distinguish these from [WSP] extensions.

The intention of this document is to define a WebSocket sub-protocol, named “x-kaazing-handshake” that allows for negotiation of available Kaazing-specific capabilities that will work over established native WebSocket connections.

The extended handshake defined in this document provides an opportunity for clients to negotiate application-class extensions after the WebSocket is established but before the application puts the WebSocket to use.

### Protocol Handshake Requirements

This section describes the handshake requirements necessary for establishing a WebSocket that speaks the KHP subprotocol.

There are two handshake request/response message pairs between a client and a server to establish a KHP WebSocket. The first request/response message pair is called the “Opening” handshake and is a standard WebSocket opening handshake using the HTTP protocol. This is to establish the raw WebSocket and to ensure that the KHP is the chosen protocol. 

The second message pair is called the “Extended” handshake; messages are sent using WebSocket messages over the established raw WebSocket. The WebSocket is not said to be _open_ until the second message pair has successfully completed.

An “extended” handshake MUST be required when KHP is the chosen protocol during the “opening” handshake.

#### Handshake Requirements for the Opening WebSocket Handshake

To negotiate the use of KHP, the WebSocket client MUST include the following sub-protocol value within the raw WebSocket handshake:

	x-kaazing-handshake

For example, the following header would be sufficient:

	Sec-WebSocket-Protocol: x-kaazing-handshake, amqp, ...

The rules defined in [WSP] apply to the server receiving such a WebSocket opening handshake.

If the server does agree to use the x-kaazing-handshake protocol, <sup>[[a]]()</sup>the server MUST send back a |Sec-WebSocket-Protocol| header field containing x-kaazing-handshake in its response.




#### Handshake Requirements for the Extended WebSocket Handshake

The extended handshake requires a WebSocket message from client to server (“extended handshake request”), followed by a server response to the client (“extended handshake response”). The extended handshake MUST only commence when the client and server successfully negotiate the “x-kaazing-handshake” protocol in the opening handshake. 

No other messages should be sent by client or server until the _extended handshake is completed_<sup>

The extended handshake WebSocket payloads take the form of HTTP Requests (for extended handshake requests) and HTTP Responses (for extended handshake responses) following the HTTP 1.1 protocol [RFC 2616].

| Title |
| --------------------- |
| Web Socket Frame |
| Http Request/Response |



If protocol negotiation was specified in the opening handshake, any protocols requested other than “x-kaazing-handshake” MUST be included in the extended handshake request as values in the header:

	Sec-WebSocket-Protocol:

If any WebSocket extensions were requested but not negotiated successfully in the opening handshake, then the client MUSTinclude any such extensions inside extended handshake request as values in the header:

	Sec-WebSocket-Extensions

The “Sec-WebSocket-Extensions” header MUST contain a non-empty list of extension names which the client desires, in an order of client preference, with the same semantics for the header as [WSP].

For example, inclusion of the following headers in the HTTP request payload within the first extended handshake request message indicates the client wishes to use a compression extension:

	Sec-WebSocket-Extensions: x-kaazing-compression

In the case that they are required, a client MAY include other headers in the first WebSocket message along with those above. This allows extension-specific information to be communicated between client and server without the need for additional handshakes.

A WebSocket server receiving the extended handshake message MUST respond with an extended handshake response message. 

In the case where the extended handshake contains an extension that was already negotiated in the opening handshake, the server MUST detect that condition and respond in error before _Closing the WebSocket Connection_.

The extended handshake response contains a HTTP status code which may or may not be “actionable”, as defined in a separate section below. When the extended handshake response has an HTTP status code that is not actionable, it is said that the _extended handshake failed_.

The actionable HTTP status codes are referenced in a separate section below. For the sake of example, some interesting response codes and their interpretation when seen in an extended handshake response are detailed in the table below.


| Extended Handshake Response Code | Client Behavior |
| ---------- | ---------- |
| 101 Switching Protocols | Clients receiving a 101 extended response SHOULD consider the _extended handshake completed_. |
| 302 Temporary Redirect | Clients receiving a 302 or 307 extended response MUST _Close the WebSocket Connection_ following the definition in [WSP], and proceed with a fresh opening handshake to the URL specified in the Location: header of the extended handshake response.  The client should make no more than a fixed maximum number of reconnect attempts when faced with these redirect codes.  Additionally, client is responsible for detecting redirect loops to matching resources and avoiding reconnection attempts in such scenarios. | 
| 401 Unauthorized | Clients receiving a 401 extended response SHOULD continue the extended handshake, and reply with a replayed extended handshake request containing additional authorization information.  If the client fails to reply in a timely manner, it is the server’s responsibility to _Close the WebSocket Connection_. |

The extended handshake response MAY include these headers: 

        Sec-WebSocket-Extensions

The “Sec-WebSocket-Extensions” header if present MUST contain a non-empty list of extensions, chosen from the requested extensions by the client and no others, which are to be used for further client-server communication.   If the “Sec-WebSocket-Extensions” header is not present, no extensions were negotiated and none apply.  Where there are no conflicts, the standard interpretation of this header as defined in [WSP] should also apply.

In the case that they are required, the server MAY include other headers in the WebSocket response message along with those above.  This allows information to be communicated from  server to client, in particular any reasons for failure.

The client then receives the extended handshake response message.  The client must _Close the WebSocket Connection_ following the definition in [WSP] if any of the following are true:
* the raw WebSocket opened during the opening handshake is closed
* the _extended handshake failed_
* the Sec-WebSocket-Extensions selected by the server are not a subset of  those requested

At this point, if the WebSocket is not closed, it is said that the _extended handshake is completed_.

### Diagrams 

Green means web socket framing around an HTTP payload content.

![](image00.png)

### Kaazing Extended Handshake Response Codes
This section captures the expected interpretation of the extended response by the client for each of the following embedded extended HTTP response code.  The definitions for the HTTP response codes were taken from the [List of HTTP Status Codes](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes) on Wikipedia 10/20/2011.

#### 1xx Informational

Request received, continuing process.[[2]](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#cite_note-RFC_2616-1)

This class of status code indicates a provisional response, consisting only of the Status-Line and optional headers, and is terminated by an empty line. Since HTTP/1.0 did not define any 1xx status codes, serversmust not send a 1xx response to an HTTP/1.0 client except under experimental conditions.

| | |
| ---- | ----|
| HTTP Response Code | 100 Continue <br> This means that the server has received the request headers, and that the client should proceed to send the request body (in the case of a request for which a body needs to be sent; for example, a [POST](https://bit.ly/1dWx8n5) request. If the request body is large, sending it to a server when a request has already been rejected based upon inappropriate headers is inefficient. To have a server check if the request could be accepted based on the request's headers alone, a client must send Expect: 100-continue as a header in its initial request[[2]](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#cite_note-RFC_2616-1) and check if a 100 Continue status code is received in response before continuing (or receive 417 Expectation Failed and not continue).[[2]](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#cite_note-RFC_2616-1) |
| Extended Handshake Action | If the client requests this with an Expect: header, and the WebSocket server supports it, then this is a valid extended handshake response code, actionable on the client which MUST send the request body. |
	
| | |
| ---- | ---- |
| HTTP Response Code | 101 Switching Protocols <br> This means the requester has asked the server to switch protocols and the server is acknowledging that it will do so.[[2]](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#cite_note-RFC_2616-1) |
| Extended Handshake Action | Clients receiving a valid WebSocket handshake [WSP] 101 extended response that corresponds to their request MUST consider the _extended handshake completed_. |
	
| | |
| ---- | ---- |
| HTTP Response Code | 102 Processing [(WebDAV)](https://en.wikipedia.org/wiki/WebDAV) (RFC 2518) <br> As a WebDAV request may contain many sub-requests involving file operations, it may take a long time to complete the request. This code indicates that the server has received and is processing the request, but no response is available yet.[[3]](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#cite_note-RFC_2518-2) This prevents the client from timing out and assuming the request was lost. |
| Extended Handshake Action | A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]. |

| | |
| --- | --- |
|HTTP Response Code | 103 Checkpoint <br> This code is used in the Resumable HTTP Requests Proposal to resume aborted PUT or POST requests.[[4]](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#cite_note-ResumableHttpRequestsProposal-3) |
| Extended Handshake Action | A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]. |
	
| | |
| --- | ---|
| HTTP Response Code | 122 Request-URI too long <br> This is a non-standard IE7-only code which means the URI is longer than a maximum of 2083 characters.[[5]](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#cite_note-4)[[6]](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#cite_note-5) (See code 414.) |
| Extended Handshake Action | A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]. |

#### 2xx Success

This class of status codes indicates the action requested by the client was received, understood, accepted and processed successfully.

| | |
| --- | ---|
| HTTP Response Code | 200 OK <br> Standard response for successful HTTP requests. The actual response will depend on the request method used. In a GET request, the response will contain an entity corresponding to the requested resource. In a POST request the response will contain an entity describing or containing the result of the action.[[2]](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#cite_note-RFC_2616-1) |
| Extended Handshake Action | A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].  The intention of the extended handshake is to receive a 101 upgrade rather than obtain a successful response. |
	
| | |
| --- | ---|
| HTTP Response Code | 201 Created <br> The request has been fulfilled and resulted in a new resource being created.[[2]](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#cite_note-RFC_2616-1) |
| Extended Handshake Action | A Client receiving 201 extended response that corresponds to their request MUST consider the _extended handshake completed_.  The intention of the server using this response code rather than a 101 response code indicates that perhaps an emulated or alternative WebSocket has been established.  The content of the response and response headers will determine exactly which. |
	
| | |
| --- | ---|
| HTTP Response Code | 202 Accepted <br> The request has been accepted for processing, but the processing has not been completed. The request might or might not eventually be acted upon, as it might be disallowed when processing actually takes place.[[2]](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#cite_note-RFC_2616-1) |
| Extended Handshake Action | A client receiving this response can choose to wait for up to a maximum amount of time for another response, before _Closing the WebSocket Connection_. |
	
| | |
| --- | ---|
| HTTP Response Code | 203 Non-Authoritative Information (since HTTP/1.1) <br> The server successfully processed the request, but is returning information that may be from another source.[[2]](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#cite_note-RFC_2616-1) |
| Extended Handshake Action | A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]. |
	
| | |
| --- | ---|
| HTTP Response Code | 204 No Content <br> The server successfully processed the request, but is not returning any content.[[2]](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#cite_note-RFC_2616-1) |
| Extended Handshake Action | A Client receiving a 204 extended response that corresponds to their request MUST consider the _extended handshake completed_. |
	
| | |
| --- | ---|
| HTTP Response Code | 205 Reset Content <br> The server successfully processed the request, but is not returning any content. Unlike a 204 response, this response requires that the requester reset the document view.[[2]](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#cite_note-RFC_2616-1) |
| Extended Handshake Action | A Client receiving 205 extended response that corresponds to their request MUST consider the _extended handshake completed_. |
	
| | |
| --- | ---|
| HTTP Response Code | 206 Partial Content <br> The server is delivering only part of the resource due to a range header sent by the client. The range header is used by tools like [wget](https://en.wikipedia.org/wiki/Wget) to enable resuming of interrupted downloads, or split a download into multiple simultaneous streams.[[2]](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#cite_note-RFC_2616-1)
| Extended Handshake Action | A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]. |
	
| | |
| --- | ---|
| HTTP Response Code | 207 Multi-Status (WebDAV) (RFC 4918) <br> The message body that follows is an [XML](https://en.wikipedia.org/wiki/XML) message and can contain a number of separate response codes, depending on how many sub-requests were made.[[7]](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#cite_note-RFC_4918-6) |
| Extended Handshake Action | A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]. |
	
| | |
| --- | ---|
| HTTP Response Code | 226 IM Used (RFC 3229) <br> The server has fulfilled a GET request for the resource, and the response is a representation of the result of one or more instance-manipulations applied to the current instance. [8](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#cite_note-RFC_3229-7) |
| Extended Handshake Action | A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]. |

#### 3xx Redirection

The client must take additional action to complete the request.[2](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#cite_note-RFC_2616-1)
This class of status code indicates that further action needs to be taken by the user agent in order to fulfil the request. The action required may be carried out by the user agent without interaction with the user if and only if the method used in the second request is GET or HEAD. A user agent should not automatically redirect a request more than five times, since such redirections usually indicate an [infinite loop](https://en.wikipedia.org/wiki/Infinite_loop).

HTTP Response Code
	300 Multiple Choices
Indicates multiple options for the resource that the client may follow. It, for instance, could be used to present different format options for video, list files with different extensions, or word sense disambiguation.[2]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP], and SHOULD attempt an opening handshake on one of the given URIs in the choices presented.
	

HTTP Response Code
	301 Moved Permanently
This and all future requests should be directed to the given URI.[2]
	Extended Handshake Action
	

A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP], and SHOULDattempt an opening handshake on the given URI.
	

HTTP Response Code
	302 Found
This is an example of industrial practice contradicting the standard.[2] HTTP/1.0 specification (RFC 1945) required the client to perform a temporary redirect (the original describing phrase was "Moved Temporarily"),[9] but popular browsers implemented 302 with the functionality of a 303 See Other. Therefore, HTTP/1.1 added status codes 303 and 307 to distinguish between the two behaviours.[10] However, some Web applications and frameworks use the 302 status code as if it were the 303.
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP], and SHOULD attempt an opening handshake on the given URI.
	

HTTP Response Code
	303 See Other (since HTTP/1.1)
The response to the request can be found under another URI using a GET method. When received in response to a POST (or PUT/DELETE), it should be assumed that the server has received the data and the redirect should be issued with a separate GET message.[2]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP], and attempt an opening handshake on the given URI.
	

HTTP Response Code
	304 Not Modified
Indicates the resource has not been modified since last requested.[2] Typically, the HTTP client provides a header like the If-Modified-Since header to provide a time against which to compare. Using this saves bandwidth and reprocessing on both the server and client, as only the header data must be sent and received in comparison to the entirety of the page being re-processed by the server, then sent again using more bandwidth of the server and client.
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	

HTTP Response Code
	305 Use Proxy (since HTTP/1.1)
Many HTTP clients (such as Mozilla[11] and Internet Explorer) do not correctly handle responses with this status code, primarily for security reasons.[2]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	

HTTP Response Code
	306 Switch Proxy
No longer used.[2] Originally meant "Subsequent requests should use the specified proxy."[12]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	

HTTP Response Code
	307 Temporary Redirect (since HTTP/1.1)
In this occasion, the request should be repeated with another URI, but future requests can still use the original URI.[2] In contrast to 303, the request method should not be changed when reissuing the original request. For instance, a POST request must be repeated using another POST request.
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].  If and only if the underlying HTTP method was a GET request, the client MUST attempt an opening handshake on the given URI.
	

HTTP Response Code
	308 Resume Incomplete
This code is used in the Resumable HTTP Requests Proposal to resume aborted PUT or POST requests.[4]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	4xx Client Error
The 4xx class of status code is intended for cases in which the client seems to have erred. Except when responding to a HEAD request, the server should include an entity containing an explanation of the error situation, and whether it is a temporary or permanent condition. These status codes are applicable to any request method. User agents should display any included entity to the user. These are typically the most common error codes encountered while online.
HTTP Response Code
	400 Bad Request
The request cannot be fulfilled due to bad syntax.[2]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	

HTTP Response Code
	401 Unauthorized
Similar to 403 Forbidden, but specifically for use when authentication is possible but has failed or not yet been provided.[2] The response must include a WWW-Authenticate header field containing a challenge applicable to the requested resource. See Basic access authentication and Digest access authentication.
	Extended Handshake Action
	Clients receiving a 401 extended response SHOULD continue the extended handshake, and reply with a replayed extended handshake request containing additional authorization information.  If the client fails to reply in a timely manner, it is the server’s responsibility to _Close the WebSocket Connection_.
	

HTTP Response Code
	402 Payment Required
Reserved for future use.[2] The original intention was that this code might be used as part of some form of digital cash or micropayment scheme, but that has not happened, and this code is not usually used. As an example of its use, however, Apple's MobileMe service generates a 402 error ("httpStatusCode:402" in the Mac OS X Console log) if the MobileMe account is delinquent.
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	

HTTP Response Code
	403 Forbidden
The request was a legal request, but the server is refusing to respond to it.[2] Unlike a 401 Unauthorized response, authenticating will make no difference.[2]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	

HTTP Response Code
	404 Not Found
The requested resource could not be found but may be available again in the future.[2] Subsequent requests by the client are permissible.
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	

HTTP Response Code
	405 Method Not Allowed
A request was made of a resource using a request method not supported by that resource;[2] for example, using GET on a form which requires data to be presented via POST, or using PUT on a read-only resource.
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	

HTTP Response Code
	406 Not Acceptable
The requested resource is only capable of generating content not acceptable according to the Accept headers sent in the request.[2]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	

HTTP Response Code
	407 Proxy Authentication Required
The client must first authenticate itself with the proxy.[2]
	Extended Handshake Action
	

A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP], and MAY choose to attempt to authenticate with a proxy.
	

HTTP Response Code
	408 Request Timeout
The server timed out waiting for the request.[2] According to W3 HTTP specifications: "The client did not produce a request within the time that the server was prepared to wait. The client MAY repeat the request without modifications at any later time."
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].
	

HTTP Response Code
	409 Conflict
Indicates that the request could not be processed because of conflict in the request, such as an edit conflict.[2]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	

HTTP Response Code
	410 Gone
Indicates that the resource requested is no longer available and will not be available again.[2] This should be used when a resource has been intentionally removed and the resource should be purged. Upon receiving a 410 status code, the client should not request the resource again in the future. Clients such as search engines should remove the resource from their indices. Most use cases do not require clients and search engines to purge the resource, and a "404 Not Found" may be used instead.
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	

HTTP Response Code
	411 Length Required
The request did not specify the length of its content, which is required by the requested resource.[2]



	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	

HTTP Response Code
	412 Precondition Failed
The server does not meet one of the preconditions that the requester put on the request.[2]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	

HTTP Response Code
	413 Request Entity Too Large
The request is larger than the server is willing or able to process.[2]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	

HTTP Response Code
	414 Request-URI Too Long
The URI provided was too long for the server to process.[2]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	

HTTP Response Code
	415 Unsupported Media Type
The request entity has a media type which the server or resource does not support.[2] For example, the client uploads an image as image/svg+xml, but the server requires that images use a different format.
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	

HTTP Response Code
	416 Requested Range Not Satisfiable
The client has asked for a portion of the file, but the server cannot supply that portion.[2] For example, if the client asked for a part of the file that lies beyond the end of the file.
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	

HTTP Response Code
	417 Expectation Failed
The server cannot meet the requirements of the Expect request-header field.[2]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	

HTTP Response Code
	418 I'm a teapot (RFC 2324)
This code was defined in 1998 as one of the traditional IETF April Fools' jokes, in RFC 2324, Hyper Text Coffee Pot Control Protocol, and is not expected to be implemented by actual HTTP servers.
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	

HTTP Response Code
	422 Unprocessable Entity (WebDAV) (RFC 4918)
The request was well-formed but was unable to be followed due to semantic errors.[7]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	

HTTP Response Code
	423 Locked (WebDAV) (RFC 4918)
The resource that is being accessed is locked.[7]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	

HTTP Response Code
	424 Failed Dependency (WebDAV) (RFC 4918)
The request failed due to failure of a previous request (e.g. a PROPPATCH).[7]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	

HTTP Response Code
	425 Unordered Collection (RFC 3648)
Defined in drafts of "WebDAV Advanced Collections Protocol",[13] but not present in "Web Distributed Authoring and Versioning (WebDAV) Ordered Collections Protocol".[14]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	

HTTP Response Code
	426 Upgrade Required (RFC 2817)
The client should switch to a different protocol such as TLS/1.0.[15]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP], and MAY attempt to establish a new opening handshake using an upgraded protocol.
	

HTTP Response Code
	428 Precondition Required
The origin server requires the request to be conditional. Intended to prevent "the 'lost update' problem, where a client GETs a resource's state, modifies it, and PUTs it back to the server, when meanwhile a third party has modified the state on the server, leading to a conflict."[16] Proposed in an Internet-Draft.


A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	

HTTP Response Code
	429 Too Many Requests
The user has sent too many requests in a given amount of time. Intended for use with rate limiting schemes. Proposed in an Internet-Draft.[16]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	

HTTP Response Code
	431 Request Header Fields Too Large
The server is unwilling to process the request because either an individual header field, or all the header fields collectively, are too large. Proposed in an Internet-Draft.[16]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	

HTTP Response Code
	444 No Response
A Nginx HTTP server extension. The server returns no information to the client and closes the connection (useful as a deterrent for malware).
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	

HTTP Response Code
	449 Retry With
A Microsoft extension. The request should be retried after performing the appropriate action.[17]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	

HTTP Response Code
	450 Blocked by Windows Parental Controls
A Microsoft extension. This error is given when Windows Parental Controls are turned on and are blocking access to the given webpage.[18]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	

HTTP Response Code
	499 Client Closed Request
An Nginx HTTP server extension. This code is introduced to log the case when the connection is closed by client while HTTP server is processing its request, making server unable to send the HTTP header back.[19]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]
	

5xx Server Error
The server failed to fulfill an apparently valid request.[2]


Response status codes beginning with the digit "5" indicate cases in which the server is aware that it has encountered an error or is otherwise incapable of performing the request. Except when responding to a HEAD request, the server should include an entity containing an explanation of the error situation, and indicate whether it is a temporary or permanent condition. Likewise, user agents should display any included entity to the user. These response codes are applicable to any request method.
HTTP Response Code
	500 Internal Server Error
A generic error message, given when no more specific message is suitable.[2]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].
	

HTTP Response Code
	501 Not Implemented
The server either does not recognise the request method, or it lacks the ability to fulfill the request.[2]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].
	

HTTP Response Code
	502 Bad Gateway
The server was acting as a gateway or proxy and received an invalid response from the upstream server.[2]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].
	

HTTP Response Code
	503 Service Unavailable
The server is currently unavailable (because it is overloaded or down for maintenance).[2] Generally, this is a temporary state.
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].
	

HTTP Response Code
	504 Gateway Timeout
The server was acting as a gateway or proxy and did not receive a timely response from the upstream server.[2]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].
	

HTTP Response Code
	505 HTTP Version Not Supported
The server does not support the HTTP protocol version used in the request.[2]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].
	

HTTP Response Code
	506 Variant Also Negotiates (RFC 2295)
Transparent content negotiation for the request results in a circular reference.[20]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].
	

HTTP Response Code
	507 Insufficient Storage (WebDAV) (RFC 4918)
The server is unable to store the representation needed to complete the request.[7]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].
	

HTTP Response Code
	509 Bandwidth Limit Exceeded (Apache bw/limited extension)
This status code, while used by many servers, is not specified in any RFCs.
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].
	

HTTP Response Code
	510 Not Extended (RFC 2774)
Further extensions to the request are required for the server to fulfill it.[21]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].
	

HTTP Response Code
	511 Network Authentication Required
The client needs to authenticate to gain network access. Intended for use by intercepting proxies used to control access to the network (e.g. "captive portals" used to require agreement to Terms of Service before granting full Internet access via a Wi-Fi hotspot). Proposed in an Internet-Draft.[16]
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].
	

HTTP Response Code
	598 (Informal convention) network read timeout error
This status code is not specified in any RFCs, but is used by some HTTP proxies to signal a network read timeout behind the proxy to a client in front of the proxy.
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].
	

HTTP Response Code
	599 (Informal convention) network connect timeout error
This status code is not specified in any RFCs, but is used by some HTTP proxies to signal a network connect timeout behind the proxy to a client in front of the proxy.
	Extended Handshake Action
	A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].
	

References


[RFC2119]  Bradner, S., "Key words for use in RFCs to Indicate Requirement Levels", BCP 14, RFC 2119, March 1997.


[WSP] The WebSocket Protocol, I.Fette, IETF, WSP


[RFC822 Section 3.1] David H. Crocker, University of Delaware,  RFC822 Section 3.1
[RFC 2616] HTTP 1.1 Protocol Specification 


1. ^ "The HTTP status codes in IIS 7.0". Microsoft. July 14, 2009. Retrieved April 1, 2009.
2. ^ a b c d e f g h i j k l m n o p q r s t u v w x y z aa ab ac ad ae af ag ah ai aj ak al am an ao ap aq ar as at Fielding, Roy T.; Gettys, James; Mogul, Jeffrey C.; Nielsen, Henrik Frystyk; Masinter, Larry; Leach, Paul J.; Berners-Lee, Tim (June 1999). Hypertext Transfer Protocol -- HTTP/1.1. IETF. RFC 2616. Retrieved October 24, 2009.
3. ^ Goland, Yaron; Whitehead, Jim; Faizi, Asad; Carter, Steve R.; Jensen, Del (February 1999). HTTP Extensions for Distributed Authoring -- WEBDAV. IETF. RFC 2518. Retrieved October 24, 2009.
4. ^ a b "A proposal for supporting resumable POST/PUT HTTP requests in HTTP/1.0.". Google. 2010. Retrieved August 8, 2011.
5. ^ Support.microsoft.com
6. ^ Lorem.biz
7. ^ a b c d e Dusseault, Lisa, ed (June 2007). HTTP Extensions for Web Distributed Authoring and Versioning (WebDAV). IETF. RFC 4918. Retrieved October 24, 2009.
8. ^ Delta encoding in HTTP. IETF. January 2002. RFC 3229. Retrieved February 25, 2011.
9. ^ Berners-Lee, Tim; Fielding, Roy T.; Nielsen, Henrik Frystyk (May 1996). Hypertext Transfer Protocol -- HTTP/1.0. IETF. RFC 1945. Retrieved October 24, 2009.
10. ^ "HTTP/1.1 Section 10 Status Code Definitions". W3C. Retrieved March 16, 2010.
11. ^ "Mozilla Bugzilla Bug 187996: Strange behavior on 305 redirect". March 3, 2003. Retrieved May 21, 2009.
12. ^ Cohen, Josh. "HTTP/1.1 305 and 306 Response Codes". HTTP Working Group.
13. ^ Slein, Judy; Whitehead, Jim; Davis, Jim; Clemm, Geoffrey; Fay, Chuck; Crawford, Jason; Chihaya, Tyson (June 18, 1999). WebDAV Advanced Collections Protocol. IETF. I-D draft-ietf-webdav-collection-protocol-04. Retrieved October 24, 2009.
14. ^ Whitehead, Jim (December 2003). Reschke, Julian F.. ed. Web Distributed Authoring and Versioning (WebDAV) Ordered Collections Protocol. IETF. RFC 3648. Retrieved October 24, 2009.
15. ^ Khare, Rohit; Lawrence, Scott (May 2000). Upgrading to TLS Within HTTP/1.1. IETF. RFC 2817. Retrieved October 24, 2009.
16. ^ a b c d Nottingham, M.; Fielding, R. (18 October 2011). "draft-nottingham-http-new-status-02 - Additional HTTP Status Codes". Internet-Drafts. Internet Engineering Task Force. Retrieved 2011-10-22.
17. ^ "2.2.6 449 Retry With Status Code". Microsoft. 2009. Retrieved October 26, 2009.
18. ^ "Screenshot of error page" (bmp). Retrieved October 11, 2009.
19. ^ Sysoev, Igor (August 2007). "Re: 499 error in nginx". Retrieved December 09, 2010.
20. ^ Holtman, Koen; Mutz, Andrew H. (March 1998). Transparent Content Negotiation in HTTP. IETF. RFC 2295. Retrieved October 24, 2009.
21. ^ Nielsen, Henrik Frystyk; Leach, Paul J.; Lawrence, Scott (February 2000). An HTTP Extension Framework. IETF. RFC 2774. Retrieved October 24, 2009.


[a]Note about Sub-Protocol Versioning


Subprotocols can be versioned in backwards-incompatible ways by changing the subprotocol name.  Subsequent backwards-incompatible versions of the x-kaazing-extensions protocol can be versioned witha  suffix; for example: x-kaazing-handshake-v2.
[b]What should happen if the client is misbehaving, and attempts to send messages prior to the completion of the KHP extended handshake?
[c]You should get a 400 in reality.  Not sure if I want to spec that.   Worth testing though - can you file a bug for me to test this and other negative cases?


Sent from my iPhone
