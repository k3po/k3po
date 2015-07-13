## <a name="h.we9xo0tvcx5l"></a><span>WebSocket Protocol: x-kaazing-handshake</span>

### <a name="h.u9x9rr3l26nc"></a><span>Abstract</span>

<span>This document specifies the behavior of a WebSocket protocol called the “Kaazing WebSocket Handshake Protocol” (KHP).   It can be used to enable extended WebSocket capabilities for WebSocket clients.  It is intended for use with native WebSocket implementations.</span>

### <a name="h.8a8tb1xahm5w"></a><span>Requirements</span>

<span>The key words "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL NOT", "SHOULD", "SHOULD NOT", "RECOMMENDED", "MAY", and "OPTIONAL" in this document are to be interpreted as described in</span><span>[ ](https://www.google.com/url?q=https%3A%2F%2Ftools.ietf.org%2Fhtml%2Frfc2119&sa=D&sntz=1&usg=AFQjCNENh9xddb5YwJQTsFNAfEXW1zXB2w)</span><span class="c14">[RFC 2119](https://www.google.com/url?q=https%3A%2F%2Ftools.ietf.org%2Fhtml%2Frfc2119&sa=D&sntz=1&usg=AFQjCNENh9xddb5YwJQTsFNAfEXW1zXB2w)</span><span> [</span><span class="c14">[RFC2119](https://www.google.com/url?q=https%3A%2F%2Ftools.ietf.org%2Fhtml%2Frfc2119&sa=D&sntz=1&usg=AFQjCNENh9xddb5YwJQTsFNAfEXW1zXB2w)</span><span>].</span>

<span></span>

<span></span>

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

<span class="c3"></span>

<span></span>

<span></span>

### <a name="h.29pvishefr4i"></a><span>Introduction</span>

<span></span>

<span>The WebSocket protocol [</span><span class="c14">[WSP](http://www.google.com/url?q=http%3A%2F%2Ftools.ietf.org%2Fhtml%2Fdraft-ietf-hybi-thewebsocketprotocol-17&sa=D&sntz=1&usg=AFQjCNGvg-JCaiL72vWAMvPSS4H7ppgPzg)</span><span>] defines notions of extension and sub-protocols.  </span> <span class="c14">[Section 1.9](http://www.google.com/url?q=http%3A%2F%2Ftools.ietf.org%2Fhtml%2Fdraft-ietf-hybi-thewebsocketprotocol-17%23section-1.9&sa=D&sntz=1&usg=AFQjCNGwwAj7RGN53AKsOKifWywsFp7t0A) </span><span> of [WSP]</span> <span>specifies that “WebSocket subprotocols” can be negotiated at time of initial handshake. This document describes a new Kaazing-specific sub-protocol who</span><span>s</span><span>e role is to define an additional handshake, allowing the client to negotiate additional capabilities, loosely called “extensions”, or “application-class extensions” to distinguish these from [WSP] extensions.</span>

<span></span>

<span>The intention of this document is to define a WebSocket sub-protocol, named “x-kaazing-handshake”  that allows for negotiation of available Kaazing-specific capabilities that will work over established native WebSocket connections.</span>

<span></span>

<span>The extended handshake defined in this document provides an opportunity for clients to negotiate application-class extensions after the WebSocket is established but before the application puts the WebSocket to use.</span>

<span></span>

<span></span>

### <a name="h.s1w3o69ee680"></a><span>Protocol Handshake Requirements</span>

<span>This section describes the handshake requirements necessary for establishing a WebSocket that speaks the KHP subprotocol.</span>

<span></span>

<span>There are two handshake request/response message pairs between a client and a server to establish a KHP WebSocket.  The first request/response message pair is called the “Opening” handshake and is a standard WebSocket opening handshake using the HTTP protocol.  This is to establish the raw WebSocket and to ensure that the KHP is the chosen protocol.  </span>

<span></span>

<span>The second message pair is called the</span> <span>“Extended” handshake</span><span>; messages are sent using WebSocket messages over the established raw WebSocket.  The WebSocket is not said to be _open_ until the second message pair has successfully  completed.</span>

<span></span>

<span>An “extended” handshake MUST be required when KHP is the chosen protocol during the “opening” handshake.</span>

<span></span>

#### <a name="h.5biyb39zunn0"></a><span>Handshake Requirements for the Opening WebSocket Handshake</span>

<span></span>

<span>To negotiate the use of KHP, the WebSocket client MUST include the following sub-protocol value within the raw WebSocket handshake:</span>

<span></span>

<span>        x-kaazing-handshake</span>

<span></span>

<span>For example, the following header would be sufficient</span><span>:</span>

<span></span>

<span class="c27">Sec-WebSocket-Protocol: x-kaazing-handshake, amqp, ...</span>

<span></span>

<span>The rules defined in [WSP] apply to the server receiving such a WebSocket opening handshake.</span>

<span></span>

<span>If the server does agree to use the x-kaazing-handshake</span><span> protocol,</span><sup>[[a]](#cmnt1)</sup><span> the server MUST send back a |Sec-WebSocket-Protocol| header field containing x-kaazing-handshake in its response.</span>

<span></span>

#### <a name="h.5biyb39zunn0"></a><span>Handshake Requirements for the Extended WebSocket Handshake</span>

<span></span>

<span>The extended handshake requires a WebSocket message from client to server (“extended handshake request”), followed by a server response to the client (“extended handshake response”).  The extended handshake MUST only commence when the client and server successfully negotiate the “x-kaazing-handshake” protocol in the opening handshake.  </span>

<span></span>

<span>No other messages should be sent by client or server until the _extended handshake is completed_</span><sup>[[b]](#cmnt2)</sup><sup>[[c]](#cmnt3)</sup><span>.</span>

<span></span>

<span>The extended handshake WebSocket payloads take the form of HTTP Requests (for extended handshake requests) and HTTP Responses (for extended handshake responses) following the HTTP 1.1 protocol  [RFC 2616].</span>

<span></span>

<span class="c27">+-----------------------+</span>

<span class="c27">| Web Socket Frame      |</span>

<span class="c27">+-----------------------+</span>

<span class="c27">| Http Request/Response |</span>

<span class="c27">+-----------------------+</span>

<span></span>

<span>If protocol negotiation was specified in the opening handshake, any protocols requested other than “x-kaazing-handshake” MUST be included in the extended handshake request as values in the header:</span>

<span></span>

<span>        Sec-WebSocket-Protocol:</span>

<span></span>

<span>If any WebSocket extensions were requested but not negotiated successfully in the opening handshake, then the client</span> <span>MUST</span><span> include any such extensions inside extended handshake request as values in the header:</span>

<span></span>

<span>        Sec-WebSocket-Extensions</span>

<span></span>

<span>The “Sec-WebSocket-Extensions” header MUST contain a non-empty list of extension names which the client desires, in an order of client preference, with the same semantics for the header as [WSP].</span>

<span></span>

<span>For example, inclusion of the following headers in the HTTP request payload within the first extended handshake request message indicates the client wishes to use a compression extension:</span>

<span></span>

<span>Sec-WebSocket-Extensions: x-kaazing-compression</span>

<span></span>

<span>In the case that they are required, a client MAY include other headers in the first WebSocket message along with those above.  This allows extension-specific information to be communicated between client and server without the need for additional handshakes.</span>

<span></span>

<span>A WebSocket server receiving the extended handshake message MUST respond with an extended handshake response message.  </span>

<span></span>

<span>In the case where the extended handshake contains an extension that was already negotiated in the opening handshake, the server MUST detect that condition and respond in error before _Closing the WebSocket Connection_.</span>

<span></span>

<span>The extended handshake response contains a HTTP status code which may or may not be “actionable”, as defined in a separate section below. When the extended handshake response has an HTTP status code that is not actionable, it is said that the _</span><span class="c26">extended handshake failed_</span><span>.</span>

<span></span>

<span>The actionable HTTP status codes are referenced in a separate section below.  For the sake of example, some interesting response codes and their interpretation when seen in an extended handshake response are detailed in the table below.</span>

<span></span>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c33" colspan="1" rowspan="1">

<span class="c22 c47">Extended Handshake Response Code</span>

</td>

<td class="c34" colspan="1" rowspan="1">

<span class="c30">Client Behavior</span>

</td>

</tr>

<tr class="c11">

<td class="c33" colspan="1" rowspan="1">

<span class="c3">101 Switching Protocols</span>

</td>

<td class="c34" colspan="1" rowspan="1">

<span class="c3">Clients receiving a 101 extended response SHOULD consider the _extended handshake completed_.</span>

</td>

</tr>

<tr class="c11">

<td class="c33" colspan="1" rowspan="1">

<span class="c3">302 Temporary Redirect</span>

</td>

<td class="c34" colspan="1" rowspan="1">

<span class="c3">Clients receiving a 302 or 307 extended response MUST _Close the WebSocket Connection_ following the definition in [WSP], and proceed with a fresh opening handshake to the URL specified in the Location: header of the extended handshake response.  The client should make no more than a fixed maximum number of reconnect attempts when faced with these redirect codes.  Additionally, client is responsible for detecting redirect loops to matching resources and avoiding reconnection attempts in such scenarios.</span>

</td>

</tr>

<tr class="c11">

<td class="c33" colspan="1" rowspan="1">

<span class="c3">401 Unauthorized</span>

</td>

<td class="c34" colspan="1" rowspan="1">

<span class="c3">Clients receiving a 401 extended response SHOULD continue the extended handshake, and reply with a replayed extended handshake request containing additional authorization information.  If the client fails to reply in a timely manner, it is the server’s responsibility to _Close the WebSocket Connection_.</span>

</td>

</tr>

</tbody>

</table>

<span></span>

<span></span>

<span></span>

<span>The extended handshake response MAY include these headers:</span>

<span></span>

<span>        Sec-WebSocket-Extensions</span>

<span></span>

<span>The “Sec-WebSocket-Extensions” header if present MUST contain a non-empty list of extensions, chosen from the requested extensions by the client and no others, which are to be used for further client-server communication.   If the “Sec-WebSocket-Extensions” header is not present, no extensions were negotiated and none apply.  Where there are no conflicts, the standard interpretation of this header as defined in [WSP] should also apply.</span>

<span></span>

<span>In the case that they are required, the server MAY include other headers in the WebSocket response message along with those above.  This allows information to be communicated from  server to client, in particular any reasons for failure.</span>

<span></span>

<span>The client then receives the extended handshake response message.  The client must _Close the WebSocket Connection_ following the definition in [WSP] if any of the following are true:</span>

*   <span>the raw WebSocket opened during the opening handshake is closed</span>
*   <span>the _</span><span class="c36">extended handshake failed</span><span>_</span>
*   <span>the Sec-WebSocket-Extensions selected by the server are not a subset of  those requested</span>

<span></span>

<span>At this point, if the WebSocket is not closed, it is said that the _</span><span class="c26">extended handshake is completed</span><span>_.</span>

### <a name="h.5402w5pyjkqp"></a><span class="c30">Diagrams</span><span> </span>

<span></span>

<span>Green means web socket framing around an HTTP payload content.</span>

<span></span>

<span style="overflow: hidden; display: inline-block; margin: 0.00px 0.00px; border: 0.00px solid #000000; transform: rotate(0.00rad) translateZ(0px); -webkit-transform: rotate(0.00rad) translateZ(0px); width: 685.00px; height: 517.30px;">![](images/image00.png)</span>

<span></span>

### <a name="h.npngrxv7fc79"></a><span>Kaazing Extended Handshake Response Codes</span>

<span>This section captures the expected interpretation of the extended response by the client for each of the following embedded extended HTTP response code.  The definitions for the HTTP response codes were taken from the</span><span>[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes&sa=D&sntz=1&usg=AFQjCNE_qEYuj_ua6IWHn3kJ7ZQ6W3HNiA)</span><span class="c14">[List of HTTP Status Codes](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes&sa=D&sntz=1&usg=AFQjCNE_qEYuj_ua6IWHn3kJ7ZQ6W3HNiA)</span><span> on Wikipedia 10/20/2011.</span>

#### <a name="h.eydn93gag3ju"></a><span>1xx Informational</span>

<span></span>

<span class="c4">Request received, continuing process.</span><span class="c4 c32">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

<span>This class of status code indicates a provisional response, consisting only of the Status-Line and optional headers, and is terminated by an empty line. Since HTTP/1.0 did not define any 1xx status codes, servers</span><span class="c36">must not</span><span> send a 1xx response to an HTTP/1.0 client except under experimental conditions.</span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">100 Continue</span>

<span class="c4">This means that the server has received the request headers, and that the client should proceed to send the request body (in the case of a request for which a body needs to be sent; for example, a</span><span class="c4">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FPOST_(HTTP)&sa=D&sntz=1&usg=AFQjCNEcuU29RduM14e9Mn7UQwSL1cRgsg)</span><span class="c4 c32">[POST](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FPOST_(HTTP)&sa=D&sntz=1&usg=AFQjCNEcuU29RduM14e9Mn7UQwSL1cRgsg)</span><span class="c4">request). If the request body is large, sending it to a server when a request has already been rejected based upon inappropriate headers is inefficient. To have a server check if the request could be accepted based on the request's headers alone, a client must send</span> <span class="c51">Expect: 100-continue</span><span class="c4"> as a header in its initial request</span><span class="c4 c32">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span><span class="c4"> and check if a</span> <span class="c51">100 Continue</span><span class="c4"> status code is received in response before continuing (or receive</span> <span class="c51">417 Expectation Failed</span><span class="c4"> and not continue).</span><span class="c4 c32">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">If the client requests this with an Expect: header, and the WebSocket server supports it, then this is a valid extended handshake response code, actionable on the client which MUST send the request body.</span>

</td>

</tr>

</tbody>

</table>

<span></span>

### <a name="h.btfdzheenh10"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">101 Switching Protocols</span>

<span class="c4">This means the requester has asked the server to switch protocols and the server is acknowledging that it will do so.</span><span class="c4 c32">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">Clients receiving a valid WebSocket handshake [WSP] 101 extended response that corresponds to their request MUST consider the _extended handshake completed_.</span>

</td>

</tr>

</tbody>

</table>

<span></span>

### <a name="h.67c9o69h2lpw"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c4 c30">102 Processing (</span><span class="c4 c30 c32">[WebDAV](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FWebDAV&sa=D&sntz=1&usg=AFQjCNENxrCfgFZ22OG2DxjxDOjQ-cKxCA)</span><span class="c22 c4">) (RFC 2518)</span>

<span class="c4">As a WebDAV request may contain many sub-requests involving file operations, it may take a long time to complete the request. This code indicates that the server has received and is processing the request, but no response is available yet.</span><span class="c4 c32">[[3]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2518-2&sa=D&sntz=1&usg=AFQjCNEshK0uRwiXBC25kKkqEPictRUjjA)</span><span class="c6 c4"> This prevents the client from timing out and assuming the request was lost.</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].</span>

</td>

</tr>

</tbody>

</table>

<span></span>

### <a name="h.pbccnt9zw1a5"></a>

### <a name="h.444zjjwrtbi3"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c3"></span>

<span class="c22 c4">103 Checkpoint</span>

<span class="c6 c4">   This code is used in the Resumable HTTP Requests Proposal to resume aborted  </span>

<span class="c4">   PUT or POST requests.</span><span class="c4 c32">[[4]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-ResumableHttpRequestsProposal-3&sa=D&sntz=1&usg=AFQjCNFjdJmYDkaI2ycjGFRq1zj4VQqrBw)</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].</span>

</td>

</tr>

</tbody>

</table>

<span></span>

### <a name="h.8o888clky5mk"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c22 c4">122 Request-URI too long</span>

<span class="c4">This is a non-standard IE7-only code which means the URI is longer than a maximum of 2083 characters.</span><span class="c4 c32">[[5]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-4&sa=D&sntz=1&usg=AFQjCNGFJItcBw8ENHgrv9_ef-NmZhG1wg)</span><span class="c4 c32">[[6]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-5&sa=D&sntz=1&usg=AFQjCNGW06El03uYNg-7TdsRMu4L9qCSjQ)</span><span class="c6 c4"> (See code 414.)</span>

</td>

</tr>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].</span>

</td>

</tr>

</tbody>

</table>

<span></span>

### <a name="h.wrhrqqujx9lu"></a>

### <a name="h.pzp6hn6b6bb7"></a>

### <a name="h.gf2sibyulyii"></a>

#### <a name="h.vn2pcos66bb7"></a><span>2xx Success</span>

<span></span>

<span class="c4">This class of status codes indicates the action requested by the client was received, understood, accepted and processed successfully.</span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c22 c4">200 OK</span>

<span class="c4">Standard response for successful HTTP requests. The actual response will depend on the request method used. In a GET request, the response will contain an entity corresponding to the requested resource. In a POST request the response will contain an entity describing or containing the result of the action.</span><span class="c4 c32">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

</td>

</tr>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].  The intention of the extended handshake is to receive a 101 upgrade rather than obtain a successful response.</span>

