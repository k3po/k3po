#ifndef ROBOT_CONTROL_HELPER_H_
#define ROBOT_CONTROL_HELPER_H_

#define ROBOT_TEST(W, X, Y, Z) robotTestWrapper((char *)W, (void *)X, (void *)Y, (int)Z)

void robotTestWrapper(char * script, void * function, void * cleanup, int timeout);

#endif

