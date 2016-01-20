## SSE Protocol Specification

This project uses [K3PO](http://github.com/k3po/k3po) to help
[SSE](https://www.w3.org/TR/eventsource) implementations
automate their verification of the necessary requirements.

The integration tests fall into the following categories:

* Process Data - `data` field
* Process Event Id - `id` field
* Process Named Events - `event` field
* Process Retry - `retry` field
* Process Line Separators
* Process Comments and Custom Fields
* Process Unnamed Events

There is no difference in the treatment of HTTP response status codes
3XX and 4XX for a SSE resource when compared to a non-SSE resource.
Tests for the aforementioned status codes will be defined in the
appropriate HTTP project under [K3PO](http://github.com/k3po/k3po).
