[![TravisCI](https://travis-ci.org/k3po/specification.ws.svg?branch=develop)](https://travis-ci.org/k3po/specification.ws)

This project uses [K3PO Protocol Droid](http://github.com/k3po/k3po) to define a set of integration tests describing the 
WebSocket Protocol ([RFC-6455](https://tools.ietf.org/html/rfc6455)) Specification.

WebSocket Protocol implementations can use these integration tests to help them meet the necessary requirements.

The integration tests fall into the following categories:
 * Opening Handshake
 * Base Framing
 * Masking
 * Fragmentation
 * Control Frames (Ping, Pong)
 * Data Frames (Text, Binary)
 * Extensibility
 * Closing Handshake
