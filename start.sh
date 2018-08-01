#!/usr/bin/env bash

#if [[ "biomine" != $(whoami) ]] ; then
#    echo "Please run this script as the biomine user."
#    exit 1
#fi

LOG=/usr/local/biomine/biomine.log

while [[ -n "$1" ]] ; do
    case $1 in
	clean)
	    echo "Removing log file $LOG"
	    if [[ -e $LOG ]] ; then rm $LOG ; fi
	    ;;
	solr)
	    cd /usr/local/solr-6.6.0
	    echo "Starting solr"
	    sudo -u solr bin/solr start -cloud -p 8983 -s example/cloud/node1/solr
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
sudo -u biomine nohup java -Xmx8G -Dconfig.properties -jar ./biomine-service/target/biomine-service-1.0-SNAPSHOT.jar > /usr/local/biomine/biomine.log &

tail -f /usr/local/biomine/biomine.log


