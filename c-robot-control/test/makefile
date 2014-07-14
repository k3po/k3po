CC = g++
CFLAGS = -Wall -c -g
LFLAGS = -Wall -g -lpthread -lgtest -lc
OBJS = robot_control.o robot_control_helper.o example_tests.o Test.o

example-tests: $(OBJS)
	$(CC) $(OBJS) $(LFLAGS) -o example-tests

robot_control.o: robot_control.c robot_control.h
	gcc $(CFLAGS) robot_control.c

robot_control_helper.o: robot_control_helper.cpp robot_control_helper.h robot_control.h 
	$(CC) $(CFLAGS) robot_control_helper.cpp

example_tests.o: example_tests.cpp robot_control_helper.h robot_control.h
	$(CC) $(CFLAGS) example_tests.cpp

Test.o: Test.cpp
	$(CC) $(CFLAGS) Test.cpp

clean:
	rm -rf *.o *~ example-tests
