# Smart_Campus_API
Developing a JAX-RS RESTful based API service for campus sensor systems
# Smart Campus REST API

## Overview

This project is a RESTful web service built using Java 17 and JAX-RS (Jersey). It simulates a Smart Campus IoT infrastructure system that tracks and manages:
* Rooms
* Sensors
* Sensor Readings

The API operates on a standalone, embedded Grizzly HTTP server. It utilizes a thread-safe, in-memory `DataStore` (using `ConcurrentHashMap`) and adheres strictly to core REST principles:
* Resource-based architecture
* Standard HTTP methods (GET, POST, DELETE)
* Sub-resource locator routing (Sensor -> Readings)
* Query parameter filtering
* Centralized exception mappers preventing stack-trace leaks
* JSON-B request/response serialization

## Technology Stack

* Java 17
* JAX-RS (Jersey)
* Embedded Grizzly HTTP Server
* Maven
* Eclipse Yasson (JSON-B)
* In-memory Thread-Safe DataStore (No external database)

## Base URL

http://localhost:8080/api/v1

## API Design Summary

### Resource Paths

* `/` = API discovery and HATEOAS links
* `/rooms` = Manage campus rooms
* `/sensors` = Manage hardware sensors
* `/sensors/{id}/readings` = Manage historical sensor readings (Sub-resource)

### Relationships

* A Room can house multiple Sensors.
* A Sensor belongs to exactly one Room.
* A Sensor contains a historical list of multiple Readings.

### Design Trade-off (Embedded Objects vs. Reference Links)
In the current implementation, calling a `Sensor` object eagerly returns the full embedded `history` list of `SensorReading` objects to eliminate the "N+1" request problem for front-end dashboards. In a production-scale environment, a lazy-loading approach utilizing HATEOAS reference links would be implemented to prevent payload bloat.

## Build & Run Instructions

### 1. Prerequisites

Make sure you have installed:
* Java JDK 17 or higher
* Maven 3.8+
* Git

### 2. Clone Repository

    git clone https://github.com/MarioOckersz/Smart_Campus_API.git
    cd Smart_Campus_API

### 3. Build the Project

This will clean the build directory and force Maven to resolve all Jakarta and Jersey dependencies.

    mvn clean compile -U

### 4. Deploy the Application

Because this API uses an embedded Grizzly server, you do not need to configure Tomcat or GlassFish. Simply run:

    mvn exec:java

### 5. Verify Server

Open your browser or Postman and navigate to: http://localhost:8080/api/v1/
You should see the API discovery JSON response.

## Sample cURL Commands

**1. API Discovery**

    curl -X GET http://localhost:8080/api/v1/

**2. Get All Rooms**

    curl -X GET http://localhost:8080/api/v1/rooms

**3. Create a Room**

    curl -X POST http://localhost:8080/api/v1/rooms \
    -H "Content-Type: application/json" \
    -d '{
      "name": "Networking Lab",
      "capacity": 45
    }'

**4. Create a Sensor**

    curl -X POST http://localhost:8080/api/v1/sensors \
    -H "Content-Type: application/json" \
    -d '{
      "type": "CO2",
      "status": "ACTIVE",
      "roomId": "REPLACE_WITH_ROOM_ID"
    }'

**5. Get Sensors (with filter)**

    curl -X GET "http://localhost:8080/api/v1/sensors?type=CO2"

**6. Get Sensor Readings**

    curl -X GET http://localhost:8080/api/v1/sensors/REPLACE_WITH_SENSOR_ID/readings

**7. Add Sensor Reading**

    curl -X POST http://localhost:8080/api/v1/sensors/REPLACE_WITH_SENSOR_ID/readings \
    -H "Content-Type: application/json" \
    -d '{
      "value": 420.5
    }'

**8. Delete a Room**

    curl -X DELETE http://localhost:8080/api/v1/rooms/REPLACE_WITH_ROOM_ID

## Business Rules

* A room cannot be deleted if it contains active sensors (Returns 409 Conflict).
* A newly created sensor must be linked to a valid, existing room ID (Returns 422 Unprocessable Entity).
* Sensors with a status of `MAINTENANCE` cannot accept new readings (Returns 403 Forbidden).
* Adding a reading via the sub-resource automatically updates the parent sensor's `currentValue` in memory to ensure state synchronization.
* If a POST request lacks an ID, the server automatically generates a secure UUID.

## Exception Handling

Custom exception mappers intercept errors and return structured JSON responses, preventing internal stack trace leaks to the client:

