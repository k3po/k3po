# K3PO

[![Build Status][build-status-image]][build-status]

[build-status-image]: https://travis-ci.org/k3po/k3po.svg?branch=develop
[build-status]: https://travis-ci.org/k3po/k3po
[pull-requests-image]: http://www.issuestats.com/github/k3po/k3po/badge/pr
[pull-requests]: http://www.issuestats.com/github/k3po/k3po
[issues-closed-image]: http://www.issuestats.com/github/k3po/k3po/badge/issue
[issues-closed]: http://www.issuestats.com/github/k3po/k3po

[![Gitter](https://badges.gitter.im/JoinChat.svg)](https://gitter.im/k3po/k3po?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

K3PO is a network driver and language agnostic testing tool.  It is designed to be able to create arbitrary network traffic and behavior, and to certify whether a network endpoint behaves correctly when subject to that behavior.  

The network behavior that the K3PO network driver creates is specified in a K3PO scripting language.  This language is described [here](https://github.com/k3po/k3po/wiki/Scripting-Language).  

The K3PO network driver can be directed to start emulating behavior defined in scripts via a [control protocol](Control Protocol).  Test frameworks in various programming languages can then utilize the control protocol to leverage K3PO for their own testing needs.

K3PO also consists of a collection of [specifications](Specifications) that validate correctness for a variety of public protocols, such as HTTP ([RFC 7230-7237](https://tools.ietf.org/html/rfc7230)), WebSocket ([RFC-6455](https://tools.ietf.org/html/rfc6455)), etc.

## Testing frameworks

K3PO is language agnostic and can be used by many different languages and test frameworks.  The Java JUnit framework is located in this project.  Other frameworks include:

[C Client Test Framework](https://github.com/kaazing/k3po.c)

[C# Client Test Framework](https://github.com/kaazing/k3po.dotnet)

[IOS Client Test Framework](https://github.com/kaazing/k3po.ios)

## Documentation

Documentation is located on the [Wiki](https://github.com/k3po/k3po/wiki)

## Java Quick Start

The examples directory is setup as an example to test java client implementations.  Run "mvn clean install -pl examples" to see it work.

