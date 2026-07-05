run:
	mvn spring-boot:run

dev:
	watchexec -r -w src/main -- mvn spring-boot:run

test:
	mvn test
