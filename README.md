# Binary Bidirectional Streams Over HTTP (BBOSH)
The Bidirectional Streams Over HTTP (BOSH) transport protocol was initially designed to transmit XML text for XMPP.
The BBOSH transport protocol extends BOSH by adding support for binary, such that it can be used to replace TCP where
WebSocket is not available.  No HTTP streaming is required, just HTTP polling or long-polling.

## Connect
A standard REST request is used to create the BBOSH connection.  Following the REST pattern, the `Location` response header
indicates the URL to be used for further communication on the new connection.

The `X-Protocol` request header captures the name and version of the BBOSH protocol, allowing for evolution of the protocol.

The `X-Accept-Strategy` and `X-Strategy` headers are used to negotiate the interaction between the BBOSH client and server.

The "polling" strategy interval indicates the time that a client should wait between receiving the previous response
and initiating the next polling request.  The polling BBOSH server can safely reclaim resources associated with the BBOSH
connection if the interval has been exceeded sufficiently to account for network round-trip time.

The "long-polling" strategy interval indicates the maximum time that a server should wait between receiving the previous request
and initiating the next response when there is no data. If data is available, then it is sent as the body of the response, and
a the client then initiates a new request immediately, potentially sending additional data in the request body. The long-polling
BBOSH server can safely reclaim resources associated with the BBOSH connection if network round-trip time has been exceeded
sufficiently.  A long-polling BBOSH client can detect an error if the time to receive a response exceeds the the interval 
sufficiently to account for network round-trip time.

    POST /connection HTTP/1.1
    Accept: application/octet-stream
    Content-Type: application/octet-stream
    Content-Length: ...
    X-Protocol: bbosh/1.0
    X-Sequence-No: 0
    X-Accept-Strategy:  polling;interval=5s, long-polling;interval=30s;requests=5
    [body optional]
    
    HTTP/1.1 201 Created
    Cache-Control: no-cache
    Content-Type: application/octet-stream
    Content-Length: ...
    Location: /controls/[ID]
    X-Strategy: polling;interval=5s
    [body optional]

The sequence number can be any value for this create request, but subsequent requests must increment the sequence number by 1 
each time.

Note: the `Content-Type` header may be omitted if `Content-Length` is zero.
Note: the `204` status code may be used if `Content-Length` is zero.

## Read & Write
In general, each HTTP request body writes data to the connection, and each HTTP response body reads data from the connection.

The "polling" strategy permits only one outstanding HTTP request per BBOSH connection. Therefore, if a polling HTTP request is
currently in flight when the client makes an attempt to write data to the connection, the HTTP response must first be received
before the next HTTP request can be sent with the data to be written.

The "long-polling" strategy permits at least two outstanding HTTP requests per BBOSH connection. Therefore, if a long-polling
HTTP request is currently in flight when the client makes an attempt to write data to the connection, a second HTTP request can
be sent in parallel. This automatically causes the first response to be sent by the BBOSH server, and the second request then
assumes responsibility for the long-polling response. The maximum number of in-flight requests is governed by the "requests"
parameter to the "long-polling" strategy.

Since the "long-polling" strategy uses more than one HTTP request in parallel, each request must be annotated with a sequence
number allowing the BBOSH server to process requests in the same order that they were sent by the BBOSH client, even if those
requests arrive at the server in a different order.  This is done by using the `X-Sequence-No` header which cannot exceed 2^53-1.
The maximum number of requests that can arrive out of order is governed by the "requests" parameter to the "long-polling"
strategy.

    PUT /connection/[ID] HTTP/1.1
    Accept: application/octet-stream
    Content-Type: application/octet-stream
    Content-Length: ...
    X-Sequence-No: 1
    [body optional]
    
    HTTP/1.1 200 OK
    Cache-Control: no-cache
    Content-Type: application/octet-stream
    Content-Length: ...
    [body optional]

Note: the `Content-Type` header may be omitted if `Content-Length` is zero.
Note: the `204` status code may be used if `Content-Length` is zero.

When no new data needs to be written to the BBOSH connection, the subsequent HTTP request can use the `GET` method instead.

    GET /connection/[ID] HTTP/1.1
    Accept: application/octet-stream
    X-Sequence-No: 2
    [no body]
    
    HTTP/1.1 200 OK
    Cache-Control: no-cache
    Content-Type: application/octet-stream
    Content-Length: ...
    [body optional]

