#!/bin/sh
. ./classpath.sh
java -cp libbin:$MYCLASSPATH net.zhuoweizhang.varodahn.net.DiscoveryClient $@
