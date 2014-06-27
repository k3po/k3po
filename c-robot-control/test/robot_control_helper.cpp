#include <gtest/gtest.h>

extern "C" {
    #include "robot_control.h"
}

void robotTestWrapper(char * script, void * function, int timeout){
	result * result = robotTest(script, function, timeout);
	ASSERT_TRUE(result != NULL);
	ASSERT_STREQ(result->actual_script, result->expected_script);
	robotFree(result);
}
