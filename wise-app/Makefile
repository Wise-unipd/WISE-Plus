# avoid conflicts in case there is a file named, for example, "run"
.PHONY: run build run-dev stop reset-db

# run the application in standard mode
run: 
	docker compose up -d --build

# compile the application
build:
	mvn clean package

# run the application in dev mode
run-dev: build
	docker compose -f docker-compose.dev.yml up -d

# stop the containers
stop:
	docker compose down

# stop the containers and delete volumes
reset-db:
	docker compose down -v
