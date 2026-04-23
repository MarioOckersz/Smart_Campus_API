# Smart_Campus_API
Developing a JAX-RS RESTful based API service for campus sensor systems
# Smart Campus API

This is a RESTful API built to manage IoT sensors and rooms across a university campus. It handles creating rooms, registering sensors, and logging sensor data over time. 

## API Design Overview

This project is built using Java 17, Jakarta REST (Jersey), and runs on an embedded Grizzly server. 

Here is a quick breakdown of how the backend is structured:
* **Data Storage:** Instead of a real database, I used a Singleton `DataStore` with `ConcurrentHashMap`. This acts as an in-memory database that stays thread-safe when multiple requests hit the server at the same time.
* **Sub-Resource Locators:** I used sub-resources for the sensor readings (e.g., `/sensors/{id}/readings`). This is important for state synchronization. Whenever a new reading is POSTed to the history list, the sub-resource automatically updates the parent sensor's `currentValue`. 
* **Error Handling:** I implemented custom Exception Mappers. If someone tries to delete a room that still has sensors, or looks for an ID that doesn't exist, the server catches the Java exception and returns a clean JSON error response (like a 404 or 409) rather than crashing or printing a raw stack trace to the client.

### API Design Trade-off: Embedded Objects vs. Lazy Loading
Right now, when you send a GET request for a Sensor object, it automatically includes the full `history` list of every reading inside the JSON response. 

The advantage of this eager-loading approach is that it solves the "N+1 request" problem. A frontend developer can get the sensor's current status and its entire historical graph in just one single HTTP request. 

The trade-off, however, is payload bloat. If a sensor records data every minute for a year, that JSON object will become massive and slow down the network. For this coursework prototype, embedding the objects made sense to easily demonstrate the data linking. But if this were deployed in a real-world campus, I would switch to lazy loading. I would return just the core sensor data and provide a HATEOAS link to fetch the history separately.

---

## How to Build and Run

### What you need:
You will need **Java 17** (or higher), **Git**, and **Maven** installed on your computer. 
* **Mac:** You can install Maven and Git quickly via terminal using `brew install maven git`.
* **Windows:** Download Maven and Git from their official sites and make sure their `bin` folders are added to your system's PATH variables.

### Steps to launch:

**1. Clone the code and enter the directory:**

    git clone https://github.com/YOUR_USERNAME/smart-campus-api.git
    cd smart-campus-api

**2. Download dependencies and compile:**
Run this command to force Maven to download the required Jakarta and Jersey libraries and compile the code.

    mvn clean compile -U

**3. Start the server:**

    mvn exec:java

If everything worked, your terminal will say `Smart Campus API running at: http://localhost:8080/api/v1/`. To kill the server, just press Enter or `Ctrl + C`.

---

## Testing the API (cURL Commands)

Here are five sample interactions you can run in a separate terminal to test the API while the server is running.

**1. View API Discovery Links**
Gets the base navigation links.

    curl -X GET http://localhost:8080/api/v1/

**2. Create a Room**
Adds a new room to the database. (Copy the ID it returns for the next steps).

    curl -X POST http://localhost:8080/api/v1/rooms \
      -H "Content-Type: application/json" \
      -d '{ "name": "Main Server Room", "capacity": 5 }'

**3. Add a Sensor**
Registers a new sensor. Replace `<room-id>` with the ID you got from step 2.

    curl -X POST http://localhost:8080/api/v1/sensors \
      -H "Content-Type: application/json" \
      -d '{ "roomId": "<room-id>", "type": "Temperature", "status": "ACTIVE" }'

**4. Log a Sensor Reading**
Adds a new data point to a sensor. Replace `<sensor-id>` with the ID from step 3.

    curl -X POST http://localhost:8080/api/v1/sensors/<sensor-id>/readings \
      -H "Content-Type: application/json" \
      -d '{ "value": 24.5 }'

**5. Filter Sensors**
Finds all sensors in the system that match a specific type.

    curl -X GET "http://localhost:8080/api/v1/sensors?type=Temperature"

**6. Test Error Handling (Bonus)**
Try to delete the room you created. The server will reject it with a 409 Conflict because the room still has an active sensor inside it.

    curl -i -X DELETE http://localhost:8080/api/v1/rooms/<room-id>
