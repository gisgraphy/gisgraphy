#!/bin/bash
export MAVEN_OPTS='-Xmx4048m -Xms256m -XX:MaxPermSize=512m';
mvn clean test
