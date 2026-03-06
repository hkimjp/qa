set dotenv-load := true

all:
    @echo just dev
    @echo just build
    @echo just zip
    @echo just github
    @echo just uberjar
    @echo just run
    @echo just deploy

dev:
    sh start-dev.sh

uberjar:
    lein uberjar

run:
    lein run

deploy host: uberjar
    scp target/qa-*-standalone.jar {{ host }}:qa/qa.jar
    ssh {{ host }} 'sudo systemctl restart qa'
    ssh {{ host }} 'systemctl status qa'

stage:
    just deploy ${STAGE}

prod:
    just deploy ${PROD}
