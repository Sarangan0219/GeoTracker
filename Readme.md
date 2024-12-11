# Vehicle Tracking System with Geofence Management

## Overview

This backend system tracks and monitors vehicles in relation to predefined geofences. The system continuously verifies vehicle positions against geofence boundaries and manages access permissions, ensuring that only authorized vehicles are allowed within designated areas.

The system supports functionalities for geofence management, vehicle position processing, alerts for unauthorized vehicle entries, and historical reporting on vehicle movements.

## Core Features

1. **Geofence Management:**
    - Define geofences with a unique name/ID.
    - Specify geofences using polygon coordinates.
    - Associate a list of authorized vehicles with each geofence.

2. **Vehicle Position Processing:**
    - Accept vehicle position updates including vehicle ID, coordinates (x, y), and timestamp.
    - Validate if vehicle positions fall within geofence boundaries.
    - Track entry and exit events and raise alerts for unauthorized entries.

3. **Historical Reporting:**
    - Generate reports with vehicle ID, geofence name, entry/exit timestamps, duration of stay, authorization status, and alerts.

4. **Alerts:**
    - Trigger alerts for unauthorized vehicle entries or geofence overstays.
    - Alerts are stored for reporting purposes.

## API Endpoints

### 1. Geofence Management APIs

- **Create a Geofence:**
    - `POST /api/v1/geofences`
    - Request body:
      ```json
      {
        "name": "Geofence 1",
        "polygon": [[lat1, lon1], [lat2, lon2], ...],
        "authorizedVehicleIds": ["vehicle1", "vehicle2"]
      }
      ```

- **Get All Geofences:**
    - `GET /api/v1/geofences`

- **Get Geofence by ID:**
    - `GET /api/v1/geofences/{geofenceId}`

- **Update Geofence:**
    - `PUT /api/v1/geofences/{geofenceId}`
    - Request body:
      ```json
      {
        "name": "Updated Geofence",
        "polygon": [[lat1, lon1], [lat2, lon2], ...],
        "authorizedVehicleIds": ["vehicle1"]
      }
      ```

- **Delete Geofence:**
    - `DELETE /api/v1/geofences/{geofenceId}`

### 2. Vehicle Management APIs

- **Create a Vehicle:**
    - `POST /api/v1/vehicles`
    - Request body:
      ```json
      {
        "vehicleId": "vehicle1",
        "make": "Toyota",
        "model": "Camry",
        "year": 2020
      }
      ```

- **Get All Vehicles:**
    - `GET /api/v1/vehicles`

- **Get Vehicle by ID:**
    - `GET /api/v1/vehicles/{vehicleId}`

- **Update Vehicle:**
    - `PUT /api/v1/vehicles/{vehicleId}`
    - Request body:
      ```json
      {
        "make": "Honda",
        "model": "Civic",
        "year": 2021
      }
      ```

- **Delete Vehicle:**
    - `DELETE /api/v1/vehicles/{vehicleId}`

### 3. Vehicle Position Update API

- **Start Journey:**
    - `POST /api/v1/vehicle-positions/{vehicleId}/journeys/start`
    - Starts a new journey for the vehicle and sets its position to the initial journey point.

- **End Journey:**
    - `POST /api/v1/vehicle-positions/{vehicleId}/journeys/end`
    - Ends an ongoing journey for the vehicle.

- **Update Vehicle Position:**
    - `POST /api/v1/vehicle-positions`
    - Request body:
      ```json
      {
        "vehicleId": "vehicle1",
        "coordinates": [latitude, longitude],
        "timestamp": "2024-12-10T12:00:00Z"
      }
      ```

  Updates the vehicle's position and processes any related geofence events.

### 4. Event Management APIs

- **Get Event History:**
    - `GET /api/v1/events/history`
    - Retrieves the history of geofence events.

- **Get Vehicle Event History:**
    - `GET /api/v1/events/{vehicleId}`
    - Retrieves the geofence events for a specific vehicle.

