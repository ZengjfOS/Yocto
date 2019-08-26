bindir ?= /usr/bin/

all:
	echo "adanXen"

install:
	install -d $(DESTDIR)$(bindir)
	touch $(DESTDIR)$(bindir)/adanXen
	#install -m 755 $(TARGET_HELLO) $(DESTDIR)$(bindir)/
