## SOCKS5 Protocol Specification

[![Build Status][build-status-image]][build-status]
[![Issue Stats][pull-requests-image]][pull-requests]
[![Issue Stats][issues-closed-image]][issues-closed]

[build-status-image]: https://travis-ci.org/k3po/specification.socks5.svg?branch=develop
[build-status]: https://travis-ci.org/k3po/specification.socks5
[pull-requests-image]: http://www.issuestats.com/github/k3po/specification.socks5/badge/pr
[pull-requests]: http://www.issuestats.com/github/k3po/specification.socks5
[issues-closed-image]: http://www.issuestats.com/github/k3po/specification.socks5/badge/issue
[issues-closed]: http://www.issuestats.com/github/k3po/specification.socks5

This project uses [K3PO](http://github.com/k3po/k3po) to help
[SOCKS5](https://tools.ietf.org/html/rfc1928) implementations
automate their verification of the necessary requirements.

The integration tests fall into the following categories:

* Establish Connection
  * No Authentication Required
  * GSSAPI (Required) -- see [RFC1961](https://tools.ietf.org/html/rfc1961)
  * Username/Password (optional) -- see [RFC1929](https://tools.ietf.org/html/rfc1929)
  * negative tests (no acceptable authentication method)
* Requests
  * Connect
  * Bind
  * UDP Associate
  * ensure negative tests for error cases (connection not allowed,
    connection refused, command not supported, etc.)

