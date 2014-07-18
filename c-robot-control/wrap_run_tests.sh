#!/bin/bash
cd /vagrant/test
make clean
sh run_tests.sh > /vagrant/target/test_results.txt
make clean
if grep --quiet FAILED /vagrant/target/test_results.txt
	then echo Tests Failed 
else
	if grep --quiet PASSED /vagrant/target/test_results.txt
		then 
			echo Tests Passed
			mkdir /vagrant/target/lib
			mkdir /vagrant/target/include
			cp /vagrant/lib/libc-robot-control.so /vagrant/target/lib
			cp /vagrant/src/robot_control.h /vagrant/target/include/
			cp /vagrant/src/robot_control_helper.h /vagrant/target/include/
	else
		echo Tests Timed Out
	fi
fi
cd /vagrant/
make clean
rm robot.jar
