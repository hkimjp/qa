#!/usr/bin/env bash

if [ -z $1 ]; then
    d=`date +%F`
else
    d=$1
fi

https -pb qa.melt.kyutech.ac.jp/api/readers/${d}
