
Requirements:

C Robot Control requires libpthread and libc

Tests in ./test use the Google Test Framework and require libgtest (tested on 1.7.0).

The included tests are scripts to be run against the kaazing gateway.

In order to run them you must build the test executable with the included makefile. The robot server must be running on port 11642 and the kaazing gateway running and configured to accept on port 8001. The test executable can be run to run your scripts against the gateway. See ./test/example_tests.cpp and ./test/Test.cpp for examples on how to robot tests using the Google Test Framework.

