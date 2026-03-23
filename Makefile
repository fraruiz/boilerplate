all: build

start:
	@docker compose -f docker-compose.ci.yml up -d

build:
	@./gradlew build --warning-mode all

test:
	@./gradlew test --warning-mode all

run:
	@./gradlew :run

# Start the app
start-server:
	@./gradlew bootRun --args='server'

start-worker:
	@./gradlew bootRun --args='worker'
