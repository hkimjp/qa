set dotenv-load

all:
	@echo make dev
	@echo make build
	@echo make zip
	@echo make github
	@echo make uberjar
	@echo make run
	@echo make deploy

dev:
	sh start-dev.sh

uberjar:
	lein uberjar

run: uberjar
	sh start.sh

deploy: uberjar
	scp target/qa-*-standalone.jar ${DEST}:qa/qa.jar
	ssh ${DEST} 'cd qa & docker compose restart'
# 	ssh ${DEST} 'sudo systemctl restart qa'
# 	ssh ${DEST} 'systemctl status qa'
