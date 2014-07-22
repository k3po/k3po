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

#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <string.h>
#include <unistd.h>
#include <pthread.h>
#include <signal.h>
#include <sys/time.h>

#include "robot_control.h"


int my_sock = -1;
char * my_file_name;

/*
** Classifies the header of the given message into an event
** Returns -1 if the message header is not recognized or error occurs
**
** Arguments:
** evt - event to be set to the classified header
** msg - the message with the header to be classified
*/
int classifyMessage(event * evt, char * msg){
	int size = getHeaderSize(msg);
	if(size == -1){
		return -1;
	}
	char * header = malloc((size + 1) * sizeof(char));
	if(header == NULL){
		perror("malloc");
		return -1;
	}
	memcpy(header, msg, size);
	*(header + size * sizeof(char)) = '\0';
	if(strcmp(header, "PREPARED") == 0){
		*evt = PREPARED;
	}
	else if(strcmp(header, "STARTED") == 0){
		*evt = STARTED;
	}
	else if(strcmp(header, "FINISHED") == 0){
		*evt = FINISHED;
	}
	else if(strcmp(header, "ERROR") == 0){
		*evt = ERROR;
	}
	else{
		fprintf(stderr, "Header in received message not recognized:\n Header: %s\nMessage: %s\n", header, msg);
		free(header);
		return -1;
	}
	free(header);
	return 0;
}

/*
** Returns a count of the number of characters (single byte) in the message header
** Returns -1 on failure
**
** Arguments:
** msg - the message with the header to compute the length of
*/
int getHeaderSize(char * msg){
	if(msg == NULL){
		fprintf(stderr, "ERR: NULL msg received\n");
		return -1;
	}
	int size = 0;
	while(*(msg + sizeof(char) * size) != '\n'){
		if(*(msg + sizeof(char) * size) == '\0'){
			fprintf(stderr, "ERR: Expected \\n before end of string\n");
			return -1;
		}
		size++;
	}
	return size;
}

/*
** Reads num_bytes worth of content from the file, file_name into the buffer file_str
** Returns -1 on failure and 0 on success
** 
** Arguments: 
** file_str - the destination for the content being read from the file (will be NULL terminated)
** file_name - the file whose content will be read
** num_bytes - the number of bytes to read from the file
*/
int readFileIntoString(char * file_str, char * file, long int num_bytes){
	FILE * fp = fopen(file, "r");
	if(fp == NULL){
		perror("fopen");
		fprintf(stderr, "Could not open file: %s\n", file);
		return -1;
	}
	size_t read = fread(file_str, 1, num_bytes, fp);
	if(read < num_bytes){
		fprintf(stderr, "Error reading file: Expected to read %ld bytes but got %d bytes\n", num_bytes, (int)read);
		return -1;
	}
	*(file_str + sizeof(char) * num_bytes) = '\0';
	int closed = fclose(fp);
	if(closed == EOF){
		perror("fclose");
		return -1;
	}
	return 0;
}

/*
** Opens and connects a socket to the given port
** Returns -1 on failure and 0 on success
** NOTE: my_sock will be set on success
** 
** Arguments:
** port - the port to connect the socket on 
*/
int createAndConnectSocket(in_port_t port){
	int sock = socket(PF_INET, SOCK_STREAM, 0);
	if(sock == -1){
		perror("socket");
		return -1;
	}

	struct sockaddr_in sockaddr;
	sockaddr.sin_family = AF_INET;
	sockaddr.sin_port = htons(port);
	sockaddr.sin_addr.s_addr = htonl(INADDR_ANY);
	int connected = connect(sock, (struct sockaddr *)&sockaddr, sizeof(sockaddr));
	if(connected == -1){
		perror("connect");
		return -1;
	}
	my_sock = sock;
	return 0;
}

