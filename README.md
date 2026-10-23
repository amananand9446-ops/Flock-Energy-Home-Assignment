# Flock Energy API

A Java Spring Boot API that provides access to meter search results, meter details, energy readings, 
and geographic location from the Urja Meter Ops portal.

The goal is to let other programs retrieve meter information through a simple API instead of interacting 
with the original portal directly.

## Features

- Search meters using a query and page number.
- Retrieve details about a meter, including its administrative hierarchy.
- Retrieve energy readings for a meter.
- Retrieve a meter's latitude and longitude.
- Check whether the API service is running.
- Handle common portal errors, including authentication and connection failures.

## Technology Stack

- Java
- Spring Boot
- Spring Web `RestClient`
- Jackson for decoding the meter details response
- Maven

## Project Structure

```text
FlockEnergyAPI/
├── src/
│   ├── main/
│   │   ├── java/com/flockenergy/api/
│   │   │   ├── client/
│   │   │   │   └── PortalClient.java
│   │   │   ├── controller/
│   │   │   │   ├── HealthController.java
│   │   │   │   └── MeterController.java
│   │   │   ├── decoder/
│   │   │   │   └── MeterDetailsDecoder.java
│   │   │   ├── model/
│   │   │   │   ├── MeterDetails.java
│   │   │   │   ├── MeterDetailsResponse.java
│   │   │   │   └── MeterHierarchy.java
│   │   │   ├── FlockEnergyApiApplication.java
│   │   │   └── RestClientConfig.java
│   │   └── resources/
│   │       └── application.properties
├── PROTOCOL.md
├── openapi.json
└── README.md
```

## Requirements

- A compatible Java Development Kit.
- Maven support.
- A valid, authorized session for the Urja Meter Ops portal.

Use the Java version configured in the project's Maven build.

## Configuration

The portal base URL and session cookie are configured in `src/main/resources/application.properties`.

The session cookie is supplied through an environment variable:

`FLOCK_PORTAL_SESSION_COOKIE`

Set its value to the complete cookie pair for the authenticated portal session. Do not put the actual
cookie in this README, in source code, or in version control.

The cookie may expire. If requests return HTTP 401, sign in to the portal again and update the 
environment variable with the current session cookie.

## Running the Application

1. Clone this repository.
2. Open the project in IntelliJ IDEA or another Java IDE.
3. Set the `FLOCK_PORTAL_SESSION_COOKIE` environment variable in the application's run configuration.
4. Allow Maven to resolve the project dependencies.
5. Run `FlockEnergyApiApplication`.
6. The API will be available at `http://localhost:8080`.

## API Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/health` | Check API status |
| GET | `/api/meters?q=J100000&page=1` | Search meters |
| GET | `/api/meters/{meterId}` | Get meter details and hierarchy |
| GET | `/api/meters/{meterId}/energy` | Get energy readings |
| GET | `/api/meters/{meterId}/location` | Get geographic coordinates |

## Example Requests

Check API health:

```http
GET http://localhost:8080/api/health
```

Search meters:

```http
GET http://localhost:8080/api/meters?q=J100000&page=1
```

Get meter details:

```http
GET http://localhost:8080/api/meters/J100003
```

Get energy readings:

```http
GET http://localhost:8080/api/meters/J100002/energy
```

Get meter location:

```http
GET http://localhost:8080/api/meters/J100003/location
```

## Design Decisions and Trade-offs

- **Separate controller and client:** Controllers handle incoming API requests, while `PortalClient`
handles communication with the original portal.
- **Use of `RestClient`:** Provides a straightforward way to send HTTP requests to the portal.
- **Separate model classes:** Meter details and hierarchy are represented by Java classes rather than 
being combined into one large object.
- **Dedicated decoder:** The meter details page uses an indexed JSON format. `MeterDetailsDecoder`
converts it into a more readable response.
- **Environment-based session configuration:** Keeps the session cookie out of the source code.
- **Pass through some responses:** Search, energy, and location responses retain the portal's observed 
JSON structure, reducing unnecessary transformations.

## Assumptions

- The user has legitimate access to the Urja Meter Ops portal.
- The observed portal endpoints remain available.
- A valid session cookie is required for authenticated requests.
- Portal response structures may change over time.
- Energy readings and geographic coordinates may be represented as strings in the portal responses.

## Limitations and Scope Not Covered

- The meter details decoder depends on the portal's indexed-data format.
- The session cookie may need to be refreshed after expiry.
- Automated tests for every endpoint and error condition have not yet been added.
- Comprehensive request validation, consistent API error responses, retries, and timeouts have not yet
been implemented.
- Only the observed endpoints needed for this assignment are covered.

## Future Improvements

- Add automated unit and integration tests.
- Return consistent HTTP status codes and structured error responses.
- Add validation for meter IDs and page numbers.
- Configure connection and response timeouts.
- Improve resilience to changes in the portal's response format.
- Add structured logging without exposing secrets.
- Add caching where appropriate.

## Documentation

- [Portal protocol and observed behaviour](PROTOCOL.md)
- [OpenAPI specification](openapi.json)

## Reflection

### 1. What assumptions did you make?

I assumed that the portal's existing endpoints would provide the meter data needed for this project and
that a valid login session would allow me to access them.
I also assumed that the response formats I observed during development would remain the same while the API
was being used.
I decided to keep the search, energy, and location responses in their original JSON format.
For meter details, I converted the portal's response into a simpler format because the original response 
was difficult to understand and use directly.

### 2. What was the hardest part, and how did you get unstuck?

The hardest part was getting the meter details from the portal. Its response was different from normal
JSON because some values were stored at different positions in an array and referred to using indexes.

My first attempt returned empty values for the meter details. I checked the original response again and
understood that I needed to use those indexes to find the actual values. After correcting the logic, 
the API returned the expected meter details and hierarchy.

This helped me understand the importance of checking the actual response before deciding how to process it.


### 3. What would you improve with another day?

I would first add tests to check whether the endpoints return the expected responses, including when
the portal returns an error. I would also improve error handling so that users of my API receive clearer
error messages when the session expires or the portal cannot be reached.

I would add validation for inputs such as page numbers and meter IDs. Finally, I would test the project
from a fresh setup and make sure the README and OpenAPI documentation match the actual implementation.

### 4. What mistake did you make?

While writing the meter details decoder, I initially treated the indexes in the response as if
they were the actual data objects. Because of this, the meter detail fields were empty.

I checked the response structure again, understood how the indexes worked, and corrected the code to 
retrieve the actual objects first. This taught me to understand the data format properly before writing the logic to process it.

### 5. What is your self-critique?

I was able to build the main API endpoints and test them successfully with the portal. I also learned
how to call an existing API, handle session-based authentication, and convert an unusual JSON response
into Java objects.

However, I still need to add automated tests and improve error handling. The meter details decoder 
also depends on the format used by the original portal, so it may need changes if that format changes. 
Given more time, I would focus on these improvements and test more error cases rather than adding 
more features.