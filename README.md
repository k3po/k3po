# K3PO Protocol Droid [![TravisCI](https://travis-ci.org/k3po/k3po.svg?branch=develop)](https://travis-ci.org/k3po/k3po)

[![Gitter](https://badges.gitter.im/JoinChat.svg)](https://gitter.im/k3po/k3po?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

The K3PO Protocol Droid is a Network Protocol Testing Tool

K3PO provides a network scripting language that allows authoring of network protocol tests.  These test are programming language agnostic.  Test Frameworks are then provided to run these tests for different programming languages.

In a K3PO script, you define the exact sequence of events that a network connection goes through in its lifetime.  If this exact sequence of events does not happen, the script is considered to have “failed”.  A K3PO script thus defines a “behavior”.  Testing, then, is comprised of defining the expected behaviors, and then running the script against your code to see if those expectations are met.

## Links

[C Client Test Framework](https://github.com/kaazing/k3po.c)

[C# Client Test Framework](https://github.com/kaazing/k3po.dotnet)

[IOS Client Test Framework](https://github.com/kaazing/k3po.ios)

## Java Quick Start

The examples directory is setup as an example to test java client implementations.  Run "mvn clean install -pl examples" to see it work.

## K3PO Language

The K3PO language is a scripting language that defines the exact sequence of events in a network connection.

A K3PO script is considered a “session”.  Within a script, you will define a set of expectations for one or more network connections.  Each of these network connections (and their associated expectations) is considered a “channel”.  A session might be comprised of a single short-lived channel, or several interacting channels.

##### Client Hello World

```
# This is a tcp client helloworld which would be used to test a server
  connect tcp://localhost:9876
  connected
  write "hello world"
  close
  closed
```
  
##### Server Hello World
```
# This is a tcp server helloworld which would be used to test a client
  accept tcp://localhost:9876
  accepted
  write "hello world"
  closed
  
```

### Keywords

##### comment
```
# This is a comment
# Comments start with a #

```

##### accept
```
# The accept keyword indicates that the channel will be a server channel.

accept <URI>

# The URI indicates the address and port on which the server will listen; 
# the URI scheme will additionally signal any transport-level abstractions to apply to the channel IO.
```

##### accepted

```

# The accepted keyword indicates that the client channel has reached the accepted state; 
# actions on this channel (such as reading/writing messages) can now commence on this channel.

accepted
```

##### connect

```
# The connect keyword indicates that the channel will be a client channel. 

connect <URI>

# The URI indicates the address and port to which the client will connect; the URI scheme will additionally signal any transport-level abstractions to apply to the channel IO.
```

##### connected

```
# The connected keyword indicates that the client channel has reached the connected state;
# actions on this channel (such as reading/writing messages) can now commence on this channel.
connected
```

##### close

```
# The close keyword indicates that K3PO should close the channel.
close
```

##### closed
```
# The closed keyword indicates that the channel has reached the closed state;  
# no further actions will occur on this channel.  
# Note that the closed keyword is required for every channel (i.e. one for every connect or accepted).

closed 
```

##### read
```
# The read keyword is used to read in the next bytes from the network as a message.  One or messages may follow a read.

read <message>
read <message> <message> ...


```

##### write
```
# The write keyword is used to write out the bytes of a message to the network.  One or messages may follow a write.

write <message>
write <message> <message> ...

# See messages for more detail of message types
```

##### await
```
# Wait for the barrier named “<barrier>” to be notified before processing upstream events (normally read). 
read await <barrier>

# Wait for the barrier named “<barrier>” to be notified before processing downstream subsequent actions.
write await <barrier>
```

##### notify
```
# The read notify keyword is used to indicate that the named barrier has been reached; 
# any channels currently at an await keyword for that named barrier will then be able to proceed. 
# Semantically equivalent to “write notify”. For readability use “read notify” when notifying 
# a barrier that a downstream event has occurred.

read notify <barrier>

# The write notify keyword is used to indicate that the named barrier has been reached; 
# any channels currently at an await keyword for that named barrier will then be able to proceed. 
# Semantically equivalent to ‘read notify’. For readability use “write notify” when notifying 
# a barrier that a downstream event has occurred.

write notify <barrier>
```



### Messages

K3PO allows reading/writing of messages as well as reading messages into variables to be written out latter

##### Text

```
# Any String with ""

write "Any text goes here"
read "Any text goes here"
```

##### Literal Bytes

```
# Any literal bytes as Hex [0xaa ...]

write [0x48 0x65 0x6c 0x6c 0x6f 0x2c 0x20 0x57 0x6f 0x72 0x6c 0x64 0x21 0xa]
read [0x48 0x65 0x6c 0x6c 0x6f 0x2c 0x20 0x57 0x6f 0x72 0x6c 0x64 0x21 0xa]
```


##### Regex

```
# Read a regex, A subset of the Regex as defined by the JavaPattern is supported
read /.*/

# Capture regex into variable via named group capture
read /?<capture>[abc]/
```
 [JavaPattern](http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html)

##### Types

```

# One may use the keywords byte, short, int, and long to read 1, 2, 4, and 8 bytes respectively
read byte
read short
read int
read long

# Read a byte and match it against the value 0x02
  read byte  2

# Read two bytes and match it against the value 0x0002
  read short 2


# Read four bytes and match it against the value 0x00000002
  read int   2


# Read eight bytes and match it against the value 0x0000000000000002  
  read long  2 

# Read a value into a variable 
read (byte:var)
read (short:var)
read (int:var)
read (long:var)

```

##### Variables

```
# Once you have captured the result of a variable you may later use this 
# variable to read and match the same exact bytes,write the variable out, 
# or use it to specify how many bytes a read statement should read. 

read ${var}
write ${var}

# Read ${var} number of bytes from a previous Type capture. 
# The variable var must have a numeric type or the statement 
# will fail (it must have been captured through a read 
# byte/short/int/long directive).

read ([...]:var)
read ([0..$var]:capture)

# El expressions

# The ${var} syntax comes from the fact that it is actually an 
# EL Expression. So for variables that have numeric type you can 
# write expressions like

${var-1}

# An error will occur and the script will fail if var is not a 
# number (is an array of bytes). One noteworthy side effect is 
# that these expressions will be coerced to type long. For 
# example, consider this script fragment:

read (short:var)
write ${var}
write ${var-1}

# The first write, will write out 2 bytes, the second one will write out 8.

```
