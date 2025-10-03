#!/usr/bin/env bash

if [ -z "$1" ]; then
    echo "usage: $0 <version>"
    exit
fi


gsed "s|^(\(defproject .+) .+|\1 \"$1\"|" project.clj

now=`date '+%F %T'`
gsed -e "s|(\(def \^:private version).*|\1 \"$1\")|" \
       -e "s|(\(def \^:private updated).*|\1 \"$now\")|"  src/qa/view/page.clj

