#!/bin/bash
echo Starting Setup...

yum -y install glibc
yum -y install gcc
yum -y install gcc-c++
yum -y install git
yum -y install cmake
yum -y install unzip

cd /home/vagrant
wget -O jdk-7u60-linux-x64.rpm --no-check-certificate --no-cookies --header "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/7u60-b19/jdk-7u60-linux-x64.rpm
rpm -Uvh jdk-7u60-linux-x64.rpm
rm jdk-7u60-linux-x64.rpm
wget http://supergsego.com/apache/maven/maven-3/3.0.5/binaries/apache-maven-3.0.5-bin.tar.gz
gtar -xf apache-maven-3.0.5-bin.tar.gz
rm apache-maven-3.0.5-bin.tar.gz
mv apache-maven-3.0.5 /usr/local
echo 'export M2_HOME=/usr/local/apache-maven-3.0.5' >> /etc/profile.d/maven.sh
echo 'export M2=$M2_HOME/bin' >> /etc/profile.d/maven.sh
echo 'export PATH=$M2:$PATH' >> /etc/profile.d/maven.sh
source /etc/profile.d/maven.sh
wget https://googletest.googlecode.com/files/gtest-1.7.0.zip
unzip gtest-1.7.0.zip
rm gtest-1.7.0.zip
mkdir ./gtest-1.7.0/lib
cd ./gtest-1.7.0/lib
cmake ..
make 
mv libgtest_main.a /usr/local/lib
mv libgtest.a /usr/local/lib
cp -r ../include/gtest/ /usr/local/include/
cd ../..
rm -rf ./gtest-1.7.0

# requires kaaz-net and c-robot-control be checked out and in the shared vagrant folder
cd /home/vagrant
cp -r /vagrant/kaaz-net .
cp -r /vagrant/robot .
chown -R vagrant *
chgrp -R vagrant *
tr -d '\15\32' < ./robot/c-robot-control/test/run_tests.sh > ./robot/c-robot-control/test/tmp.sh
mv ./robot/c-robot-control/test/tmp.sh ./robot/c-robot-control/test/run_tests.sh
cd kaaz-net
mvn clean install
cd ../robot
mvn clean install -DskipITs

# install c-robot-control shared library
cd c-robot-control
make install

echo Setup Complete
