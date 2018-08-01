#!/usr/bin/env bash


/usr/local/zookeeper-3.4.6/bin/zkServer.sh stop
/usr/local/solr-6.6.0/bin/solr stop
rm -rf /usr/local/zoo-data
rm -rf /usr/local/solr

