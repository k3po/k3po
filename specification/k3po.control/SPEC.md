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

##### PREPARE command

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


##### START command

###### Description
This command will start the execution of the scripts that were sent in a previous PREPARE command. An error will occur if there is no PREPARE
command send before the START command. If the START command was already sent, an error will occur. If the command is successful, the server
sends back a STARTED event. 

###### Headers
N/A

###### Content
N/A


##### ABORT command

###### Description
This command will abort the current execution. It can be sent also after a PREPARE command (and before a START command).
TO DO - not sure what is sent back after an ABORT.

###### Headers
N/A

###### Content
N/A


##### AWAIT command

###### Description
This command will respond with a NOTIFIED event after the barrier with the name sent in the header is triggered on the k3po server.

###### Headers
- barrier - the name of the barrier that should be waited on

###### Content
N/A


##### NOTIFIED command

###### Description
This command will trigger the barrier with the name sent in the header and will respond with a NOTIFIED event with the same barrier name.

###### Headers
- barrier - the name of the barrier that should be triggered

###### Content
N/A


##### DISPOSE command

###### Description
This command will notify the k3po server that it should end the session and clean-up the resources. When execution is finished, a DISPOSED
event is sent back.

###### Headers
N/A

###### Content
N/A


### EVENTS

##### PREPARED event

###### Description
This event is sent by the server after a PREPARE command executed successfully.

###### Headers
- content-length - the length of the content of the command. This header is optional, should be present only when content is not empty.
- barrier - the name of a barrier in the prepared script. Can occur multiple times (multiple barriers).
	
###### Content
The content for prepared event is the content of the script that will be executed on the k3po server. 



##### STARTED event

###### Description
This event is sent by the server after a START command executed successfully.

###### Headers
N/A
	
###### Content
N/A 


##### NOTIFIED event

###### Description
This event is sent by the server when a barrier is triggered.

###### Headers
- barrier - the name of the barrier that was triggered.
	
###### Content
N/A 


##### DISPOSED event

###### Description
This event is sent by the server when the server was cleaned-up. It is sent normally as a response for a DISPOSE command.

###### Headers
N/A
	
###### Content
N/A 


##### FINISHED event

###### Description
This event is sent by the server when the server has finished execution of the scripts.

###### Headers
- content-length - the length of the content of the command. This header is optional, should be present only when content is not empty.
	
###### Content
The content for the event is the observed script after the execution on the k3po server. This script can be compared with the script in the
prepared event to check if the test was successful. 


##### ERROR event

###### Description
This event is sent by the server when the server encountered an error while processing another command.

###### Headers
- content-length - the length of the content of the command. This header is optional, should be present only when content is not empty.
- summary - a short description of the error (normally the error type)
	
###### Content
The content for the event is the description of the exception in case we have an exception or a more detailed description in case there is no exception. 