/*
** Sends PREPARE command to robot using given socket file descriptor
** Returns -1 on failure, 0 on success
**
** Arguments:
** sock - the file descriptor for a socket for which to send the command
** script_name - the name of the script being prepared
** script - the contents of the script being prepared
*/
int writePrepare(int sock, char * script_name, char * script){
	int length = strlen(script);
	char content_length [10] = {'\0'};
	sprintf(content_length, "%d", length);

	int expected_size = strlen("PREPARE\n") + strlen("name:") + strlen(script_name)
			+ strlen("\n") + strlen("content-length:") + strlen(content_length) + strlen("\n\n") + strlen(script) + 1;
	char * prepare_str = malloc(sizeof(char) * expected_size);
	if(prepare_str == NULL){
		perror("malloc");
		return -1;
	}
	strcpy(prepare_str, "PREPARE\n");
	strcat(prepare_str, "name:");
	strcat(prepare_str, script_name);
	strcat(prepare_str, "\n");
	strcat(prepare_str, "content-length:");
	strcat(prepare_str, content_length);
	strcat(prepare_str, "\n\n");
	strcat(prepare_str, script);

	int sent = sendMessage(sock, prepare_str);
	free(prepare_str);
	if(sent == -1){
		return -1;
	}
	return 0;
}

/*
** Sends START command to robot using given socket file descriptor
** Returns -1 on failure, 0 on success
**
** Arguments:
** sock - the file descriptor for a socket for which to send the command
** script_name - the name of the script being started
*/
int writeStart(int sock, char * script_name){
	int size = strlen("START\n") + strlen("name:") + strlen(script_name) + strlen("\n\n") + 1;
	char * start_str = malloc(sizeof(char) * size);
	if(start_str == NULL){
		perror("malloc");
		return -1;
	}
	strcpy(start_str, "START\n");
	strcat(start_str, "name:");
	strcat(start_str, script_name);
	strcat(start_str, "\n\n");

	int sent = sendMessage(sock, start_str);
	free(start_str);
	if(sent == -1){
		return -1;
	}
	return 0;
}

/*
** Sends ABORT command to robot using given socket file descriptor
** Returns -1 on failure, 0 on success
**
** Arguments:
** sock - the file descriptor for a socket for which to send the command
** script_name - the name of the script being aborted
*/
int sendMessage(int sock, char * msg){
	int sent = send(sock, msg, strlen(msg), 0);
	if(sent == -1){
		perror("sent");
		return -1;
	}
	else if(sent < strlen(msg)){
		fprintf(stderr, "Error sending message: Expected to send %d bytes but only sent %d bytes", (int)strlen(msg), sent);
		return -1;
	}
	return sent;
}

/*
** Sends ABORT command to robot using given socket file descriptor
** Returns -1 on failure, 0 on success
**
** Arguments:
** sock - the file descriptor for a socket for which to send the command
** script_name - the name of the script being aborted
*/
int writeAbort(int sock, char * script_name){
	int size = strlen("ABORT\n") + strlen("name:") + strlen(script_name) + strlen("\n\n") + 1;
	char * abort_str = malloc(sizeof(char) * size);
	if(abort_str == NULL){
		perror("malloc");
		return -1;
	}
	strcpy(abort_str, "ABORT\n");
	strcat(abort_str, "name:");
	strcat(abort_str, script_name);
	strcat(abort_str, "\n\n");

	int sent = sendMessage(sock, abort_str);
	free(abort_str);
	if(sent == -1){
		return -1;
	}
	return 0;
}

/*
** Returns the content of the provided message
** Returns NULL on failure
** 
** Arguments:
** msg - the message with the content to extract
*/
char * readContent(char * msg){
	int start_idx, len;
	start_idx = len = 0;
	while(*(msg + sizeof(char) * start_idx) != '\n' || *(msg + sizeof(char) * (start_idx + 1)) != '\n'){
		start_idx++;
	}
	start_idx += 2;
	while(*(msg + sizeof(char) * start_idx + len) != '\0'){
		len++;
	}
	char * content = malloc(sizeof(char) * (len + 1));
	if(content == NULL){
		perror("malloc");
		return NULL;
	}
	memcpy(content, msg + sizeof(char) * start_idx, len + 1);
	return content;
}

/*
** Reads message of unknown size using given socket file descriptor
** Returns the message read
** Returns NULL on failure
**
** Arguments:
** sock - the file descriptor for a socket for which to read from
*/
char * readMessage(int sock){
	char buf[256];
	int size = 512;
	int copied = 0;
	char * big_buf = malloc(sizeof(char) * size);
	if(buf == NULL){
		perror("malloc");
		return NULL;
	}
	int recvd;
	do{
		recvd = recv(sock, buf, sizeof(buf), 0);
		if(recvd == -1){
			perror("recv");
			free(big_buf);
			return NULL;
		}
		if(size - copied - 1 < recvd){
			// grow big buffer
			size *= 2;
			char * tmp = malloc(sizeof(char) * size);
			if(tmp == NULL){
				perror("malloc");
				free(big_buf);
				return NULL;
			}
			memcpy(tmp, big_buf, size/2);
			free(big_buf);
			big_buf = tmp;
		}
		memcpy(big_buf + sizeof(char) * copied, buf, recvd);
		copied += recvd;
	}while(recvd >= 256);
	*(big_buf + copied) = '\0';
	return big_buf;
}

