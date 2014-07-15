Quick Start Guide:

    Download and install VirtualBox: https://www.virtualbox.org/wiki/Downloads
    Download and install Vagrant: http://www.vagrantup.com/downloads.html
    Create a folder to work in
    Navigate to that folder
    Clone the following from github into your current directory
    -https://github.com/kaazing/kaaz-net
    -https://github.com/kaazing/robot (c-robot-control branch)
    Download the Vagrantfile and setup.sh from this repository
    Place the Vagrantfile and setup.sh in your current directory
    Run the command vagrant up from a terminal/command line in your current directory
    Run the command vagrant ssh in the same window
    Navigate to ~/robot/c-robot-control/test
    Run sh run_tests.sh for some example tests
    
C-Robot-Control as a Shared Library:

    Run "make install" from the c-robot-control directory
    This will install c-robot-control as a shared library at /usr/local
    To install at different directory, run "make install prefix=[path_to_install_directory]"

