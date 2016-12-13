# K3po Control Protocol

## Abstract

This document defines the K3po control protocol used to communicate with a remote K3po server.

## Requirements
The key words "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL NOT", "SHOULD", "SHOULD NOT", "RECOMMENDED", "MAY",
and "OPTIONAL" in this document are to be interpreted as described in [RFC2119](https://tools.ietf.org/html/rfc2119).

## The Protocol

The protocol is made up of Commands and Events.  Commands are actions that can be triggered by an external entity.
Events are triggered by observed actions or state changes and are sent back to the external entity.  The protocol 
is formatted by

TODO normalize

```
Command / Event Name\n
headers
empty line
content

```

headers are key value pairs separated by a ":" and delimited by a new line "\n"

If the "content-length" header is specified, then optional content of the specified length can be added.

Headers that are not understood / processed by a command can be ignored.


### Commands

##### PREPARE

###### Description

This command will make the k3po server load and prepare a set of scripts for execution. The scripts are not yet executed, just loaded from
disk and parsed. Additional override values for properties existing in the scripts can be given in the content of the command.
If the command is successful, the server sends back a PREPARED event. In case of failure, an ERROR event is sent.

###### Headers

- version - the version of the protocol. Currently the only supported version is 2.0.
- content-length - the length of the content of the command. This header is optional, should be present only when content is not empty.
- origin - the root folder for the scripts to be executed. This header is optional
- name - the name of the script to be executed. It is a relative path to the origin. Can appear more than once (several scripts to be executed).

###### Content

The content for prepare command is a set of properties with values that will overwrite the values in the script. The properties overwritten must
exist in the scripts. Otherwise the prepare will fail.


##### START

###### Description

This command will start the execution of the scripts that were sent in a previous PREPARE command. An error will occur if there is no PREPARE
command send before the START command. If the START command was already sent, an error will occur. If the command is successful, the server
sends back a STARTED event. 

###### Headers

N/A

###### Content

N/A


##### ABORT

###### Description

This command will abort the current execution. It can be sent also after a PREPARE command (and before a START command).
TO DO - not sure what is sent back after an ABORT.

###### Headers

N/A

###### Content

N/A


##### AWAIT

###### Description

This command will respond with a NOTIFIED event after the barrier with the name sent in the header is notified on the k3po server.

###### Headers

- barrier - the name of the barrier that should be waited on

###### Content

N/A


##### NOTIFIED

###### Description

This command will notify the barrier with the name sent in the header and will respond with a NOTIFIED event with the same barrier name.

###### Headers

- barrier - the name of the barrier that should be notified

###### Content

N/A


##### DISPOSE

###### Description

This command will notify the k3po server that it should end the 

###### Headers

- barrier - the name of the barrier that should be notified

###### Content

N/A


### EVENTS

##### PREPARED

###### Description

This event is sent by the server after a PREPARE command executed successfully.

###### Headers

- content-length - the length of the content of the command. This header is optional, should be present only when content is not empty.
- barrier - the name of a barrier in the prepared script. Can occur multiple times (multiple barriers).
	
###### Content

The content for prepared event is the content of the script that will be executed on the k3po server. 


##### STARTED

###### Description

This event is sent by the server after a START command executed successfully.

###### Headers

N/A
	
###### Content

N/A 




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
