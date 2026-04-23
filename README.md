# Smart Campus IoT Gateway 

**A General Computer Systems (GCS) Infrastructure Project** This is a lightweight, high-performance RESTful API designed to manage IoT sensor networks across a campus. It handles the registration, live telemetry, and lifecycle of physical rooms and hardware endpoints.

---

## 🏗️ System Architecture & Stack

This gateway is built to run as a standalone service, avoiding the need for heavy external application servers. It uses a thread-safe, in-memory architecture for fast data processing.

* **Language:** Java 17
* **Framework:** JAX-RS (Jersey)
* **Server:** Embedded Grizzly HTTP Server
* **Serialization:** Eclipse Yasson (JSON-B)
* **Build System:** Apache Maven
* **Persistence:** In-Memory Concurrent Collections

---

## ⚙️ Core System Rules & Logic

The API follows strict business logic to ensure data integrity:

1.  **Relational Integrity:** Sensors must be linked to a valid `roomId`. If the room doesn't exist, the system returns a `422 Unprocessable Entity` error.
2.  **Real-time Sync:** When you post a new reading, the system automatically updates the parent sensor's `currentValue` immediately.
3.  **Maintenance Mode:** If a sensor is marked as `MAINTENANCE`, it will block all incoming data readings and return a `403 Forbidden` error.
4.  **Safety Deletions:** You cannot delete a room if it still has sensors inside it. You must remove the sensors first (returns `409 Conflict`).
5.  **Secure IDs:** If you don't provide an ID when creating a room or sensor, the server generates a secure UUID for you.

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
