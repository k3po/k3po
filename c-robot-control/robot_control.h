#ifndef ROBOT_CONTROL_H_
#define ROBOT_CONTROL_H_

#include <netinet/in.h>

#define PORT 11642
#define FILE_EXT ".rpt"

typedef enum {INIT, PREPARED, STARTED, FINISHED, ERROR} event;
typedef struct {
	char * actual_script;
	char * expected_script;
} result;

result * robotTest(char * file_name, void * func, int seconds);
int prepareRobot(char * file_name);
int createAndConnectSocket(in_port_t port);
char * readFileIntoStringWrapper(char * file_name);
int readFileIntoString(char * file_str, char * file_name, long int num_bytes);
long int countBytesInFile(char * file);
int writePrepare(int sock, char * script_name, char * script);
int writeStart(int sock, char * script_name);
int writeAbort(int sock, char * script_name);
int sendMessage(int sock, char * msg);
char * readMessage(int sock);
int containsDuplicate(char * msg);
char * getFirst(char * msg);
int classifyMessage(event * evt, char * msg);
int getHeaderSize(char * msg);
char * readContent(char * msg);
char * doThreaded(void * func1, void * arg1, void * func2, void * arg2);
pthread_t * createThread(void * func, void * arg);
char * doSequential(int * sock);
char * handleControl(int * sock);
void catch_alarm (int sig);
void robotFree(result * result);

#endif
