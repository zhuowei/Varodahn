#!/bin/sh
. ./classpath.sh
java -cp bin:$MYCLASSPATH net.zhuoweizhang.varodahn.PskTester $@
