#!/usr/bin/env bash

mvn install:install-file -Dfile=./biomine-index/src/lib/bioLinker-1.0-SNAPSHOT.jar -DgroupId=csfg -DartifactId=bioLinker -Dversion=1.0-SNAPSHOT -Dpackaging=jar