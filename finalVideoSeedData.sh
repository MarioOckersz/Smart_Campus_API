#!/bin/bash
# Seed the Smart Campus API with dummy data

curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id": "room-101", "name": "Main Lecture Hall", "capacity": 150}'

curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id": "room-102", "name": "Networking Lab", "capacity": 40}'

curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id": "room-103", "name": "Server Room", "capacity": 5}'

curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id": "sens-temp-01", "roomId": "room-101", "type": "Temperature", "status": "ACTIVE"}'

curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id": "sens-co2-01", "roomId": "room-101", "type": "CO2", "status": "ACTIVE"}'

curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id": "sens-temp-02", "roomId": "room-103", "type": "Temperature", "status": "MAINTENANCE"}'

curl -X POST http://localhost:8080/api/v1/sensors/sens-temp-01/readings \
  -H "Content-Type: application/json" \
  -d '{"value": 22.5}'

curl -X POST http://localhost:8080/api/v1/sensors/sens-temp-01/readings \
  -H "Content-Type: application/json" \
  -d '{"value": 23.1}'

curl -X POST http://localhost:8080/api/v1/sensors/sens-temp-01/readings \
  -H "Content-Type: application/json" \
  -d '{"value": 24.0}'

curl -X POST http://localhost:8080/api/v1/sensors/sens-co2-01/readings \
  -H "Content-Type: application/json" \
  -d '{"value": 450.0}'
