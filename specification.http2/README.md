## HTTP 2.0 Protocol Specification

[![Build Status][build-status-image]][build-status]
[![Issue Stats][pull-requests-image]][pull-requests]
[![Issue Stats][issues-closed-image]][issues-closed]

[build-status-image]: https://travis-ci.org/k3po/specification.http2.svg?branch=develop
[build-status]: https://travis-ci.org/k3po/specification.http2
[pull-requests-image]: http://www.issuestats.com/github/k3po/specification.http2/badge/pr
[pull-requests]: http://www.issuestats.com/github/k3po/specification.http2
[issues-closed-image]: http://www.issuestats.com/github/k3po/specification.http2/badge/issue
[issues-closed]: http://www.issuestats.com/github/k3po/specification.http2

This project uses [K3PO](http://github.com/k3po/k3po) to help
[HTTP 2.0](https://tools.ietf.org/html/draft-ietf-httpbis-http2-16) implementations
automate their verification of the necessary requirements.

The integration tests fall into the following categories:

* Negotiating a HTTP 2.0
* Processing HTTP headers
* Simple DATA frames
  * Single stream request/response
  * Multi-stream request/response
  * Server push
* Flow control
* Dependant streams and prioritization
* PING frames
* Proxy use cases (HTTP CONNECT method)
* Extention negotiation
