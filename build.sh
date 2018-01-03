#!/bin/bash

rm ./dist/*.jar
mvn clean package -DskipTests
cp ./target/rss-log-*-jar-with-dependencies.jar ./dist/rsslog.jar

