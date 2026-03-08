set dotenv-load

printenv:
    printenv

env var:
    @printenv  {{ var }}

dev:
    @echo 'after jacking in the repl, development wil start by (dev), (go).'
    lein repl

run:
    lein run

stop:
    #!/usr/bin/env bash
    PID=`lsof -i:${PORT} -t`
    kill ${PID}
    echo killed PID ${PID}

up:
    docker compose up -d

down:
    docker compose down

restart:
    just down up

uberjar:
    lein uberjar

deploy host: uberjar
    ssh {{host}} mkdir -p qa
    scp Justfile compose.yaml {{host}}:qa/
    scp target/qa-*-standalone.jar {{ host }}:qa/qa.jar
    ssh {{ host }} 'cd qa && just up'

stage:
    just deploy ${STAGE}

prod:
    just deploy ${PROD}

clean:
    rm -rf target
    fd -I \.bak$ --exec rm
