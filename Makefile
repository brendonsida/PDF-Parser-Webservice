PROGS=app

all: $(PROGS)

app:
	cd tabula; \
	mvn clean compile assembly:single; \
	cp -Rv target ../test-suite/

clean:
	cd tabula; mvn clean
	cd test-suite; rm -fr target
