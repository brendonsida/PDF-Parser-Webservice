SRC=src
WWW=www
JAR=jars
TABULA=$(SRC)/target

all: server

server: 
	javac -cp $(JAR)/*:. $(SRC)/*.java
	
cleaninstall: clean
	rm -fr $(TABULA)

clean:
	rm -fr $(SRC)/*.class $(WWW)/*.pdf $(WWW)/*.csv

