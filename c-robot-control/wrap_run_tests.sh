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
			cp -r /vagrant/lib/libc-robot-control.so /vagrant/target/
	else
		echo Tests Timed Out
	fi
fi
cd /vagrant/
make clean
rm robot.jar
