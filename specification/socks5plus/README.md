This project uses [K3PO](http://github.com/k3po/k3po) to help
[SOCKS5+](https://docs.google.com/document/d/19mcro7X54MdnLSu6Z49dhgWgXjcYYzWT2wVOiyFKYXQ/edit#heading=h.d8k9qoljk5zx) implementations
automate their verification of the necessary requirements.

The integration tests fall into the following categories:

* Establish Connection
  * No Authentication Required
* Requests
  * Connect
  * Bind
  * ensure negative tests for error cases (connection not allowed,
    connection refused, command not supported, etc.)

