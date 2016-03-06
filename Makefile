HEALTHCHECK=checkProjectDependencies
PROGS=app
SERVER=webserver
SERVERSRC=$(SERVER)/src
SERVERSTORAGE=www
TABULA=tabula-java
TABULA_JAR=target
TESTSUITE=test-suite

all: $(PROGS)

app: 
	# ./$(HEALTHCHECK)
	cd $(TABULA); mvn clean compile assembly:single
	cd $(SERVER); make

# server:
# 	javac $(SERVER)/$(SERVERSRC)/Server.java

clean:
	cd $(SERVER); make clean
	cd test-suite; ./runtestsuite clean

cleaninstall:
	cd $(TABULA); mvn clean
	cd $(SERVER); make cleaninstall
	cd test-suite; rm -fr $(TABULA_JAR); ./runtestsuite clean
