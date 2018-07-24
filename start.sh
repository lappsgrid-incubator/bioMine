#!/usr/bin/env bash

if [[ "biomine" != $(whoami) ]] ; then
    echo "Please run this script as the biomine user."
    exit 1
fi

java -Dconfig.properties -jar ./biomine-service/target/biomine-service-1.0-SNAPSHOT.jar

