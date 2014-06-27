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

// RET 0 on success. -1 on error, -2 means no duplicate
int containsDuplicate(char * msg){
	int length = strlen(msg);
	if(length % 2 != 0){
		return -2;
	}
	char * msgA = malloc(sizeof(char) * (length/2 + 1));
	if(msgA == NULL){
		perror("malloc");
		return -1;
	}
	char * msgB = malloc(sizeof(char) * (length/2 + 1));
	if(msgB == NULL){
		perror("malloc");
		return -1;
	}
	memcpy(msgA, msg, length/2);
	*(msgA + length/2) = '\0';
	memcpy(msgB, msg + sizeof(char) * length/2, length/2);
	*(msgB + length/2) = '\0';
	if(strcmp(msgA, msgB) == 0){
		return 0;
	}
	return -2;
}

// RET NULL on failure/error. if has duplicate, returns part of msg w/ dupl. removed, if no duplicate, returns orig msg
char * getFirst(char * msg){
	int dupl = containsDuplicate(msg);
	if(dupl == 0){
		*(msg + strlen(msg)/2 * sizeof(char)) = '\0';
	}
	else if(dupl == -1){
		return NULL;
	}
	return msg;
}

// RET -1 if unrecognized header on msg, 0 on success. sets evt to appropriate event type: PREPARED, STARTED, FINISHED OR ERROR
int classifyMessage(event * evt, char * msg){
	int size = getHeaderSize(msg);
	if(size == -1){
		return -1;
	}
	char * header = malloc((size + 1) * sizeof(char));
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
		fprintf(stderr, "Unrecognized header in received message:\n Header: %s\nMessage: %s\n", header, msg);
		free(header);
		return -1;
	}
	free(header);
	return 0;
}

// RET -1 if null or is of invalid header format, else count of number of chars (single-byte) in the msg header
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

// RET -1 on failure, 0 on success reads num_bytes bytes into file_str from file (you must allocate num_bytes + 1 bytes to file_str)
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

// RET -1 on failure, 0 on success and value of my_sock is set
// note: my_sock is set here if successful
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

// RET -1 on failure, 0 on success. sends PREPARE command to given socket with given script_name and script
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
	if(sent == -1){
		free(prepare_str);
		return -1;
	}
	free(prepare_str);
	return 0;
}

// RET -1 on failure, 0 on success. writes START command to given socket
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
	if(sent == -1){
		free(start_str);
		return -1;
	}
	free(start_str);
	return 0;
}

// RET -1 on failure, # bytes sent on success
int sendMessage(int sock, char * msg){
	int sent = send(sock, msg, strlen(msg), 0);
	if(sent == -1){
		perror("sent");
		free(msg);
		return -1;
	}
	else if(sent < strlen(msg)){
		fprintf(stderr, "Error sending message: Expected to send %d bytes but only sent %d bytes", (int)strlen(msg), sent);
		free(msg);
		return -1;
	}
	return sent;
}

// RET -1 on failure, 0 on success. writes ABORT command to given socket
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
	if(sent == -1){
		free(abort_str);
		return -1;
	}
	free(abort_str);
	return 0;
}

// RET NULL on failure. allocates space for the message content and returns ptr to message content
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

// RET NULL on failure. Reads message of arbitrary size from given socket, allocates space for it and returns ptr to it
char * readMessage(int sock){
	char buf[256];
	int size = 512;
	int copied = 0;
	char * big_buf = malloc(sizeof(char) * size);
	if(buf == NULL){
		perror("malloc");
		return NULL;
	}
	ssize_t recvd;
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
			memcpy(tmp, big_buf, size/2);
			if(tmp == NULL){
				perror("malloc");
				free(big_buf);
				return NULL;
			}
			free(big_buf);
			big_buf = tmp;
		}
		memcpy(big_buf + sizeof(char) * copied, buf, recvd);
		copied += recvd;
	}while(recvd >= 256);
	*(big_buf + copied) = '\0';
	return getFirst(big_buf);
}

// RET -1 on failure, number of bytes in given file on success
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

// RET -1 on failure, 0 on success. sets up robot in preparation for test (get to started state)
// note: my_sock will also be established on success
int prepareRobot(char * file_name){
	int created = createAndConnectSocket(PORT);
	if(created == -1){
		return -1;
	}

	char * file_str = readFileIntoStringWrapper(file_name);
	if(file_str == NULL){
		return -1;
	}

	writePrepare(my_sock, file_name, file_str);

	event * last_event = malloc(sizeof(event));
	if(last_event == NULL){
		perror("malloc");
		free(file_str);
		return -1;
	}
	*last_event = INIT;
	char * msg;
	while(*last_event != STARTED && *last_event != ERROR){
		msg = readMessage(my_sock);
		if(msg == NULL){
			free(file_str);
			free(last_event);
			return -1;
		}
		classifyMessage(last_event, msg);
		if(*last_event == PREPARED){
			writeStart(my_sock, file_name);
		}
		else if(*last_event == ERROR){
			fprintf(stderr, "ERR: unexpected state error received from robot server\n%s\n", msg);
			free(file_str);
			free(last_event);
			free(msg);
			return -1;
		}
		free(msg);
	}
	free(file_str);
	free(last_event);
	return 0;
}

