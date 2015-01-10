# WebSocket Protocol Specification

[![Build Status][build-status-image]][build-status]
[![Issue Stats][pull-requests-image]][pull-requests]
[![Issue Stats][issues-closed-image]][issues-closed]

[build-status-image]: https://travis-ci.org/k3po/specification.ws.svg?branch=develop
[build-status]: https://travis-ci.org/k3po/specification.ws
[pull-requests-image]: http://www.issuestats.com/github/k3po/specification.ws/badge/pr
[pull-requests]: http://www.issuestats.com/github/k3po/specification.ws
[issues-closed-image]: http://www.issuestats.com/github/k3po/specification.ws/badge/issue
[issues-closed]: http://www.issuestats.com/github/k3po/specification.ws


This project uses [K3PO](http://github.com/k3po/k3po) to to help WebSocket Protocol 
([RFC-6455](https://tools.ietf.org/html/rfc6455)) implementations automate their verification of the necessary requirements.

The integration tests fall into the following categories:
 * Opening Handshake
 * Base Framing
 * Masking
 * Fragmentation
 * Control Frames (Ping, Pong)
 * Data Frames (Text, Binary)
 * Extensibility
 * Closing Handshake
