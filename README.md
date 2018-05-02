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
