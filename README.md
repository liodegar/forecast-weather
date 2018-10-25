# Forecast Weather Microservice REST API (FWMRA)

This API provides basic consolidated average info retrieved from openweathermap.org. The consolidated info has the following averages:

1.	Average of daily (6h-18h) temperature, in Celsius, for the following 3 days
2.	Average of nightly (18h-6h) temperature, in Celsius, for the following 3 days
3.	Average of pressure for the following 3 days


## Design considerations
- The API supports two levels of security by using Spring Security and JSON Web Token.

- Two levels of caching are provided, one at the client level (ETags) and the other at the application level (Redis to cache the static city information).

- Logging: slf4j was used to decouple from any specific implementation. The underlying logging is provided by LogBack. The generated log file is called weatherLogging.log,
located in ./logs directory.

- Exception handling: all the errors and exceptions are gracefully managed.

- Circuit Breaker Pattern: is used to manage in properly way the interaction with the API provider.

- Documentation: all the classes are intradocumented (Javadocs) and also the API is exposed via Swagger.

- CityID: due the API provider recommends to use the API with the cityID, when the user provides only the city name and ISO country code, the application,
by using these parameters look for the cityId (Redis cached info), and then the provider API is invoked only by using the cityID.

- No hard coded values. All the config properties are defined in the `application.properties` file


## Getting Started

In order to start the application and run the microservices, you should execute from a command line the following instruction:

`java -jar target/forecast-weather-0.0.1-SNAPSHOT.jar`

After this the Forecast Weather API will start.

### Prerequisites

A version of JDK 8 or higher should be installed in order to run the application.


### Running the web services

1. To get access to the API, you should get a token in the following endpoint:

   http://localhost:8080/weather/generateToken

   Passing as parameter:
   {
    "parameter":"weatherSubject"
   }

   and using BASIC auth with the following credentials:

        username=user
        password=finleap@2018


   After invoking these endpoint you will get something like this:

   {
       "code": "SUCCESS",
       "result": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ3ZWF0aGVyU3ViamVjdCIsImV4cCI6MTUzOTU2NzI0OX0.J3Xli1EV-T_cP-nQ_uJbkYGcYJdGINSvlmrwC6cSiHY"
   }


   You should copy all the result value (this is the generated token) in order to invoke other endpoints.

2. After generating the token, you can invoke the statistics by invoking:

   `http://localhost:8080/weather/data`

    Passing as parameter:

    {
     "parameter":{"name":"Berlin", "isoCountryCode":"DE"}
    }

    and using BASIC auth with the following credentials:

       username=user   
       password=finleap@2018


      And you should pass in the header, the previous generated token:

      `authorizationToken=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ3ZWF0aGVyU3ViamVjdCIsImV4cCI6MTUzOTU2NzI0OX0.J3Xli1EV-T_cP-nQ_uJbkYGcYJdGINSvlmrwC6cSiHY`


## More info

You can run check all the functionalities exposed by the API in: `http://localhost:8080/swagger-ui.html`


## Built With

* [Maven](https://maven.apache.org/) - Dependency Management


## Authors

* **Liodegar Bracamonte** - *Initial work* - [liodegar@gmail.com)


## License

Apache License 2.0.

## Acknowledgments

* To the all open source software contributors.


