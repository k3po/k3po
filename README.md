[![TravisCI](https://travis-ci.org/k3po/specification.ws.svg?branch=develop)](https://travis-ci.org/k3po/specification.ws)

WebSocket Protocol ([RFC-6455](https://tools.ietf.org/html/rfc6455)) Specification

This project uses [K3PO](http://github.com/k3po/k3po) to to help WebSocket Protocol implementations automate their verification of the necessary requirements.

The integration tests fall into the following categories:
 * Opening Handshake
 * Base Framing
 * Masking
 * Fragmentation
 * Control Frames (Ping, Pong)
 * Data Frames (Text, Binary)
 * Extensibility
 * Closing Handshake
