#!/bin/bash
cd /vagrant/test
make clean
sh run_tests.sh > /vagrant/target/test_results.txt
make clean
if grep --quiet FAILED /vagrant/target/test_results.txt
	then
		echo Tests Failed
else
	cp -r /vagrant/lib/libc-robot-control.so /vagrant/target/
fi
cd /vagrant/
make clean
rm robot.jar
