SRCS := $(wildcard *.c)
OBJS := $(SRCS:.c=.o)
# hello
HELLO_OBJS := $(filter hello%.o, $(OBJS))
TARGET_HELLO := hello
# targets
TARGET :=  $(TARGET_HELLO)
# varables used in yocto
bindir ?= /usr/bin

LIBS :=

.PHONY: clean all

all: $(TARGET)

%.o : %.c
	$(CC) -c $< -o $@ ${LDFLAGS} ${LIBS}

$(TARGET_HELLO) : $(OBJS)
	$(CC) $(HELLO_OBJS) -o $@ ${LIBS} ${LDFLAGS}

install:
	install -d $(DESTDIR)$(bindir)
	install -m 755 $(TARGET_HELLO) $(DESTDIR)$(bindir)/