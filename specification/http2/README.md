## HTTP 2.0 Protocol Specification

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
