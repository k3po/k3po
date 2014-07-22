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

#include <gtest/gtest.h>
#include "robot_test.h"

extern "C" {
    #include <netinet/in.h>
    #include <sys/socket.h>
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
	**	timeout (in seconds, set <= 0 for no timeout) 
	*/
	ROBOT_TEST("exampleScript", echo_test, NULL, 10);
}

TEST(Example, Test2){
	ROBOT_TEST("exampleScript2", alt_echo_test, NULL, 10);
}


/*
** Simple echo test
** Receive message "Testing...123" and send it back
*/
void alt_echo_test(){
	int sock = socket_connect(8001);	
	ASSERT_TRUE(sock != -1);
	char * expected_msg = (char *)"Testing...123";
	char * recv_msg = (char *)malloc(strlen(expected_msg) + 1);
	int recvd = recv(sock, recv_msg, strlen(expected_msg), 0);
	ASSERT_TRUE(recvd == (int)(strlen(expected_msg)));
	*(recv_msg + recvd) = '\0';
	ASSERT_STREQ(expected_msg, recv_msg);
	int sent = send(sock, recv_msg, strlen(recv_msg), 0);
	ASSERT_TRUE(sent == (int)(strlen(recv_msg)));
	int closed = close(sock);
	ASSERT_TRUE(closed != -1);
	free(recv_msg);
}

/*
** Simple echo test
** Send message "Hello, world!" and receive it back
*/
void echo_test(){
	int sock = socket_connect(8001);
	ASSERT_TRUE(sock != -1);
	char * send_msg = (char *)"Hello, world!";
	int sent = send(sock, send_msg, strlen(send_msg), 0); 	
	ASSERT_TRUE(sent == (int)(strlen(send_msg)));
	char * recv_msg = (char *)malloc(strlen(send_msg) + 1);
	int recvd = recv(sock, recv_msg, strlen(send_msg), 0);
	ASSERT_TRUE(recvd == (int)(strlen(send_msg)));
	*(recv_msg + recvd) = '\0';
	ASSERT_STREQ(send_msg, recv_msg);
	int closed = close(sock);
	ASSERT_TRUE(closed != -1);
	free(recv_msg);
}

/*
** Opens socket on given port and connects
*/
int socket_connect(in_port_t port){
	int sock = socket(PF_INET, SOCK_STREAM, 0);
	if(sock == -1){
		return -1;
	}
	
	struct sockaddr_in sockaddr;
	sockaddr.sin_family = AF_INET;
	sockaddr.sin_port = htons(port);
	sockaddr.sin_addr.s_addr = htonl(INADDR_ANY);
	int connected = connect(sock, (struct sockaddr *)&sockaddr, sizeof(sockaddr));
	if(connected == -1){
		return -1;
	}
		
	return sock;
}
