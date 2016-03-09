#!/bin/bash

mvn clean

mvn package

java -jar ./target/team.frontend.app-jar-with-dependencies.jar
