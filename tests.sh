#!/bin/bash
export MAVEN_OPTS='-Xmx2048m -Xms256m';
mvn clean test
