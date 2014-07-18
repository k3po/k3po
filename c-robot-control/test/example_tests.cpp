#include <gtest/gtest.h>

#include "robot_control_helper.h"

extern "C" {
    #include "robot_control.h"
    #include <netinet/in.h>
    #include <stdio.h>
    #include <stdlib.h>
    #include <sys/socket.h>
    #include <string.h>
    #include <unistd.h>
}

void echo_test();
void alt_echo_test();
int socket_connect(in_port_t port);

TEST(Example, Test){
	/*	Arguments: 
	**	scriptName (located in ./scripts/, .rpt extension assumed), 
	**	functionPointer (function where your client code is, NULL if none), 
	**	functionPointer (function to any cleanup code you need to run after the clientCode, NULL if none) 
	**	Timeout (in seconds, set <= 0 for no timeout) 
	*/
	ROBOT_TEST("exampleScript", echo_test, NULL, 10);
}

TEST(Example, Test2){
	ROBOT_TEST("exampleScript2", alt_echo_test, NULL, 10);
}

void alt_echo_test(){
	int sock = socket_connect(8001);	
	char * expected_msg = (char *)"Testing...123";
	char * recv_msg = (char *)malloc(strlen(expected_msg) + 1);
	recv(sock, recv_msg, strlen(expected_msg) + 1, 0);
	ASSERT_STREQ(expected_msg, recv_msg);
	send(sock, recv_msg, strlen(recv_msg), 0);
	close(sock);
	free(recv_msg);
}

void echo_test(){
	int sock = socket_connect(8001);
	char * send_msg = (char *)"Hello, world!";
	send(sock, send_msg, strlen(send_msg), 0); 	
	char * recv_msg = (char *)malloc(strlen(send_msg) + 1);
	recv(sock, recv_msg, strlen(send_msg) + 1, 0);
	ASSERT_STREQ(send_msg, recv_msg);
	close(sock);
	free(recv_msg);
}


int socket_connect(in_port_t port){
	int sock = socket(PF_INET, SOCK_STREAM, 0);

	struct sockaddr_in sockaddr;
	sockaddr.sin_family = AF_INET;
	sockaddr.sin_port = htons(port);
	sockaddr.sin_addr.s_addr = htonl(INADDR_ANY);
	connect(sock, (struct sockaddr *)&sockaddr, sizeof(sockaddr));

	return sock;
}
