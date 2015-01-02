#!/bin/sh

java -Djava.awt.headless=true -jar ../lib/ext/CMDRunner.jar --tool Reporter "$@"
