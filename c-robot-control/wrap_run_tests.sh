#  Copyright (c) 2014 "Kaazing Corporation," (www.kaazing.com)
#
#  This file is part of Robot.
#
#  Robot is free software: you can redistribute it and/or modify
#  it under the terms of the GNU Affero General Public License as
#  published by the Free Software Foundation, either version 3 of the
#  License, or (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU Affero General Public License for more details.
#
#  You should have received a copy of the GNU Affero General Public License
#  along with this program. If not, see <http://www.gnu.org/licenses/>.

#!/bin/bash

# Run script to execute tests and store output in file
cd /vagrant/test
make clean
sh run_tests.sh > /vagrant/target/test_results.txt
make clean

# Check if tests failed, passed or timed out
if grep --quiet FAILED /vagrant/target/test_results.txt
	then echo Tests Failed 
else
	if grep --quiet PASSED /vagrant/target/test_results.txt
		then
			# Copy over header files and shared library to /lib and /include in target directory
			echo Tests Passed
			mkdir /vagrant/target/lib
			mkdir /vagrant/target/include
			cp /vagrant/lib/libc-robot-control.so /vagrant/target/lib
			cp /vagrant/src/robot_test.h /vagrant/target/include/
	else
		echo Tests Timed Out
	fi
fi

# Clean-up
cd /vagrant/
make clean
rm robot.jar
