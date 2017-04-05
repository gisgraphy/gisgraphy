#!/bin/bash
export MAVEN_OPTS='-Xmx4048m -Xms256m';
mvn clean test
