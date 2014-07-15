CC = g++
CFLAGS = -Wall -g -fPIC
LFLAGS = -Wall -g -lpthread -lgtest -lc -lkws_websocket
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
	cp src/robot_control.h src/robot_control_helper.h $(prefix)/include
	cd $(prefix)/lib;ln -s $(LIB_SONAME).$(VERSION) $(LIB_SONAME)