</td>

</tr>

</tbody>

</table>

<span></span>

### <a name="h.k0n7nnbr046a"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c22 c4">201 Created</span>

<span class="c4">The request has been fulfilled and resulted in a new resource being created.</span><span class="c1">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

</td>

</tr>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving 201 extended response that corresponds to their request MUST consider the _extended handshake completed_.  The intention of the server using this response code rather than a 101 response code indicates that perhaps an emulated or alternative WebSocket has been established.  The content of the response and response headers will determine exactly which.</span>

</td>

</tr>

</tbody>

</table>

<span></span>

### <a name="h.d3qffw35nglj"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c22 c4">202 Accepted</span>

<span class="c4">The request has been accepted for processing, but the processing has not been completed. The request might or might not eventually be acted upon, as it might be disallowed when processing actually takes place.</span><span class="c1">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

</td>

</tr>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c6 c4">A client receiving this response can choose to wait for up to a maximum amount of time for another response, before _Closing the WebSocket Connection_.</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c28" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c50" colspan="1" rowspan="1">

<span class="c22 c4">203 Non-Authoritative Information (since HTTP/1.1)</span>

<span class="c4">The server successfully processed the request, but is returning information that may be from another source.</span><span class="c1">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

</td>

</tr>

<tr class="c11">

<td class="c28" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c50" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].</span>

</td>

</tr>

</tbody>

</table>

<span></span>

### <a name="h.e9x0e9hmcju4"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c31" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c35" colspan="1" rowspan="1">

<span class="c22 c4">204 No Content</span>

<span class="c4">The server successfully processed the request, but is not returning any content.</span><span class="c1">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

</td>

</tr>

<tr class="c11">

<td class="c31" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c35" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving a 204 extended response that corresponds to their request MUST consider the _extended handshake completed_.</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c28" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c50" colspan="1" rowspan="1">

<span class="c22 c4">205 Reset Content</span>

<span class="c4">The server successfully processed the request, but is not returning any content. Unlike a 204 response, this response requires that the requester reset the document view.</span><span class="c1">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

</td>

</tr>

<tr class="c11">

<td class="c28" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c50" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving 205 extended response that corresponds to their request MUST consider the _extended handshake completed_.</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c31" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c35" colspan="1" rowspan="1">

<span class="c22 c4">206 Partial Content</span>

<span class="c4">The server is delivering only part of the resource due to a range header sent by the client. The range header is used by tools like</span><span class="c4">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FWget&sa=D&sntz=1&usg=AFQjCNGs79WeldzDQ7-Pt53W1lHyd9pI_g)</span><span class="c4 c32">[wget](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FWget&sa=D&sntz=1&usg=AFQjCNGs79WeldzDQ7-Pt53W1lHyd9pI_g)</span><span class="c4"> to enable resuming of interrupted downloads, or split a download into multiple simultaneous streams.</span><span class="c1">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

</td>

</tr>

<tr class="c11">

<td class="c31" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c35" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c31" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c35" colspan="1" rowspan="1">

<span class="c22 c4">207 Multi-Status (WebDAV) (RFC 4918)</span>

<span class="c4">The message body that follows is an</span><span class="c4">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FXML&sa=D&sntz=1&usg=AFQjCNG2TQNZWGJOnV0i6HtTvhqU8BXthw)</span><span class="c4 c32">[XML](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FXML&sa=D&sntz=1&usg=AFQjCNG2TQNZWGJOnV0i6HtTvhqU8BXthw)</span><span class="c4"> message and can contain a number of separate response codes, depending on how many sub-requests were made.</span><span class="c1">[[7]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_4918-6&sa=D&sntz=1&usg=AFQjCNGSj3eJsERLp_t124XkfIG_8dgktw)</span>

</td>

</tr>

<tr class="c11">

<td class="c31" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c35" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c44" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c53" colspan="1" rowspan="1">

<span class="c22 c4">226 IM Used (RFC 3229)</span>

<span class="c4">The server has fulfilled a GET request for the resource, and the response is a representation of the result of one or more instance-manipulations applied to the current instance.</span><span class="c4">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_3229-7&sa=D&sntz=1&usg=AFQjCNHdxRyCwfe_wif9TW5Z8_QohUdUKA)</span><span class="c1">[[8]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_3229-7&sa=D&sntz=1&usg=AFQjCNHdxRyCwfe_wif9TW5Z8_QohUdUKA)</span>

</td>

</tr>

<tr class="c11">

<td class="c44" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c53" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].</span>

</td>

</tr>

</tbody>

</table>

### <a name="h.g0v7g0uxkiga"></a>

#### <a name="h.kr7xfdvwwh8s"></a><span>3xx Redirection</span>

<span></span>

<span class="c4">The client must take additional action to complete the request.</span><span class="c4 c32">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

<span>This class of status code indicates that further action needs to be taken by the user agent in order to fulfil the request. The action required</span> <span class="c36">may</span><span> be carried out by the user agent without interaction with the user if and only if the method used in the second request is GET or HEAD. A user agent</span> <span class="c36">should not</span><span> automatically redirect a request more than five times, since such redirections usually indicate an</span><span>[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FInfinite_loop&sa=D&sntz=1&usg=AFQjCNHQUedYNUmCTa-QqNlyttsYXgBDUQ)</span><span class="c32">[infinite loop](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FInfinite_loop&sa=D&sntz=1&usg=AFQjCNHQUedYNUmCTa-QqNlyttsYXgBDUQ)</span><span>.</span>

### <a name="h.11bpob1mu9he"></a>

### <a name="h.t6s64kbhsiyz"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c22 c4">300 Multiple Choices</span>

<span class="c4">Indicates multiple options for the resource that the client may follow. It, for instance, could be used to present different format options for video, list files with different</span><span class="c4">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FFile_extensions&sa=D&sntz=1&usg=AFQjCNF26yZniYAQVR0-5IKOOiyTzruNxA)</span><span class="c4 c32">[extensions](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FFile_extensions&sa=D&sntz=1&usg=AFQjCNF26yZniYAQVR0-5IKOOiyTzruNxA)</span><span class="c4">, or</span><span class="c4">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FWord_sense_disambiguation&sa=D&sntz=1&usg=AFQjCNGasL-CVYMqFT-tuU83lMdLCsuTiA)</span><span class="c4 c32">[word sense disambiguation](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FWord_sense_disambiguation&sa=D&sntz=1&usg=AFQjCNGasL-CVYMqFT-tuU83lMdLCsuTiA)</span><span class="c4">.</span><span class="c1">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

</td>

</tr>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP], and SHOULD attempt an opening handshake on one of the given URIs in the choices presented.</span>

</td>

</tr>

</tbody>

</table>

<span></span>

### <a name="h.ilqe8qw1j0ki"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c39 c4 c30 c32">[301 Moved Permanently](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FHTTP_301&sa=D&sntz=1&usg=AFQjCNH5A2MGNMGQpfru64Jla2Qy01LiIw)</span>

<span class="c4">This and all future requests should be directed to the given</span><span class="c4">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FURI&sa=D&sntz=1&usg=AFQjCNErhKd8wEoT3DSKrXV4rQuOYRkM3w)</span><span class="c4 c32">[URI](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FURI&sa=D&sntz=1&usg=AFQjCNErhKd8wEoT3DSKrXV4rQuOYRkM3w)</span><span class="c4">.</span><span class="c1">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

</td>

</tr>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c3"></span>

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP], and SHOULDattempt an opening handshake on the given URI.</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c39 c4 c30 c32">[302 Found](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FHTTP_302&sa=D&sntz=1&usg=AFQjCNEESSumqIc5NXMmkaE-z4O4kLC9Sw)</span>

