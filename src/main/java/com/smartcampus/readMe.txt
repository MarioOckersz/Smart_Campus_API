Hi Greetings from Mario, I will guide you through the program 
file structures.

_____FULL SYSTEM MAP______

smart-campus-api/
├── .gitignore                     # Hides IDE files from GitHub
├── pom.xml                        # Maven dependencies (Jersey, Grizzly, JSON-B)
├── README.md                      # This file 
└── src/main/java/com/smartcampus/
    │
    ├── Main.java                  # Will run the Grizzly Server
    │
    ├── config/
    │   └── SmartCampusApp.java    # Registers the classes and sets the /api/v1 path
    │
    ├── data/
    │   └── DataStore.java         # Runs singleton database with ConcurrentHashMaps
    │
    ├── exceptions/                # Custom logic errors
    │   ├── LinkedResourceNotFoundException.java
    │   ├── RoomNotEmptyException.java
    │   └── SensorUnavailableException.java
    │
    ├── models/                    # The data shapes (POJOs)
    │   ├── Room.java
    │   ├── Sensor.java
    │   └── SensorReading.java
    │
    ├── providers/                 # The security and logging shield
    │   ├── ApiLoggingFilter.java
    │   ├── GlobalExceptionMapper.java
    │   ├── LinkedResourceNotFoundMapper.java
    │   ├── ResponseHelper.java
    │   ├── RoomNotEmptyExceptionMapper.java
    │   └── SensorUnavailableExceptionMapper.java
    │
    └── resources/                 # The API Endpoints (Controllers)
        ├── DiscoveryResource.java     # HATEOAS root
        ├── RoomResource.java          # Room's CRUD powers
        ├── SensorReadingResource.java # Sub-resource for history tracing
        └── SensorResource.java        # Sensor's CRUD powers



MAKE SURE YOU ARE IN THE FOLDER WHERE THE pom.xml FILE SITS AND :
Run these commands...

1. To Compile and download dependencies:
mvn clean compile -U

2. To Start the Grizzly server:
mvn exec:java
_______________________________________________________________________________________________________________________________________________________________________________

The "Main.java" will run the Grizzly Server.

"config": Configures the Jakarta application 
and explicitly registers every class to 
prevent startup crashes

The "DataStore.java" from the data folder 
runs a singleton database with ConcurrentHashMaps
to keep everything thread-safe in memory.


models: Contains the plain Java objects 
(Rooms, Sensors, Readings) that move data around


resources: The chefs of the kitchen. 
These files catch the incoming HTTP requests 
(GET, POST, DELETE) and handle the core business logic.


exceptions & providers: These work together to 
catch internal Java errors and turn them into clean, 
safe JSON error messages (like 409 Conflict or 500 Server Error)
so the client never sees a stack trace.

 
