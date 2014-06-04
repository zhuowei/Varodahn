#!/bin/sh
. ./classpath.sh
rm -r libbin
mkdir libbin
javac -source 1.5 -target 1.5 -d libbin -cp src:$MYCLASSPATH `find src/net/zhuoweizhang/varodahn/net -name "*.java" -printf "%p "` \
`find src/net/zhuoweizhang/varodahn/proto -name "*.java" -printf "%p "`
#jar cvf ../app/libs/carlib.jar -C bin .