Note: the `Content-Type` header may be omitted if `Content-Length` is zero.
Note: the `204` status code may be used if `Content-Length` is zero.

## Close (server)
When the server chooses to close the BBOSH connection, it sends `404` as the next response status code, with optional body to
flush any remaining data to the client.

    PUT /connection/[ID] HTTP/1.1
    Accept: application/octet-stream
    Content-Type: application/octet-stream
    Content-Length: ...
    X-Sequence-No: ...
    [body optional]
    
    HTTP/1.1 404 Not Found
    Cache-Control: no-cache
    Content-Type: application/octet-stream
    Content-Length: ...
    [body optional]

Note: the `Content-Type` header may be omitted if `Content-Length` is zero.

## Close (client)
When the client chooses to close the BBOSH connection, it sends a DELETE request with optional body to flush any remaining
data to the server.  In response, the server confirms the connection has been closed with 200 OK status code, and optional body
to flush any remaining data to the client.

    DELETE /connection/[ID] HTTP/1.1
    Accept: application/octet-stream
    Content-Type: application/octet-stream
    Content-Length: ...
    X-Sequence-No: ...
    [body optional]
    
    HTTP/1.1 200 OK
    Cache-Control: no-cache
    Content-Type: application/octet-stream
    Content-Length: ...
    [body optional]

Note: the `Content-Type` header may be omitted if `Content-Length` is zero.
Note: the `204` status code may be used if `Content-Length` is zero.

## Close (simultaneous)
When both the client and server choose to close the BBOSH connection simultaneously, the client sends a DELETE request as before,
and the server's response has status code 404, still with optional body to flush any remaining data to the client.

    DELETE /connection/[ID] HTTP/1.1
    Accept: application/octet-stream
    Content-Type: application/octet-stream
    Content-Length: ...
    X-Sequence-No: ...
    [body optional]
    
    HTTP/1.1 404 Not Found
    Cache-Control: no-cache
    Content-Type: application/octet-stream
    Content-Length: ...
    [body optional]

## HTTP PUT or DELETE method not available?
Use `POST` method with `X-HTTP-Method-Override` header with value of `PUT` or `DELETE`.

## HTTP Content-Type application/octet-stream not available?
Use `text/plain;charset=utf-8` instead and encode the payload as utf8-encoded-binary.

If a client cannot send or receive a specific character values such as `\u0000`, use the higher order equivalent character value
such as `\u0100`. Only the lower 8 bits are used to decode the byte value.

Use the `X-Accept-Charset-Mask` header to indicate which of the 256 byte values need to be masked with `0x0100` to create the
higher order equivalent character values.  The value of this header is a base64-encoded deflated 256-bit sequence, where a 1 bit
indicates that the byte value in that bit position requires masking with 0x100 to calculate the corresponding character value.

The `X-Charset-Mask` header indicates which of the 256 byte values are masked with `0x100` in the current payload using the same
syntax for the header value as `X-Accept-Charset-Mask`.

## HTTP response status code 201 or 204 not available?
When a client has minimal support for HTTP status codes (Flash plugin), use request header `X-Accept-HTTP-Status-Code` with 
status codes that can be processed by the client, such as 200 indicating that all `2xx` codes should be returned as status code 
`200` with `X-HTTP-Status-Code-Override` response header containing the actual status code, or `3xx` indicating that all `3xx`
status codes can be handled explicitly.

    POST /connection HTTP/1.1
    Accept: application/octet-stream
    Content-Type: application/octet-stream
    Content-Length: ...
    X-Protocol: bbosh/1.0
    X-Accept-Strategy:  polling;interval=5s, long-polling;interval=30s;requests=5
    X-Accept-HTTP-Status-Code: 200, 3xx, 404, 5xx
    [body optional]
    
    HTTP/1.1 200 Created
    Cache-Control: no-cache
    Content-Type: application/octet-stream
    Content-Length: ...
    Location: /controls/[ID]
    X-Strategy: polling;interval=5s
    X-HTTP-Status-Code-Override: 201
    [body optional]

Note: this example effectively disables the (minor) benefit of response status code `204` because `Content-Length` cannot be
omitted for response status code `200`.
