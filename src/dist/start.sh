#!/bin/bash

SCRIPTDIR=$(dirname ${BASH_SOURCE[0]})
cd $SCRIPTDIR
source config.sh

LOG_FILE="./logs/$NAME.log"
PID_DIR=./run
mkdir -p $PID_DIR
PIDFILE=$PID_DIR/$NAME.pid

if [ -f $PIDFILE ]; then
 printf "%s\n" "$NAME is already started"
 exit 0;
fi

# the ammount of memory depends on the amount of data in the fulltext engine. 
# you can decrease it if you haven't imported a lot of countries
touch $LOG_FILE
echo "$NAME starting ..."
echo "Logs are outptut to $LOG_FILE"
echo "Use the stop.sh script (in the same directory) to shutdown"
java -DSTOP.PORT=8079 -DSTOP.KEY=stopkey -Dfile.encoding=UTF-8 -Xmx4G -Xms512m -jar start.jar > $LOG_FILE 2>&1 &
PID=$!
#echo "Saving PID" $PID " to " $PIDFILE
if [ -z $PID ]; then
   printf "%s\n" "Fail"
else
   echo $PID > $PIDFILE
   printf "%s\n" "Ok"
fi




