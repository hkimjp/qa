set dotenv-load := true

printenv:
    printenv

env var:
    @printenv  {{ var }}

dev:
    sh start-dev.sh

run:
    lein run

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
