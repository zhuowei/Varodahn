#!/bin/sh
. ./classpath.sh
rm -r pbin
mkdir pbin
javac -source 1.5 -target 1.5 -d pbin -cp protosrc:$MYCLASSPATH `find protosrc -name "*.java" -printf "%p "`
jar cvf libs/protosrc-compiled.jar -C pbin .
