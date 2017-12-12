# iFood Backend Test - Connection

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
* A Restaurant may schedule an **unavailable** status due to the following reasons:
  - lack of delivery staff
  - connection issues (bad internet)<sup>1</sup>
  - overloaded due to offline orders
  - holidays
* A Restaurant may also be **online**/**offline**:
  - In order to a Restaurant be considered **online**, it must be inside the opening hours AND have interacted with our platform (aka sent a keep-alive signal) in the last two minutes AND its status must be **available**. Otherwise, it should be considered **offline**.
* The Restaurants are ranked according to the time they spent **offline**, inside the opening hours and without a scheduled **unavailability**.
* Example:
![restaurant connection timeline](https://www.lucidchart.com/publicSegments/view/4d48ac9c-e543-4531-abd5-eff0d9788ea6/image.png)

## Requirements

We are talking about 100.000 restaurants and the online/offline information must be precise and timely. Please, consider using other protocols than HTTP in this communication scenario, such as MQTT or XMPP.

You will have to keep the connection state of the Restaurants freshly available for whoever wants to query it. Make sure you do it in a way that scales. We advise the use of some type of compute grid to do this processing in parallel by the instances of the service. Apache Ignite might be a good choice. Or not.

This service needs to answer promptly:
* Given a list of Restaurants, whether they are **online** or **offline**.
* Given a specific Restaurant, its unavailability history.
* Reports for iFood commercial team so they can measure how our Restaurants are ranked, according to the amount of time they spent **offline** (see the diagram above, the red section on the "Restaurant Status" timeline).

We expect your solution to be bundled in a way that we can easily run it locally, with a simple readme containing the rationale behind your implementation decisions and the steps needed to run it. Consider using some container/VM solution for that.
