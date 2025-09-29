

# Ride Sharing Client-Server System

## Overview
This system simulates a basic ride-sharing application with a client-server architecture. The client can be either a **Rider** or a **Driver**, and the communication between the client and server is done using **Sockets**.

- **Rider**: The rider can register, create new trips, view previous trips, and end the ride.
- **Driver**: The driver can accept trip requests, update the trip status, and notify when the ride is completed.

## Technologies
- **Java**: The entire system is built in Java.
- **Sockets**: The communication between the client and server is done using Java Socket API.
- **Object Streams**: Java `ObjectInputStream` and `ObjectOutputStream` are used for sending and receiving objects between the client and server.

## Project Structure
1. **Server Side:**
   - Listens for incoming client connections.
   - Handles requests from both riders and drivers.
   - Manages the ride lifecycle (starting, ending, and completion).
   
2. **Client Side (Rider and Driver):**
   - **Rider Client**: Allows the rider to create a trip, view trips, and end the ride.
   - **Driver Client**: Allows the driver to accept a ride, update the status, and complete the ride.

### Rider Client:
- Connects to the server and registers as a rider.
- The rider can create a trip, view previous trips, or exit the system.
- Sends location details, matching strategy, and receives information about matched drivers.
- Allows the rider to end the trip, notifying the server when the ride is completed.

### Driver Client:
- Accepts trip requests from riders.
- Updates trip status (e.g., ride started, ride completed).
- Notifies when the ride is completed.

## Features
- **Trip Creation**: Riders can create a new trip by providing source and destination coordinates, choosing a matching strategy, and confirming the trip.
- **Ride Completion**: Riders can end the ride by sending an "end" message to the server.
- **Trip Notifications**: The system sends messages to both the rider and driver about the trip status.
- **Matching Strategy**: Riders can select between "Least Time-Based" or "High Rating" to match with drivers.

## How to Use
1. **Start the Server:**
   - Run the `Server` class.
   - The server will wait for clients (Riders and Drivers) to connect.

2. **Start the Rider Client:**
   - Run the `RiderClient` class.
   - Enter your name and ID when prompted.
   - Choose the option to create a new trip or view previous trips.
   - Follow the prompts to enter source and destination, select matching strategy, and wait for the driver confirmation.
   - Type "end" to finish the ride.

3. **Start the Driver Client:**
   - Run the `DriverClient` class.
   - The driver will receive ride requests from riders and can accept the ride.
   - The driver will update the ride status and notify when the ride is completed.

## Code Explanation
### RiderClient
- **Registration**: The rider is registered with the server by sending their name and ID.
- **Trip Creation**: The rider provides source and destination coordinates and chooses a matching strategy.
- **Trip Status**: The rider is notified when the driver accepts the ride and can then end the ride with the `"end"` command.

### Server
- The server listens for connections from both riders and drivers.
- The server handles trip creation, matching, and sends updates to both the rider and the driver.

### DriverClient
- The driver listens for ride requests and can accept or reject them.
- The driver updates the status of the ride and notifies when the ride is completed.

## Known Issues
- If the server is not responding after the `"end"` message from the rider, check the synchronization and message handling in both client and server.
- Ensure that the server is not blocking indefinitely while waiting for a message.

## Future Enhancements
- **Database Integration**: Add support for storing rider and driver details in a database.
- **Real-Time Location Tracking**: Implement real-time location updates for the rider and driver.
- **Advanced Trip Matching Algorithms**: Implement more complex algorithms for matching riders with drivers based on preferences, availability, etc.

## Author
P.Imayvaramban