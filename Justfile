set dotenv-load

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
	# sh start.sh

deploy: uberjar
  scp target/qa-*-standalone.jar ${DEST}:qa/qa.jar
  ssh ${DEST} 'sudo systemctl restart qa'
  ssh ${DEST} 'systemctl status qa'

# deplopy to eq.local, docker
eq: uberjar
	scp target/qa-*-standalone.jar eq.local:qa/qa.jar
	ssh eq.local 'cd qa & docker compose restart'
