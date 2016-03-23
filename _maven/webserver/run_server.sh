#!/bin/bash

mvn clean install

java -jar ./target/team.frontend.app-jar-with-dependencies.jar
