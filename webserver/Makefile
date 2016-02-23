SRC=src
WWW=www
TABULA=$(SRC)/target

all: server

server: 
	javac -cp $(SRC)/pdfbox-app-1.8.11.jar:. $(SRC)/*.java
	
cleaninstall: clean
	rm -fr $(TABULA)

clean:
	rm -fr $(SRC)/*.class $(WWW)/*.pdf $(WWW)/*.csv

