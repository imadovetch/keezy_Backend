# Reservation System - Quick API Reference

## Status Transitions

### Guest Workflow
1. **Create Reservation** → Status: `PENDING`
   ```
   POST /api/reservations
   Body: { serviceId, hotelId, reservationDate, reservationTime, numberOfGuests, notes }
   ```

2. **View My Reservations**
   ```
   GET /api/reservations/my-reservations
   ```

3. **Cancel Reservation** (from PENDING, IN_PROGRESS, or BLOCKED)
   ```
   DELETE /api/reservations/{reservationId}
   ```

4. **Unblock Reservation** (from BLOCKED back to PENDING)
   ```
   POST /api/reservations/{reservationId}/unblock
   ```

### Staff Workflow
1. **View Pending Reservations** (as Owner)
   ```
   GET /api/reservations/hotel/{hotelId}
   ```

2. **Start Processing** (PENDING → IN_PROGRESS)
   ```
   POST /api/reservations/{reservationId}/start
   ```

3. **Complete Service** (IN_PROGRESS → DONE)
   ```
   POST /api/reservations/{reservationId}/complete
   ```

4. **Block Reservation** (if issue occurs)
   ```
   POST /api/reservations/{reservationId}/block
   Body: { reason: "Guest did not show up" }
   ```

5. **Unblock Reservation** (BLOCKED → PENDING)
   ```
   POST /api/reservations/{reservationId}/unblock
   ```

## Complete Request Examples

### 1. Guest Creates Reservation
```bash
curl -X POST http://localhost:8080/api/reservations \
  -H "Authorization: Bearer eyJhbGc..." \
  -H "Content-Type: application/json" \
  -d '{
    "serviceId": "550e8400-e29b-41d4-a716-446655440000",
    "hotelId": "550e8400-e29b-41d4-a716-446655440111",
    "reservationDate": "2026-02-15",
    "reservationTime": "10:00",
    "numberOfGuests": 2,
    "notes": "Need vegetarian options"
  }'
```

**Response (201 Created):**
```json
{
  "messages": [],
  "errors": [],
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440222",
    "guestId": "550e8400-e29b-41d4-a716-446655440333",
    "guestEmail": "guest@example.com",
    "serviceName": "Breakfast",
    "serviceId": "550e8400-e29b-41d4-a716-446655440000",
    "hotelName": "Grand Hotel",
    "hotelId": "550e8400-e29b-41d4-a716-446655440111",
    "reservationDate": "2026-02-15",
    "reservationTime": "10:00",
    "status": "PENDING",
    "numberOfGuests": 2,
    "notes": "Need vegetarian options",
    "createdAt": 1707936000000,
    "updatedAt": 1707936000000
  },
  "success": true
}
```

### 2. Staff Starts Reservation
```bash
curl -X POST http://localhost:8080/api/reservations/550e8400-e29b-41d4-a716-446655440222/start \
  -H "Authorization: Bearer eyJhbGc..." \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```json
{
  "messages": [],
  "errors": [],
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440222",
    "guestId": "550e8400-e29b-41d4-a716-446655440333",
    "guestEmail": "guest@example.com",
    "serviceName": "Breakfast",
    "serviceId": "550e8400-e29b-41d4-a716-446655440000",
    "hotelName": "Grand Hotel",
    "hotelId": "550e8400-e29b-41d4-a716-446655440111",
    "reservationDate": "2026-02-15",
    "reservationTime": "10:00",
    "status": "IN_PROGRESS",
    "numberOfGuests": 2,
    "notes": "Need vegetarian options",
    "createdAt": 1707936000000,
    "updatedAt": 1707936005000
  },
  "success": true
}
```

### 3. Staff Completes Reservation
```bash
curl -X POST http://localhost:8080/api/reservations/550e8400-e29b-41d4-a716-446655440222/complete \
  -H "Authorization: Bearer eyJhbGc..." \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```json
{
  "messages": [],
  "errors": [],
  "data": {
    "status": "DONE",
    ...
  },
  "success": true
}
```

### 4. Staff Blocks Reservation
```bash
curl -X POST http://localhost:8080/api/reservations/550e8400-e29b-41d4-a716-446655440222/block \
  -H "Authorization: Bearer eyJhbGc..." \
  -H "Content-Type: application/json" \
  -d '{
    "reason": "Guest did not show up - no show"
  }'
```

**Response (200 OK):**
```json
{
  "messages": [],
  "errors": [],
  "data": {
    "status": "BLOCKED",
    "notes": "Need vegetarian options | Blocked: Guest did not show up - no show",
    ...
  },
  "success": true
}
```

