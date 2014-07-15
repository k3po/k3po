CC = g++
CFLAGS = -Wall -c -g
LFLAGS = -Wall -g -lpthread -lgtest -lc -lc-robot-control
OBJS = example_tests.o Test.o

example-tests: $(OBJS)
	$(CC) $(OBJS) $(LFLAGS) -o example-tests

example_tests.o: example_tests.cpp
	$(CC) $(CFLAGS) example_tests.cpp

Test.o: Test.cpp
	$(CC) $(CFLAGS) Test.cpp

clean:
	rm -rf *.o *~ example-tests
