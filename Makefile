HEALTHCHECK=checkProjectDependencies
PROGS=app server
SERVER=WebserviceTest
SERVERSRC=src
SERVERSTORAGE=www
TABULA=tabula-java
TABULA_JAR=target
TESTSUITE=test-suite

all: $(PROGS)

app: 
	./$(HEALTHCHECK); \
	cd $(TABULA); \
	mvn clean compile assembly:single; \
	cp -Rv $(TABULA_JAR) ../$(TESTSUITE); \
	cp -Rv $(TABULA_JAR) ../$(SERVER)/$(SERVERSRC)

server:
	javac $(SERVER)/$(SERVERSRC)/Server.java

clean:
	cd $(TABULA); mvn clean
	cd $(SERVER); rm -fr $(SERVERSRC)/*.class $(SERVERSRC)/$(TABULA_JAR) $(SERVERSTORAGE)/*.pdf
	cd test-suite; rm -fr $(TABULA_JAR); ./runtestsuite clean