<span class="c4">This is an example of industrial practice contradicting the standard.</span><span class="c4 c32">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span><span class="c4"> HTTP/1.0 specification (RFC 1945) required the client to perform a temporary redirect (the original describing phrase was "Moved Temporarily"),</span><span class="c4 c32">[[9]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_1945-8&sa=D&sntz=1&usg=AFQjCNHJbnMRyNRhFOHoJuZ6_cgx9S1ceg)</span><span class="c4"> but popular browsers implemented 302 with the functionality of a 303 See Other. Therefore, HTTP/1.1 added status codes 303 and 307 to distinguish between the two behaviours.</span><span class="c4 c32">[[10]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC2616-10-9&sa=D&sntz=1&usg=AFQjCNGt3gJkyxnF_lb24qy6XR2_yvUk6g)</span><span class="c6 c4"> However, some Web applications and frameworks use the 302 status code as if it were the 303.</span>

</td>

</tr>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP], and SHOULD attempt an opening handshake on the given URI.</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c4 c30 c32">[303 See Other](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FHTTP_303&sa=D&sntz=1&usg=AFQjCNENXl6zjeFDuTnDDsNBTELbC_s_QA)</span><span class="c22 c4"> (since HTTP/1.1)</span>

<span class="c4">The response to the request can be found under another</span><span class="c4">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FUniform_Resource_Identifier&sa=D&sntz=1&usg=AFQjCNH_8VnSMUeKm1fbfSIMvERFG8MxmA)</span><span class="c4 c32">[URI](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FUniform_Resource_Identifier&sa=D&sntz=1&usg=AFQjCNH_8VnSMUeKm1fbfSIMvERFG8MxmA)</span><span class="c4"> using a GET method. When received in response to a POST (or PUT/DELETE), it should be assumed that the server has received the data and the redirect should be issued with a separate GET message.</span><span class="c1">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

</td>

</tr>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP], and attempt an opening handshake on the given URI.</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c22 c4">304 Not Modified</span>

<span class="c4">Indicates the resource has not been modified since last requested.</span><span class="c4 c32">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span><span class="c6 c4"> Typically, the HTTP client provides a header like the If-Modified-Since header to provide a time against which to compare. Using this saves bandwidth and reprocessing on both the server and client, as only the header data must be sent and received in comparison to the entirety of the page being re-processed by the server, then sent again using more bandwidth of the server and client.</span>

</td>

</tr>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c22 c4">305 Use Proxy (since HTTP/1.1)</span>

<span class="c4">Many HTTP clients (such as</span><span class="c4">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FMozilla&sa=D&sntz=1&usg=AFQjCNH_jkQT324o9_2X2lvnnIhGAP5Fdg)</span><span class="c4 c32">[Mozilla](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FMozilla&sa=D&sntz=1&usg=AFQjCNH_jkQT324o9_2X2lvnnIhGAP5Fdg)</span><span class="c4 c32">[[11]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-mozilla_bugzilla_bug_187996-10&sa=D&sntz=1&usg=AFQjCNGyk-mHlKjMMR627kU3nMqwYD_0ow)</span><span class="c4"> and</span><span class="c4">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FInternet_Explorer&sa=D&sntz=1&usg=AFQjCNEtsoc7vB7oJJ460OU5j2xAClqHOA)</span><span class="c4 c32">[Internet Explorer](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FInternet_Explorer&sa=D&sntz=1&usg=AFQjCNEtsoc7vB7oJJ460OU5j2xAClqHOA)</span><span class="c4">) do not correctly handle responses with this status code, primarily for security reasons.</span><span class="c1">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

</td>

</tr>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c22 c4">306 Switch Proxy</span>

<span class="c4">No longer used.</span><span class="c4 c32">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span><span class="c4"> Originally meant "Subsequent requests should use the specified proxy."</span><span class="c1">[[12]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-11&sa=D&sntz=1&usg=AFQjCNFWxAa4IyObpKCBcq1P9vxDv05mAA)</span>

</td>

</tr>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c22 c4">307 Temporary Redirect (since HTTP/1.1)</span>

<span class="c4">In this occasion, the request should be repeated with another URI, but future requests can still use the original URI.</span><span class="c4 c32">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span><span class="c6 c4"> In contrast to 303, the request method should not be changed when reissuing the original request. For instance, a POST request must be repeated using another POST request.</span>

</td>

</tr>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].  If and only if the underlying HTTP method was a GET request, the client MUST attempt an opening handshake on the given URI.</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c22 c4">308 Resume Incomplete</span>

<span class="c4">This code is used in the Resumable HTTP Requests Proposal to resume aborted PUT or POST requests.</span><span class="c1">[[4]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-ResumableHttpRequestsProposal-3&sa=D&sntz=1&usg=AFQjCNFjdJmYDkaI2ycjGFRq1zj4VQqrBw)</span>

</td>

</tr>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

### <a name="h.41r71uvw62b8"></a>

### <a name="h.l7dm0ftgeuy8"></a>

#### <a name="h.6coxonb7itkx"></a><span>4xx Client Error</span>

<span class="c4">The 4xx class of status code is intended for cases in which the client seems to have erred. Except when responding to a HEAD request, the server</span> <span class="c4 c36">should</span><span class="c4"> include an entity containing an explanation of the error situation, and whether it is a temporary or permanent condition. These status codes are applicable to any request method. User agents</span> <span class="c4 c36">should</span><span class="c4"> display any included entity to the user. These are typically the most common error codes encountered while online.</span>

### <a name="h.mmwworce48gf"></a>

### <a name="h.swqumgs3rcj4"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c22 c4">400 Bad Request</span>

<span class="c4">The request cannot be fulfilled due to bad syntax.</span><span class="c1">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

</td>

</tr>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c22 c4">401 Unauthorized</span>

<span class="c4">Similar to</span> <span class="c4 c36">403 Forbidden</span><span class="c4">, but specifically for use when authentication is possible but has failed or not yet been provided.</span><span class="c4 c32">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span><span class="c4"> The response must include a WWW-Authenticate header field containing a challenge applicable to the requested resource. See</span><span class="c4">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FBasic_access_authentication&sa=D&sntz=1&usg=AFQjCNE1YdRxSeAnkCI6OWfgrKUjQ2HtGQ)</span><span class="c4 c32">[Basic access authentication](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FBasic_access_authentication&sa=D&sntz=1&usg=AFQjCNE1YdRxSeAnkCI6OWfgrKUjQ2HtGQ)</span><span class="c4"> and</span><span class="c4">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FDigest_access_authentication&sa=D&sntz=1&usg=AFQjCNEv6l98PtYfb__zb1UJWq92d0by4A)</span><span class="c4 c32">[Digest access authentication](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FDigest_access_authentication&sa=D&sntz=1&usg=AFQjCNEv6l98PtYfb__zb1UJWq92d0by4A)</span><span class="c6 c4">.</span>

</td>

</tr>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c6 c4">Clients receiving a 401 extended response SHOULD continue the extended handshake, and reply with a replayed extended handshake request containing additional authorization information.  If the client fails to reply in a timely manner, it is the server’s responsibility to _Close the WebSocket Connection_.</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c4 c22">402 Payment Required</span>

<span class="c4">Reserved for future use.</span><span class="c4 c32">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span><span class="c4"> The original intention was that this code might be used as part of some form of</span><span class="c4">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FDigital_cash&sa=D&sntz=1&usg=AFQjCNGNK6GZEP8SjSgbO5XJiPjG5g_rZA)</span><span class="c4 c32">[digital cash](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FDigital_cash&sa=D&sntz=1&usg=AFQjCNGNK6GZEP8SjSgbO5XJiPjG5g_rZA)</span><span class="c4"> or</span><span class="c4">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FMicropayment&sa=D&sntz=1&usg=AFQjCNG1D4DzXZtBn_O6rBhBlWeRnqvAKg)</span><span class="c4 c32">[micropayment](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FMicropayment&sa=D&sntz=1&usg=AFQjCNG1D4DzXZtBn_O6rBhBlWeRnqvAKg)</span><span class="c4"> scheme, but that has not happened, and this code is not usually used. As an example of its use, however, Apple's</span><span class="c4">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FMobileMe&sa=D&sntz=1&usg=AFQjCNHyxzmpXfegMCn8eUReNIsRSD3D1Q)</span><span class="c4 c32">[MobileMe](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FMobileMe&sa=D&sntz=1&usg=AFQjCNHyxzmpXfegMCn8eUReNIsRSD3D1Q)</span><span class="c6 c4"> service generates a 402 error ("httpStatusCode:402" in the Mac OS X Console log) if the MobileMe account is delinquent.</span>

</td>

</tr>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c39 c4 c30 c32">[403 Forbidden](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FHTTP_403&sa=D&sntz=1&usg=AFQjCNGKSFpF7sAEG02aXjaYRPvll5nqoQ)</span>

<span class="c4">The request was a legal request, but the server is refusing to respond to it.</span><span class="c4 c32">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span><span class="c4"> Unlike a</span> <span class="c4 c36">401 Unauthorized</span><span class="c4"> response, authenticating will make no difference.</span><span class="c1">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

</td>

</tr>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c4 c30 c32 c39">[404 Not Found](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FHTTP_404&sa=D&sntz=1&usg=AFQjCNGwYquPWzNmoARkK4hQBiNstf_g3A)</span>

<span class="c4">The requested resource could not be found but may be available again in the future.</span><span class="c4 c32">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span><span class="c6 c4"> Subsequent requests by the client are permissible.</span>

</td>

</tr>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c22 c4">405 Method Not Allowed</span>

<span class="c4">A request was made of a resource using a request method not supported by that resource;</span><span class="c4 c32">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span><span class="c6 c4"> for example, using GET on a form which requires data to be presented via POST, or using PUT on a read-only resource.</span>

</td>

</tr>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c22 c4">406 Not Acceptable</span>

<span class="c4">The requested resource is only capable of generating content not acceptable according to the Accept headers sent in the request.</span><span class="c1">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

</td>

</tr>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c22 c4">407 Proxy Authentication Required</span>

<span class="c4">The client must first authenticate itself with the proxy.</span><span class="c1">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

</td>

</tr>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c3"></span>

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP], and MAY choose to attempt to authenticate with a proxy.</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c22 c4">408 Request Timeout</span>

<span class="c4">The server timed out waiting for the request.</span><span class="c4 c32">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span><span class="c6 c4"> According to W3 HTTP specifications: "The client did not produce a request within the time that the server was prepared to wait. The client MAY repeat the request without modifications at any later time."</span>

</td>

</tr>

<tr class="c11">

<td class="c5" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c17" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].</span>

</td>

</tr>

</tbody>

</table>

<span></span>

### <a name="h.8nw30ehn9nmu"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">409 Conflict</span>

<span class="c4">Indicates that the request could not be processed because of conflict in the request, such as an</span><span class="c4">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FEdit_conflict&sa=D&sntz=1&usg=AFQjCNGcFjQsKQmfyWGOOcEcvSBkkaSTwg)</span><span class="c4 c32">[edit conflict](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FEdit_conflict&sa=D&sntz=1&usg=AFQjCNGcFjQsKQmfyWGOOcEcvSBkkaSTwg)</span><span class="c4">.</span><span class="c1">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.xqu1y918jpv4"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">410 Gone</span>

<span class="c4">Indicates that the resource requested is no longer available and will not be available again.</span><span class="c4 c32">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span><span class="c6 c4"> This should be used when a resource has been intentionally removed and the resource should be purged. Upon receiving a 410 status code, the client should not request the resource again in the future. Clients such as search engines should remove the resource from their indices. Most use cases do not require clients and search engines to purge the resource, and a "404 Not Found" may be used instead.</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.kp7u1jkuyjkr"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">411 Length Required</span>

<span class="c4">The request did not specify the length of its content, which is required by the requested resource.</span><span class="c1">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

<span class="c1">[](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.6nwwvyvsezgl"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">412 Precondition Failed</span>

<span class="c4">The server does not meet one of the preconditions that the requester put on the request.</span><span class="c1">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.efbd06np958l"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">413 Request Entity Too Large</span>

<span class="c4">The request is larger than the server is willing or able to process.</span><span class="c1">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.24bea4xrtfkx"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">414 Request-URI Too Long</span>

<span class="c4">The</span><span class="c4">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FURI&sa=D&sntz=1&usg=AFQjCNErhKd8wEoT3DSKrXV4rQuOYRkM3w)</span><span class="c4 c32">[URI](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FURI&sa=D&sntz=1&usg=AFQjCNErhKd8wEoT3DSKrXV4rQuOYRkM3w)</span><span class="c4"> provided was too long for the server to process.</span><span class="c1">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.40z9w7fl4jsp"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">415 Unsupported Media Type</span>

