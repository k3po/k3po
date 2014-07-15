#!/bin/bash
mkdir ./tmp
cd ../../cli/target

# start robot
nohup java -jar robot.jar start background &> /dev/null &
sleep 1
echo Robot Started

# store robot pid
echo $! >> ../../c-robot-control/test/tmp/robot_pid

# build tests and run
cd ../../c-robot-control/test
make
./example-tests

# kill robot process
kill -9 $(cat ./tmp/robot_pid)

echo Robot Stopped
rm -r ./tmp
