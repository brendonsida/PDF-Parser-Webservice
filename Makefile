PROGS=app
TABULA=tabula-java

all: $(PROGS)

app:
	cd $(TABULA); \
	mvn clean compile assembly:single; \
	cp -Rv target ../test-suite/

clean:
	cd $(TABULA); mvn clean
	cd test-suite; rm -fr target; ./runtestsuite clean