<span class="c4">The request entity has a</span><span class="c4">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FInternet_media_type&sa=D&sntz=1&usg=AFQjCNHEjwHgr8xgjnbV5Bbq_rJq3rJ8cg)</span><span class="c4 c32">[media type](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FInternet_media_type&sa=D&sntz=1&usg=AFQjCNHEjwHgr8xgjnbV5Bbq_rJq3rJ8cg)</span><span class="c4"> which the server or resource does not support.</span><span class="c4 c32">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span><span class="c4"> For example, the client uploads an image as</span><span class="c4">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FScalable_Vector_Graphics&sa=D&sntz=1&usg=AFQjCNFBMQuGnTCniNHVcW_bXN9amFYYew)</span><span class="c4 c32">[image/svg+xml](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FScalable_Vector_Graphics&sa=D&sntz=1&usg=AFQjCNFBMQuGnTCniNHVcW_bXN9amFYYew)</span><span class="c6 c4">, but the server requires that images use a different format.</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.36xnfd14otkd"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">416 Requested Range Not Satisfiable</span>

<span class="c4">The client has asked for a portion of the file, but the server cannot supply that portion.</span><span class="c4 c32">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span><span class="c6 c4"> For example, if the client asked for a part of the file that lies beyond the end of the file.</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.t13ec9a2mqrz"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">417 Expectation Failed</span>

<span class="c4">The server cannot meet the requirements of the Expect request-header field.</span><span class="c1">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.oop0zauce2fg"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">418 I'm a teapot (RFC 2324)</span>

<span class="c4">This code was defined in 1998 as one of the traditional</span><span class="c4">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FIETF&sa=D&sntz=1&usg=AFQjCNFSr-A0cMB4WOam6tdvNg1UDtm9dw)</span><span class="c4 c32">[IETF](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FIETF&sa=D&sntz=1&usg=AFQjCNFSr-A0cMB4WOam6tdvNg1UDtm9dw)</span><span class="c4">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FApril_Fools%2527_Day_RFC&sa=D&sntz=1&usg=AFQjCNElt155ZriizTwreJrJQq7bTob2-g)</span><span class="c4 c32">[April Fools' jokes](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FApril_Fools%2527_Day_RFC&sa=D&sntz=1&usg=AFQjCNElt155ZriizTwreJrJQq7bTob2-g)</span><span class="c4">, in</span><span class="c4">[ ](http://www.google.com/url?q=http%3A%2F%2Ftools.ietf.org%2Fhtml%2Frfc2324&sa=D&sntz=1&usg=AFQjCNFaA0QJ1OqMKl4m41nNjD1zkuGHlA)</span><span class="c4 c41">[RFC 2324](http://www.google.com/url?q=http%3A%2F%2Ftools.ietf.org%2Fhtml%2Frfc2324&sa=D&sntz=1&usg=AFQjCNFaA0QJ1OqMKl4m41nNjD1zkuGHlA)</span><span class="c4">,</span><span class="c4">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FHyper_Text_Coffee_Pot_Control_Protocol&sa=D&sntz=1&usg=AFQjCNGoqW6CleFzQ-nhghJeWhKqLaIJag)</span><span class="c4 c36 c32">[Hyper Text Coffee Pot Control Protocol](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FHyper_Text_Coffee_Pot_Control_Protocol&sa=D&sntz=1&usg=AFQjCNGoqW6CleFzQ-nhghJeWhKqLaIJag)</span><span class="c6 c4">, and is not expected to be implemented by actual HTTP servers.</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c4 c6">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.ronnvkvkwylr"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">422 Unprocessable Entity (WebDAV) (RFC 4918)</span>

<span class="c4">The request was well-formed but was unable to be followed due to semantic errors.</span><span class="c1">[[7]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_4918-6&sa=D&sntz=1&usg=AFQjCNGSj3eJsERLp_t124XkfIG_8dgktw)</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.ozi3zo50jiq6"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">423 Locked (WebDAV) (RFC 4918)</span>

<span class="c4">The resource that is being accessed is locked.</span><span class="c1">[[7]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_4918-6&sa=D&sntz=1&usg=AFQjCNGSj3eJsERLp_t124XkfIG_8dgktw)</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.tvbh2xy7r93r"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">424 Failed Dependency (WebDAV) (RFC 4918)</span>

<span class="c4">The request failed due to failure of a previous request (e.g. a PROPPATCH).</span><span class="c1">[[7]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_4918-6&sa=D&sntz=1&usg=AFQjCNGSj3eJsERLp_t124XkfIG_8dgktw)</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.5nwxywl75qhm"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">425 Unordered Collection (RFC 3648)</span>

<span class="c4">Defined in drafts of "WebDAV Advanced Collections Protocol",</span><span class="c4 c32">[[13]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-WEBDAV_CP_04-12&sa=D&sntz=1&usg=AFQjCNFoGMZtiiKtM96HlZKBNI251HXc9A)</span><span class="c4"> but not present in "Web Distributed Authoring and Versioning (WebDAV) Ordered Collections Protocol".</span><span class="c1">[[14]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_3648-13&sa=D&sntz=1&usg=AFQjCNFG9AreQqgmuoAH5K0nSwC52iydYA)</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.cybljqvalca8"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">426 Upgrade Required (RFC 2817)</span>

<span class="c4">The client should switch to a different protocol such as</span><span class="c4">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FTransport_Layer_Security&sa=D&sntz=1&usg=AFQjCNF3e7161i72E9L5Avi0VAYTlFcWgg)</span><span class="c4 c32">[TLS/1.0](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FTransport_Layer_Security&sa=D&sntz=1&usg=AFQjCNF3e7161i72E9L5Avi0VAYTlFcWgg)</span><span class="c4">.</span><span class="c1">[[15]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2817-14&sa=D&sntz=1&usg=AFQjCNH45VkQZeC4TyNmooYXw3BPP0XZaw)</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP], and MAY attempt to establish a new opening handshake using an upgraded protocol.</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.qkf8pot6dxao"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">428 Precondition Required</span>

<span class="c4">The origin server requires the request to be conditional. Intended to prevent "the 'lost update' problem, where a client GETs a resource's state, modifies it, and PUTs it back to the server, when meanwhile a third party has modified the state on the server, leading to a conflict."</span><span class="c4 c32">[[16]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-http-new-status-02-15&sa=D&sntz=1&usg=AFQjCNGzXRtwCnWc5GEe-lQdmQo7OsLY1w)</span><span class="c6 c4"> Proposed in an Internet-Draft.</span>

<span class="c6 c4"></span>

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.f9lm2t5ehaaj"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">429 Too Many Requests</span>

<span class="c4">The user has sent too many requests in a given amount of time. Intended for use with</span><span class="c4">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FRate_limiting&sa=D&sntz=1&usg=AFQjCNHwpNmlha_0T_gVSKmbJxw81cE0bg)</span><span class="c4 c32">[rate limiting](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FRate_limiting&sa=D&sntz=1&usg=AFQjCNHwpNmlha_0T_gVSKmbJxw81cE0bg)</span><span class="c4"> schemes. Proposed in an Internet-Draft.</span><span class="c1">[[16]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-http-new-status-02-15&sa=D&sntz=1&usg=AFQjCNGzXRtwCnWc5GEe-lQdmQo7OsLY1w)</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.tyz6cgljfckp"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">431 Request Header Fields Too Large</span>

<span class="c4">The server is unwilling to process the request because either an individual header field, or all the header fields collectively, are too large. Proposed in an Internet-Draft.</span><span class="c1">[[16]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-http-new-status-02-15&sa=D&sntz=1&usg=AFQjCNGzXRtwCnWc5GEe-lQdmQo7OsLY1w)</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.6la6cn20uoww"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">444 No Response</span>

<span class="c4">A</span><span class="c4">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FNginx&sa=D&sntz=1&usg=AFQjCNFGJ8P9fCqyVxUjC-ckJo47OL3NZw)</span><span class="c4 c32">[Nginx](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FNginx&sa=D&sntz=1&usg=AFQjCNFGJ8P9fCqyVxUjC-ckJo47OL3NZw)</span><span class="c6 c4"> HTTP server extension. The server returns no information to the client and closes the connection (useful as a deterrent for malware).</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.prs9f6xs99as"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">449 Retry With</span>

<span class="c4">A Microsoft extension. The request should be retried after performing the appropriate action.</span><span class="c1">[[17]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-MS_DD891478-16&sa=D&sntz=1&usg=AFQjCNE9EYTRVsROwNfs39zp8tP6ikt1Fw)</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.88erou69f8fu"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">450 Blocked by Windows Parental Controls</span>

<span class="c4">A Microsoft extension. This error is given when Windows Parental Controls are turned on and are blocking access to the given webpage.</span><span class="c1">[[18]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-17&sa=D&sntz=1&usg=AFQjCNFIZ-3o1iDM_DZu28w6ddPYW7bJXA)</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.yv2ifbymjdcc"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">499 Client Closed Request</span>

<span class="c4">An Nginx HTTP server extension. This code is introduced to log the case when the connection is closed by client while HTTP server is processing its request, making server unable to send the HTTP header back.</span><span class="c1">[[19]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-Nginx_mailing_list.2C_2007-08-27-18&sa=D&sntz=1&usg=AFQjCNE9vbSEtInWOLISVvOPpm1i5At4Fw)</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP]</span>

</td>

</tr>

</tbody>

</table>

<span></span>

### <a name="h.k22496ojptdp"></a>

#### <a name="h.3hrxijjawtc9"></a><span>5xx Server Error</span>

<span class="c4">The server failed to fulfill an apparently valid request.</span><span class="c4 c32">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

<span></span>

<span>Response status codes beginning with the digit "5" indicate cases in which the server is aware that it has encountered an error or is otherwise incapable of performing the request. Except when responding to a HEAD request, the server</span> <span class="c36">should</span><span> include an entity containing an explanation of the error situation, and indicate whether it is a temporary or permanent condition. Likewise, user agents</span> <span class="c36">should</span><span> display any included entity to the user. These response codes are applicable to any request method.</span>

### <a name="h.cmxcbzgyqhd8"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">500 Internal Server Error</span>

<span class="c4">A generic error message, given when no more specific message is suitable.</span><span class="c1">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">501 Not Implemented</span>

<span class="c4">The server either does not recognise the request method, or it lacks the ability to fulfill the request.</span><span class="c1">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.cx2p3q1eneax"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">502 Bad Gateway</span>

<span class="c4">The server was acting as a gateway or proxy and received an invalid response from the upstream server.</span><span class="c1">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.qcxpuz9gt05c"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">503 Service Unavailable</span>

<span class="c4">The server is currently unavailable (because it is overloaded or down for maintenance).</span><span class="c4 c32">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span><span class="c6 c4"> Generally, this is a temporary state.</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.l86f8fw1ns7h"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">504 Gateway Timeout</span>

<span class="c4">The server was acting as a gateway or proxy and did not receive a timely response from the upstream server.</span><span class="c1">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.1eb5slddjz1o"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">505 HTTP Version Not Supported</span>

<span class="c4">The server does not support the HTTP protocol version used in the request.</span><span class="c1">[[2]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2616-1&sa=D&sntz=1&usg=AFQjCNFYPyEDsA8ui2l5bkeuleCo_ZOj3g)</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.rxw9p53z7skn"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">506 Variant Also Negotiates (RFC 2295)</span>

<span class="c4">Transparent</span><span class="c4">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FContent_negotiation&sa=D&sntz=1&usg=AFQjCNHcBrGVQONVoIMLN5YSMYElSdT8BA)</span><span class="c4 c32">[content negotiation](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FContent_negotiation&sa=D&sntz=1&usg=AFQjCNHcBrGVQONVoIMLN5YSMYElSdT8BA)</span><span class="c4"> for the request results in a</span><span class="c4">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FCircular_reference&sa=D&sntz=1&usg=AFQjCNHkL3lnlWOaN2qTst7ED9U3XvQaFw)</span><span class="c4 c32">[circular reference](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FCircular_reference&sa=D&sntz=1&usg=AFQjCNHkL3lnlWOaN2qTst7ED9U3XvQaFw)</span><span class="c4">.</span><span class="c1">[[20]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2295-19&sa=D&sntz=1&usg=AFQjCNGaYqAse03EywAq7XyzBK66ABvG8Q)</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.o5o2bbonkq55"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">507 Insufficient Storage (WebDAV) (RFC 4918)</span>

