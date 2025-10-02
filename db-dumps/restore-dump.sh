#!/bin/sh
if [ -z "$1" ]; then
  echo "usage: $0 file.sql"
  exit
fi

#dropdb qa
#createdb qa
pg_restore -U postgres -h db --port 15432 -Fc -d qa $1
