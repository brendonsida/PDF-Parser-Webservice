 #!/bin/bash
 
 javac -cp .:../jars/* *.java
 # javac -cp pdfbox-app-1.8.11.jar:. *.java
 java -cp .:../jars/* Server 
 # java -cp pdfbox-app-1.8.11.jar:. Server 