<span class="c4">The server is unable to store the representation needed to complete the request.</span><span class="c1">[[7]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_4918-6&sa=D&sntz=1&usg=AFQjCNGSj3eJsERLp_t124XkfIG_8dgktw)</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.bixsz97frx7y"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">509 Bandwidth Limit Exceeded (Apache bw/limited extension)</span>

<span class="c6 c4">This status code, while used by many servers, is not specified in any RFCs.</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.vgvn8w92hley"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">510 Not Extended (RFC 2774)</span>

<span class="c4">Further extensions to the request are required for the server to fulfill it.</span><span class="c1">[[21]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-RFC_2774-20&sa=D&sntz=1&usg=AFQjCNHSU9ZLle-o_sKEsxEXJxeehevVJA)</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.v0n8tml4289f"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">511 Network Authentication Required</span>

<span class="c4">The client needs to authenticate to gain network access. Intended for use by intercepting proxies used to control access to the network (e.g. "captive portals" used to require agreement to Terms of Service before granting full Internet access via a Wi-Fi hotspot). Proposed in an Internet-Draft.</span><span class="c1">[[16]](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_note-http-new-status-02-15&sa=D&sntz=1&usg=AFQjCNGzXRtwCnWc5GEe-lQdmQo7OsLY1w)</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.beeyvomkyuxq"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">598 (Informal convention) network read timeout error</span>

<span class="c6 c4">This status code is not specified in any RFCs, but is used by some HTTP proxies to signal a network read timeout behind the proxy to a client in front of the proxy.</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].</span>

</td>

</tr>

</tbody>

</table>

<span></span>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20"></table>

### <a name="h.pot9osnbvegq"></a>