// RET NULL on failure, test result ptr on success (mem allocated, will be error if error recv'd, else actual script)
char * handleControl(int * sock){
	event * last_event = malloc(sizeof(event));
	if(last_event == NULL){
		perror("malloc");
		return NULL;
	}
	*last_event = STARTED;
	char * msg;
	while(*last_event != FINISHED && *last_event != ERROR){
		msg = readMessage(*sock);
		if(msg == NULL){
			free(last_event);
			return NULL;
		}
		int classified = classifyMessage(last_event, msg);
		if(classified == -1){
			free(last_event);
			free(msg);
			return NULL;
		}
	}
	if(*last_event == FINISHED || *last_event == ERROR){
		free(last_event);
		char * content = readContent(msg);
		free(msg);
		if(content == NULL){
			return NULL;
		}
		return content;
	}
	else{
		fprintf(stderr, "Final state is invalid: \n");
		free(last_event);
		free(msg);
		return NULL;
	}
}

// signal handler for timeout
void catch_alarm (int sig)
{
	printf("Test has timed out...stopping now\n");
	// stop waiting for finish and send abort
	writeAbort(my_sock, my_file_name);
}

// RET NULL on failure, ptr to file content string on success
// memory is allocated
char * readFileIntoStringWrapper(char * file_name){
	int size = strlen(file_name) + strlen(FILE_EXT) + 1;
	char * file = malloc(size * sizeof(char));
	if(file == NULL){
		perror("malloc");
		return NULL;
	}
	strcpy(file, file_name);
	strcat(file, FILE_EXT);
	long int num_bytes = countBytesInFile(file);
	char * file_str = malloc(num_bytes + 1);
	if(file_str == NULL){
		perror("malloc");
		free(file);
		return NULL;
	}
	int read = readFileIntoString(file_str, file, num_bytes);
	if(read == -1){
		fprintf(stderr, "Failed to read file into string\n");
		free(file);
		free(file_str);
		return NULL;
	}
	free(file);
	return file_str;
}

// RETURN NULL on failure, struct with results on success (set seconds <= 0 if want no timeout)
// func is a function ptr to test code to execute
// note: result_t struct and its contents need to be free'd
// note: my_file_name is set here
result * robotTest(char * file_name, void * func, int seconds){
	my_file_name = file_name;

	if(seconds > 0){
		signal(SIGALRM, catch_alarm);
		alarm(seconds);
	}

	int prepared = prepareRobot(file_name);
	if(prepared == -1){
		fprintf(stderr, "Failed to prepare the robot\n");
		return NULL;
	}
	char * file_str = readFileIntoStringWrapper(file_name);
	if(file_str == NULL){
		free(file_str);
		return NULL;
	}

	char * actual;
	if(func == NULL){
		actual = doSequential(&my_sock);
	}
	else{
		actual = doThreaded((void *)&handleControl, &my_sock, func, NULL);
	}

	if(actual == NULL){
		free(file_str);
		return NULL;
	}

	result * res = malloc(sizeof(result));
	if(res == NULL){
		perror("malloc");
		free(file_str);
		return NULL;
	}
	int closed = close(my_sock);
	if(closed == -1){
		perror("close");
		free(file_str);
		return NULL;
	}
	res->actual_script = actual;
	res->expected_script = file_str;
	return res;
}

// RET NULL on failure, test result ptr on success
char * doSequential(int * sock){
	return handleControl(sock);
}

// RET NULL on failure, pthread_t ptr on success (memory is allocated)
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

// RET NULL on failure, ptr to results on success
// execute control communication on 1 thread, client code on other
char * doThreaded(void * func1, void * arg1, void * func2, void * arg2){
	pthread_t * thread1 = createThread(func1, arg1);
	if(thread1 == NULL){
		free(thread1);
		return NULL;
	}
	pthread_t * thread2 = createThread(func2, arg2);
	if(thread2 == NULL){
		free(thread2);
		return NULL;
	}

	char * thread1_val = malloc(sizeof(char));
	char * tmp = thread1_val;
	if(thread1_val == NULL){
		perror("malloc");
		free(thread1);
		free(thread2);
		return NULL;
	}

	pthread_join(*thread1, (void **)&thread1_val);
	pthread_join(*thread2, NULL);
	free(thread1);
	free(thread2);
	free(tmp);
	return thread1_val;
}

// Frees the memory associated with the result struct at the end of the test
void robotFree(result * result){
	if(result == NULL){
		return;
	}
	free(result->actual_script);
	free(result->expected_script);
	free(result);
}
