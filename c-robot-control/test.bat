@ECHO OFF
ECHO Checking if Vagrant and VirtualBox are installed...
vagrant -v > vagrant_version 2>&1
SET vagrant=%errorlevel%

IF %vagrant% == 0 (
ECHO Vagrant Version:
TYPE vagrant_version
) ELSE (
ECHO Vagrant is not installed
)
DEL vagrant_version

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
		XCOPY ..\cli\target\robot.jar . /-Y > NUL 2>&1 
		vagrant up
		ECHO Starting tests...
		vagrant ssh -c "sh /vagrant/wrap_run_tests.sh"
		vagrant destroy -f
		ECHO =====TEST RESULTS=====
		TYPE .\target\test_results.txt
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

:CHECK_TEST_RESULTS
FINDSTR FAILED .\target\test_results.txt > NUL 2>&1
SET failed=%errorlevel%
IF %failed% == 0 (
	GOTO :EXIT_FAIL_TESTS
) ELSE (
	GOTO :CHECK_PASSED
)

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