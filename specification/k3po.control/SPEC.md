# K3po Control Protocol

## Abstract

This document defines the K3po control protocol used by a test framework to communicate with a remote K3po driver.

## Requirements
The key words "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL NOT", "SHOULD", "SHOULD NOT", "RECOMMENDED", "MAY",
and "OPTIONAL" in this document are to be interpreted as described in [RFC2119](https://tools.ietf.org/html/rfc2119).

## The Protocol

The protocol is made up of Commands and Events.  Commands are actions that can be triggered by the test framework.
Events are triggered by observed actions or state changes in the driver and are sent back to the test framework.  The protocol 
is formatted by:

>Control Message   =   command-line LF
>                      *( header-field LF )
>                      LF
>                      [ message-body LF ]

command-line      = command LF

command              = token

header-field = field-name ":" field-value

LF, message-body, field-name, field-value, and token are defined in [HTTP RFC](https://tools.ietf.org/html/rfc7230).

If the "content-length" header is specified, then a message body of the specified length must be added.

Headers that are not understood / processed by a command must be ignored.


### Flow

The communication between the test framework and the driver is a conversation - the test framework sends a command, the driver responds with an
event sent asynchronously when the action is finished. The only exception is the FINISHED event - the START command will have two events as a 
response - STARTED when the execution of the scripts is started and FINISHED when the execution of the scripts is finished.

Throughout the flow description bellow, **an error** represents an ERROR event sent by the driver.

- The test framework initiates the conversation with a **PREPARE** command. This command will make the k3po server load and prepare a set
of scripts for execution. The scripts are not yet executed, just loaded from disk and parsed. If there are ACCEPT instructions in the scripts,
a bound on corresponding ports is performed. If the command is successful, the driver sends back a PREPARED event. In case of failure, an
ERROR event is sent.

- If the PREPARED command is successful, a **START** command may be sent by the test framework. This command will start the execution of the
 clients in the scripts that were sent in the PREPARE command. If the command is successful, the driver sends back a STARTED event. This 
 command will also trigger the sending of a FINISHED event whenever the scripts finished execution on the k3po server.
 
- If there is no PREPARE command send before the START command or if the PREPARE command was not successful an error with a message "Script has not
 been prepared or is still preparing" must occur. 
 
- If the START command was already sent, an error with a message "Script has already been started" must occur.

- Any time after the PREPARE command and before the DISPOSE command, the test framework can send an **AWAIT** command for a barrier existing 
in the scripts. The command receives a response with a NOTIFIED event only when the corresponding barrier is notified. In case the barrier 
does not exist, an error must be returned.

- Any time after the PREPARE command *and before DISPOSE?*, the test framework can send a **NOTIFY** command for a barrier existing in the scripts.
 The command notifies the barrier and a response with a NOTIFIED event is received. In case the barrier does not exist, an error must be returned.

- Any time after the PREPARE command *and before DISPOSE?*, the test framework can send an **ABORT** command. The driver must abort the current 
execution. It may be sent any time after a PREPARE command.

- Any time after the PREPARE command, the test framework can send a **DISPOSE** command. The driver must abort the execution and release the resources 
in use. When done, a DISPOSED event must be sent back.


TODO - after the test is finished, you can start a new test from the beginning, with a new PREPARE. This does not seem to work very well. Usually 
the server gets stuck. Sometimes even a new connection will not help, still the test will not work. Should we support this or should we force the 
closing of the connection and opening a new one ? (refuse any command after a DISPOSE)


TODO - at this moment, nothing is returned by the driver after an ABORT command. I think we should return something. A FINISHED is not acceptable,
 as it should return the executed script, which is not executed or is partial. Also, DISPOSED should not be returned, as the server is not 
 disposed yet. Maybe we should create another event - ABORTED ?


TODO - ABORT and DISPOSE commands have a very similar functionality currently. Theoretically, the difference is that DISPOSE will release the resources 
(it is actually calling abort and then release resources). But the driver seems to be in the same state after an ABORT or DISPOSE. Same commands can be 
sent with the same responses (call again AWAIT/NOTIFY/ABORT/DISPOSE). We need to clearly specify the different functionality for ABORT and DISPOSE or 
remove one of them. 



### Commands

##### PREPARE command

###### Description
This command will initiate a new conversation. Scripts that should be loaded are given in the **name** headers. Additional override values for
properties existing in the scripts can be given in the message body.

###### Headers
- version - the version of the protocol. Currently the only supported version is 2.0.
- content-length - the length of the message body. This header is optional, should be present only when message body is not empty.
- origin - the root folder for the scripts to be executed. This header is optional
- name - the name of the script to be executed. It is a relative path to the origin. Can appear more than once (several scripts to be executed).

###### Message body
The message body for prepare command is a set of properties with format
*( property_name new_property_value LF) 
that will overwrite the values in the script. The properties overwritten must exist in the scripts. Otherwise the prepare must fail with an 
ERROR event.

Example:

```
PREPARE
version:2.0
name:test.rpt
content-length:32

connect_url http://localhost:8081
```

##### START command

###### Description
This command will start the execution of the test.

###### Headers
N/A

###### Message body
N/A


##### ABORT command

###### Description
This command will abort the current execution. It may be sent any time after a PREPARE command.

###### Headers
N/A

###### Message body
N/A


##### AWAIT command

###### Description
This command will have as a response a NOTIFIED event after the barrier with the name sent in the header is triggered on the k3po server.

###### Headers
- barrier - the name of the barrier that should be waited on

###### Message body
N/A


##### NOTIFIED command

###### Description
This command will trigger on the k3po server the barrier with the name sent in the header and will have a response with a NOTIFIED event 
with the same barrier name.

###### Headers
- barrier - the name of the barrier that should be triggered

###### Message body
N/A


##### DISPOSE command

###### Description
This command will notify the driver that it should end the session and clean-up the resources.

###### Headers
N/A

###### Message body
N/A


### EVENTS

##### PREPARED event

###### Description
This event is sent by the driver after a PREPARE command executed successfully.

###### Headers
- content-length - the length of the message body. This header is optional, should be present only when message body is not empty.
- barrier - the name of a barrier in the prepared script. Can occur multiple times (multiple barriers).
	
###### Message body
The message body for prepared event is the content of the script that will be executed on the k3po server. 



##### STARTED event

###### Description
This event is sent by the driver after a START command executed successfully.

###### Headers
N/A
	
###### Message body
N/A 


##### NOTIFIED event

###### Description
This event is sent by the driver when a barrier is triggered.

###### Headers
- barrier - the name of the barrier that was triggered.
	
###### Message body
N/A 


##### DISPOSED event

###### Description
This event is sent by the driver when the k3po server was cleaned-up. It is sent as a response for a DISPOSE command.

###### Headers
N/A
	
###### Message body
N/A 


##### FINISHED event

###### Description
This event is sent by the driver when the k3po server has finished execution of the scripts.

###### Headers
- content-length - the length of the message body. This header is optional, should be present only when the message body is 
not empty.
	
###### Message body
The message body for the event is the observed script after the execution on the k3po server. This script can be compared with the script in the
prepared event to check if the test was successful. 


##### ERROR event

###### Description
This event is sent by the driver when the k3po server encountered an error while processing a command.

###### Headers
- content-length - the length of the message body. This header is optional, should be present only when the message body is not empty.
- summary - a short description of the error (normally the error type)
	
###### Message body
The message body for the event is the description of the exception in case we have an exception or a more detailed description in case
there is no exception. 

