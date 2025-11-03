# Real Estate Platform - API Documentation

This document describes the publicly exposed REST API for the Real Estate Platform, routed through the API Gateway.

### Base URL
- Local development via API Gateway: `http://localhost:9090`

### Services and Routes (via API Gateway)
- `appointment-service` → `/api/appointments/**`
- `auth-service` → `/api/auth/**`
- `property-service` → `/api/properties/**`

CORS is allowed for origin `http://localhost:3000`.

---

## Authentication

The Auth Service integrates with Firebase. Some `auth` endpoints are public; other `auth` endpoints require an `Authorization: Bearer <ID_TOKEN>` header, where `<ID_TOKEN>` is a Firebase ID token.

- Public: `POST /api/auth/register`, `POST /api/auth/login`
- Protected: `POST /api/auth/logout`, `GET /api/auth/profile`, `PUT /api/auth/profile`

Note: Other services (appointments, properties) currently do not enforce authentication at the service level, but your deployment/gateway policies may still require headers.

---

## Conventions

- Content-Type for requests with body: `application/json`
- Timestamps use ISO-8601 strings (e.g., `2025-11-03T10:15:30`)
- IDs:
  - Auth `user.id`: string (MongoDB-generated)
  - Appointment `id`: string representation of MongoDB ObjectId
  - Appointment `buyerId`, `sellerId`, `propertyId`: UUID strings
  - Property `id`: string (MongoDB-generated)

---

## Auth Service (`/api/auth`)

### POST `/api/auth/register` (Public)
Register or link a user with Firebase.

Request body:
```json
{
  "idToken": "FIREBASE_ID_TOKEN",
  "name": "John Doe",
  "email": "john@example.com",
  "role": "BUYER", 
  "imageUrl": "https://example.com/avatar.jpg"
}
```

- `role` enum: `BUYER` | `SELLER` | `ADMIN`

Response (201 Created on success; 400 on failure):
```json
{
  "message": "string",
  "user": {
    "id": "string",
    "firebaseUid": "string",
    "name": "string",
    "email": "string",
    "role": "BUYER",
    "imageUrl": "string",
    "status": "ACTIVE",
    "createdAt": "2025-11-03T10:15:30",
    "updatedAt": "2025-11-03T10:15:30"
  },
  "accessToken": "string",
  "success": true
}
```

### POST `/api/auth/login` (Public)
Login using a Firebase ID token.

Request body:
```json
{
  "idToken": "FIREBASE_ID_TOKEN"
}
```

Response (200 OK on success; 401 on failure):
```json
{
  "message": "string",
  "user": { ...same as above... },
  "accessToken": "string",
  "success": true
}
```

### POST `/api/auth/logout` (Protected)
Invalidate current session.

Headers:
- `Authorization: Bearer <ID_TOKEN>`

Response (200 OK on success; 400 on failure):
```json
{
  "message": "string",
  "user": { ... },
  "accessToken": "string",
  "success": true
}
```

### GET `/api/auth/profile` (Protected)
Get current user profile.

Headers:
- `Authorization: Bearer <ID_TOKEN>`

Response (200 OK; 401 on failure):
```json
{
  "id": "string",
  "firebaseUid": "string",
  "name": "string",
  "email": "string",
  "role": "BUYER",
  "imageUrl": "string",
  "status": "ACTIVE",
  "createdAt": "2025-11-03T10:15:30",
  "updatedAt": "2025-11-03T10:15:30"
}
```

### PUT `/api/auth/profile` (Protected)
Update profile.

Headers:
- `Authorization: Bearer <ID_TOKEN>`

Request body:
```json
{
  "name": "New Name",
  "imageUrl": "https://example.com/new-avatar.jpg"
}
```

Response (200 OK; 401 on failure):
```json
{
  "id": "string",
  "firebaseUid": "string",
  "name": "New Name",
  "email": "string",
  "role": "BUYER",
  "imageUrl": "https://example.com/new-avatar.jpg",
  "status": "ACTIVE",
  "createdAt": "2025-11-03T10:15:30",
  "updatedAt": "2025-11-03T10:15:30"
}
```

---

## Property Service (`/api/properties`)

Note: Properties are currently unauthenticated at the service level.

### POST `/api/properties`
Create (or update) a property.

Request body:
```json
{
  "title": "2BHK Apartment",
  "location": "New York",
  "price": 250000.0,
  "description": "Spacious and well-lit."
}
```

Response (200 OK):
```json
{
  "id": "string",
  "title": "2BHK Apartment",
  "location": "New York",
  "price": 250000.0,
  "description": "Spacious and well-lit."
}
```

### GET `/api/properties`
List all properties.

