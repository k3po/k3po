@ECHO OFF
ECHO Checking if Vagrant and VirtualBox are installed...
vagrant -v > vagrant_version 2>&1
set vagrant=%errorlevel%

IF %vagrant% == 0 (
TYPE vagrant_version
) ELSE (
ECHO Vagrant is not installed
)
del vagrant_version

"C:\Program Files\Oracle\VirtualBox\VBoxManage.exe" -v > virtualbox_version 2>&1
set virtualbox=%errorlevel%

IF %virtualbox% == 0 (
	set vara=VirtualBox Version 
	set /p varb= < virtualbox_version
	set newvar=%vara%%varb%
	ECHO %newvar%
) ELSE (
	ECHO Virtualbox is not installed
)
del virtualbox_version 

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
		goto :CHECK_TEST_RESULTS
	) ELSE (
		ECHO [WARNING]Tests cannot be run in this environment...nothing will be built
	)
) ELSE (
	ECHO [WARNING]Tests cannot be run in this environment...nothing will be built
)

:EXIT_SUCCESS
EXIT /B 0

:CHECK_TEST_RESULTS
findstr FAILED .\target\test_results.txt
set found=%errorlevel%
IF %found% == 0 (
	goto :EXIT_FAIL_TESTS
) ELSE (
 	echo Tests Passed
	goto EXIT_SUCCESS
)

:EXIT_FAIL_TESTS
ECHO Tests failed...Failing Build
EXIT /B 1