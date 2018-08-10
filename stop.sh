#!/usr/bin/env bash

# See http://stackoverflow.com/questions/59895/getting-the-source-directory-of-a-bash-script-from-within
SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
    DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
    SOURCE="$(readlink "$SOURCE")"
    [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"

PID_FILE=$DIR/biomine.pid

if [[ ! -e $PID_FILE ]] ; then
    echo "The bioMine service is not running"
    exit
fi
PID=`cat $PID_FILE`
echo "Killing process $PID"
kill -9 $PID
rm $PID_FILE


