#!/bin/sh
. ./classpath.sh
rm -r bin
mkdir bin
javac -source 1.5 -target 1.5 -d bin -cp src:$MYCLASSPATH `find src -name "*.java" -printf "%p "`
#jar cvf ../app/libs/carlib.jar -C bin .
