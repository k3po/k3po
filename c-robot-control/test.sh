#!/bin/bash
echo "Checking if Vagrant and VirtualBox are installed..."
vagrant -v &> /dev/null
vagrant=$?
if [ ! $vagrant ]
	then
		echo vagrant -v
else
	echo "Vagrant is not installed"
fi

vboxmanage -v &> /dev/null
virtualbox=$?
if [ ! $virtualbox ]
	then
		echo "VirtualBox "$(vboxmanage -v)
else
	echo "VirtualBox is not installed"
fi

if [ ! $vagrant ]
	then if [ ! $virtualbox ]
		then
			echo "Both are installed"
			cp ../cli/target/robot.jar
			vagrant up
			echo "Starting tests..."
			vagrant ssh -c "sh /vagrant/wrap_run_tests.sh"
			# vagrant destroy -f
			echo "=====TEST RESULTS====="
			cat ./target/test_results.txt
			echo "Tests complete..."
			if grep --quiet FAILED .\target\test_results.txt
				then echo "Tests failed...Failing Build"
				exit 1
			else
				echo "Tests Passed"
			fi
	else
		echo "[WARNING]Tests cannot be run in this environment...nothing will be built"
	fi
else
	echo "[WARNING]Tests cannot be run in this environment...nothing will be built"
fi

exit 0
