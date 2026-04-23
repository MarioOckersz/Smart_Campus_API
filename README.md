# Smart Campus IoT Gateway 

**A General Computer Systems (GCS) Infrastructure Project**
This is a lightweight RESTful API I built to handle the IoT sensor network for a university campus. It manages rooms, links sensors to those rooms, and tracks every single data reading in a historical log.

---

## 🏗️ System Architecture & Stack

I designed this to be a standalone service. You don't need to install Tomcat or GlassFish separately because it uses an embedded server.

* **Language:** Java 17
* **Framework:** JAX-RS (Jersey)
* **Server:** Embedded Grizzly HTTP Server
* **Build System:** Apache Maven
* **Persistence:** In-memory `ConcurrentHashMap` (Thread-safe)

---

## ⚙️ Project Logic & "Part" Requirements

### Part 1: Service Architecture & Setup
The API starts at `/api/v1` as required. I used a custom `Application` subclass with the `@ApplicationPath` annotation to handle the versioning.

**Q: JAX-RS Lifecycle & Data Sync**
By default, JAX-RS resources are **request-scoped**. This means every time someone hits an endpoint, the server makes a brand new instance of the class and then throws it away. Because of this, you can't just store data in a regular `List` inside the resource class—it would disappear instantly. That’s why I used a **Singleton DataStore**. It stays alive for the whole lifecycle of the app, and I used `ConcurrentHashMap` so that if two people post a reading at the same time, the data doesn't get corrupted or lost.

**Q: Why use Hypermedia (HATEOAS)?**
The discovery endpoint at the root (`/api/v1`) gives the client links to everything else. This is way better than static docs because if I ever change a URL, the client doesn't break—it just follows the new link. It makes the API "self-discoverable."

---

### Part 2: Room Management
I implemented the `/rooms` path to handle all the lecture halls and labs.

**Q: Returning IDs vs. Full Objects**
If I only return IDs, the response is tiny and saves bandwidth, but the client has to make a separate request for every single room to see the names. Returning full objects is "heavy" on the network, but it’s much easier for the dev building the frontend because they get everything in one shot. I went with full objects for the prototype because our data is small.

**Q: Is DELETE idempotent?**
Yes. In my code, if you delete room `101`, it’s gone (204 No Content). If you send the exact same request again, the server just says "Not Found" (404). Even though the status code changes, the *state* of the server is the same: the room is still gone. That’s idempotency.

---

### Part 3: Sensor Operations
The `/sensors` endpoint handles the hardware. It checks if a room exists before adding a sensor to it.

**Q: What if the client sends XML instead of JSON?**
Since I used `@Consumes(MediaType.APPLICATION_JSON)`, if a client tries to send XML or plain text, JAX-RS will automatically block it and return a **415 Unsupported Media Type**. The code won't even try to run, which protects the backend from processing garbage data.

**Q: Why use @QueryParam for filtering?**
I used query params for the sensor "type" (e.g., `?type=CO2`) because filtering is an optional action on a collection. If I put it in the path (like `/sensors/type/CO2`), it implies that "CO2" is a permanent resource location. Query parameters are the "standard" way to do searches and filters in REST.

---

### Part 4: Sub-Resources & Nesting
To handle the thousands of readings each sensor gets, I used the **Sub-Resource Locator** pattern.

**Q: Benefits of Sub-Resource Locators**
Instead of having one massive, 1000-line `SensorResource` class that handles everything, I delegated the readings logic to a separate `SensorReadingResource` class. It makes the code way easier to read and maintain. It also makes sure that you can't even get to the readings unless you have a valid sensor ID first.

---

### Part 5: Error Handling & Security
The API is "leak-proof." I used `ExceptionMappers` for everything.

**Q: 422 vs 404 for missing references**
If the JSON you sent is perfect but the `roomId` inside it doesn't exist, a **422 Unprocessable Entity** is much more accurate. A 404 usually means the URL is wrong; a 422 tells the dev "I understood your request, but the data you gave me doesn't work with my logic."

**Q: Security risks of Stack Traces**
Exposing a Java stack trace is a massive gift to hackers. It tells them exactly what libraries I’m using (like Jersey or Yasson), my internal file paths, and even my class names. They can use that to find specific vulnerabilities (CVEs) for those versions. That’s why my "Global Safety Net" mapper turns every crash into a boring 500 error.

**Q: Why use Filters for logging?**
If I put `Logger.info()` in every method, I’d be repeating myself 20 times. By using a `ContainerRequestFilter`, I write the logging code **once**, and it automatically catches every single request that hits the server. It’s cleaner and I’m less likely to forget to log an endpoint.

---

## 🛠️ Build and Launch

**1. Clone and Enter**
```bash
git clone [https://github.com/MarioOckersz/Smart_Campus_API.git](https://github.com/MarioOckersz/Smart_Campus_API.git)
cd Smart_Campus_API
```

**2. Build**
```bash
mvn clean compile -U
```

**3. Run**
```bash
mvn exec:java
```

---

## 🧪 Sample Tests (cURL)

**Check API Discovery:**
```bash
curl -X GET http://localhost:8080/api/v1/
```

**Create a Room:**
```bash
curl -X POST http://localhost:8080/api/v1/rooms -H "Content-Type: application/json" -d '{"name": "Main Lab", "capacity": 50}'
```

**Trigger 409 Conflict (Delete room with sensors):**
```bash
curl -i -X DELETE http://localhost:8080/api/v1/rooms/ID_WITH_SENSORS_HERE
```
