# iFood Backend Test - Connection  [![Build Status](https://travis-ci.org/mushira4/ifood-backend-connection-test.svg?branch=master)](https://travis-ci.org/mushira4/ifood-backend-connection)

Our goal is to be the best delivery company in the world. In order to achieve that we must do our best to manage the connection between iFood platform and all of our 100.000 restaurants.

## Tasks

Your task is to develop one (or more, feel free) service(s) to:
* handle connection between Restaurant and iFood Platform
* store and delete schedules of unavailabilities
* consolidate and provide data about the restaurants' connection

You also will have to emulate a client (keep it simple) for the Restaurant. The client must:
* guarantee “keep-alive” interactions with your service
* create and cancel schedules of unavailabilities

Fork this repository and submit your code.

## Business Rules

* In iFood Platform, the opening hour for the Restaurants are from 10:00 am to 11:00 pm.
* A Restaurant may be **available**/**unavailable**.
* A Restaurant may scheduled the status **unavailable**, due to the following reasons:
  - lack of delivery staff
  - connection issues (bad internet)<sup>1</sup>
  - overloaded due to offline orders
  - holidays
* A Restaurant may also be **online**/**offline**:
  - In order to a Restaurant be considered **online**, it must be inside the opening hour AND have interacted with our platform (aka sent a keep-alive signal) in the last two minutes AND its status should be **available**. Otherwise, it should be considered **offline**.
* The Restaurants are ranked according to the time they spent **offline**, inside the opening hour, without a scheduled **unavailability**.
* Example:
![restaurant connection timeline](https://www.lucidchart.com/publicSegments/view/4d48ac9c-e543-4531-abd5-eff0d9788ea6/image.png)

## Requirements

In order to handle 100.000 simultaneously connections, we would like to use an IOT protocol (like MQTT or XMPP) for this scenario.

You will have to keep the connection state of the Restaurants freshly available for whoever wants to query it. We advise the use of some type of compute grid to do this processing in parallel by the instances of the service. Apache Ignite might be a good choice. Or not.

This service needs to answer promptly:
* Given a list of Restaurants, whether they are **online** or **offline**.
* Given a specific Restaurant, its unavailability history.
* Reports for iFood commercial team so they can measure how our Restaurants are ranked, according to the amount of time they spent **offline** (see the diagram above, the red section on the "Restaurant Status" timeline).


## Project Stack
The project uses the following stack:
 - Java 9
 - Gradle 4.6 (Gradle Wrapper)
 - Spring Boot 1.5
 - Apache Ignite 2.4.0
 - Swagger 2.8

## Project Structure
The project was conceived following the recommendations of DDD described by Eric Evans,
the domain was created using TDD technique to maintain the architecture the most testable and flexible as possible.

It is separated in the modules: interface, model and infrastructure.
The model block is divided in two parts: domain and services.

## Project tests
The modules are tested by the test type which suits it the most
 - Into the interface module there are acceptance tests which are end-to-end tests.
 - Into the domain module there are unit tests which tests the bussiness requirements.
 - Into the infrastructure actually there are no tests **TODO: Need to be implemented integration tests**


## Running the application
To run the application just type in the root of the project folder
```
./gradlew bootRun
```

## Application Rest endpoints
The application endpoints can be viewed at /swagger-ui.html.

## Application MQTT endpoint
The MQTT endpoint is available at http://localhost:1883 with the topic "restaurantHealthSignalTopic"

## Test data
There are some restaurants` codes that can be used to test the application:
 - restaurant1
 - restaurant2
 - restaurant3