Response (200 OK):
```json
[
  {
    "id": "string",
    "title": "string",
    "location": "string",
    "price": 0,
    "description": "string"
  }
]
```

### GET `/api/properties/{id}`
Get a property by ID.

Response (200 OK):
```json
{
  "id": "string",
  "title": "string",
  "location": "string",
  "price": 0,
  "description": "string"
}
```

### DELETE `/api/properties/{id}`
Delete a property by ID.

Response (200 OK):
```text
Property deleted with id: {id}
```

---

## Appointment Service (`/api/appointments`)

Note: Endpoints are currently unauthenticated at the service level.

### POST `/api/appointments`
Create an appointment.

Request body:
```json
{
  "buyerId": "00000000-0000-0000-0000-000000000000",
  "sellerId": "00000000-0000-0000-0000-000000000000",
  "propertyId": "00000000-0000-0000-0000-000000000000",
  "scheduledAt": "2025-12-01T10:00:00",
  "notes": "Please confirm availability."
}
```
Validation:
- `buyerId`, `sellerId`, `propertyId`: required UUIDs
- `scheduledAt`: required, must be in the future
- `notes`: optional, max 1000 chars

Response (200 OK):
```json
{
  "id": "6565f9c9c98d2a1f3a0e8b12",
  "buyerId": "uuid",
  "sellerId": "uuid",
  "propertyId": "uuid",
  "scheduledAt": "2025-12-01T10:00:00",
  "status": "PENDING",
  "notes": "Please confirm availability.",
  "createdAt": "2025-11-03T10:15:30",
  "updatedAt": null,
  "canceledAt": null
}
```
`status` enum: `PENDING` | `CONFIRMED` | `RESCHEDULED` | `CANCELLED` | `COMPLETED`

### GET `/api/appointments`
List all appointments.

Response (200 OK):
```json
[
  {
    "id": "6565f9c9c98d2a1f3a0e8b12",
    "buyerId": "uuid",
    "sellerId": "uuid",
    "propertyId": "uuid",
    "scheduledAt": "2025-12-01T10:00:00",
    "status": "PENDING",
    "notes": "string",
    "createdAt": "2025-11-03T10:15:30",
    "updatedAt": null,
    "canceledAt": null
  }
]
```

### GET `/api/appointments/{id}`
Get appointment by ID.

Response (200 OK):
```json
{
  "id": "6565f9c9c98d2a1f3a0e8b12",
  "buyerId": "uuid",
  "sellerId": "uuid",
  "propertyId": "uuid",
  "scheduledAt": "2025-12-01T10:00:00",
  "status": "PENDING",
  "notes": "string",
  "createdAt": "2025-11-03T10:15:30",
  "updatedAt": null,
  "canceledAt": null
}
```

### PUT `/api/appointments/{id}`
Update an appointment (same shape as create).

Request body:
```json
{
  "buyerId": "uuid",
  "sellerId": "uuid",
  "propertyId": "uuid",
  "scheduledAt": "2025-12-01T10:00:00",
  "notes": "Rescheduling."
}
```

Response (200 OK): AppointmentResponse (same schema as above)

### DELETE `/api/appointments/{id}`
Soft delete (sets status to `CANCELLED`).

Response (204 No Content)

---

## Quick cURL Examples

- Register:
```bash
curl -X POST http://localhost:9090/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"idToken":"FIREBASE_ID_TOKEN","name":"John","email":"john@example.com","role":"BUYER"}'
```

- Login:
```bash
curl -X POST http://localhost:9090/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"idToken":"FIREBASE_ID_TOKEN"}'
```

- Get Profile:
```bash
curl -X GET http://localhost:9090/api/auth/profile \
  -H "Authorization: Bearer FIREBASE_ID_TOKEN"
```

- Create Property:
```bash
curl -X POST http://localhost:9090/api/properties \
  -H "Content-Type: application/json" \
  -d '{"title":"2BHK","location":"NY","price":250000,"description":"Nice"}'
```

- Create Appointment:
```bash
curl -X POST http://localhost:9090/api/appointments \
  -H "Content-Type: application/json" \
  -d '{"buyerId":"00000000-0000-0000-0000-000000000000","sellerId":"00000000-0000-0000-0000-000000000000","propertyId":"00000000-0000-0000-0000-000000000000","scheduledAt":"2025-12-01T10:00:00","notes":"Please confirm"}'
```

---

## Notes

- API Gateway runs on port `9090`, with service discovery via Eureka.
- Only `auth` endpoints enforce Firebase token validation. Adjust security policies as needed for other services if required by your deployment.
- Update the CORS allowed origins in gateway if your frontend origin differs from `http://localhost:3000`.
