#!/bin/bash

sdir="$(dirname $0)"

echo "Script dir=$sdir"

echo "Killing all started mavenzip instances..."
pgrep -a -f rsslog | awk '{print $1;}' | while read -r a; do kill -9 $a; done

cd $sdir/dist

# jetty-runner
java -jar -Dport=80 rsslog.jar
