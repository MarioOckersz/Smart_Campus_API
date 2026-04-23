# Smart Campus IoT Gateway 

A lightweight, high-performance RESTful API engineered to manage interconnected campus environments. This backend service handles the registration, telemetry, and lifecycle management of physical rooms and hardware sensors.

---

## 🏗️ System Architecture & Stack

This gateway is designed to run completely standalone, bypassing the need for heavy application servers like Tomcat. It uses a thread-safe, volatile memory architecture for rapid state management.

**Core Stack:**
* **Language:** Java 17
* **Framework:** JAX-RS (Jersey)
* **Server:** Embedded Grizzly HTTP HTTP Server
* **Serialization:** Eclipse Yasson (JSON-B)
* **Build System:** Apache Maven
* **Persistence Layer:** In-Memory Concurrent Collections (ConcurrentHashMap)

---

## ⚙️ Core System Capabilities & Constraints

The API enforces strict data integrity rules at the application layer:
* **Relational Integrity:** Sensors cannot be orphaned; they must be initialized with a valid roomId (triggers 422 Unprocessable Entity if invalid).
* **State Synchronization:** Submitting a reading via the sub-resource automatically updates the parent sensor’s currentValue property in real-time.
* **Hardware Lifecycle:** Sensors flagged as MAINTENANCE are locked and will reject incoming telemetry (triggers 403 Forbidden).
* **Deletion Safeguards:** Physical rooms containing active hardware endpoints cannot be purged from the system (triggers 409 Conflict).
* **Automated UUIDs:** Payloads submitted without explicit IDs will have secure UUIDs generated server-side.

---

## 📡 Endpoint Reference

Base URI: http://localhost:8080/api/v1

| HTTP Method | Endpoint | Description |
| :--- | :--- | :--- |
| GET | / | System discovery and HATEOAS navigation links. |
| GET | /rooms | Retrieve a collection of all campus rooms. |
| POST | /rooms | Provision a new physical room. |
| GET | /sensors | Retrieve sensors (Supports ?type= query filtering). |
| POST | /sensors | Provision and link a new hardware sensor. |
| GET | /sensors/{id}/readings | [Sub-resource] Fetch telemetry history for a specific sensor. |
| POST | /sensors/{id}/readings | [Sub-resource] Push a new telemetry data point to a sensor. |
| DELETE | /rooms/{id} | Decommission a room (must be empty). |

---

## 🛠️ Deployment & Execution

Prerequisites: Java 17+, Maven 3.8+, Git.

**1. Pull the Source Code**
    git clone https://github.com/MarioOckersz/Smart_Campus_API.git
    cd Smart_Campus_API

**2. Resolve Dependencies & Compile**
    mvn clean compile -U

**3. Boot the Gateway**
No external container configuration is required.
    mvn exec:java

(The service will bind to port 8080. Access http://localhost:8080/api/v1/ to verify the heartbeat.)

---

## 💻 CLI Integration Tests (cURL)

Use the following commands to validate the routing and business logic.

1. Check System Heartbeat & Links
    curl -X GET http://localhost:8080/api/v1/

2. Provision a Room
    curl -X POST http://localhost:8080/api/v1/rooms -H "Content-Type: application/json" -d '{"name": "Networking Lab", "capacity": 45}'

3. Retrieve Room Roster
    curl -X GET http://localhost:8080/api/v1/rooms

4. Provision a Sensor (Replace ID dynamically)
    curl -X POST http://localhost:8080/api/v1/sensors -H "Content-Type: application/json" -d '{"type": "CO2", "status": "ACTIVE", "roomId": "INSERT_ROOM_ID"}'

5. Push Telemetry Data
    curl -X POST http://localhost:8080/api/v1/sensors/INSERT_SENSOR_ID/readings -H "Content-Type: application/json" -d '{"value": 420.5}'

6. Fetch Sensor History
    curl -X GET http://localhost:8080/api/v1/sensors/INSERT_SENSOR_ID/readings

7. Query Sensors by Filter
    curl -X GET "http://localhost:8080/api/v1/sensors?type=CO2"

8. Attempt Room Decommission
    curl -X DELETE http://localhost:8080/api/v1/rooms/INSERT_ROOM_ID

---

## 🛡️ Observability & Error Mapping

### Exception Shielding
To prevent framework stack traces from leaking to the consumer, the system intercepts internal failures using custom JAX-RS ExceptionMapper classes:
* SensorUnavailableExceptionMapper -> 403 Forbidden
* Standard JAX-RS -> 404 Not Found
* RoomNotEmptyExceptionMapper -> 409 Conflict
* LinkedResourceNotFoundMapper -> 422 Unprocessable Entity
* GlobalExceptionMapper -> 500 Internal Server Error

### Traffic Logging
All network traffic is captured via a ContainerRequestFilter and ContainerResponseFilter, utilizing java.util.logging to record HTTP methods, target URIs, and final resolution status codes for auditing purposes.

---

## 📐 Architectural Decisions & Trade-offs

### Eager vs. Lazy Loading of Sensor History
Currently, requesting a Sensor object eagerly serializes and returns its entire embedded history array of SensorReading objects. 

**Rationale:** This decision was made to circumvent the "N+1" network request problem, allowing consumer dashboards to render a sensor's current operational state alongside its historical trendline in a single HTTP transaction. 

**Future Scaling:** As telemetry data accumulates in a production deployment, this eager-loading strategy will result in severe payload bloat. For subsequent iterations, the architecture will transition to a lazy-loading model, omitting the embedded array and instead providing a HATEOAS navigational link to the /readings sub-resource to conserve bandwidth.
