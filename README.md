# Smart Campus IoT Gateway 

**A General Computer Systems (GCS) Infrastructure Project**
This is a lightweight, high-performance RESTful API designed to manage IoT sensor networks across a university campus. It handles the registration, live telemetry, and lifecycle of physical rooms and hardware endpoints.

---

## 🏗️ System Architecture & Stack

This gateway is designed to run as a standalone service, avoiding the need for heavy external application servers. It uses a thread-safe, in-memory architecture for fast data processing.

* **Language:** Java 17
* **Framework:** JAX-RS (Jersey)
* **Server:** Embedded Grizzly HTTP Server
* **Serialization:** Eclipse Yasson (JSON-B)
* **Build System:** Apache Maven
* **Persistence:** In-Memory Concurrent Collections (`ConcurrentHashMap`)

---

## ⚙️ Core System Rules & Logic

The API follows strict business logic to ensure data integrity:

1. **Relational Integrity:** Sensors must be linked to a valid `roomId`. If the room doesn't exist, the system returns a `422 Unprocessable Entity` error.
2. **Real-time Sync:** When you post a new reading, the system automatically updates the parent sensor's `currentValue` immediately.
3. **Maintenance Mode:** If a sensor is marked as `MAINTENANCE`, it will block all incoming data readings and return a `403 Forbidden` error.
4. **Safety Deletions:** You cannot delete a room if it still has sensors inside it. You must remove the sensors first (returns `409 Conflict`).
5. **Secure IDs:** If you don't provide an ID when creating a room or sensor, the server generates a secure UUID for you.

---

## 📡 Endpoint Reference

**Base URL:** `http://localhost:8080/api/v1`

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/` | System discovery and HATEOAS links |
| `GET` | `/rooms` | Get all registered rooms |
| `POST` | `/rooms` | Register a new room |
| `GET` | `/sensors` | Get sensors (Supports `?type=` filter) |
| `POST` | `/sensors` | Register and link a new sensor |
| `GET` | `/sensors/{id}/readings` | Get telemetry history for a sensor |
| `POST` | `/sensors/{id}/readings` | Push a new data reading to a sensor |
| `DELETE` | `/rooms/{id}` | Remove an empty room |

---

## 🛠️ How to Build and Run

### Prerequisites
You need **Java 17+**, **Maven**, and **Git** installed on your system.

### 1. Clone the Code
```bash
git clone [https://github.com/MarioOckersz/Smart_Campus_API.git](https://github.com/MarioOckersz/Smart_Campus_API.git)
cd Smart_Campus_API
```

### 2. Compile and Build
```bash
mvn clean compile -U
```

### 3. Run the Server
```bash
mvn exec:java
```
*(The API will be live at http://localhost:8080/api/v1/)*

---

## 🧪 CLI Integration Tests (cURL)

You can test the system by running these commands in a separate terminal while the server is running.

### 1. Check API Status
```bash
curl -X GET http://localhost:8080/api/v1/
```

### 2. Create a New Room
```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"name": "Networking Lab", "capacity": 45}'
```

### 3. List All Rooms
```bash
curl -X GET http://localhost:8080/api/v1/rooms
```

### 4. Create a Sensor
*(Replace `INSERT_ROOM_ID` with an actual ID from the step above)*
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"type": "CO2", "status": "ACTIVE", "roomId": "INSERT_ROOM_ID"}'
```

### 5. Push a New Data Reading
*(Replace `INSERT_SENSOR_ID` with an actual Sensor ID)*
```bash
curl -X POST http://localhost:8080/api/v1/sensors/INSERT_SENSOR_ID/readings \
  -H "Content-Type: application/json" \
  -d '{"value": 420.5}'
```

### 6. Get Sensor History
```bash
curl -X GET http://localhost:8080/api/v1/sensors/INSERT_SENSOR_ID/readings
```

### 7. Delete a Room
```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/INSERT_ROOM_ID
```

---

## 🛡️ Error Handling & Observability

### Exception Shielding
The system uses custom `ExceptionMapper` classes to catch internal errors and return clean JSON instead of raw code crashes:
* **403 Forbidden:** Sensor is in maintenance.
* **404 Not Found:** Resource doesn't exist.
* **409 Conflict:** Cannot delete a room with active sensors.
* **422 Unprocessable Entity:** Invalid Room ID provided.
* **500 Server Error:** General internal failure.

### Traffic Logging
An `ApiLoggingFilter` tracks every request. It logs the HTTP method, the URI, and the final status code to the server console so you can monitor traffic in real-time.

---

## 📐 Architectural Trade-offs

### Eager vs. Lazy Loading
Currently, when you request a `Sensor`, it returns the entire historical `readings` list inside the object. 

* **The Benefit:** This prevents the "N+1" problem. A developer can get the sensor status and the history in one single request, which is much faster for small systems. 
* **The Trade-off:** As the history grows to thousands of rows, the payload size will increase. In a real-world production version, I would change this to "Lazy Loading," where history is only sent if specifically requested via the `/readings` endpoint to save bandwidth.
