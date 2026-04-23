# Smart_Campus_API
Developing a JAX-RS RESTful based API service for campus sensor systems
# Smart Campus API

A RESTful backend service designed to manage IoT infrastructure across a university campus. This API allows for the tracking and management of physical rooms, hardware sensors, and their time-stamped data readings.

## 🏛️ API Design Overview

This project is built using **Java 17**, **Jakarta RESTful Web Services (JAX-RS/Jersey)**, and an embedded **Grizzly HTTP Server**. 

### Architectural Highlights
* **Thread-Safe Data Layer:** Utilizes a Singleton `DataStore` with `ConcurrentHashMap` to safely manage in-memory data across concurrent network requests.
* **Sub-Resource Locators:** The API heavily leverages sub-resource delegation (e.g., `/sensors/{id}/readings`). This ensures strict state synchronization, automatically updating a parent sensor's `currentValue` whenever a new reading is POSTed to its history.
* **Leak-Proof Exception Handling:** Implements `ExceptionMapper` classes to intercept internal Java exceptions (`RoomNotEmptyException`, `LinkedResourceNotFoundException`, etc.) and translate them into standardized, secure JSON error responses (HTTP 403, 409, 422, 500) preventing raw stack traces from reaching the client.

### Design Trade-offs: Embedded Objects vs. Reference Links
In the current implementation, calling a `Sensor` object eagerly returns the full embedded `history` list of `SensorReading` objects. 
* **The Trade-off:** The advantage of embedding the history list is that it eliminates the "N+1" request problem, allowing a frontend dashboard to render the sensor's current state and historical graph in a single HTTP request. 
* **Conclusion:** For this prototype, embedding the objects was chosen to easily demonstrate state synchronization in memory. However, in a production-scale Smart Campus environment with thousands of data points, a lazy-loading approach utilizing HATEOAS reference links would be implemented to prevent payload bloat.

---

## 🚀 Build and Launch Instructions

### Prerequisites
You must have **Java 17** (or higher), **Apache Maven**, and **Git** installed on your machine.
* **For Mac users:** Open your terminal and install Maven and Git using Homebrew: `brew install maven git`
* **For Windows users:** Download Maven and Git from their official websites, install/extract them, and ensure their `bin` folders are added to your Windows System Environment Variables (`PATH`).

### Step-by-Step Setup

**1. Clone the repository and navigate into the folder:**

    git clone https://github.com/MarioOckersz/Smart_Campus_API.git
    cd Smart_Campus_API

**2. Clean the project and force-download dependencies:**
This ensures Maven resolves all Jakarta, Jersey, and Yasson dependencies correctly from the internet.

    mvn clean compile -U

**3. Launch the Grizzly Server:**

    mvn exec:java

**4. Verify the Server:**
Wait a few seconds. Once the terminal displays `Smart Campus API running at: http://localhost:8080/api/v1/`, the API is successfully running and ready to accept HTTP requests. 

*(Note: To stop the server at any time, press `Enter` in the terminal or `Ctrl + C`.)*

---

## 🧪 Sample API Interactions (cURL)

The following commands demonstrate successful interactions with the core endpoints of the API. You can run these in a separate terminal window while the server is running.

### 1. API Discovery & HATEOAS Links
Retrieves the root API information and available navigation links.

    curl -X GET http://localhost:8080/api/v1/

### 2. Create a Room
Creates a new physical room in the system. Note the returned `id` for the next steps.

    curl -X POST http://localhost:8080/api/v1/rooms \
      -H "Content-Type: application/json" \
      -d '{ "name": "Main Server Room", "capacity": 5 }'

### 3. Register a Sensor to a Room
Creates a new sensor and links it to the room. *(Replace `<room-id>` with the ID generated in Step 2).*

    curl -X POST http://localhost:8080/api/v1/sensors \
      -H "Content-Type: application/json" \
      -d '{ "roomId": "<room-id>", "type": "Temperature", "status": "ACTIVE" }'

### 4. Record a Sensor Reading (Sub-Resource)
Logs a new data point to the sensor's history and updates its current value. *(Replace `<sensor-id>` with the ID generated in Step 3).*

    curl -X POST http://localhost:8080/api/v1/sensors/<sensor-id>/readings \
      -H "Content-Type: application/json" \
      -d '{ "value": 24.5 }'

### 5. Filter Sensors via Query Parameters
Retrieves a list of all sensors filtered strictly by their operational type.

    curl -X GET "http://localhost:8080/api/v1/sensors?type=Temperature"

### 6. Trigger Business Logic Protection (Bonus)
Demonstrates the custom 409 Conflict Mapper. The system will safely reject this request because you cannot delete a room that currently houses active sensors. *(Replace `<room-id>` with the ID generated in Step 2).*

    curl -i -X DELETE http://localhost:8080/api/v1/rooms/<room-id>
