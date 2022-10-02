#!/bin/bash
mvn clean 
mvn package
echo "$1" | java --enable-preview -jar target/parser-demo-1.0-SNAPSHOT.jar 
bash stitch_gif.sh "$1"
rm graph-*.png