- **Get Vehicle Journey History:**
    - `GET /api/v1/events/journey/{vehicleId}`
    - Retrieves the journey events (start and end) for a specific vehicle.

### 5. Authentication APIs

- **Register User:**
    - `POST /api/v1/auth/register`
    - Request body:
      ```json
      {
        "username": "user",
        "password": "password123"
      }
      ```

  Registers a new user.

- **Login User:**
    - `POST /api/v1/auth/login`
    - Request body:
      ```json
      {
        "username": "user",
        "password": "password123"
      }
      ```

  Authenticates the user and returns a JWT.

- **Refresh Token:**
    - `POST /api/v1/auth/refresh-token`
    - Refreshes the JWT for the user, keeping their session active.

### Note:
Always generate a **JWT token** after logging in or registering a user. This token should be added as a **Bearer token** in the `Authorization` header for authenticated API requests. There are two types of users:

- **ADMIN**: Required for performing actions like creating a vehicle or geofence.
- **USER**: Can view vehicle and geofence data but does not have permission to create or update them.

Ensure to include the appropriate token for the user role (ADMIN or USER) when making requests to the API.


## System Overview

This system tracks vehicle positions, manages journey events, and processes geofence-related events. The system reacts to vehicle movements and generates appropriate events such as:

- **Journey Start Event**: When a vehicle starts its journey.
- **Journey End Event**: When a vehicle ends its journey.
- **GeoFence Entry/Exit Events**: When a vehicle enters or exits a geofence.
- **GeoFence Inside/Outside Events**: When a vehicle is inside or outside a geofence.

### 1. Vehicle Positioning
- A vehicle's position is tracked via the `VehiclePosition` entity, which includes attributes like latitude, longitude, recorded timestamp, and geofence information.
- A vehicle's position can be updated using the `VehiclePositionRequest` object, which contains new coordinates and other relevant data for the vehicle.

### 2. Journey Events
- **Journey Start**: A journey is considered started when a vehicle is activated. The system saves the vehicle's initial position and logs a journey start event.
- **Journey End**: When a vehicle journey ends, the system saves the final position and logs a journey end event.

### 3. Geofence Handling
- **GeoFenceEntryEvent**: Triggered when a vehicle enters a geofence.
- **GeoFenceExitEvent**: Triggered when a vehicle exits a geofence.
- **GeoFenceInsideEvent**: Triggered when a vehicle remains inside a geofence.
- **GeoFenceOutsideEvent**: Triggered when a vehicle is outside all geofences.

Each of these events is logged and stored in the database.

## Flow of Vehicle Position Updates and Event Handling

### 1. Starting a Journey
- When a vehicle starts its journey, the `startJourney` method is invoked with the vehicle's ID. The system retrieves the vehicle from the database, marks it as active, and sets its initial position at `(0.0, 0.0)`.
- A `JourneyStartEvent` is generated and saved to record the journey's start.
- The initial position of the vehicle is saved in the database.

### 2. Ending a Journey
- When a journey ends, the `endJourney` method is called with the vehicle's ID. The system marks the vehicle as inactive and retrieves its final position.
- A `JourneyEndEvent` is generated and saved to record the journey's end.
- The final position is saved.

### 3. Processing Vehicle Position Updates
- As the vehicle moves, position updates are processed by the `processVehiclePosition` method. The method receives the `VehiclePositionRequest` containing the new position data.
- The system retrieves the vehicle from the database, checks if the vehicle is active, and updates the position accordingly.
- Depending on the position relative to geofences, the system triggers events such as `GeoFenceEntryEvent`, `GeoFenceExitEvent`, `GeoFenceInsideEvent`, or `GeoFenceOutsideEvent`. These events are generated by the respective event handlers (`GeoFenceEntryEventHandler`, `GeoFenceExitEventHandler`, etc.).
- The appropriate event is saved in the database.

