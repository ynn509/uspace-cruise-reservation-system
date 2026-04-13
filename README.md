
# USpace Cruise Reservation System



## Context
This project was developed as part of a university course.

The base application was provided, and I implemented several features and tests across multiple iterations (TP1 to TP4).

---

## My Contributions
- Implemented new features (crew member, excursions, user accounts, reservation cost)
- Applied object-oriented design principles (SOLID, abstraction, dependency inversion)
- Integrated Hibernate with H2 database
- Developed unit, component, and end-to-end tests (JUnit, Cucumber, Cypress)
- Improved code quality and refactored existing components

---

## Overview

USpace is a space cruise reservation system that allows users to:
- Book cruises
- View booking details
- View cruise details
- Manage additional features such as excursions and user accounts

The project follows clean architecture principles and applies best practices in software design and testing.

---

## Prerequisites

- Java JDK (version 18 or higher)
- Maven 3
- Node.js (version 18.3 or higher)
- npm (version 10 or higher)

---

## Running the Project

### Backend

Start the backend server:

```bash
mvn clean install
mvn exec:java
````

Server runs at:
[http://localhost:8181](http://localhost:8181)

Health check:
[http://localhost:8181/health](http://localhost:8181/health)

---

### Frontend

```bash
cd webapp
npm install
npm run dev
```

Application runs at:
[http://localhost:5173](http://localhost:5173)

---

## Running Tests

### Backend tests

```bash
mvn test
```

### Frontend unit tests

```bash
npm run test:unit
```

### End-to-End tests (Cypress)

On Windows:

```bash
./run-e2e.sh
```

On Linux:

```bash
source run-e2e.sh
```

This script:

* Starts backend (in-memory DB)
* Starts frontend
* Runs Cypress tests

---

## Features

### Book a Cruise

Only one cruise is currently available:

* **JUPITER_MOONS_EXPLORATION_2085**
* Departure: 2085-01-25 at 12:00

#### Conditions

* Cabins must be available
* Booking must be before departure
* At least one traveler is required

#### Endpoint

POST `cruises/{cruiseId}/bookings`

Example:

```json
{
  "travelers": [
    {
      "name": "John Doe",
      "category": "CHILD"
    },
    {
      "name": "Jane Doe",
      "category": "ADULT"
    }
  ],
  "cabinType": "STANDARD",
  "bookingDateTime": "2084-04-08T12:30"
}
```

---

### Get Booking Details

GET `cruises/{cruiseId}/bookings/{bookingId}`

---

### Get Cruise Details

GET `cruises/{cruiseId}`

---

## Possible Responses

* **200 OK**
* **201 CREATED**
* **400 BAD REQUEST**
* **404 NOT FOUND**

---

## Tech Stack

* Java (OOP, SOLID principles)
* Maven
* Hibernate + H2 database
* REST APIs (Jersey)
* JUnit / Mockito
* Cucumber (BDD tests)
* Cypress (End-to-End tests)
* Vue.js (Frontend)

---

## Key Concepts Applied

* Object-Oriented Design
* Dependency Inversion
* Abstraction
* Polymorphism
* Clean Code principles
* Test Pyramid (unit, integration, E2E)

---

## Notes

* The project was originally developed in French, but key elements have been summarized in English for clarity.
* The base code was provided as part of the course; my work focused on extending and improving the system.

```

