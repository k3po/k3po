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

CC = g++
CFLAGS = -Wall -g -fPIC
LFLAGS = -Wall -g -lpthread -lgtest -lc
OBJS = $(OBJDIR)/robot_control.o $(OBJDIR)/robot_control_helper.o 
OBJDIR = lib
LIB_SONAME = libc-robot-control.so
LIBTARGET = $(OBJDIR)/$(LIB_SONAME)
VERSION = 1.0.0

prefix	:= /usr/local

all : $(LIBTARGET)

clean : 
	rm -rf $(OBJDIR)

$(LIBTARGET) : $(OBJS)
	$(CC) -shared -o $(LIBTARGET) $(CFLAGS) $(LFLAGS) $(OBJS)

$(OBJS): | $(OBJDIR)

$(OBJDIR):                
	mkdir $(OBJDIR)

$(OBJDIR)/robot_control.o: src/robot_control.c
	gcc $(CFLAGS) -c src/robot_control.c -o $@
$(OBJDIR)/robot_control_helper.o: src/robot_control_helper.cpp
	$(CC) $(CFLAGS) -c src/robot_control_helper.cpp -o $@

install : $(LIBTARGET)
	test -d $(prefix) || mkdir $(prefix)
	test -d $(prefix)/include || mkdir $(prefix)/include
	test -d $(prefix)/lib || mkdir $(prefix)/lib
	if [ -a $(prefix)/$(LIBTARGET) ]; then rm $(prefix)/$(LIBTARGET); fi;
	cp $(LIBTARGET) $(prefix)/$(LIBTARGET).$(VERSION)
	cp src/robot_test.h $(prefix)/include
	cd $(prefix)/lib;ln -s $(LIB_SONAME).$(VERSION) $(LIB_SONAME)
