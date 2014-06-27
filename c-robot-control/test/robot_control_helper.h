#ifndef ROBOT_CONTROL_HELPER_H_
#define ROBOT_CONTROL_HELPER_H_

#define ROBOT_TEST(X, Y, Z) robotTestWrapper((char *)X, (void *)Y, (int)Z)

void robotTestWrapper(char * script, void * function, int timeout);

#endif
