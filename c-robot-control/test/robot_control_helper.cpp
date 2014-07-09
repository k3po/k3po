#include <gtest/gtest.h>
#include "robot_control_helper.h"

extern "C" {
	#include "robot_control.h"
}

void robotTestWrapper(char * script, void * function, void * cleanup, int timeout){
	result * result = robotTest(script, function, timeout);
	ASSERT_TRUE(result != NULL);
	// use expect so can free mem after
	EXPECT_STREQ(result->expected_script, result->actual_script);
	robotFree(result);
	if(cleanup != NULL){
		((void (*)(void)) cleanup)();
	}
}

