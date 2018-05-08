## Features
* Connects to a MQTT broker to received keep-alive and schedule messages
* Serves API to check restaurant online status, unschedule a unavailability and rank restaurants
based on their offline status
* Can initializes a mock environment using docker-composer
* Uses Apache Ignite to do faster processing and caching for some features

## Requirements
* Gradle
* JDK 8
* Docker
* Docker composer

## Running
It's possible to run the main application using the gradle wrapper from the Linux or Mac command
line:

    ./gradlew bootRun

In Windows:

    gradlew.bat bootRun

To run the Docker images (mosquitto, apache-ignite, mysql):

     docker-composer up -d

The client can be found on the subproject Client, it's a simple command line that sends keep-alive
every 1 minute, and send a fix schedule for restaurant 1 at a fixed time if initialized with
schedule parameter from command line

The application is configured with Flyway, once the application is booted it's goind to create the
tables and initiaze with 5 restaurants for test purposes.


## Docker

The docker compose file configures 3 containers:
- MySQL
- Eclipse Mosquitto
- Apache Ignite

For MySQL:
- Exposes the port 3306 on the host
- Creates a schema called 'connection'
- Sets the default password for root
- Creates and sets the password for the application's user

For Apache Ignite:
- Exposes the range of ports 47500-47600 and 47100-47200
- Configures the network in mode 'host'
- Mounts the file main/resources/docker/config/ignite/config.xml as the config
- Disables the quiet mode for logging

For Eclipse Mosquitto:
- Exposes the port 1883 on the host
- Mounts the file main/resources/docker/config/mosquitto/mosquitto.conf as the config
- Mounts the directory in the environment variable MOSQUITTO_DATA as the data directory

To change the data directory for Eclipse Mosquitto you have three options:
- Modify the variable MOSQUITTO_DATA in the file .env in projects' root directory
- Create a environment variable or just for the shell called MOSQUITTO_DATA with the directory as value
- Pass the variable with the -e VARIABLE=VALUE option

*For more information please check docker-compose's documentation:
https://docs.docker.com/compose/environment-variables/*
