#!/bin/bash

mvn clean jetty:run-war -Dmaven.test.skip -Plocal