* **403 Forbidden:** `SensorUnavailableExceptionMapper` (e.g., Sensor is in maintenance).
* **404 Not Found:** Standard JAX-RS missing resource.
* **409 Conflict:** `RoomNotEmptyExceptionMapper` (Attempted to delete a room containing sensors).
* **422 Unprocessable Entity:** `LinkedResourceNotFoundMapper` (Invalid linked Room ID).
* **500 Internal Server Error:** `GlobalExceptionMapper` (Catch-all for unhandled server exceptions).

## Logging

An `ApiLoggingFilter` implementing `ContainerRequestFilter` and `ContainerResponseFilter` utilizes `java.util.logging` to securely record:
* Incoming requests (HTTP Method + URI)
* Outgoing responses (Final Status Code)


---

## 🏗️ Architecture (Class Diagram)

```mermaid
classDiagram
    direction TB

    %% 1.1 Setup
    class SmartCampusApp {
        <<Application>>
        @ApplicationPath("/api/v1")
        +getClasses() Set
    }

    class DataStore {
        <<Singleton>>
        +ConcurrentHashMap~String, Room~ rooms
        +ConcurrentHashMap~String, Sensor~ sensors
    }

    %% Models
    class Room {
        +String id
        +String name
        +int capacity
        +List~String~ sensorIds
    }

    class Sensor {
        +String id
        +String roomId
        +String type
        +String status
        +double currentValue
        +List~SensorReading~ history
    }

    class SensorReading {
        +String id
        +long timestamp
        +double value
    }

    %% Resources
    class DiscoveryResource {
        <<JAX-RS Resource>>
        +getDiscovery() Response
    }
    note for DiscoveryResource "1.2 Discovery: Returns HATEOAS links\nand version metadata"

    class RoomResource {
        <<JAX-RS Resource>>
        +getRooms() Response
        +createRoom(Room) Response
        +getRoomById(String) Response
        +deleteRoom(String) Response
    }
    note for RoomResource "2.2 Deletion: 409 Conflict logic\nblocks deletion if sensors present"

    class SensorResource {
        <<JAX-RS Resource>>
        +getSensors(@QueryParam type) Response
        +createSensor(Sensor) Response
        +getReadingsResource(String) SensorReadingResource
    }
    note for SensorResource "3.1 Integrity: Validates roomId existence\n3.2 Filtering: Dynamic search by type"

    class SensorReadingResource {
        <<Sub-Resource Locator>>
        -String sensorId
        +getHistory() Response
        +addReading(SensorReading) Response
    }
    note for SensorReadingResource "4.2 History: Side-effect updates\nparent Sensor.currentValue"

    %% 5.1 Specific Exception Mappers
    class RoomNotEmptyExceptionMapper {
        <<Provider>>
        +toResponse(RoomNotEmptyException) Response
    }
    note for RoomNotEmptyExceptionMapper "5.1 Maps to 409 Conflict"

    class LinkedResourceNotFoundMapper {
        <<Provider>>
        +toResponse(LinkedResourceNotFoundException) Response
    }
    note for LinkedResourceNotFoundMapper "5.1 Maps to 422 Unprocessable"

    class SensorUnavailableExceptionMapper {
        <<Provider>>
        +toResponse(SensorUnavailableException) Response
    }
    note for SensorUnavailableExceptionMapper "5.1 Maps to 403 Forbidden"

    %% 5.2 Global Mapper
    class GlobalExceptionMapper {
        <<Provider>>
        +toResponse(Throwable) Response
    }
    note for GlobalExceptionMapper "5.2 Safety Net: Returns 500\n(Leak-Proof, no stack trace)"

    %% 5.3 Logging
    class ApiLoggingFilter {
        <<Provider>>
        <<ContainerRequestFilter>>
        <<ContainerResponseFilter>>
        +filter(ContainerRequestContext) void
        +filter(ContainerRequestContext, ContainerResponseContext) void
    }

    %% Relationships
    SmartCampusApp ..> DiscoveryResource : registers
    SmartCampusApp ..> RoomResource : registers
    SmartCampusApp ..> SensorResource : registers

    RoomResource ..> DataStore : reads/writes
    SensorResource ..> DataStore : reads/writes
    SensorReadingResource ..> DataStore : reads/writes

    SensorResource --> SensorReadingResource : delegates locator
    Room "1" -- "*" Sensor : contains
    Sensor "1" -- "*" SensorReading : owns history

    }
```