/*
** Counts the number of bytes worth of content are present in the given file 
** Returns number of bytes in file on success, -1 on failure
** 
** Arguments:
** file - the file for which the number of bytes of content will be counted
*/
long int countBytesInFile(char * file){
	FILE * fp = fopen(file, "r");
	if(fp == NULL){
		perror("fopen");
		fprintf(stderr, "Could not open file: %s\n", file);
		return -1;
	}
	int seek = fseek(fp, 0L, SEEK_END);
	if(seek != 0){
		perror("fseek");
		return -1;
	}
	long int size = ftell(fp);
	if(size == -1){
		perror("ftell");
		return -1;
	}
	int closed = fclose(fp);
	if(closed == EOF){
		perror("fclose");
		return -1;
	}
	return size;
}

/*
** Establishes communication with the robot and prepares it to run a test against the given script
** Returns the contents of the given script on success
** Returns NULL if error occurs
** NOTE: my_sock will be set on success
**
** Arguments:
** file_name - the name of the script (must be located in ./scripts/ .rpt extension assumed),
*/
char * prepareRobot(char * file_name){
	int created = createAndConnectSocket(PORT);
	if(created == -1){
		return NULL;
	}
	
	char * file_str = readFileIntoStringWrapper(file_name);
	if(file_str == NULL){
		int closed = close(my_sock);
		if(closed == -1){
			perror("close");
		}
		return NULL;
	}

	int prepared = writePrepare(my_sock, file_name, file_str);
	if(prepared == -1){
		free(file_str);
		int closed = close(my_sock);
		if(closed == -1){
			perror("close");
		}
		return NULL;
	}
	
	event last_event = INIT;
	char * msg;
	while(last_event != STARTED && last_event != ERROR){
		msg = readMessage(my_sock);
		if(msg == NULL){
			free(file_str);
			int closed = close(my_sock);
			if(closed == -1){
				perror("close");
			}
			return NULL;
		}
		int classified = classifyMessage(&last_event, msg);
		free(msg);
		if(classified == -1){
			free(file_str);
			int closed = close(my_sock);
			if(closed == -1){
				perror("close");
			}
			return NULL;
		}
		if(last_event == PREPARED){
			int started = writeStart(my_sock, file_name);
			if(started == -1){
				free(file_str);
				int closed = close(my_sock);
				if(closed == -1){
					perror("close");
				}
				return NULL;
			}
		}
		else if(last_event == ERROR){
			fprintf(stderr, "something went wrong during prepare robot: unexpected state error received from robot server\n%s\n", msg);
			free(file_str);
			int closed = close(my_sock);
			if(closed == -1){
				perror("close");
			}
			return NULL;
		}
	}
	return file_str;
}

/*
** Handles communication of robot during execution of script until
** an ERROR is received or the FINISHED state is reached. Returns a
** pointer to a result structure with the results of the robot test execution
**
** Arguments:
** sock - socket file descriptor for communication with the robot
*/
char * handleControl(int * sock){
	event last_event = STARTED;
	char * msg;
	while(last_event != FINISHED && last_event != ERROR){
		msg = readMessage(*sock);
		if(msg == NULL){
			return NULL;
		}
		int classified = classifyMessage(&last_event, msg);
		if(classified == -1){
			free(msg);
			return NULL;
		}
	}
	char * content = readContent(msg);
	free(msg);
	if(content == NULL){
		return NULL;
	}
	return content;
}

/*
** Signal handler for timeout
** Aborts the robot script execution and finishes up the test
*/
void catch_alarm (int sig)
{
	printf("Test has timed out...stopping now\n");

	int aborted = writeAbort(my_sock, my_file_name);
	if(aborted == -1){
		fprintf(stderr, "Error aborting script after timeout\n");
		exit(EXIT_FAILURE);
	}
}

