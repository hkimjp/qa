#!/usr/bin/env bash

echo 'duct app'
echo 'after jacking in the repl, development wil start by (dev), (go).'

QA_DEV=true \
PORT=3000 \
DATABASE_URL='jdbc:postgresql://db/qa?user=postgres&password=hamlet' \
lein repl
