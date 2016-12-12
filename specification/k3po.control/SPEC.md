# K3po Control Protocol

## Abstract

This document defines the MUXOR protocol used to multiplex logical connections over a shared transport connection.

## Requirements
The key words "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL NOT", "SHOULD", "SHOULD NOT", "RECOMMENDED", "MAY",
and "OPTIONAL" in this document are to be interpreted as described in [RFC2119][].

## The Protocol

The protocol is made up of Commands and Events.  Commands are actions that can be triggered by an external entity.
Events are triggered by observed actions or state changes.  The protocol is formatted by

TODO normalize

```
Command / Event Name\n
headers
empty line
content

```

headers are key value pairs seperated by a ":" and delimitated by a new line "\n"

If the "content-length" header is specified, then optional content of the specified length can be added.

Headers that are not understood / processed by a command can be ignored.

### COMMANDS 


##### Prepare

TODO, put headers here

...

#####  


### EVENTS

##### Prepared

TODO, put headers here

...


PREPARE - prepare script with location of where to load script from class path
PREPARED - script Prepared (with what the scripts are, so test framework can diff)

START - Start client connections
STARTED - Client connections started 

FINISHED - Script has finished and here i s the results

ABORT - ABORT execution and return finished if possible (with in 5 seconds??)

DISPOSE - START Clean up
DISPOSED - Clean up

NOTIFY - trigger a barrier
NOTIFIED - a barrier has been triggered 