### 5. Guest Views All Reservations
```bash
curl -X GET http://localhost:8080/api/reservations/my-reservations \
  -H "Authorization: Bearer eyJhbGc..."
```

**Response (200 OK):**
```json
{
  "messages": [],
  "errors": [],
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440222",
      "status": "DONE",
      ...
    },
    {
      "id": "550e8400-e29b-41d4-a716-446655440444",
      "status": "PENDING",
      ...
    }
  ],
  "success": true
}
```

### 6. Guest Cancels Reservation
```bash
curl -X DELETE http://localhost:8080/api/reservations/550e8400-e29b-41d4-a716-446655440222 \
  -H "Authorization: Bearer eyJhbGc..."
```

**Response (200 OK):**
```json
{
  "messages": [],
  "errors": [],
  "data": {
    "status": "CANCELLED",
    ...
  },
  "success": true
}
```

### 7. Owner Views Hotel Reservations
```bash
curl -X GET http://localhost:8080/api/reservations/hotel/550e8400-e29b-41d4-a716-446655440111 \
  -H "Authorization: Bearer eyJhbGc..."
```

**Response (200 OK):**
```json
{
  "messages": [],
  "errors": [],
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440222",
      "status": "DONE",
      ...
    },
    {
      "id": "550e8400-e29b-41d4-a716-446655440444",
      "status": "IN_PROGRESS",
      ...
    }
  ],
  "success": true
}
```

## Error Examples

### Invalid Date Format
```
POST /api/reservations
Body: { ..., "reservationDate": "2026-02-15 10:00", ... }
```

**Response (400 Bad Request):**
```json
{
  "messages": [],
  "errors": ["Date must be in yyyy-MM-dd format"],
  "data": null,
  "success": false
}
```

### Double Booking
```
POST /api/reservations
Body: { serviceId, hotelId, reservationDate: "2026-02-15", reservationTime: "10:00", ... }
(when 10:00 slot is already booked)
```

**Response (400 Bad Request):**
```json
{
  "messages": [],
  "errors": ["This time slot has already been reserved. Please choose another time"],
  "data": null,
  "success": false
}
```

### Unauthorized Access
```
POST /api/reservations/{reservationId}/start
Authorization: Bearer {guest_token}  ← Guest trying to start (STAFF only)
```

**Response (403 Forbidden):**
```
Access Denied: The user does not have the required role(s): ROLE_STAFF
```

### Invalid Status Transition
```
POST /api/reservations/{reservationId}/complete
(when status is PENDING, not IN_PROGRESS)
```

**Response (400 Bad Request):**
```json
{
  "messages": [],
  "errors": ["Only in-progress reservations can be completed. Current status: PENDING"],
  "data": null,
  "success": false
}
```

### Not Found
```
GET /api/reservations/550e8400-invalid-uuid
```

**Response (404 Not Found):**
```json
{
  "messages": [],
  "errors": ["Reservation not found"],
  "data": null,
  "success": false
}
```

## Role Access Summary

| Endpoint | Method | GUEST | STAFF | OWNER |
|----------|--------|-------|-------|-------|
| /reservations | POST | ✓ | - | - |
| /reservations/my-reservations | GET | ✓ | - | - |
| /reservations/my-active-reservations | GET | ✓ | - | - |
| /reservations/{id} | GET | ✓* | - | ✓ |
| /reservations/{id} | PUT | ✓* | - | - |
| /reservations/{id} | DELETE | ✓* | - | - |
| /reservations/{id}/start | POST | - | ✓ | - |
| /reservations/{id}/complete | POST | - | ✓ | - |
| /reservations/{id}/block | POST | - | ✓ | - |
| /reservations/{id}/unblock | POST | ✓* | ✓ | ✓ |
| /reservations/{id}/confirm | POST | - | - | ✓ |
| /reservations/hotel/{id} | GET | - | - | ✓ |
| /reservations/service/{id} | GET | - | - | ✓ |

*Only for own reservations

## Important Notes

1. **Token Format**: Include token in `Authorization: Bearer {token}` header
2. **Content-Type**: Always use `Content-Type: application/json`
3. **Date/Time**: Dates are `yyyy-MM-dd`, times are `HH:mm` (24-hour format)
4. **Hotel UUID**: Use `uuid` field from hotel, not `id`
5. **Timestamps**: All `createdAt`/`updatedAt` are in milliseconds since epoch
6. **Status Values**: PENDING, IN_PROGRESS, DONE, CANCELLED, BLOCKED
7. **Error Responses**: Check `errors` array for validation messages
