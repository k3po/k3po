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

