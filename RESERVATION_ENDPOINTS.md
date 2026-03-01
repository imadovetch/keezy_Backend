# Reservation System - Complete Endpoint Reference

## Quick Links
- [Guest Endpoints](#guest-endpoints)
- [Staff Endpoints](#staff-endpoints)
- [Owner Endpoints](#owner-endpoints)
- [Public Endpoints](#public-endpoints)
- [Status Codes](#status-codes)
- [Error Messages](#error-messages)

---

## Guest Endpoints

### 1. Create Reservation
```
POST /api/reservations
Authorization: Bearer {guest_token}
Content-Type: application/json

{
  "serviceId": "service-uuid",
  "hotelId": "hotel-uuid",
  "reservationDate": "2026-02-15",
  "reservationTime": "10:00",
  "numberOfGuests": 2,
  "notes": "Special dietary requirements"
}

Response: 201 Created
{
  "messages": [],
  "errors": [],
  "data": {
    "id": "reservation-uuid",
    "status": "PENDING",
    ...
  },
  "success": true
}
```

### 2. Get All My Reservations
```
GET /api/reservations/my-reservations
Authorization: Bearer {guest_token}

Response: 200 OK
{
  "messages": [],
  "errors": [],
  "data": [
    {
      "id": "reservation-uuid",
      "status": "PENDING",
      ...
    }
  ],
  "success": true
}
```

### 3. Get Active Reservations (PENDING/CONFIRMED only)
```
GET /api/reservations/my-active-reservations
Authorization: Bearer {guest_token}

Response: 200 OK
{
  "data": [
    // Only PENDING and CONFIRMED reservations
  ],
  "success": true
}
```

### 4. Get Reservation Details
```
GET /api/reservations/{reservationId}
Authorization: Bearer {guest_token}

Response: 200 OK
{
  "data": {
    "id": "reservation-uuid",
    "guestId": "guest-uuid",
    "guestEmail": "guest@example.com",
    "serviceName": "Breakfast",
    "serviceId": "service-uuid",
    "hotelName": "Grand Hotel",
    "hotelId": "hotel-uuid",
    "reservationDate": "2026-02-15",
    "reservationTime": "10:00",
    "status": "PENDING",
    "numberOfGuests": 2,
    "notes": "Special dietary requirements",
    "createdAt": 1707936000000,
    "updatedAt": 1707936000000
  },
  "success": true
}
```

### 5. Update My Reservation
```
PUT /api/reservations/{reservationId}
Authorization: Bearer {guest_token}
Content-Type: application/json

{
  "numberOfGuests": 3,
  "notes": "Updated dietary requirements"
}

Response: 200 OK
{
  "data": {
    // Updated reservation (only PENDING can be updated)
  },
  "success": true
}
```

### 6. Cancel My Reservation
```
DELETE /api/reservations/{reservationId}
Authorization: Bearer {guest_token}

Response: 200 OK
{
  "data": {
    "status": "CANCELLED",
    ...
  },
  "success": true
}
```

### 7. Unblock My Reservation
```
POST /api/reservations/{reservationId}/unblock
Authorization: Bearer {guest_token}

Response: 200 OK
{
  "data": {
    "status": "PENDING",  // Restored to PENDING
    ...
  },
  "success": true
}
```

---

## Staff Endpoints

### 1. Start Processing (PENDING → IN_PROGRESS)
```
POST /api/reservations/{reservationId}/start
Authorization: Bearer {staff_token}

Response: 200 OK
{
  "data": {
    "status": "IN_PROGRESS",
    ...
  },
  "success": true
}
```

### 2. Complete Service (IN_PROGRESS → DONE)
```
POST /api/reservations/{reservationId}/complete
Authorization: Bearer {staff_token}

Response: 200 OK
{
  "data": {
    "status": "DONE",
    ...
  },
  "success": true
}
```

### 3. Block Reservation
```
POST /api/reservations/{reservationId}/block
Authorization: Bearer {staff_token}
Content-Type: application/json

{
  "reason": "Guest did not show up - no show"
}

Response: 200 OK
{
  "data": {
    "status": "BLOCKED",
    "notes": "Original notes | Blocked: Guest did not show up - no show",
    ...
  },
  "success": true
}
```

### 4. Unblock Reservation
```
POST /api/reservations/{reservationId}/unblock
Authorization: Bearer {staff_token}

Response: 200 OK
{
  "data": {
    "status": "PENDING",  // Restored to PENDING
    ...
  },
  "success": true
}
```

---

## Owner Endpoints

### 1. Get All Hotel Reservations
```
GET /api/reservations/hotel/{hotelId}
Authorization: Bearer {owner_token}

Response: 200 OK
{
  "data": [
    {
      "id": "reservation-uuid",
      "status": "PENDING",
      ...
    },
    {
      "id": "reservation-uuid-2",
      "status": "IN_PROGRESS",
      ...
    }
  ],
  "success": true
}
```

### 2. Get All Service Reservations
```
GET /api/reservations/service/{serviceId}
Authorization: Bearer {owner_token}

Response: 200 OK
{
  "data": [
    // All reservations for this service
  ],
  "success": true
}
```

### 3. Start Processing
```
POST /api/reservations/{reservationId}/confirm
Authorization: Bearer {owner_token}

Response: 200 OK
{
  "data": {
    "status": "IN_PROGRESS",  // Moves to IN_PROGRESS
    ...
  },
  "success": true
}
```

### 4. Unblock Reservation
```
POST /api/reservations/{reservationId}/unblock
Authorization: Bearer {owner_token}

Response: 200 OK
{
  "data": {
    "status": "PENDING",  // Restored to PENDING
    ...
  },
  "success": true
}
```

---

## Public Endpoints

### Get Reservation Details
```
GET /api/reservations/{reservationId}
Authorization: Bearer {any_token}  # GUEST or OWNER

Response: 200 OK
(Guests can only access their own, Owners can access any)
```

---

## Status Codes

| Code | Status | Meaning |
|------|--------|---------|
| 200 | OK | Successful GET, PUT, POST (status change) |
| 201 | Created | Successful POST (create) |
| 400 | Bad Request | Invalid input or business rule violation |
| 401 | Unauthorized | Missing or invalid JWT token |
| 403 | Forbidden | Valid token but wrong role/permissions |
| 404 | Not Found | Reservation/Service/Hotel not found |
| 500 | Server Error | Unexpected server error |

---

## Error Messages

### Validation Errors (400)

**Invalid Date Format**
```json
{
  "errors": ["Date must be in yyyy-MM-dd format"],
  "success": false
}
```

**Invalid Time Format**
```json
{
  "errors": ["Time must be in HH:mm format"],
  "success": false
}
```

**Double Booking**
```json
{
  "errors": ["This time slot has already been reserved. Please choose another time"],
  "success": false
}
```

**Date Not Available**
```json
{
  "errors": ["Service is not available on the selected date"],
  "success": false
}
```

**Time Not Available**
```json
{
  "errors": ["Selected time slot is not available for this service"],
  "success": false
}
```

**Invalid Status Transition**
```json
{
  "errors": ["Only pending reservations can be started. Current status: IN_PROGRESS"],
  "success": false
}
```

**Cannot Update Non-Pending**
```json
{
  "errors": ["Can only update pending reservations"],
  "success": false
}
```

**Cannot Cancel Done/Cancelled**
```json
{
  "errors": ["This reservation is already cancelled"],
  "success": false
}
```

**Guest Not Found**
```json
{
  "errors": ["Guest not found"],
  "success": false
}
```

**Service Not Found**
```json
{
  "errors": ["Service not found"],
  "success": false
}
```

**Hotel Not Found**
```json
{
  "errors": ["Hotel not found"],
  "success": false
}
```

**Reservation Not Found**
```json
{
  "errors": ["Reservation not found"],
  "success": false
}
```

**Service Not in Hotel**
```json
{
  "errors": ["Service does not belong to this hotel"],
  "success": false
}
```

### Authorization Errors (403)

**Only Guest Role Can Create**
```
Only guests can make reservations
```

**Only Staff Can Perform**
```
Only staff members can change reservation status
```

**Only Staff or Guest Can Unblock**
```
Unauthorized: Only guest, staff, or owner can unblock reservations
```

**Cannot Access Other's Reservation**
```
Unauthorized: You can only update your own reservations
```

**Can Only Cancel Own**
```
Unauthorized: You can only cancel your own reservations
```

### Authentication Errors (401)

**Missing Token**
```
Access Denied: Missing Authorization Header
```

**Invalid Token**
```
Access Denied: Invalid JWT Token
```

**Expired Token**
```
Access Denied: JWT Token Expired
```

---

## Field Validation Rules

### Create Reservation DTO

| Field | Type | Required | Validation |
|-------|------|----------|-----------|
| serviceId | String | Yes | @NotBlank |
| hotelId | String | Yes | @NotBlank |
| reservationDate | String | Yes | @NotBlank, @Pattern(yyyy-MM-dd) |
| reservationTime | String | Yes | @NotBlank, @Pattern(HH:mm) |
| numberOfGuests | Integer | Yes | @NotNull, @Min(1), @Max(100) |
| notes | String | No | Optional |

### Update Status DTO

| Field | Type | Required | Validation |
|-------|------|----------|-----------|
| reason | String | No | Optional reason/explanation |

---

## Example Postman Collection

```bash
# 1. Register Guest
curl -X POST http://localhost:8080/api/auth/register/guest \
  -H "Content-Type: application/json" \
  -d '{"email":"guest@test.com","password":"password123"}'

# 2. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"guest@test.com","password":"password123"}'

# 3. Create Reservation
curl -X POST http://localhost:8080/api/reservations \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "serviceId":"service-uuid",
    "hotelId":"hotel-uuid",
    "reservationDate":"2026-02-15",
    "reservationTime":"10:00",
    "numberOfGuests":2
  }'

# 4. Get My Reservations
curl -X GET http://localhost:8080/api/reservations/my-reservations \
  -H "Authorization: Bearer {token}"

# 5. Staff Starts
curl -X POST http://localhost:8080/api/reservations/{id}/start \
  -H "Authorization: Bearer {staff_token}"

# 6. Staff Completes
curl -X POST http://localhost:8080/api/reservations/{id}/complete \
  -H "Authorization: Bearer {staff_token}"

# 7. Get Details
curl -X GET http://localhost:8080/api/reservations/{id} \
  -H "Authorization: Bearer {token}"
```

---

## Rate Limiting (Future)

Currently no rate limiting. Recommended:
- Guest: 100 requests/hour
- Staff: 500 requests/hour
- Owner: 1000 requests/hour

---

## Pagination (Future Enhancement)

Add to list endpoints:
```
?page=0&size=20&sort=createdAt,desc
```

---

**Last Updated**: February 14, 2026
**Version**: 1.0
**Status**: Production Ready ✅
