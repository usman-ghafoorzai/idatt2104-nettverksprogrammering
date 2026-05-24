# IDATT2104 Nettverksprogrammering

This repository contains my coursework for **IDATT2104 Nettverksprogrammering** at NTNU.

It includes Java networking assignments, socket/server exercises, a small Spring Boot backend with a simple frontend, a WebSocket exercise, and bundled report PDFs. This is a student coursework repository, not a production service.

## Course Context

- **Course:** IDATT2104 Nettverksprogrammering
- **Institution:** NTNU
- **Type:** Completed coursework archive

## Repository Structure

| Path | Description |
| --- | --- |
| `datakommunikasjon/ovinger/` | Report PDFs for data communication assignments |
| `nettverksprogrammering/ovinger/nPOving1-2/` | Threading and worker-pattern exercises |
| `nettverksprogrammering/ovinger/nPOving3/` | TCP echo and simple HTTP server exercises |
| `nettverksprogrammering/ovinger/nPOving4/` | UDP and HTTPS server exercise |
| `nettverksprogrammering/ovinger/nPOving5-Backend/` | Spring Boot backend exercise |
| `nettverksprogrammering/ovinger/nPOving5-Frontend/` | Simple static frontend |
| `nettverksprogrammering/ovinger/nPOving6-WebSocketServer/` | WebSocket server exercise |
| [`docs/ASSIGNMENT-INDEX.md`](docs/ASSIGNMENT-INDEX.md) | Assignment overview and folder map |

For a more detailed assignment overview, see [`docs/ASSIGNMENT-INDEX.md`](docs/ASSIGNMENT-INDEX.md).

## Technologies Covered

- Java networking with `ServerSocket`, `Socket`, and UDP sockets
- Basic HTTP server handling
- HTTPS server setup with Java SSL APIs
- Multi-threading and worker/event-loop patterns
- Spring Boot REST backend
- Simple browser frontend using the Fetch API
- WebSocket protocol handling

## How to Run

The repository is split into separate exercise folders, so each assignment is run from its own directory.

Plain Java exercises (`nPOving1-2`, `nPOving3`, `nPOving4`):

```bash
cd <assignment-folder>
javac *.java
java <MainClass>
```

Spring Boot backend (`nPOving5-Backend`):

```bash
cd nettverksprogrammering/ovinger/nPOving5-Backend
.\mvnw.cmd spring-boot:run
```

On macOS/Linux:

```bash
cd nettverksprogrammering/ovinger/nPOving5-Backend
./mvnw spring-boot:run
```

Frontend (`nPOving5-Frontend`):

```bash
cd nettverksprogrammering/ovinger/nPOving5-Frontend
# Open index.html in a browser (backend should run on localhost:8080)
```

WebSocket server (`nPOving6-WebSocketServer`):

```bash
cd nettverksprogrammering/ovinger/nPOving6-WebSocketServer
mvn compile
# Run main class: edu.ntnu.WebSocketServer
```

## Repository Hygiene Note

This repository has been cleaned to remove tracked local/IDE/build artifacts (`.idea`, `*.iml`, `out`).
If similar files appear again during local work, they should stay untracked.

For the HTTPS assignment (`nPOving4`), `keystore.jks` is kept as a local demo coursework artifact.
It is not meant to represent production-safe certificate handling.

## Limitations

- Coursework snapshot with separate exercises, not one unified application
- Some paths/ports/passwords are hardcoded for demo purposes in assignment code
- Setup and run steps vary by folder and are intentionally lightweight

## Academic Integrity

This repository is shared for learning and portfolio documentation.
If you are taking the same/similar course, use it only as reference and implement your own solutions.

## Status

Completed coursework archive with documentation and repository cleanup.
