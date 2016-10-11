# Http Multi-Factor Authentication protocol

## Abstract

This document defines the HTTP Multi Factor Authentication
## Requirements
The key words "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL NOT", "SHOULD", "SHOULD NOT", "RECOMMENDED", "MAY",
and "OPTIONAL" in this document are to be interpreted as described in [RFC2119](https://www.ietf.org/rfc/rfc2119.txt).

## The Protocol

This spec is an extension to [HTTP Authorization](https://tools.ietf.org/html/rfc7235).  It defines a new Http Header Sec-Challenge-Identity.

#### Sec-Challenge-Identity

The Sec-Challenge-Identity is a single value header containing a UUIDs.  UUIDs value will follow Java JVM syntax as defined in [UUID.toString()](https://docs.oracle.com/javase/7/docs/api/java/util/UUID.html#toString())

The following are valid examples of the Header

```
 Sec-Challenge-Identity: 067e6162-3b6f-4ae2-a171-2470b63dff00

```

A server that received a Sec-Challenge-Identity MAY use the UUID to look up stored state to validate user identity and authentication.

A client that receives a Sec-Challenge-Identity in a 401 SHOULD include the Sec-Challenge-Identity with the same value in any subsequent requests.

 A proxy forwarding a request MUST NOT modify any Sec-Challenge-Identity fields in that request. Sec-Challenge-Identity follows the same rules for caching as Authorization Header. (See Section 3.2 of [RFC7234] for details of and requirements pertaining to handling of the Authorization field by HTTP caches.)

