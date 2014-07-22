/*  Copyright (c) 2014 "Kaazing Corporation," (www.kaazing.com)
**
**  This file is part of Robot.
**
**  Robot is free software: you can redistribute it and/or modify
**  it under the terms of the GNU Affero General Public License as
**  published by the Free Software Foundation, either version 3 of the
**  License, or (at your option) any later version.
**
**  This program is distributed in the hope that it will be useful,
**  but WITHOUT ANY WARRANTY; without even the implied warranty of
**  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
**  GNU Affero General Public License for more details.
**
**  You should have received a copy of the GNU Affero General Public License
**  along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

#ifndef ROBOT_CONTROL_H_
#define ROBOT_CONTROL_H_

#include <netinet/in.h>

#define PORT 11642
#define FILE_EXT ".rpt"
#define SCRIPTS_LOCATION "./scripts/"

typedef enum {INIT, PREPARED, STARTED, FINISHED, ERROR} event;
typedef struct {
	char * actual_script;
	char * expected_script;
} result;

/*
** Executes the given client code against the provided robot script and
** returns the expected script and actual script in a result structure.
** Returns NULL if error occurs
**
** Arguments:
** file_name - the name of the script (must be located in ./scripts/ .rpt extension assumed),
** func - function pointer (function where your client code is, NULL if none), 
** seconds - timeout (set <= 0 for no timeout)
*/
result * robotTest(char * file_name, void * func, int seconds);

/*
** Establishes communication with the robot and prepares it to run a test against the given script
** Returns the contents of the given script on success
** Returns NULL if error occurs
**
** Arguments:
** file_name - the name of the script (must be located in ./scripts/ .rpt extension assumed),
*/
char * prepareRobot(char * file_name);

/*
** Opens and connects a socket to the given port
** Returns -1 on failure and 0 on success
** 
** Arguments:
** port - the port to connect the socket on 
*/
int createAndConnectSocket(in_port_t port);

/*
** Reads the contents of script with the given file name (expected .rpt extension and located in ./scripts)
** Returns the contents of the script in a string
** Returns NULL if error occurs
**
** Arguments:
** file_name - the name of the file whose contents will be read (expected .rpt extension and located in ./scripts)
*/
char * readFileIntoStringWrapper(char * file_name);

/*
** Reads num_bytes worth of content from the file, file_name into the buffer file_str
** Returns -1 on failure and 0 on success
** 
** Arguments: 
** file_str - the destination for the content being read from the file (will be NULL terminated)
** file_name - the file whose content will be read
** num_bytes - the number of bytes to read from the file
*/
int readFileIntoString(char * file_str, char * file_name, long int num_bytes);

/*
** Counts the number of bytes worth of content are present in the given file 
** Returns number of bytes in file on success, -1 on failure
** 
** Arguments:
** file - the file for which the number of bytes of content will be counted
*/
long int countBytesInFile(char * file);

/*
** Sends PREPARE command to robot using given socket file descriptor
** Returns -1 on failure, 0 on success
**
** Arguments:
** sock - the file descriptor for a socket for which to send the command
** script_name - the name of the script being prepared
** script - the contents of the script being prepared
*/
int writePrepare(int sock, char * script_name, char * script);

/*
** Sends START command to robot using given socket file descriptor
** Returns -1 on failure, 0 on success
**
** Arguments:
** sock - the file descriptor for a socket for which to send the command
** script_name - the name of the script being started
*/
int writeStart(int sock, char * script_name);

/*
** Sends ABORT command to robot using given socket file descriptor
** Returns -1 on failure, 0 on success
**
** Arguments:
** sock - the file descriptor for a socket for which to send the command
** script_name - the name of the script being aborted
*/
int writeAbort(int sock, char * script_name);

/*
** Sends ABORT command to robot using given socket file descriptor
** Returns -1 on failure, 0 on success
**
** Arguments:
** sock - the file descriptor for a socket for which to send the command
** script_name - the name of the script being aborted
*/
int sendMessage(int sock, char * msg);

/*
** Reads message of unknown size using given socket file descriptor
** Returns the message read
** Returns NULL on failure
**
** Arguments:
** sock - the file descriptor for a socket for which to read from
*/
char * readMessage(int sock);

/*
** Classifies the header of the given message into an event
** Returns -1 if the message header is not recognized or error occurs
**
** Arguments:
** evt - event to be set to the classified header
** msg - the message with the header to be classified
*/
int classifyMessage(event * evt, char * msg);

/*
** Returns a count of the number of characters (single byte) in the message header
** Returns -1 on failure
**
** Arguments:
** msg - the message with the header to compute the length of
*/
int getHeaderSize(char * msg);

/*
** Returns the content of the provided message
** Returns NULL on failure
** 
** Arguments:
** msg - the message with the content to extract
*/
char * readContent(char * msg);


/*
** Creates two threads, each with the specified start routine and argument
** as indicated by corresponding func and arg
** Returns the return value of the first thread's start routine 
** Returns NULL on failure
** NOTE: join is called on both threads 
**
** Arguments:
** func1 - the start routine of the first thread (communication thread)
** arg1 - the argument of the start routine of the first thread
** func2 - the start routine of the second thread (client thread)
** arg2 - the argument of the start routine of the second thread
*/
char * doThreaded(void * func1, void * arg1, void * func2, void * arg2);

/*
** Returns a newly created pthread_t * with default attributes and the given 
** start routine and argument
** Returns NULL on failure
**
** Arguments:
** func - the start routine of the thread
** arg - the argument of the start routine of the thread
*/
pthread_t * createThread(void * func, void * arg);

/*
** Handles communication of robot during execution of script until
** an ERROR is received or the FINISHED state is reached. Returns a
** pointer to a result structure with the results of the robot test execution
**
** Arguments:
** sock - socket file descriptor for communication with the robot
*/
char * handleControl(int * sock);

/*
** Signal handler for timeout
** Aborts the robot script execution and finishes up the test
** -----DO NOT USE-----
*/
void catch_alarm (int sig);

/*
** Frees the memory associated with the given result struct pointer
** Does nothing if result is NULL 
**
** Arguments:
** result - the result struct * to be free'd 
*/
void robotFree(result * result);

#endif
