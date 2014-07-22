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
echo "Checking if Vagrant and VirtualBox are installed..."

# Check if vagrant command works to see if Vagrant is installed
vagrant -v &> /dev/null
vagrant=$?
if [ $vagrant -eq 0 ]
	then
		echo $(vagrant -v)
else
	echo "Vagrant is not installed"
fi

# Check if vboxmanage command works to see if VirtualBox is installed
vboxmanage -v &> /dev/null
virtualbox=$?
if [ $virtualbox -eq 0 ]
	then
		echo "VirtualBox "$(vboxmanage -v)
else
	echo "VirtualBox is not installed"
fi

if [ $vagrant -eq 0 ]
	then if [ $virtualbox -eq 0 ]
		then
			echo "Both are installed"
			
			# Copy over dependency
			cp ../cli/target/robot.jar .
			
			# Prepare vagrant environment and execute script over SSH
			vagrant up
			echo "Starting tests..."
			vagrant ssh -c "sh /vagrant/wrap_run_tests.sh"
			vagrant destroy -f
			
			# Show test results
			echo "=====TEST RESULTS====="
			cat ./target/test_results.txt
			echo "Tests complete..."
			
			# Fail build with error status 1 if tests failed (hard fail or timeout)
			if grep --quiet FAILED ./target/test_results.txt
				then echo "Tests failed...Failing Build"
				exit 1
			else
				if grep --quiet PASSED ./target/test_results.txt
					then 
						echo Tests Passed
				else
					echo "Tests failed due to timeout. Consider re-running with a higher timeout in example_tests.cpp"
					echo "Tests failed...Failing Build"
					exit 1
				fi
			fi
	else
		echo "[WARNING]Tests cannot be run in this environment...nothing will be built"
	fi
else
	echo "[WARNING]Tests cannot be run in this environment...nothing will be built"
fi

exit 0
