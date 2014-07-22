@ECHO OFF
REM   Copyright (c) 2014 "Kaazing Corporation," (www.kaazing.com)
REM
REM   This file is part of Robot.
REM
REM   Robot is free software: you can redistribute it and/or modify
REM   it under the terms of the GNU Affero General Public License as
REM   published by the Free Software Foundation, either version 3 of the
REM   License, or (at your option) any later version.
REM
REM   This program is distributed in the hope that it will be useful,
REM   but WITHOUT ANY WARRANTY; without even the implied warranty of
REM   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
REM   GNU Affero General Public License for more details.
REM
REM   You should have received a copy of the GNU Affero General Public License
REM   along with this program. If not, see <http://www.gnu.org/licenses/>.

ECHO Checking if Vagrant and VirtualBox are installed...

REM Check if vagrant command works to see if Vagrant is installed
vagrant -v > vagrant_version 2>&1
SET vagrant=%errorlevel%

IF %vagrant% == 0 (
ECHO Vagrant Version:
TYPE vagrant_version
) ELSE (
ECHO Vagrant is not installed
)
DEL vagrant_version

REM Check if vboxmanage command works to see if VirtualBox is installed (NOTE: assumes it was installed to default location)
"C:\Program Files\Oracle\VirtualBox\VBoxManage.exe" -v > virtualbox_version 2>&1
SET virtualbox=%errorlevel%
IF %virtualbox% == 0 (
	ECHO VirtualBox Version:
	TYPE virtualbox_version
) ELSE (
	ECHO Virtualbox is not installed
)
DEL virtualbox_version 

IF %vagrant% == 0 (
	IF %virtualbox% == 0 (
		REM Copy over dependency
		XCOPY ..\cli\target\robot.jar . /-Y > NUL 2>&1 
		
		REM Prepare vagrant environment and execute script over SSH
		vagrant up
		ECHO Starting tests...
		vagrant ssh -c "sh /vagrant/wrap_run_tests.sh"
		vagrant destroy -f
		
		REM Show test results
		ECHO =====TEST RESULTS=====
		TYPE .\target\test_results.txt
		
		REM Check test results
		ECHO Tests complete...
		GOTO :CHECK_TEST_RESULTS
	) ELSE (
		ECHO [WARNING]Tests cannot be run in this environment...nothing will be built
	)
) ELSE (
	ECHO [WARNING]Tests cannot be run in this environment...nothing will be built
)

:EXIT_SUCCESS
EXIT /B 0

REM Fail build with error level 1 if tests failed (hard fail or timeout)
:CHECK_TEST_RESULTS
FINDSTR FAILED .\target\test_results.txt > NUL 2>&1
SET failed=%errorlevel%
IF %failed% == 0 (
	GOTO :EXIT_FAIL_TESTS
) ELSE (
	GOTO :CHECK_PASSED
)

REM Fail build with error level 1 if tests failed due to timeout
:CHECK_PASSED
FINDSTR PASSED .\target\test_results.txt > NUL 2>&1
SET passed=%errorlevel%
IF %passed% == 0 (
	ECHO Tests Passed
	GOTO :EXIT_SUCCESS
) ELSE (
	ECHO Tests failed due to timeout. Consider re-running with a higher timeout in example_tests.cpp
	GOTO :EXIT_FAIL_TESTS
)

:EXIT_FAIL_TESTS
ECHO Tests failed...Failing Build
EXIT /B 1