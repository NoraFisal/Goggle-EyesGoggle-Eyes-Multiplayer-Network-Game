# Goggle-Eyes 👀

## Overview

Goggle-Eyes is a real-time multiplayer network game developed using Java and Socket Programming. The system enables multiple players to connect to a central server, join a waiting room, compete in interactive gameplay, and track scores through a live leaderboard.

The project demonstrates client-server communication, multithreading, graphical user interface design, and real-time game synchronization.

---

## Features

### Player Registration

* Create a username and join the game server.
* Manage connected players dynamically.

### Connected Players Lobby

* View all currently connected players.
* Start multiplayer sessions.

### Waiting Room System

* Automatic waiting room management.
* Countdown timer before game launch.
* Supports multiple players joining simultaneously.

### Real-Time Multiplayer Gameplay

* Interactive number-recognition challenge.
* Synchronized gameplay across connected clients.
* Multiple competitive rounds.

### Live Score Tracking

* Real-time score updates.
* Automatic score synchronization between clients.

### Leaderboard System

* Dynamic ranking of players.
* Automatic winner detection and game-over handling.

### Game Management

* Player disconnect handling.
* Waiting room updates.
* Session reset after game completion.

---

## Technologies Used

### Programming Language

* Java

### Networking

* Java Sockets
* TCP Communication
* Client-Server Architecture

### GUI Development

* Java Swing

### Concepts Applied

* Multithreading
* Real-Time Communication
* Event-Driven Programming
* Network Programming
* Multiplayer Game Logic

---

## System Architecture

Client Application
↓
Socket Communication
↓
Game Server
↓
Player Management
↓
Waiting Room
↓
Gameplay Synchronization
↓
Score Tracking & Leaderboard

---

## Project Components

### GameServer

Responsible for:

* Managing client connections
* Synchronizing players
* Tracking scores
* Handling waiting rooms
* Broadcasting game events

### GameClient

Responsible for:

* Player interaction
* Game interface
* Gameplay logic
* Score submission
* Timer management

### RegistrationFrame

* User registration interface

### ConnectedPlayersFrame

* Displays connected players
* Launches multiplayer sessions

### WaitingRoomFrame

* Manages player waiting state
* Countdown before game start

---

## Learning Outcomes

Through this project, the following concepts were implemented and practiced:

* Network Programming
* Client-Server Systems
* Socket Communication
* Multiplayer Game Development
* Java Swing GUI Design
* Real-Time Data Synchronization
* Concurrent Programming

---

## Future Improvements

* Online matchmaking system
* Database integration
* Player profiles and statistics
* Chat system
* Enhanced game modes
* Authentication and login system
* Cloud-hosted game server

---

## Author

Nora Albyahi

AI & Information Technology Student

King Saud University

Interested in AI, Game Intelligence, Data Science, and Intelligent Digital Experiences.
