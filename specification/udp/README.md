This project uses [K3PO](http://github.com/k3po/k3po) to help UDP Protocol 
[RFC-768](https://tools.ietf.org/html/rfc768) implementations automate their verification of the necessary
specification requirements.

Note: all testing of the UDP protocol is done above what is provided by the OS/Java stack, thus the semantics
of udp protocol are currently not tested in this project. This K3PO specification doesn't provide any means
of dealing with UDP reliability. 

## Connections

UDP is a connection less protocol. However, K3PO scripts follow connection oriented syntax and semantics.
This specification introduces the virtual connections using the remote address of a client and
connections can be idle-timed out. The lifecycle of these connections follow other connection-oriented
protocols like TCP.

```
accept udp://localhost:8080

# stream 1
accepted
connected
read "client1 data"
...

# stream 2
accepted
connected
read "client2"
...

accept udp://localhost:8081

# stream 3
accepted
connected
read "client3"
...

# stream 4
accepted
connected
read "client4"
...

```

In the above example, UDP servers are bound at 8080 and 8081 ports. There are two clients (different
remote address) are connected to port 8080 and similarly there are two clients connected to port 8081.
A connection can be timed out by specifying an option `timeout`. The timeout is specified in milliseconds.
If a connection is idle for the specified amount then the connection is closed. If the option `timeout`
is not specified then the connection is not idle timed out.

```
accept udp://localhost:8080
       option timeout 1000

accepted
connected
read "client1 data"

# idle for 1 sec and the connection is closed
closed
```

```
connect udp://localhost:8080
       option timeout 1000

connected
write "client1 data"

# idle for 1 sec and the connection is closed
closed
```


