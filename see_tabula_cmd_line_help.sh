#!/bin/sh

TAB_JAR="./tabula-java/target/tabula-0.8.0-jar-with-dependencies.jar"

make_target() {
    make
}

execute() {
    java -jar $TAB_JAR --help
}

if [ -e $TAB_JAR ]
then
    execute
else
    make_target
    execute
fi
