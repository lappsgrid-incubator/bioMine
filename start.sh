#!/usr/bin/env bash

#if [[ "biomine" != $(whoami) ]] ; then
#    echo "Please run this script as the biomine user."
#    exit 1
#fi

LOG=/var/data/biomine/biomine.log
PID_FILE=/var/data/biomine/biomine.pid

if [[ -e $PID_FILE ]] ; then
    pid=$(cat $PID_FILE)
    echo "It appears the bioMine server is already running ($pid)"
    exit 1
fi

while [[ -n "$1" ]] ; do
    case $1 in
	clean)
	    echo "Removing log file $LOG"
	    if [[ -e $LOG ]] ; then rm $LOG ; fi
	    ;;
	solr)
	    cd /var/data/solr-7.4.0
	    echo "Starting solr"
	    sudo -u solr bin/solr start -cloud -p 8983 -m 8g
	    cd -
	    ;;
	*)
	    echo "Invalid option $1"
	    exit 1
	    ;;	
    esac
    shift
done

echo "Starting BioMine"
nohup java -Xmx1G -Dconfig.properties -jar ./biomine-service/target/biomine-service-1.0-SNAPSHOT.jar > $LOG &
echo $! > $PID_FILE

tail -f $LOG



