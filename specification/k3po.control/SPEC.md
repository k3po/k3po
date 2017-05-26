# K3po Control Protocol

## Abstract

This document defines the K3po control protocol used by a test framework to communicate with a remote K3po driver.

## Requirements
The key words "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL NOT", "SHOULD", "SHOULD NOT", "RECOMMENDED", "MAY",
and "OPTIONAL" in this document are to be interpreted as described in [RFC2119](https://tools.ietf.org/html/rfc2119).

## Terminology

#### K3PO Driver

The K3PO driver interprets K3PO scripts and exhibits the scripts defined network behavior.  It provides a server 
interface that allows clients to control it by the protocol set out in this spec. 

#### Test Framework

The test framework allows users to write tests in a specified manner.  The test framework defines an API for when
and how K3PO scripts should run. When ran, the test framework acts as a client to the K3PO driver and controls the
driver via this control protocol.

#### Test

A test encompasses the prepartion, execution, results, and clean up of simultaneous K3PO script(s) execution.

## Introduction

The K3PO control protocol defines how the test frameworks can utilize K3PO scripts in tests.  The test framework communicates with the driver.  The driver is responsible for executing the K3PO scripts.  The K3PO scripting language has several features such as Barriers and Properties which may need to be coordinated with the running test on the test framework.   The K3PO protocol defines how this is done.

The K3po Protocol is asynchronous.  The driver sends messages called *events* that may occur in reaction a state change on the driver, or to a behavior defined in the script.   The test framework sends *commands* to the driver.

## Message Format

A message is either a Command or an Event.  Commands are sent by the test framework.
Events are triggered by observed actions or state changes in the driver and are sent back to the test framework.  All messages
are formatted as follows:

    Control Message   =   command-line LF
                          *( header-field LF )
                          LF
                          [ message-body LF ]


- command-line      = command LF
- command              = token
- header-field = field-name ":" field-value
- LF, message-body, field-name, field-value, and token are defined in [HTTP RFC](https://tools.ietf.org/html/rfc7230).

If the "content-length" header is specified, then a message body of the specified length must be added.

Receivers SHOULD ignore undefined message headers and message types. Senders MUST NOT send undefined message headers or message types. 

## Message Types

### Commands

##### PREPARE command

###### Description
This **PREPARE** command informs the driver to prepare one or more K3PO script for execution. 

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

    PREPARE
    version:2.0
    name:test.rpt
    content-length:34
    
    connect_url http://localhost:8081

##### START command

###### Description
This command will start the execution of the test.

###### Headers
N/A

###### Message body
N/A


##### ABORT command

###### Description
The **ABORT** command will abort the current running test.  The driver will stop any executing scripts, and SHOULD return a FINISHED event if it has not already been sent.

###### Headers
N/A

###### Message body
N/A


##### AWAIT command

###### Description
This **AWAIT** command indicates that the test framework wishes to be notified via a **NOTIFIED** event when a barrier has been
triggered.  Note, the **NOTIFIED** event may never be sent back if the barrier is never triggered.

###### Headers
- barrier - the name of the barrier that should be waited on

###### Message body
N/A


##### NOTIFY command

###### Description
The **NOTIFY** command is used by the test framework to unblock script execution that is awaiting a barrier of the same 
name included in the **NOTIFY** command.

###### Headers
- barrier - the name of the barrier that should be triggered

###### Message body
N/A


### EVENTS

##### PREPARED event

###### Description
The **PREPARED** event is sent once on or more K3PO scripts is prepared for execution.  Note: the preparation of the scripts are
triggered by the PREPARE command.

###### Headers
- content-length - the length of the message body. This header is optional, should be present only when message body is not empty.
- barrier - the name of a barrier in the prepared script. Can occur multiple times (multiple barriers).
	
###### Message body
The message body for prepared event is the content of the script that will be executed on the driver. 

##### STARTED event

###### Description
The **STARTED** event is sent after the script has begun execution.  Note: execution is triggered by the START command.

###### Headers
N/A
	
###### Message body
N/A 


##### NOTIFIED event

###### Description
This event notifys the client that a barrier has been triggered.  It may be sent after a barrier is triggered, or after a AWAIT command if the barrier was previously completed.  It MUST be sent in response to a NOTIFY command, after that barrier is completed.

###### Headers
- barrier - the name of the barrier that was triggered.
	
###### Message body
N/A 


##### FINISHED event

###### Description
This event is sent by the driver when the driver has finished execution of the scripts. 

###### Headers
- content-length - the length of the message body. This header is optional, should be present only when the message body is 
not empty.
- notified - (occurs 0 or more times), the value is the name of a barrier that completed.  All completed barriers MUST be sent in the FINISHED event.
- awaiting -  (occurs 0 or more times), the value is the name of a barrier that did not complete.  All incomplete barriers must be sent in the FINISHED event.
	
###### Message body
The message body for the event is the observed script after the execution on the driver. This script can be compared with the script in the
prepared event to check if the test was successful. 


##### ERROR event

###### Description
The **ERROR** event is sent by the driver when the driver encountered an unrecoverable error.

###### Headers
- content-length - the length of the message body. This header is optional, should be present only when the message body is not empty.
- summary - a short description of the error (normally the error type)
	
###### Message body
The message body for the event is the description of the exception in case we have an exception or a more detailed description in case
there is no exception. 


## Message Exchanges

The test framework initializes a connection to the driver.  Only one test may be in execution at a time per a single 
connection.  

The test framework initiates the start of a test with a **PREPARE** command. When receiving a **PREPARE** the driver SHOULD 
load and prepare a set of scripts for execution.   If there are `accept` instructions in the scripts, a bind on 
corresponding ports/files MUST be performed during preparation.

When the scripts are successfully prepared, the driver MUST send a **PREPARED** event.

The test framework signals the start of a test with the **START** command.  The driver MUST start the script execution
when it receives the **START** command.

The driver MUST send the **STARTED** event when the scripts have started.

The driver MUST send the **FINISHED** event, when
  1) all K3PO scripts have finished, either by successfully completing or 
hitting undefined behavior AND 
  2) after sending a **STARTED** event (note, script execution may have completed if the script
is just accepts prior to the **START** command or **STARTED** event)

Prior to receiving a **FINISHED** command, the test framework may request script execution to stop by sending an  **ABORT** 
command. The driver must abort the current script execution, which should trigger a **FINISHED** command.  If the script 
execution has already stopped the driver will ignore the **ABORT** command (note, in this case the driver should already have
sent the **FINISHED** or be in the process of sending the **FINISHED**). 

Any time after the **PREPARE** command the test framework can send an **AWAIT** command.
 
If the driver receives an **AWAIT** command and the scripts are still executing (i.e. the **FINISHED** command has not been sent
 or is not in the process of being sent), the driver MUST send a **NOTIFIED** event if the barrier has already complete or 
 when/if the barrier does completes.  The driver MAY ignore await commands if it receives them after script execution.


The test framework signals the end of the test by closing the underlying connection.  This triggers the clean up of any 
outstanding resources on the driver (such as left open server streams).  The driver should not prepare any new tests that may
reuse resources from the last test, until the last test is completely cleaned up

Any unrecoverable error case triggers an **ERROR** event.  The underlying connection MUST not be reused.

#### Common Error Cases

Below is a non-exhaustive list of example error conditions

1. An **ERROR** event is sent if the test framework requests scripts to be prepared that can not be found
2. An **ERROR** event is sent if the test framework sends an **AWAIT** for a barrier that does not exist
2. An **ERROR** event is sent if the test framework indicates that it is trying to run multiple tests at the same time (multiple **START** or **PREPARE** commands are sent)