/*
** Reads the contents of script with the given file name (expected .rpt extension and located in ./scripts)
** Returns the contents of the script in a string
** Returns NULL if error occurs
**
** Arguments:
** file_name - the name of the file whose contents will be read (expected .rpt extension and located in ./scripts)
*/
char * readFileIntoStringWrapper(char * file_name){
	int size = strlen(SCRIPTS_LOCATION) + strlen(file_name) + strlen(FILE_EXT) + 1;
	char * file = malloc(size * sizeof(char));
	if(file == NULL){
		perror("malloc");
		return NULL;
	}
	strcpy(file, SCRIPTS_LOCATION);
	strcat(file, file_name);
	strcat(file, FILE_EXT);
	long int num_bytes = countBytesInFile(file);
	if(num_bytes == -1){
		free(file);
		return NULL;
	}
	char * file_str = malloc(num_bytes + 1);
	if(file_str == NULL){
		perror("malloc");
		free(file);
		return NULL;
	}
	int read = readFileIntoString(file_str, file, num_bytes);
	if(read == -1){
		fprintf(stderr, "Failed to read file into string");
		free(file);
		free(file_str);
		return NULL;
	}
	free(file);
	return file_str;
}

/*
** Executes the given client code against the provided robot script and
** returns the expected script and actual script in a result structure.
** Returns NULL if error occurs
** NOTE: my_file_name is set here
**
** Arguments:
** file_name - the name of the script (must be located in ./scripts/ .rpt extension assumed),
** func - function pointer (function where your client code is, NULL if none), 
** seconds - timeout (set <= 0 for no timeout)
*/
result * robotTest(char * file_name, void * func, int seconds){
	my_file_name = file_name;

	if(seconds > 0){
		signal(SIGALRM, catch_alarm);
		alarm(seconds);
	}

	char * file_str = prepareRobot(file_name);
	if(file_str == NULL){
		fprintf(stderr, "Failed to prepare the robot: Is the robot running?\n");
		free(file_str);
		int closed = close(my_sock);
		if(closed == -1){
			perror("closed");
		}		
		return NULL;
	}

	char * actual;
	if(func == NULL){
		actual = handleControl(&my_sock);
	}
	else{
		actual = doThreaded((void *)&handleControl, &my_sock, func, NULL);
	}

	if(actual == NULL){
		free(file_str);
		int closed = close(my_sock);
		if(closed == -1){
			perror("closed");
		}
		return NULL;
	}

	result * res = malloc(sizeof(result));
	if(res == NULL){
		perror("malloc");
		free(file_str);
		int closed = close(my_sock);
		if(closed == -1){
			perror("closed");
		}
		return NULL;
	}
	int closed = close(my_sock);
	if(closed == -1){
		perror("closed");
	}
	res->actual_script = actual;
	res->expected_script = file_str;
	return res;
}

/*
** Returns a newly created pthread_t * with default attributes and the given 
** start routine and argument
** Returns NULL on failure
**
** Arguments:
** func - the start routine of the thread
** arg - the argument of the start routine of the thread
*/
pthread_t * createThread(void * func, void * arg){
	pthread_t * thread = malloc(sizeof(pthread_t));
	if(thread == NULL){
		perror("malloc");
		return NULL;
	}
	int res = pthread_create(thread, NULL, func, arg);
	if(res != 0){
		perror("pthread_create");
		free(thread);
		return NULL;
	}
	return thread;
}

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
char * doThreaded(void * func1, void * arg1, void * func2, void * arg2){
	pthread_t * communication_thread = createThread(func1, arg1);
	if(communication_thread == NULL){
		return NULL;
	}
	pthread_t * client_thread = createThread(func2, arg2);
	if(client_thread == NULL){
		free(communication_thread);
		return NULL;
	}

	char * communication_thread_val = malloc(sizeof(char));
	if(communication_thread_val == NULL){
		perror("malloc");
		free(communication_thread);
		free(client_thread);
		return NULL;
	}
	char * tmp = communication_thread_val;
	pthread_join(*communication_thread, (void **)&communication_thread_val);
	pthread_join(*client_thread, NULL);
	free(communication_thread);
	free(client_thread);
	free(tmp);
	return communication_thread_val;
}

/*
** Frees the memory associated with the given result struct pointer
** Does nothing if result is NULL 
**
** Arguments:
** result - the result struct * to be free'd 
*/
void robotFree(result * result){
	if(result != NULL){
		free(result->actual_script);
		free(result->expected_script);
		free(result);
	}
}
