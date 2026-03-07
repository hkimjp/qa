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

uberjar:
    lein uberjar

deploy host: uberjar
    scp target/qa-*-standalone.jar {{ host }}:qa/qa.jar
    ssh {{ host }} 'sudo systemctl restart qa'
    ssh {{ host }} 'systemctl status qa'

stage:
    just deploy ${STAGE}

prod:
    just deploy ${PROD}
