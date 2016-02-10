PROGS=app

all: $(PROGS)

app:
	cd tabula-cmdline; \
	mvn clean compile assembly:single; \
	cp -Rv target ../test-suite/

clean:
	cd tabula-cmdline; mvn clean
	cd test-suite; rm -fr target; ./runtestsuite clean
