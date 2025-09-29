
# Online Ride-Sharing — Server

This repository contains the server-side components for the **Online Ride-Sharing** system:

1. **STS4 Spring Boot Server** — handles WebSocket connections with the Next.js Rider & Driver frontends, session/connection persistence, authentication verification, persistence (MongoDB), live updates, and accepting driver responses.  
2. **Matching & Processing TCP Server** — a dedicated TCP service that performs heavy processing: route generation, vehicle selection, lane/lot extraction, scoring & ranking drivers (loyalty, acceptance ratio, rating, distance), and pricing algorithms (surge, rating-based, vehicle-based). It returns ranked driver lists and issues ride requests to driver clients.

This README documents how the servers interact, how to run them, message contracts, algorithms, and troubleshooting. Design context is based on the project spec. :contentReference[oaicite:1]{index=1}

---

## Table of Contents
- [High-level Architecture](#high-level-architecture)
- [Responsibilities](#responsibilities)
- [Data flow & Message Formats](#data-flow--message-formats)
  - [WebSocket (STS4) messages](#websocket-sts4-messages)
  - [TCP Server messages](#tcp-server-messages)
- [Algorithms & Matching Logic](#algorithms--matching-logic)
- [Persistence & DB model (summary)](#persistence--db-model-summary)
- [Getting started (dev)](#getting-started-dev)
  - [Prerequisites](#prerequisites)
  - [Environment variables (examples)](#environment-variables-examples)
  - [Run STS4 Spring Boot server](#run-sts4-spring-boot-server)
  - [Run Matching TCP server](#run-matching-tcp-server)
- [Endpoints & WebSocket topics](#endpoints--websocket-topics)
- [Testing flows](#testing-flows)
- [Deployment notes](#deployment-notes)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License & Credits](#license--credits)
- [Contact](#contact)

---

## High-level Architecture

```

[Next.js Rider Client]     [Next.js Driver Client]
\                       /
\                     /
\  WebSocket (wss)   /
\  to STS4 Server  /
-----------------
|
STS4 Spring Boot Server (connection persistence, auth, DB)
|
(1) persists rides, sessions, wallet, driver loyalty, cancellations
(2) sends requests to -> Matching & Processing TCP Server (TCP)
|
Matching Server (route gen, vehicle selection, scoring)
|
returns ranked driver list -> STS4 -> notifies Driver clients
|
MongoDB / log storage

````

See the design spec for module decomposition and diagrams. :contentReference[oaicite:2]{index=2}

---

## Responsibilities

### STS4 Spring Boot Server
- Accept and validate WebSocket connections from Rider & Driver frontends (Next.js).  
- Authenticate and verify Clerk tokens (or other auth) for every connection.  
- Maintain persistent mapping: `userId <-> websocket session` (reconnect handling).  
- Receive ride requests from Rider frontend, store ride record in MongoDB.  
- Forward ride request payloads to Matching TCP Server and await ranked-driver response.  
- Send driver notifications (request messages) via WebSocket to driver clients.  
- Receive driver accept/decline responses, update ride status.  
- Handle cancellation logic: update loyalty, reassign drivers, notify affected parties.  
- Expose REST APIs for ride status, history, wallet, admin actions, and debug endpoints.  

### Matching & Processing TCP Server
- Receive ride request + context over TCP (or gRPC/HTTP depending on implementation choice).  
- Generate candidate drivers (from DB or in-memory index) within a search radius.  
- Compute route(s) (use OSRM or internal route generator), lane & lot extraction for advanced analytics.  
- Score drivers with multi-factor ranking (loyalty, acceptance ratio, rating, distance, ETA).  
- Compute pricing using configurable algorithms (surge multiplier, rating-based adjustments, vehicle-based fares).  
- Return ranked list of driver proposals (with price proposals and route options).  
- Optionally push low-level telemetry to STS4 for analytics/logging.

---

## Data flow & Message Formats

> Keep message formats consistent and versioned (e.g., `v1/rideRequest`) to allow future changes.

### WebSocket (STS4) messages (JSON)

**Rider → STS4** (new ride request)
```json
{
  "type": "ride.request",
  "payload": {
    "requestId": "uuid-v4",
    "userId": "rider123",
    "pickup": {"lat": 12.9716, "lon": 77.5946, "address": "From string"},
    "dropoff": {"lat": 12.9352, "lon": 77.6245, "address": "To string"},
    "vehicleClass": "sedan",
    "preferences": {"priority": "normal", "avoidTolls": false},
    "timestamp": 1696000000000
  }
}
````

**STS4 → Matching TCP** (forwarded payload)

```json
{
  "action": "processRide",
  "ride": { /* same payload + rider metadata, wallet balance, surge zone */ }
}
```

**TCP → STS4** (matching result)

```json
{
  "action": "matchingResult",
  "requestId": "uuid-v4",
  "candidates": [
    {
      "driverId": "driver-1",
      "eta": 240,               // seconds
      "distance": 1800,        // meters
      "score": 0.82,
      "price": 120.00,
      "route": { "polyline": "...", "duration": 900, "distance": 7000 }
    },
    ...
  ]
}
```

**STS4 → Driver (notification)**

```json
{
  "type": "ride.offer",
  "payload": {
    "offerId": "offer-uuid",
    "requestId": "uuid-v4",
    "pickup": { "lat": ..., "lon": ..., "address": "..." },
    "dropoff": { "lat": ..., "lon": ..., "address": "..." },
    "estimatedFare": 120.00,
    "routeSummary": { "duration": 900, "distance": 7000 },
    "expiresAt": 1696000050000
  }
}
```

**Driver → STS4** (accept/decline)

```json
{
  "type": "ride.response",
  "payload": {
    "offerId": "offer-uuid",
    "driverId": "driver-1",
    "response": "accept",
    "timestamp": 1696000020000
  }
}
```

---

### TCP Server messages (over TCP socket or structured RPC)

Use length-prefixed JSON or a lightweight RPC protocol. Example request:

**STS4 → TCP**

```json
{
  "cmd": "processRide",
  "data": { /* ride + context + available driver snapshots */ }
}
```

**TCP → STS4**

```json
{
  "cmd": "processed",
  "data": {
    "requestId": "uuid",
    "rankedDrivers": [
      {"driverId":"d1","score":0.91,"price":150,"eta":300,"route":{...}},
      ...
    ]
  }
}
```

---

## Algorithms & Matching Logic

Implement algorithms as pluggable strategies so you can A/B test:

1. **Driver Scoring** (weighted aggregation)

   * `score = w1*norm(loyalty) + w2*norm(acceptanceRate) + w3*norm(rating) + w4*norm(distanceScore) + w5*norm(waitTimePenalty)`
   * Normalize each factor to 0..1. Weights `w1..w5` are configurable.

2. **Pricing**

   * **Base fare**: distance & time formula
   * **Vehicle-based multiplier**: sedan, SUV, bike
   * **Surge pricing**: multiplier applied when demand/supply imbalance in zone > threshold
   * **Rating-based adjustment**: optional discount or premium for driver rating
   * **Zone-based & time-of-day adjustments**

3. **Reassignment / Retry logic**

   * If a driver declines, record reason (if provided), decrement loyalty points, mark `candidate.excludedUntil` for N minutes depending on reason.
   * Continue down ranked list until accepted or timeout. On driver cancel after accept: mark a penalty, re-run matching with updated parameters.

4. **Driver loyalty & penalties**

   * Maintain `loyaltyPoints` per driver; increase for accepted & completed rides, decrease for cancellations/no-shows or prolonged idle declines.
   * Use loyalty as a positive signal in scoring or to prefer high-loyalty drivers during dispatch.

5. **Acceptance ratio**

   * Keep rolling window (e.g., last 30 days / 100 offers) to compute acceptance ratio.

6. **Distance & ETA**

   * Use OSRM or internal route generator to estimate ETA; favor lower ETA but balance fairness.

---

## Persistence & DB model (summary)

Use MongoDB collections (or relational DB + indexes) to persist:

* `users` (riders & drivers; profile, clerk id)
* `vehicles` (vehicle docs, verification status)
* `rides` (ride requests, status, timestamps, assignedDriver)
* `offers` (which drivers were offered, offer state)
* `wallets` (balances, transactions)
* `driver_stats` (loyaltyPoints, acceptanceRate, ratings)
* `logs` (audit trail, matching requests/responses)

Keep indexes on `driver.location` (geo index), `rides.requestId`, `offers.offerId`, and `users.userId`.

---

## Getting started (dev)

### Prerequisites

* Java 11+ and Spring Tools 4 (STS4) for the STS4 server project.
* Maven wrapper (`./mvnw`) or Maven installed.
* Node.js (for frontend) — frontends are separate repos.
* MongoDB running (local or Atlas).
* Matching TCP server runtime (Java / Kotlin / Node / Go implementation — whichever you used).
* OSRM server or routing service (if the TCP server computes routes using OSRM).

### Environment variables (examples)

**STS4 (Spring Boot) `.env` / application.properties**:

```
SERVER_PORT=8080
MONGODB_URI=mongodb://localhost:27017/onlineridesharing
JWT_SECRET=some_secret
CLERK_PUBLIC_KEY=clerk_pk_...
CLERK_SECRET=clerk_sk_...
WEBSOCKET_PATH=/ws
TCP_SERVER_HOST=127.0.0.1
TCP_SERVER_PORT=6000
```

**Matching TCP server config**:

```
LISTEN_PORT=6000
OSRM_BASE_URL=http://localhost:5000
SEARCH_RADIUS_METERS=5000
WEIGHT_LOYALTY=0.25
WEIGHT_ACCEPT=0.20
WEIGHT_RATING=0.25
WEIGHT_DISTANCE=0.20
WEIGHT_IDLE=0.10
SURGE_THRESHOLD=1.3
```

> **Security**: store secrets with a secret manager in production; never commit API keys.

---

## Run STS4 Spring Boot server

From the STS4 project root (or command line):

```bash
# linux / mac
./mvnw spring-boot:run

# or with maven
mvn spring-boot:run

# windows (if using mvnw.cmd)
./mvnw.cmd spring-boot:run
```

* STS4 will expose REST APIs (e.g., `http://localhost:8080/api/...`) and WebSocket endpoint (e.g., `ws://localhost:8080/ws`).
* Use STS4 to debug, set breakpoints, and inspect WebSocket sessions.

---

## Run Matching TCP Server

Depends on your implementation language. Example for a Java-based TCP server:

```bash
# if it is a separate Maven module
cd matching-tcp-server
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=6000"
```

Or run the compiled JAR:

```bash
java -jar matching-tcp-server/target/matching-tcp-server.jar --server.port=6000
```

The STS4 server must be able to open a TCP connection (or the TCP server must expose a client-accessible endpoint) at the configured host/port.

---

## Endpoints & WebSocket topics

### REST (examples)

* `POST /api/rides` — create ride (internal fallback; normally created via WebSocket)
* `GET  /api/rides/{id}` — ride status
* `GET  /api/drivers/{id}/stats` — driver metrics
* `POST /api/admin/force-reassign` — admin reassign tools
* `GET  /api/health` — health check

### WebSocket topics (suggested)

* `ride.request` — Rider sends a ride request
* `ride.update.{rideId}` — subscribe to ride updates
* `driver.offer.{driverId}` — STS4 sends offers to driver
* `driver.response` — driver accepts/declines
* `system.notifications` — general system notifications

Implement authentication handshake on connect: client sends token; server validates and attaches userId to session.

---

## Testing flows

1. **Simulate Rider request**

   * Connect via WebSocket client (Postman / wscat / test page).
   * Send `ride.request` payload. Confirm STS4 persists ride and forwards to TCP server.

2. **Simulate Matching Server response**

   * Either run the real TCP server or a test stub that returns precomputed `rankedDrivers` JSON.
   * Confirm offers sent to the respective driver WebSocket sessions.

3. **Driver accept**

   * Driver WebSocket sends `ride.response` with `accept`.
   * STS4 marks ride as `ASSIGNED`, notifies the rider, and updates DB.

4. **Driver cancel**

   * Handle cancel: mark event, decrement loyalty, and trigger re-matching.

5. **Edge-cases**

   * No drivers found: STS4 notifies rider with helpful message and retry options.
   * Network partition: STS4 should attempt reconnection and mark sessions stale after configurable timeout.

---

## Deployment notes

* Run STS4 behind a reverse proxy (Nginx) and enable secure WebSocket TLS (wss).
* Scaling:

  * STS4 horizontally: maintain sticky sessions or use a centralized session store (Redis) to map `userId->connection` if you run multiple instances. WebSocket routing via sticky session load balancer or use a message broker to fan-out offers.
  * Matching TCP server: can be scaled horizontally; use a job queue (Kafka/RabbitMQ) or RPC gateway to distribute matching tasks.
* Monitoring: instrument metrics for matching latency, offers/second, accept ratio, and loyalty drift.

---

## Troubleshooting

* **WebSocket not connecting**: check endpoint path, allowed origins, and TLS. Verify Clerk token validation logs on server.
* **No matching results**: confirm TCP server is running and STS4 can reach it (telnet host:port).
* **Offers not delivered**: verify driver sessions active and `userId->session` mapping is correct.
* **High matching latency**: profile matching algorithms, cache precomputed nearest-driver sets, consider in-memory geospatial index (Redis GEO).
* **DB contention**: optimize indexes, use capped collections for logs if necessary.

---

## Contributing

* Keep matching logic modular (Strategy pattern) so new heuristics can be added with minimal changes.
* Write unit tests for scoring & pricing algorithms.
* Add integration tests that simulate end-to-end WebSocket → TCP → driver response flows.

---

## License & Credits

* Use your preferred license (MIT recommended).
* Design & module breakdown inspired by internal spec. 

---

## Contact

Open issues or PRs in this repository. For design questions, reference the spec document included in the project.