[](#)[](#)

<table cellpadding="0" cellspacing="0" class="c20">

<tbody>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">HTTP Response Code</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c22 c4">599 (Informal convention) network connect timeout error</span>

<span class="c6 c4">This status code is not specified in any RFCs, but is used by some HTTP proxies to signal a network connect timeout behind the proxy to a client in front of the proxy.</span>

</td>

</tr>

<tr class="c11">

<td class="c19" colspan="1" rowspan="1">

<span class="c4">Extended Handshake Action</span>

</td>

<td class="c12" colspan="1" rowspan="1">

<span class="c6 c4">A Client receiving this response MUST _Close the WebSocket Connection_ following the definition in [WSP].</span>

</td>

</tr>

</tbody>

</table>

<span></span>

### <a name="h.5f8mlf7pr5xj"></a>

### <a name="h.qleph48yczj8"></a>

## <a name="h.lemg6nsskp1p"></a><span>References</span>

<span></span>

<span>[RFC2119]  Bradner, S., "Key words for use in RFCs to Indicate Requirement Levels",</span><span>[ ](https://www.google.com/url?q=https%3A%2F%2Ftools.ietf.org%2Fhtml%2Fbcp14&sa=D&sntz=1&usg=AFQjCNEGxXJa-sJpd4nMGGIndeToR0zUuw)</span><span class="c14">[BCP 14](https://www.google.com/url?q=https%3A%2F%2Ftools.ietf.org%2Fhtml%2Fbcp14&sa=D&sntz=1&usg=AFQjCNEGxXJa-sJpd4nMGGIndeToR0zUuw)</span><span>,</span><span>[ ](https://www.google.com/url?q=https%3A%2F%2Ftools.ietf.org%2Fhtml%2Frfc2119&sa=D&sntz=1&usg=AFQjCNENh9xddb5YwJQTsFNAfEXW1zXB2w)</span><span class="c14">[RFC 2119](https://www.google.com/url?q=https%3A%2F%2Ftools.ietf.org%2Fhtml%2Frfc2119&sa=D&sntz=1&usg=AFQjCNENh9xddb5YwJQTsFNAfEXW1zXB2w)</span><span>, March 1997.</span>

<span></span>

<span>[WSP] The WebSocket Protocol, I.Fette, IETF,</span> <span class="c14">[WSP](http://www.google.com/url?q=http%3A%2F%2Ftools.ietf.org%2Fhtml%2Frfc6455&sa=D&sntz=1&usg=AFQjCNFgxuIFPjPFv7bXP495c5XruOhl_A)</span>

<span></span>

<span>[RFC822 Section 3.1]</span> <span>David H. Crocker, University of Delaware,</span><span class="c54"> </span> <span class="c14">[RFC822 Section 3.1](http://www.google.com/url?q=http%3A%2F%2Ftools.ietf.org%2Fhtml%2Frfc822%23section-3.1&sa=D&sntz=1&usg=AFQjCNHPHFM0kzKGPxWL_VcHviv9WSG26A)</span>

<span>[RFC 2616]</span> <span class="c14">[HTTP 1.1 Protocol Specification](https://www.google.com/url?q=https%3A%2F%2Ftools.ietf.org%2Fhtml%2Frfc2616&sa=D&sntz=1&usg=AFQjCNEp_ehUtmPiFAR8qgvSl3oKEW71rw)</span>

<span></span>

1.  <span class="c7 c30">[^](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-MS_KB943891_0-0&sa=D&sntz=1&usg=AFQjCNGGYnUsVIpsm94AcjhDdXrRAEwYyw)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fsupport.microsoft.com%2Fkb%2F943891%2F&sa=D&sntz=1&usg=AFQjCNFx4XpmHaf-6NeVR72RR7LeHbcaoQ)</span><span class="c15 c41">["The HTTP status codes in IIS 7.0"](http://www.google.com/url?q=http%3A%2F%2Fsupport.microsoft.com%2Fkb%2F943891%2F&sa=D&sntz=1&usg=AFQjCNFx4XpmHaf-6NeVR72RR7LeHbcaoQ)</span><span class="c15">.</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FMicrosoft&sa=D&sntz=1&usg=AFQjCNEGsQS8JYL3l3M-OG4aVVkw6VNlAQ)</span><span class="c7">[Microsoft](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FMicrosoft&sa=D&sntz=1&usg=AFQjCNEGsQS8JYL3l3M-OG4aVVkw6VNlAQ)</span><span class="c15">. July 14, 2009\. Retrieved April 1, 2009.</span>
2.  <span class="c15">^</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-0&sa=D&sntz=1&usg=AFQjCNEj_TgXPh9oXHak7QpjXq9OivxJJw)</span><span class="c7 c26">[a](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-0&sa=D&sntz=1&usg=AFQjCNEj_TgXPh9oXHak7QpjXq9OivxJJw)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-1&sa=D&sntz=1&usg=AFQjCNE0UiZ5cp2vG2aQrqKSMvAEN9wwIw)</span><span class="c7 c26">[b](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-1&sa=D&sntz=1&usg=AFQjCNE0UiZ5cp2vG2aQrqKSMvAEN9wwIw)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-2&sa=D&sntz=1&usg=AFQjCNHFxvQpeG7I4vF0t5Pqm8GFN0uirw)</span><span class="c7 c26">[c](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-2&sa=D&sntz=1&usg=AFQjCNHFxvQpeG7I4vF0t5Pqm8GFN0uirw)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-3&sa=D&sntz=1&usg=AFQjCNHCC30EKgcAoX4yTYCZ7BNR8YJQdA)</span><span class="c7 c26">[d](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-3&sa=D&sntz=1&usg=AFQjCNHCC30EKgcAoX4yTYCZ7BNR8YJQdA)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-4&sa=D&sntz=1&usg=AFQjCNFy0Gao9RAkwfHXm1QzBb1clVQmKA)</span><span class="c7 c26">[e](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-4&sa=D&sntz=1&usg=AFQjCNFy0Gao9RAkwfHXm1QzBb1clVQmKA)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-5&sa=D&sntz=1&usg=AFQjCNFvLwr_g4zzYKdVWfeNHg0lSF58lw)</span><span class="c7 c26">[f](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-5&sa=D&sntz=1&usg=AFQjCNFvLwr_g4zzYKdVWfeNHg0lSF58lw)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-6&sa=D&sntz=1&usg=AFQjCNFv8nMWCmldOAqrLiXbl07F_A8L0Q)</span><span class="c7 c26">[g](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-6&sa=D&sntz=1&usg=AFQjCNFv8nMWCmldOAqrLiXbl07F_A8L0Q)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-7&sa=D&sntz=1&usg=AFQjCNH6C4q1D5g3ZAWie6R8xewVTdnjHQ)</span><span class="c7 c26">[h](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-7&sa=D&sntz=1&usg=AFQjCNH6C4q1D5g3ZAWie6R8xewVTdnjHQ)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-8&sa=D&sntz=1&usg=AFQjCNEKT3I5BnTDd0xPdY5K9vLYHpnwHQ)</span><span class="c7 c26">[i](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-8&sa=D&sntz=1&usg=AFQjCNEKT3I5BnTDd0xPdY5K9vLYHpnwHQ)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-9&sa=D&sntz=1&usg=AFQjCNGCn-FX2o45A_O4uSD4mDCL8CKDBw)</span><span class="c7 c26">[j](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-9&sa=D&sntz=1&usg=AFQjCNGCn-FX2o45A_O4uSD4mDCL8CKDBw)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-10&sa=D&sntz=1&usg=AFQjCNEKlsac5slWzi4NwYbbDX_LXmKtKg)</span><span class="c7 c26">[k](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-10&sa=D&sntz=1&usg=AFQjCNEKlsac5slWzi4NwYbbDX_LXmKtKg)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-11&sa=D&sntz=1&usg=AFQjCNE9uMp8B7T50VSqx7dKGtkj_cjBWw)</span><span class="c7 c26">[l](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-11&sa=D&sntz=1&usg=AFQjCNE9uMp8B7T50VSqx7dKGtkj_cjBWw)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-12&sa=D&sntz=1&usg=AFQjCNG4shQ8qVBqAJYIdmoJ0fAjRt_iog)</span><span class="c7 c26">[m](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-12&sa=D&sntz=1&usg=AFQjCNG4shQ8qVBqAJYIdmoJ0fAjRt_iog)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-13&sa=D&sntz=1&usg=AFQjCNG1fjOnLTB_HFZFBviQxh7pET4rSA)</span><span class="c7 c26">[n](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-13&sa=D&sntz=1&usg=AFQjCNG1fjOnLTB_HFZFBviQxh7pET4rSA)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-14&sa=D&sntz=1&usg=AFQjCNF0PwFNXOTIEn_j1Zl6YkUpHO9q1A)</span><span class="c7 c26">[o](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-14&sa=D&sntz=1&usg=AFQjCNF0PwFNXOTIEn_j1Zl6YkUpHO9q1A)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-15&sa=D&sntz=1&usg=AFQjCNG1Ktx4NxksvUV3hrolRieZ_cHgMw)</span><span class="c7 c26">[p](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-15&sa=D&sntz=1&usg=AFQjCNG1Ktx4NxksvUV3hrolRieZ_cHgMw)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-16&sa=D&sntz=1&usg=AFQjCNFLb9krNHdrb-atMAH-71HFOmFv9g)</span><span class="c7 c26">[q](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-16&sa=D&sntz=1&usg=AFQjCNFLb9krNHdrb-atMAH-71HFOmFv9g)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-17&sa=D&sntz=1&usg=AFQjCNGpba0brOdCPut0kolvuiYm1oWO_w)</span><span class="c7 c26">[r](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-17&sa=D&sntz=1&usg=AFQjCNGpba0brOdCPut0kolvuiYm1oWO_w)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-18&sa=D&sntz=1&usg=AFQjCNE6yzsFAJqIlLq_IFOeZZN2BCu0Yw)</span><span class="c7 c26">[s](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-18&sa=D&sntz=1&usg=AFQjCNE6yzsFAJqIlLq_IFOeZZN2BCu0Yw)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-19&sa=D&sntz=1&usg=AFQjCNETsDjHsTZBF4tNfHtJ7GfOizTbpA)</span><span class="c7 c26">[t](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-19&sa=D&sntz=1&usg=AFQjCNETsDjHsTZBF4tNfHtJ7GfOizTbpA)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-20&sa=D&sntz=1&usg=AFQjCNFAMvDRRoqbzosXDhWHBNiFEwlINg)</span><span class="c7 c26">[u](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-20&sa=D&sntz=1&usg=AFQjCNFAMvDRRoqbzosXDhWHBNiFEwlINg)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-21&sa=D&sntz=1&usg=AFQjCNF37Xz5DEBmgNNczo-PlJbfsBtsRQ)</span><span class="c7 c26">[v](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-21&sa=D&sntz=1&usg=AFQjCNF37Xz5DEBmgNNczo-PlJbfsBtsRQ)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-22&sa=D&sntz=1&usg=AFQjCNGQlX9AGC_wgUHP_eEKrF5hBP4FDQ)</span><span class="c7 c26">[w](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-22&sa=D&sntz=1&usg=AFQjCNGQlX9AGC_wgUHP_eEKrF5hBP4FDQ)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-23&sa=D&sntz=1&usg=AFQjCNG_0SqDlHm1j_UQaEhRv2NbF_Fz8A)</span><span class="c7 c26">[x](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-23&sa=D&sntz=1&usg=AFQjCNG_0SqDlHm1j_UQaEhRv2NbF_Fz8A)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-24&sa=D&sntz=1&usg=AFQjCNEKs1vEgf_4X1Pz-OGPrMtV-NAEUQ)</span><span class="c7 c26">[y](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-24&sa=D&sntz=1&usg=AFQjCNEKs1vEgf_4X1Pz-OGPrMtV-NAEUQ)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-25&sa=D&sntz=1&usg=AFQjCNGV4ZE7qWT3Gh3hwUdGqq_El6hOIw)</span><span class="c7 c26">[z](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-25&sa=D&sntz=1&usg=AFQjCNGV4ZE7qWT3Gh3hwUdGqq_El6hOIw)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-26&sa=D&sntz=1&usg=AFQjCNHhd-HxVFzH2XPN-McT_Rgs3b_IXQ)</span><span class="c7 c26">[aa](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-26&sa=D&sntz=1&usg=AFQjCNHhd-HxVFzH2XPN-McT_Rgs3b_IXQ)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-27&sa=D&sntz=1&usg=AFQjCNGB4wmhQwB_Ze1H1KJfloLPxBPTRA)</span><span class="c7 c26">[ab](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-27&sa=D&sntz=1&usg=AFQjCNGB4wmhQwB_Ze1H1KJfloLPxBPTRA)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-28&sa=D&sntz=1&usg=AFQjCNF6wpBoS4mZrl1Gk6qMB0qz0TyArQ)</span><span class="c7 c26">[ac](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-28&sa=D&sntz=1&usg=AFQjCNF6wpBoS4mZrl1Gk6qMB0qz0TyArQ)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-29&sa=D&sntz=1&usg=AFQjCNG4h2jRDoEX6I6Q9CTKw1Jba-GDUQ)</span><span class="c7 c26">[ad](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-29&sa=D&sntz=1&usg=AFQjCNG4h2jRDoEX6I6Q9CTKw1Jba-GDUQ)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-30&sa=D&sntz=1&usg=AFQjCNGbiWhu2jH7Wt6Au_dywo1aJEbf9A)</span><span class="c7 c26">[ae](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-30&sa=D&sntz=1&usg=AFQjCNGbiWhu2jH7Wt6Au_dywo1aJEbf9A)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-31&sa=D&sntz=1&usg=AFQjCNHzNMiJ6MVta1lLs4h_GsQUU0Vpxg)</span><span class="c7 c26">[af](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-31&sa=D&sntz=1&usg=AFQjCNHzNMiJ6MVta1lLs4h_GsQUU0Vpxg)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-32&sa=D&sntz=1&usg=AFQjCNHL2vfhmv-vrVry5PaQ_aSGhg4U2g)</span><span class="c7 c26">[ag](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-32&sa=D&sntz=1&usg=AFQjCNHL2vfhmv-vrVry5PaQ_aSGhg4U2g)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-33&sa=D&sntz=1&usg=AFQjCNGKBVJ-570opj-I6fcYhtMnuVJRUA)</span><span class="c7 c26">[ah](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-33&sa=D&sntz=1&usg=AFQjCNGKBVJ-570opj-I6fcYhtMnuVJRUA)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-34&sa=D&sntz=1&usg=AFQjCNEPhDhw2RIfLVys-6TaSxifiW1MBw)</span><span class="c7 c26">[ai](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-34&sa=D&sntz=1&usg=AFQjCNEPhDhw2RIfLVys-6TaSxifiW1MBw)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-35&sa=D&sntz=1&usg=AFQjCNGuoRhz6WnJ1idUb1XbJI7HcrqDYQ)</span><span class="c7 c26">[aj](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-35&sa=D&sntz=1&usg=AFQjCNGuoRhz6WnJ1idUb1XbJI7HcrqDYQ)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-36&sa=D&sntz=1&usg=AFQjCNEoaqtELQaJS-uwioneLG7voGnv1A)</span><span class="c7 c26">[ak](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-36&sa=D&sntz=1&usg=AFQjCNEoaqtELQaJS-uwioneLG7voGnv1A)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-37&sa=D&sntz=1&usg=AFQjCNHmuDSuDd3RWcG2tG__oTBbxV5SEg)</span><span class="c7 c26">[al](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-37&sa=D&sntz=1&usg=AFQjCNHmuDSuDd3RWcG2tG__oTBbxV5SEg)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-38&sa=D&sntz=1&usg=AFQjCNEI5CYF5HPcopHPfNZ2sihRcccx8g)</span><span class="c7 c26">[am](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-38&sa=D&sntz=1&usg=AFQjCNEI5CYF5HPcopHPfNZ2sihRcccx8g)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-39&sa=D&sntz=1&usg=AFQjCNHt7q6_t63dCq5wTXI_5LAUaIlKnQ)</span><span class="c7 c26">[an](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-39&sa=D&sntz=1&usg=AFQjCNHt7q6_t63dCq5wTXI_5LAUaIlKnQ)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-40&sa=D&sntz=1&usg=AFQjCNGv25V5zc0sASO87LZH0LhczD7U-A)</span><span class="c7 c26">[ao](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-40&sa=D&sntz=1&usg=AFQjCNGv25V5zc0sASO87LZH0LhczD7U-A)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-41&sa=D&sntz=1&usg=AFQjCNHc7LvP9gWRuH0R-nNIEd5XSViglg)</span><span class="c7 c26">[ap](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-41&sa=D&sntz=1&usg=AFQjCNHc7LvP9gWRuH0R-nNIEd5XSViglg)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-42&sa=D&sntz=1&usg=AFQjCNHdWsgtJt6Kacl2N_rZpJH_EwYNoQ)</span><span class="c7 c26">[aq](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-42&sa=D&sntz=1&usg=AFQjCNHdWsgtJt6Kacl2N_rZpJH_EwYNoQ)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-43&sa=D&sntz=1&usg=AFQjCNEGxs-2neK1Sle43l-ZUI2q68-oPw)</span><span class="c7 c26">[ar](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-43&sa=D&sntz=1&usg=AFQjCNEGxs-2neK1Sle43l-ZUI2q68-oPw)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-44&sa=D&sntz=1&usg=AFQjCNFyqCYxUctIdtAsVF-hJh2JMDKqmw)</span><span class="c7 c26">[as](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-44&sa=D&sntz=1&usg=AFQjCNFyqCYxUctIdtAsVF-hJh2JMDKqmw)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-45&sa=D&sntz=1&usg=AFQjCNFBj4e-w8EoBx5FZs0jZsgQeRIAdQ)</span><span class="c7 c26">[at](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2616_1-45&sa=D&sntz=1&usg=AFQjCNFBj4e-w8EoBx5FZs0jZsgQeRIAdQ)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FRoy_Fielding&sa=D&sntz=1&usg=AFQjCNENK0m1O0R48bvQ_O1EE3UTywdhBw)</span><span class="c7">[Fielding, Roy T.](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FRoy_Fielding&sa=D&sntz=1&usg=AFQjCNENK0m1O0R48bvQ_O1EE3UTywdhBw)</span><span class="c15">;</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FJim_Gettys&sa=D&sntz=1&usg=AFQjCNGdYSj8PpKVL05L6tR36FerLiSMdw)</span><span class="c7">[Gettys, James](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FJim_Gettys&sa=D&sntz=1&usg=AFQjCNGdYSj8PpKVL05L6tR36FerLiSMdw)</span><span class="c15">; Mogul, Jeffrey C.;</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FHenrik_Frystyk_Nielsen&sa=D&sntz=1&usg=AFQjCNEtGw_7nQktntFcfOUIDpEwqNnxhw)</span><span class="c7">[Nielsen, Henrik Frystyk](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FHenrik_Frystyk_Nielsen&sa=D&sntz=1&usg=AFQjCNEtGw_7nQktntFcfOUIDpEwqNnxhw)</span><span class="c15">; Masinter, Larry; Leach, Paul J.;</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FTim_Berners-Lee&sa=D&sntz=1&usg=AFQjCNH9hICVWse99T65FXXn0znClm6jcw)</span><span class="c7">[Berners-Lee, Tim](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FTim_Berners-Lee&sa=D&sntz=1&usg=AFQjCNH9hICVWse99T65FXXn0znClm6jcw)</span><span class="c15"> (June 1999).</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Ftools.ietf.org%2Fhtml%2Frfc2616&sa=D&sntz=1&usg=AFQjCNEXGfZZQY-JKdF0Ee-Ex_mp1hi05A)</span><span class="c15 c18">[Hypertext Transfer Protocol -- HTTP/1.1](http://www.google.com/url?q=http%3A%2F%2Ftools.ietf.org%2Fhtml%2Frfc2616&sa=D&sntz=1&usg=AFQjCNEXGfZZQY-JKdF0Ee-Ex_mp1hi05A)</span><span class="c15">.</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FInternet_Engineering_Task_Force&sa=D&sntz=1&usg=AFQjCNGjnaKzF_B6ABTD2pJqE2YodAzBSA)</span><span class="c7">[IETF](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FInternet_Engineering_Task_Force&sa=D&sntz=1&usg=AFQjCNGjnaKzF_B6ABTD2pJqE2YodAzBSA)</span><span class="c15">. RFC 2616\. Retrieved October 24, 2009.</span>
3.  <span class="c7 c30">[^](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2518_2-0&sa=D&sntz=1&usg=AFQjCNFH1Tjrncw04OQlYp_IxOiU2HjW3g)</span><span class="c15"> Goland, Yaron;</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FJim_Whitehead_(professor)&sa=D&sntz=1&usg=AFQjCNHmzDHsHxz0GqxLEs-qioGZPjugAg)</span><span class="c7">[Whitehead, Jim](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FJim_Whitehead_(professor)&sa=D&sntz=1&usg=AFQjCNHmzDHsHxz0GqxLEs-qioGZPjugAg)</span><span class="c15">; Faizi, Asad; Carter, Steve R.; Jensen, Del (February 1999).</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Ftools.ietf.org%2Fhtml%2Frfc2518&sa=D&sntz=1&usg=AFQjCNFG1cqLxzo4cbJhwLZ1lIuAY-ZanQ)</span><span class="c15 c18">[HTTP Extensions for Distributed Authoring -- WEBDAV](http://www.google.com/url?q=http%3A%2F%2Ftools.ietf.org%2Fhtml%2Frfc2518&sa=D&sntz=1&usg=AFQjCNFG1cqLxzo4cbJhwLZ1lIuAY-ZanQ)</span><span class="c15">.</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FInternet_Engineering_Task_Force&sa=D&sntz=1&usg=AFQjCNGjnaKzF_B6ABTD2pJqE2YodAzBSA)</span><span class="c7">[IETF](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FInternet_Engineering_Task_Force&sa=D&sntz=1&usg=AFQjCNGjnaKzF_B6ABTD2pJqE2YodAzBSA)</span><span class="c15">. RFC 2518\. Retrieved October 24, 2009.</span>
4.  <span class="c15">^</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-ResumableHttpRequestsProposal_3-0&sa=D&sntz=1&usg=AFQjCNEZ5LyOqxc_Q1o6L8XKYpjaan12gQ)</span><span class="c7 c26">[a](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-ResumableHttpRequestsProposal_3-0&sa=D&sntz=1&usg=AFQjCNEZ5LyOqxc_Q1o6L8XKYpjaan12gQ)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-ResumableHttpRequestsProposal_3-1&sa=D&sntz=1&usg=AFQjCNE45YCbpOehFerXS4a2R4XWRJ3dsg)</span><span class="c7 c26">[b](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-ResumableHttpRequestsProposal_3-1&sa=D&sntz=1&usg=AFQjCNE45YCbpOehFerXS4a2R4XWRJ3dsg)</span><span class="c15">[ ](http://code.google.com/p/gears/wiki/ResumableHttpRequestsProposal)</span><span class="c15 c41">["A proposal for supporting resumable POST/PUT HTTP requests in HTTP/1.0."](http://code.google.com/p/gears/wiki/ResumableHttpRequestsProposal)</span><span class="c15">.</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FGoogle&sa=D&sntz=1&usg=AFQjCNHVXYYBLZd3-yUi8iiqzi3yxh7PAQ)</span><span class="c7">[Google](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FGoogle&sa=D&sntz=1&usg=AFQjCNHVXYYBLZd3-yUi8iiqzi3yxh7PAQ)</span><span class="c15">. 2010\. Retrieved August 8, 2011.</span>
5.  <span class="c7 c30">[^](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-4&sa=D&sntz=1&usg=AFQjCNFnmmsgmuSA4rbzJiCjMa6DKi9_xA)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fsupport.microsoft.com%2Fkb%2F208427&sa=D&sntz=1&usg=AFQjCNE94NAHtjTDUPo_3nsW1NGFR9m2uQ)</span><span class="c15 c41">[Support.microsoft.com](http://www.google.com/url?q=http%3A%2F%2Fsupport.microsoft.com%2Fkb%2F208427&sa=D&sntz=1&usg=AFQjCNE94NAHtjTDUPo_3nsW1NGFR9m2uQ)</span>
6.  <span class="c7 c30">[^](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-5&sa=D&sntz=1&usg=AFQjCNHx-bWC9i5Ljcf0i-lWeOjKFg4d-A)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fwww.lorem.biz%2Fhttp-status-codes.asp&sa=D&sntz=1&usg=AFQjCNH0yluuFikP7_Rk5DPlrLplgmC_uA)</span><span class="c15 c41">[Lorem.biz](http://www.google.com/url?q=http%3A%2F%2Fwww.lorem.biz%2Fhttp-status-codes.asp&sa=D&sntz=1&usg=AFQjCNH0yluuFikP7_Rk5DPlrLplgmC_uA)</span>
7.  <span class="c15">^</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_4918_6-0&sa=D&sntz=1&usg=AFQjCNEE6a9DXuajLKgoGhAk3BBxWm-aPA)</span><span class="c7 c26">[a](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_4918_6-0&sa=D&sntz=1&usg=AFQjCNEE6a9DXuajLKgoGhAk3BBxWm-aPA)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_4918_6-1&sa=D&sntz=1&usg=AFQjCNFA3CBCr5N98Qcubosm4wVjSG8sdQ)</span><span class="c7 c26">[b](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_4918_6-1&sa=D&sntz=1&usg=AFQjCNFA3CBCr5N98Qcubosm4wVjSG8sdQ)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_4918_6-2&sa=D&sntz=1&usg=AFQjCNGe0qd6r7e-_nidxNRnCNSYi8lDKQ)</span><span class="c7 c26">[c](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_4918_6-2&sa=D&sntz=1&usg=AFQjCNGe0qd6r7e-_nidxNRnCNSYi8lDKQ)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_4918_6-3&sa=D&sntz=1&usg=AFQjCNHxHH2TnsyMBgIcgTR5bk2uOxW30g)</span><span class="c7 c26">[d](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_4918_6-3&sa=D&sntz=1&usg=AFQjCNHxHH2TnsyMBgIcgTR5bk2uOxW30g)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_4918_6-4&sa=D&sntz=1&usg=AFQjCNGBHzbTm7At9nEb-W7PYMdd9PEDYQ)</span><span class="c7 c26">[e](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_4918_6-4&sa=D&sntz=1&usg=AFQjCNGBHzbTm7At9nEb-W7PYMdd9PEDYQ)</span><span class="c15"> Dusseault, Lisa, ed (June 2007).</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Ftools.ietf.org%2Fhtml%2Frfc4918&sa=D&sntz=1&usg=AFQjCNFiB84F6wZ1hnQc-nR_eSJy22q3TQ)</span><span class="c15 c18">[HTTP Extensions for Web Distributed Authoring and Versioning (WebDAV)](http://www.google.com/url?q=http%3A%2F%2Ftools.ietf.org%2Fhtml%2Frfc4918&sa=D&sntz=1&usg=AFQjCNFiB84F6wZ1hnQc-nR_eSJy22q3TQ)</span><span class="c15">.</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FInternet_Engineering_Task_Force&sa=D&sntz=1&usg=AFQjCNGjnaKzF_B6ABTD2pJqE2YodAzBSA)</span><span class="c7">[IETF](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FInternet_Engineering_Task_Force&sa=D&sntz=1&usg=AFQjCNGjnaKzF_B6ABTD2pJqE2YodAzBSA)</span><span class="c15">. RFC 4918\. Retrieved October 24, 2009.</span>
8.  <span class="c7 c30">[^](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_3229_7-0&sa=D&sntz=1&usg=AFQjCNHFNJF288aX_BTGf6W7GLfFdHvU2g)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Ftools.ietf.org%2Fhtml%2Frfc3229&sa=D&sntz=1&usg=AFQjCNG58qDe6OVSjjmdQPUiI8Prk01QTA)</span><span class="c15 c18">[Delta encoding in HTTP](http://www.google.com/url?q=http%3A%2F%2Ftools.ietf.org%2Fhtml%2Frfc3229&sa=D&sntz=1&usg=AFQjCNG58qDe6OVSjjmdQPUiI8Prk01QTA)</span><span class="c15">.</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FInternet_Engineering_Task_Force&sa=D&sntz=1&usg=AFQjCNGjnaKzF_B6ABTD2pJqE2YodAzBSA)</span><span class="c7">[IETF](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FInternet_Engineering_Task_Force&sa=D&sntz=1&usg=AFQjCNGjnaKzF_B6ABTD2pJqE2YodAzBSA)</span><span class="c15">. January 2002\. RFC 3229\. Retrieved February 25, 2011.</span>
9.  <span class="c7 c30">[^](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_1945_8-0&sa=D&sntz=1&usg=AFQjCNFW3ZCKKFmdbqIbqBYhzQwxEjjCEQ)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FTim_Berners-Lee&sa=D&sntz=1&usg=AFQjCNH9hICVWse99T65FXXn0znClm6jcw)</span><span class="c7">[Berners-Lee, Tim](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FTim_Berners-Lee&sa=D&sntz=1&usg=AFQjCNH9hICVWse99T65FXXn0znClm6jcw)</span><span class="c15">;</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FRoy_Fielding&sa=D&sntz=1&usg=AFQjCNENK0m1O0R48bvQ_O1EE3UTywdhBw)</span><span class="c7">[Fielding, Roy T.](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FRoy_Fielding&sa=D&sntz=1&usg=AFQjCNENK0m1O0R48bvQ_O1EE3UTywdhBw)</span><span class="c15">;</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FHenrik_Frystyk_Nielsen&sa=D&sntz=1&usg=AFQjCNEtGw_7nQktntFcfOUIDpEwqNnxhw)</span><span class="c7">[Nielsen, Henrik Frystyk](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FHenrik_Frystyk_Nielsen&sa=D&sntz=1&usg=AFQjCNEtGw_7nQktntFcfOUIDpEwqNnxhw)</span><span class="c15"> (May 1996).</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Ftools.ietf.org%2Fhtml%2Frfc1945&sa=D&sntz=1&usg=AFQjCNGLzNGIQqb2Ua3OJ1q8BCjufgLb5w)</span><span class="c15 c18">[Hypertext Transfer Protocol -- HTTP/1.0](http://www.google.com/url?q=http%3A%2F%2Ftools.ietf.org%2Fhtml%2Frfc1945&sa=D&sntz=1&usg=AFQjCNGLzNGIQqb2Ua3OJ1q8BCjufgLb5w)</span><span class="c15">.</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FInternet_Engineering_Task_Force&sa=D&sntz=1&usg=AFQjCNGjnaKzF_B6ABTD2pJqE2YodAzBSA)</span><span class="c7">[IETF](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FInternet_Engineering_Task_Force&sa=D&sntz=1&usg=AFQjCNGjnaKzF_B6ABTD2pJqE2YodAzBSA)</span><span class="c15">. RFC 1945\. Retrieved October 24, 2009.</span>
10.  <span class="c7 c30">[^](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC2616-10_9-0&sa=D&sntz=1&usg=AFQjCNGKzt9aUXdrwqZ6txywMfmFkWpxnQ)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fwww.w3.org%2FProtocols%2Frfc2616%2Frfc2616-sec10.html%23sec10.3.3&sa=D&sntz=1&usg=AFQjCNH0iQpKhA1AR1pci8XOXOZBKMEpIw)</span><span class="c15 c41">["HTTP/1.1 Section 10 Status Code Definitions"](http://www.google.com/url?q=http%3A%2F%2Fwww.w3.org%2FProtocols%2Frfc2616%2Frfc2616-sec10.html%23sec10.3.3&sa=D&sntz=1&usg=AFQjCNH0iQpKhA1AR1pci8XOXOZBKMEpIw)</span><span class="c15">.</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FW3C&sa=D&sntz=1&usg=AFQjCNEc1ValvQKxwPUJd2VcpTil9MHyBQ)</span><span class="c7">[W3C](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FW3C&sa=D&sntz=1&usg=AFQjCNEc1ValvQKxwPUJd2VcpTil9MHyBQ)</span><span class="c15">. Retrieved March 16, 2010.</span>
11.  <span class="c7 c30">[^](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-mozilla_bugzilla_bug_187996_10-0&sa=D&sntz=1&usg=AFQjCNGLbzxpZRW9bbOCQQZ-s1uaHliGNg)</span><span class="c15">[ ](https://www.google.com/url?q=https%3A%2F%2Fbugzilla.mozilla.org%2Fshow_bug.cgi%3Fid%3D187996&sa=D&sntz=1&usg=AFQjCNHOk5IMN-QGcrG5S5YFYMGwV3xpFQ)</span><span class="c15 c41">["Mozilla Bugzilla Bug 187996: Strange behavior on 305 redirect"](https://www.google.com/url?q=https%3A%2F%2Fbugzilla.mozilla.org%2Fshow_bug.cgi%3Fid%3D187996&sa=D&sntz=1&usg=AFQjCNHOk5IMN-QGcrG5S5YFYMGwV3xpFQ)</span><span class="c15">. March 3, 2003\. Retrieved May 21, 2009.</span>
12.  <span class="c7 c30">[^](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-11&sa=D&sntz=1&usg=AFQjCNEVSCyo9CWGQibj68gLwmGqZRYf-A)</span><span class="c15"> Cohen, Josh.</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fftp.ics.uci.edu%2Fpub%2Fietf%2Fhttp%2Fdraft-cohen-http-305-306-responses-00.txt&sa=D&sntz=1&usg=AFQjCNHKdUiS_zclLn4WWiuz3WUNFpPt5Q)</span><span class="c15 c41">["HTTP/1.1 305 and 306 Response Codes"](http://www.google.com/url?q=http%3A%2F%2Fftp.ics.uci.edu%2Fpub%2Fietf%2Fhttp%2Fdraft-cohen-http-305-306-responses-00.txt&sa=D&sntz=1&usg=AFQjCNHKdUiS_zclLn4WWiuz3WUNFpPt5Q)</span><span class="c15">. HTTP Working Group.</span>
13.  <span class="c7 c30">[^](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-WEBDAV_CP_04_12-0&sa=D&sntz=1&usg=AFQjCNHS8-3B1yIpb26n742jOyr1_Bthhw)</span><span class="c15"> Slein, Judy;</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FJim_Whitehead_(professor)&sa=D&sntz=1&usg=AFQjCNHmzDHsHxz0GqxLEs-qioGZPjugAg)</span><span class="c7">[Whitehead, Jim](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FJim_Whitehead_(professor)&sa=D&sntz=1&usg=AFQjCNHmzDHsHxz0GqxLEs-qioGZPjugAg)</span><span class="c15">; Davis, Jim; Clemm, Geoffrey; Fay, Chuck; Crawford, Jason; Chihaya, Tyson (June 18, 1999).</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Ftools.ietf.org%2Fhtml%2Fdraft-ietf-webdav-collection-protocol-04&sa=D&sntz=1&usg=AFQjCNGhfadtu4cBDtVwoOV5RQdwiRgkPA)</span><span class="c15 c18">[WebDAV Advanced Collections Protocol](http://www.google.com/url?q=http%3A%2F%2Ftools.ietf.org%2Fhtml%2Fdraft-ietf-webdav-collection-protocol-04&sa=D&sntz=1&usg=AFQjCNGhfadtu4cBDtVwoOV5RQdwiRgkPA)</span><span class="c15">.</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FInternet_Engineering_Task_Force&sa=D&sntz=1&usg=AFQjCNGjnaKzF_B6ABTD2pJqE2YodAzBSA)</span><span class="c7">[IETF](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FInternet_Engineering_Task_Force&sa=D&sntz=1&usg=AFQjCNGjnaKzF_B6ABTD2pJqE2YodAzBSA)</span><span class="c15">. I-D draft-ietf-webdav-collection-protocol-04\. Retrieved October 24, 2009.</span>
14.  <span class="c7 c30">[^](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_3648_13-0&sa=D&sntz=1&usg=AFQjCNHCUhRcQ7-3-jeq2_aPZBkV6DihyA)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FJim_Whitehead_(professor)&sa=D&sntz=1&usg=AFQjCNHmzDHsHxz0GqxLEs-qioGZPjugAg)</span><span class="c7">[Whitehead, Jim](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FJim_Whitehead_(professor)&sa=D&sntz=1&usg=AFQjCNHmzDHsHxz0GqxLEs-qioGZPjugAg)</span><span class="c15"> (December 2003). Reschke, Julian F.. ed.</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Ftools.ietf.org%2Fhtml%2Frfc3648&sa=D&sntz=1&usg=AFQjCNEH2WFnaaCYQOjYustroukOs1ABpg)</span><span class="c15 c18">[Web Distributed Authoring and Versioning (WebDAV) Ordered Collections Protocol](http://www.google.com/url?q=http%3A%2F%2Ftools.ietf.org%2Fhtml%2Frfc3648&sa=D&sntz=1&usg=AFQjCNEH2WFnaaCYQOjYustroukOs1ABpg)</span><span class="c15">.</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FInternet_Engineering_Task_Force&sa=D&sntz=1&usg=AFQjCNGjnaKzF_B6ABTD2pJqE2YodAzBSA)</span><span class="c7">[IETF](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FInternet_Engineering_Task_Force&sa=D&sntz=1&usg=AFQjCNGjnaKzF_B6ABTD2pJqE2YodAzBSA)</span><span class="c15">. RFC 3648\. Retrieved October 24, 2009.</span>
15.  <span class="c7 c30">[^](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2817_14-0&sa=D&sntz=1&usg=AFQjCNF_tU5jtCh7dRgOKdlv7yZJr4GmhQ)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FRohit_Khare&sa=D&sntz=1&usg=AFQjCNEjMQcIiL8WrOTwB3X1r9IM3VlM5A)</span><span class="c7">[Khare, Rohit](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FRohit_Khare&sa=D&sntz=1&usg=AFQjCNEjMQcIiL8WrOTwB3X1r9IM3VlM5A)</span><span class="c15">; Lawrence, Scott (May 2000).</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Ftools.ietf.org%2Fhtml%2Frfc2817&sa=D&sntz=1&usg=AFQjCNHI8xB7aNuaqSeYe7JzzRNAZVhjMQ)</span><span class="c15 c18">[Upgrading to TLS Within HTTP/1.1](http://www.google.com/url?q=http%3A%2F%2Ftools.ietf.org%2Fhtml%2Frfc2817&sa=D&sntz=1&usg=AFQjCNHI8xB7aNuaqSeYe7JzzRNAZVhjMQ)</span><span class="c15">.</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FInternet_Engineering_Task_Force&sa=D&sntz=1&usg=AFQjCNGjnaKzF_B6ABTD2pJqE2YodAzBSA)</span><span class="c7">[IETF](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FInternet_Engineering_Task_Force&sa=D&sntz=1&usg=AFQjCNGjnaKzF_B6ABTD2pJqE2YodAzBSA)</span><span class="c15">. RFC 2817\. Retrieved October 24, 2009.</span>
16.  <span class="c15">^</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-http-new-status-02_15-0&sa=D&sntz=1&usg=AFQjCNErhbEB_Q-f785fz3MPJXrYIq2b-g)</span><span class="c7 c26">[a](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-http-new-status-02_15-0&sa=D&sntz=1&usg=AFQjCNErhbEB_Q-f785fz3MPJXrYIq2b-g)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-http-new-status-02_15-1&sa=D&sntz=1&usg=AFQjCNFkk5X4TKYjeUNG4Fr7Pj8PwHhuIg)</span><span class="c7 c26">[b](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-http-new-status-02_15-1&sa=D&sntz=1&usg=AFQjCNFkk5X4TKYjeUNG4Fr7Pj8PwHhuIg)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-http-new-status-02_15-2&sa=D&sntz=1&usg=AFQjCNH-YD4bdU160ATNC9bikmvqYUPnCQ)</span><span class="c7 c26">[c](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-http-new-status-02_15-2&sa=D&sntz=1&usg=AFQjCNH-YD4bdU160ATNC9bikmvqYUPnCQ)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-http-new-status-02_15-3&sa=D&sntz=1&usg=AFQjCNFKSQo9wVoPspC3fYvLZQlXFYBRDw)</span><span class="c7 c26">[d](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-http-new-status-02_15-3&sa=D&sntz=1&usg=AFQjCNFKSQo9wVoPspC3fYvLZQlXFYBRDw)</span><span class="c15"> Nottingham, M.; Fielding, R. (18 October 2011).</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Ftools.ietf.org%2Fhtml%2Fdraft-nottingham-http-new-status-02&sa=D&sntz=1&usg=AFQjCNFrCLYzh4mxG_0Pu8GbEavOYkBczA)</span><span class="c15 c41">["draft-nottingham-http-new-status-02 - Additional HTTP Status Codes"](http://www.google.com/url?q=http%3A%2F%2Ftools.ietf.org%2Fhtml%2Fdraft-nottingham-http-new-status-02&sa=D&sntz=1&usg=AFQjCNFrCLYzh4mxG_0Pu8GbEavOYkBczA)</span><span class="c15">.</span> <span class="c15 c36">Internet-Drafts</span><span class="c15">.</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FInternet_Engineering_Task_Force&sa=D&sntz=1&usg=AFQjCNGjnaKzF_B6ABTD2pJqE2YodAzBSA)</span><span class="c7">[Internet Engineering Task Force](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FInternet_Engineering_Task_Force&sa=D&sntz=1&usg=AFQjCNGjnaKzF_B6ABTD2pJqE2YodAzBSA)</span><span class="c15">. Retrieved 2011-10-22.</span>
17.  <span class="c7 c30">[^](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-MS_DD891478_16-0&sa=D&sntz=1&usg=AFQjCNG9bL5D1Xe80KALZ6cKubUaEsxJxg)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fmsdn.microsoft.com%2Fen-us%2Flibrary%2Fdd891478(PROT.10).aspx&sa=D&sntz=1&usg=AFQjCNE-1v-iAkidH4OrP4amrjONC5C64A)</span><span class="c15 c41">["2.2.6 449 Retry With Status Code"](http://www.google.com/url?q=http%3A%2F%2Fmsdn.microsoft.com%2Fen-us%2Flibrary%2Fdd891478(PROT.10).aspx&sa=D&sntz=1&usg=AFQjCNE-1v-iAkidH4OrP4amrjONC5C64A)</span><span class="c15">.</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FMicrosoft&sa=D&sntz=1&usg=AFQjCNEGsQS8JYL3l3M-OG4aVVkw6VNlAQ)</span><span class="c7">[Microsoft](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FMicrosoft&sa=D&sntz=1&usg=AFQjCNEGsQS8JYL3l3M-OG4aVVkw6VNlAQ)</span><span class="c15">. 2009\. Retrieved October 26, 2009.</span>
18.  <span class="c7 c30">[^](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-17&sa=D&sntz=1&usg=AFQjCNH4dhRdItsUNpfoZ8t0plV6lk1J-w)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fzfhb6a.bay.livefilestore.com%2Fy1pKZJpcqDcSF9uKwaTmx301Ilr7cbJGN94HXCoHvPGwuwAlt5DA4ln0Y-F1WE6ZUC3URdiJdRe4hILTo87jWx2Yg&sa=D&sntz=1&usg=AFQjCNHr9_l_Cu5ivXz29hLZox5Ig8U6bw)</span><span class="c15 c41">["Screenshot of error page"](http://www.google.com/url?q=http%3A%2F%2Fzfhb6a.bay.livefilestore.com%2Fy1pKZJpcqDcSF9uKwaTmx301Ilr7cbJGN94HXCoHvPGwuwAlt5DA4ln0Y-F1WE6ZUC3URdiJdRe4hILTo87jWx2Yg&sa=D&sntz=1&usg=AFQjCNHr9_l_Cu5ivXz29hLZox5Ig8U6bw)</span><span class="c15"> (bmp). Retrieved October 11, 2009.</span>
19.  <span class="c7 c30">[^](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-Nginx_mailing_list.2C_2007-08-27_18-0&sa=D&sntz=1&usg=AFQjCNENqGx6-L_XKURpSxvqNp9PBIz2Kg)</span><span class="c15"> Sysoev, Igor (August 2007).</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fmarc.info%2F%3Fl%3Dnginx%26m%3D120127275109824%26w%3D2&sa=D&sntz=1&usg=AFQjCNHh7HDBh9vJeThpzFEDi8_6_ccz2g)</span><span class="c15 c41">["Re: 499 error in nginx"](http://www.google.com/url?q=http%3A%2F%2Fmarc.info%2F%3Fl%3Dnginx%26m%3D120127275109824%26w%3D2&sa=D&sntz=1&usg=AFQjCNHh7HDBh9vJeThpzFEDi8_6_ccz2g)</span><span class="c15">. Retrieved December 09, 2010.</span>
20.  <span class="c7 c30">[^](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2295_19-0&sa=D&sntz=1&usg=AFQjCNEeHUvmjHCLE8E2er-hQ9o6k7VJeQ)</span><span class="c15"> Holtman, Koen; Mutz, Andrew H. (March 1998).</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Ftools.ietf.org%2Fhtml%2Frfc2295&sa=D&sntz=1&usg=AFQjCNEisVbicERIl6Q9P9LeuT7GvWKP8Q)</span><span class="c15 c18">[Transparent Content Negotiation in HTTP](http://www.google.com/url?q=http%3A%2F%2Ftools.ietf.org%2Fhtml%2Frfc2295&sa=D&sntz=1&usg=AFQjCNEisVbicERIl6Q9P9LeuT7GvWKP8Q)</span><span class="c15">.</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FInternet_Engineering_Task_Force&sa=D&sntz=1&usg=AFQjCNGjnaKzF_B6ABTD2pJqE2YodAzBSA)</span><span class="c7">[IETF](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FInternet_Engineering_Task_Force&sa=D&sntz=1&usg=AFQjCNGjnaKzF_B6ABTD2pJqE2YodAzBSA)</span><span class="c15">. RFC 2295\. Retrieved October 24, 2009.</span>
21.  <span class="c7 c30">[^](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FList_of_HTTP_status_codes%23cite_ref-RFC_2774_20-0&sa=D&sntz=1&usg=AFQjCNGs4Xh2mQO09T1Qes3s0jmbsofPAQ)</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FHenrik_Frystyk_Nielsen&sa=D&sntz=1&usg=AFQjCNEtGw_7nQktntFcfOUIDpEwqNnxhw)</span><span class="c7">[Nielsen, Henrik Frystyk](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FHenrik_Frystyk_Nielsen&sa=D&sntz=1&usg=AFQjCNEtGw_7nQktntFcfOUIDpEwqNnxhw)</span><span class="c15">; Leach, Paul J.; Lawrence, Scott (February 2000).</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Ftools.ietf.org%2Fhtml%2Frfc2774&sa=D&sntz=1&usg=AFQjCNFV1-Ij3gFY8FdmWZwqOJkup2RpYg)</span><span class="c15 c18">[An HTTP Extension Framework](http://www.google.com/url?q=http%3A%2F%2Ftools.ietf.org%2Fhtml%2Frfc2774&sa=D&sntz=1&usg=AFQjCNFV1-Ij3gFY8FdmWZwqOJkup2RpYg)</span><span class="c15">.</span><span class="c15">[ ](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FInternet_Engineering_Task_Force&sa=D&sntz=1&usg=AFQjCNGjnaKzF_B6ABTD2pJqE2YodAzBSA)</span><span class="c7">[IETF](http://www.google.com/url?q=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FInternet_Engineering_Task_Force&sa=D&sntz=1&usg=AFQjCNGjnaKzF_B6ABTD2pJqE2YodAzBSA)</span><span class="c15">. RFC 2774\. Retrieved October 24, 2009.</span>
