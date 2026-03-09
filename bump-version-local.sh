#!/usr/bin/env bash

if [ -z "$1" ]; then
    echo "usage: $0 <version>"
    exit
fi


if [ -x "/run/current-system/sw/bin/sed" ]; then
    SED="/run/current-system/sw/bin/sed -E"
else
    SED="/usr/bin/sed -E"
fi

${SED} -i '' "s|^(\(defproject .+) .+|\1 \"$1\"|" project.clj

now=`date '+%F %T'`
${SED} -i '' \
    -e "s|(\(def \^:private version).*|\1 \"$1\")|" \
    -e "s|(\(def \^:private updated).*|\1 \"$now\")|"  src/qa/view/page.clj
