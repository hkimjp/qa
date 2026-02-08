#!/usr/bin/env bash

echo 'duct app'
echo 'after jacking in the repl, development wil start by (dev), (go).'

export QA_DEV=true
export PORT=3000
export DATABASE_URL='jdbc:postgresql://db/qa?user=postgres&password=hamlet'
lein repl
