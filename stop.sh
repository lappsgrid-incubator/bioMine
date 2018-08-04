#!/usr/bin/env bash

PID_FILE=/usr/local/biomine/biomine.pid

if [[ ! -e $PID_FILE ]] ; then
    echo "The bioMine service is not running"
    exit
fi
PID=`cat $PID_FILE`
echo "Killing process $PID"
kill -9 $PID
rm $PID_FILE


