#!/bin/bash

cd `dirname ${BASH_SOURCE[0]}`

# the ammount of memory depends on the amount of data in the fulltext engine. 
# you can decrease it if you haven't imported a lot of countries
java -Dfile.encoding=UTF-8 -Xmx4G -Xms512m -jar start.jar
