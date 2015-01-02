#!/bin/bash
# This script is a simple respawn daemon for those of us who dont want
# to deal with the /etc/event.d, monit etc...
#
# file: respawn.sh
# usage: /path/respawn.sh [program name] [sleeptime] 
#
# when the program closes, logger will display a message in a terminal
# and log the message including the programs PID to the syslog service
# (see file /var/log/syslog)


if [ "$1" == "" ] || [ "$2" == "" ]; then
  echo error: not enough parameters given.
  echo usage: respawn.sh [program name] [sleep time]
  exit 1
fi

PNAME=$1
STIME=$2

while [ true ]

do
	sleep $STIME
#	if ps ax | grep -v grep | $PNAME > /dev/null
	netstat -tnlp 2>&1| grep 8080 > /dev/null
	launched=$?
	echo "status ="$launched
	if [ $launched -eq 0  ];
	then
		logger -i -s -t respawn.sh "$PNAME is started. Re-checking in $STIME seconds."
	else
		$PNAME &
	fi
done
