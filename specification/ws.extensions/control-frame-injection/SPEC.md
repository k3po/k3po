## WebSocket Extension: Control Frame Injection

### Abstract

This document specifies a general strategy for WebSocket extensions that need to optionally inject control frames into a WebSocket message flow while minimizing overhead for the non-control frames in the message flow.

### Requirements

The key words "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL NOT", "SHOULD", "SHOULD NOT", "RECOMMENDED", "MAY", and "OPTIONAL" in this document are to be interpreted as described in [RFC 2119](https://tools.ietf.org/html/rfc2119).

## Table of Contents

  * [Introduction](#introduction)
  * [Negotiating Control Byte Sequence](#negotiating-control-byte-sequence)
  * [Protocol Definitions](#protocol-definitions)
	  * [Control Frame](#control-frame)
	  * [Non-control Frame](#non-control-frame)
	  * [Non-control Frame (colliding)](#non-control-frame-colliding)
  * [Message Processing](#message-processing)

### Introduction

The most straightforward mechanism to support injection control frames into the WebSocket message flow would be to define a WebSocket extension that reserves a single byte acting as a flag to interpret the rest of the frame payload as a control frame or a non-control frame. This implies that every frame has an additional byte which adds significant bandwidth overhead either for small messages, or if the ratio of control frames to non-control frames is very low.

Frames will be interpreted as control-frames if the frame payload is prefixed by a “control” byte sequence.  Non-control frames are delivered unmodified unless their payload happens to collide with the “control” prefix byte sequence, to effectively escape that case.


### Negotiating Control Byte Sequence

When the WebSocket-Extension, such as “x-kaazing-control-example” is requested by the client, the server responds with both the extension name and a readable hex text extension parameter indicating the control byte sequence that identifies control frames.

WebSocket-Extension: x-kaazing-control-example;f5a28e28

### Protocol Definitions

Let <`CONTROL`> represent a negotiated 4-byte sequence, for example 0xf5 0xa2 0x8e 0x28.

#### Control Frame

<`CONTROL`>  control-specific-payload

#### Non-control Frame

normal payload with no additional overhead

#### Non-control Frame (colliding)

<`CONTROL`> (empty)
normal frame payload with no additional overhead

The presence of the “escaping” frame instructs the runtime to process the next frame as a non-control frame even though it might begin with the <`CONTROL`> byte sequence.

### Message Processing

When a WebSocket message arrives at the client, it may have a <`CONTROL`> byte sequence at the beginning of the payload.  When an empty “control” frame is delivered to the client, it causes the client to treat the next frame as application data, even if the next frame matches a negotiated <`CONTROL`> byte sequence.

Note: control frames are **not** delivered to the application.

When multiple control extensions are negotiated on the same WebSocket handshake, it is in the server’s best interests to select a set of unique control sequences that have maximal bit overlap to support a cheap check for the overlapping bits on both the server and the client.