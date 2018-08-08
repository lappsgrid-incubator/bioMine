#!/usr/bin/env bash
set -eu

for file in `cat index.txt` ; do
    echo "Posting $file"
    curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' http://localhost:8080/biomine/indexer/index/path/{doc}?collection=literature\&path=$file
    sleep 5
done
