# ifood-backend-connection


## Run Application
1) Install Postgres, Apache Ignite and Eclipse Mosquitto in Docker using the `docker-compose up -d` command inside the *docker* folder
2) Return to the root directory and run the `make run` command

Specify a profile:
*SPRING_PROFILES_ACTIVE=staging ./gradlew bootRun*
## Run Unit tests and Integration tests
`make test`

## Enable SQL logs
Add the proprieties below in application.yml

**logging.level.org.hibernate.SQL:** DEBUG

**logging.level.org.hibernate.type.descriptor.sql.BasicBinder:** TRACE

## Load schema and Database Data
1. `./gradlew u`

* Technical Debt: The *MqttKeepAliveSignalIntegrationTest* integration test is marked as ignored to run by the `make test` command,
but you can run it directly in your IDE.

## MQTT Client
You can use one of the following options:
1. Run the `mqtt-client` module
1. Install the `MQTTLens - Google Chrome`app on Google Chrome

* You can be the *topic* on the application.yml file

## Architecture decision record (ADR)
You can see in the directory `adr` the records of architectural decisions.

The ADRs can be created using the following tool: https://github.com/npryce/adr-tools