### 4. Geofence Validation
- The `GeoFenceValidationStrategyFactory` is used to determine whether a vehicle is within a specific geofence. A geofence's authorized vehicle IDs are checked, and the system handles whether the vehicle is authorized to enter or exit a geofence.
- If the vehicle is inside the geofence, an event indicating the vehicle's status inside the geofence is generated. If the vehicle leaves the geofence, an exit event is triggered.

### 5. Event Logging and History
- All geofence-related events are logged and stored in the `GeoFenceEvent` table, while journey events are saved separately in the `JourneyEvent` table.
- The system provides APIs to fetch the event history for a specific vehicle o

## Database

### In-Memory Database (H2)

By default, the system uses an **in-memory H2** database for storing geofences, vehicles, vehicle positions, events, and alerts. The **`InMemoryGeoFenceRepository`** is provided for this purpose and is activated by the Spring profile `In-memory`.

- **Profile Active:** The in-memory database is activated by setting the following property in `application.properties`:

    ```properties
    spring.profiles.active=In-memory
    ```

#### Repository Structure for In-Memory

The repository interfaces and implementations are provided as follows:

- **GeoFenceRepository**: The interface that defines CRUD operations for geofences.
- **InMemoryGeoFenceRepository**: A Spring-managed repository that implements `GeoFenceRepository` using an in-memory `ConcurrentHashMap`.

### Extending to MySQL or PostgreSQL

To switch to **MySQL** or **PostgreSQL**, you can implement the corresponding repository classes by following these steps:

1. **Create a new repository implementation for MySQL/PostgreSQL:**
    - Implement the `GeoFenceRepository` interface using a relational database. You can use Spring Data JPA or any other ORM framework to interact with the database.

2. **Create the database configuration for MySQL/PostgreSQL:**
    - Update `application.properties` to include the database connection details for MySQL/PostgreSQL.
      Example for **PostgreSQL**:

      ```properties
      spring.datasource.url=jdbc:postgresql://localhost:5432/vehicle_tracking
      spring.datasource.username=your_username
      spring.datasource.password=your_password
      spring.jpa.hibernate.ddl-auto=update
      spring.profiles.active=postgresql
      ```

3. **Activate the MySQL/PostgreSQL profile:**
    - Set the active Spring profile to `postgresql` (or `mysql` if using MySQL):

      ```properties
      spring.profiles.active=postgresql
      ```

4. **Create a new Spring profile:**
    - Create a new repository implementation for the selected database and annotate it with `@Profile("postgresql")` or `@Profile("mysql")` to ensure it is activated when the corresponding profile is active.

## Error Handling

The system implements detailed error handling for the following scenarios:
- Invalid requests (e.g., missing parameters, incorrect data formats) result in `400 Bad Request` responses with a clear error message.
- Resource not found (e.g., geofence or vehicle not found) results in `404 Not Found`.
- Internal server errors result in `500 Internal Server Error` with error details.

### Sample Error Response

```json
{
  "errorCode": "NOT_FOUND",
  "errorMessage": "Vehicle with ID 'vehicle1' not found",
  "details": "No vehicle found with the given ID"
}
```


## Installation and Setup

### Prerequisites
- Java 11 or higher
- Spring Boot 2.x
- Database (H2, PostgreSQL, MySQL, or similar)
- Maven

### Steps to Run Locally

1. **Clone the repository:**

   ```bash
   git clone https://github.com/your-repo/vehicle-tracking-system.git
   cd vehicle-tracking-system
   ```

2. **Set up the database:**
    - Create a new database (e.g., `vehicle_tracking`).
    - Configure the database connection in `application.properties`.

3. **Build the project:**
   ```bash
   mvn clean install
   ```

4. **Run the application:**

   ```bash
   mvn spring-boot:run
   ```

5. **Access the API documentation:**
    - Use Postman or any other API client to interact with the endpoints.

## Conclusion

This system provides robust tracking and management of vehicles and geofences, offering full CRUD operations for geofences, vehicles, and position updates. The system ensures secure and efficient monitoring by handling vehicle movements within geofences, raising alerts for unauthorized entries, and providing historical reports for analysis.

