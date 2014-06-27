#include <gtest/gtest.h>

#include "robot_control_helper.h"

extern "C" {
    #include "robot_control.h"
}

void clientCode();

/* IF YOU HAVE CLIENT CODE TO RUN */
TEST(Example, Test){
	/* Arguments: scriptName, functionPointer (function where your client code is), Timeout (in seconds, set <= 0 for no timeout) */
	ROBOT_TEST("exampleScript", clientCode, 3);
}

/* IF YOU HAVE NO CLIENT CODE TO RUN */
TEST(Example, Test2){
	ROBOT_TEST("exampleScript2", NULL, -1);
}

void clientCode(){
	/* ADD CODE HERE */
}
