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

### Protocol Overview

_This section is non-normative._

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

...

The emulated WebSocket is now in the `CONNECTED` state.

### Server Handshake Requirements

## References

[[RFC 2616]][RFC2616]  "Hypertext Transfer Protocol -- HTTP/1.1"

[[RFC 6455]][RFC6455]  "The WebSocket Protocol"

[RFC2616]: https://tools.ietf.org/html/rfc2616
[RFC6455]: https://tools.ietf.org/html/rfc6455
