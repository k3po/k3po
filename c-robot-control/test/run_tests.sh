#!/bin/bash
mkdir ./tmp

# start robot
nohup java -jar ../robot.jar start background &> /dev/null &
sleep 5
echo Robot Started

# store robot pid
echo $! >> ./tmp/robot_pid

# build tests and run
make
./example-tests

# kill robot process
kill -9 $(cat ./tmp/robot_pid)

echo Robot Stopped
rm -r ./tmp
