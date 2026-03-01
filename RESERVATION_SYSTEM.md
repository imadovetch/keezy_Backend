# Reservation System - Complete Implementation Guide

## Overview
A complete reservation system for hotel services with role-based access control and comprehensive status management.

## Features Implemented

### 1. Core Entities
- **Reservation Entity** (`Reservation.java`)
  - Links Guest, Service, and Hotel
  - Tracks reservation date and time
  - Supports status: PENDING, IN_PROGRESS, DONE, CANCELLED, BLOCKED
  - Stores number of guests and notes
  - Includes audit fields (createdBy, createdAt, updatedBy, updatedAt)

### 2. Data Transfer Objects (DTOs)
- **CreateReservationDTO**: For creating new reservations with validation
- **ReservationDTO**: For returning reservation details
- **UpdateReservationStatusDTO**: For status change operations with optional reason

### 3. Repository
- **ReservationRepository**: Comprehensive query methods
  - Find by guest, service, hotel
  - Find existing reservations for double-booking prevention
  - Find active reservations
  - Find by hotel UUID (note: Hotel uses `uuid` not `id`)

### 4. Service Layer (ReservationService)
Complete business logic with validation:

#### Creating Reservations
- `createReservation(CreateReservationDTO, guestId)` - GUEST only
  - Validates guest exists and has GUEST role
  - Validates service and hotel exist
  - Validates service belongs to hotel
  - Validates date format (yyyy-MM-dd)
  - Validates time format (HH:mm)
  - Validates date is available in service
  - Validates time slot is available in service
  - Prevents double booking
  - Creates reservation with PENDING status

#### Reading Reservations
- `getGuestReservations(guestId)` - Get all reservations for a guest
- `getActiveReservations(guestId)` - Get PENDING and CONFIRMED reservations only
- `getReservation(reservationId)` - Get single reservation details
- `getHotelReservations(hotelId)` - OWNER only, get all hotel reservations
- `getServiceReservations(serviceId)` - OWNER only, get all service reservations

#### Updating Reservations
- `updateReservation(reservationId, CreateReservationDTO, guestId)` - GUEST only
  - Can only update PENDING reservations
  - Can update numberOfGuests and notes

#### Status Management
- `startReservation(reservationId, staffId)` - STAFF only
  - PENDING → IN_PROGRESS

- `completeReservation(reservationId, staffId)` - STAFF only
  - IN_PROGRESS → DONE

- `blockReservation(reservationId, staffId, blockReason)` - STAFF only
  - Can block from any state except DONE or CANCELLED
  - Stores block reason in notes

- `unblockReservation(reservationId, userId)` - GUEST, STAFF, or OWNER
  - BLOCKED → PENDING

#### Cancellation
- `cancelReservation(reservationId, guestId)` - GUEST only
  - Can cancel from any state except DONE
  - Updates status to CANCELLED

### 5. Controller (ReservationController)
RESTful endpoints with Swagger documentation:

#### Guest Endpoints (GUEST role required)
- `POST /api/reservations` - Create new reservation
- `GET /api/reservations/my-reservations` - Get all guest reservations
- `GET /api/reservations/my-active-reservations` - Get active reservations only
- `PUT /api/reservations/{reservationId}` - Update own reservation
- `DELETE /api/reservations/{reservationId}` - Cancel own reservation
- `POST /api/reservations/{reservationId}/unblock` - Unblock own reservation

#### Staff Endpoints (STAFF role required)
- `POST /api/reservations/{reservationId}/start` - PENDING → IN_PROGRESS
- `POST /api/reservations/{reservationId}/complete` - IN_PROGRESS → DONE
- `POST /api/reservations/{reservationId}/block` - Block reservation with reason
- `POST /api/reservations/{reservationId}/unblock` - Unblock reservation

#### Owner Endpoints (OWNER role required)
- `GET /api/reservations/hotel/{hotelId}` - Get all hotel reservations
- `GET /api/reservations/service/{serviceId}` - Get all service reservations
- `POST /api/reservations/{reservationId}/unblock` - Unblock reservation
- `POST /api/reservations/{reservationId}/confirm` - Start processing reservation

#### Public/Accessible Endpoints
- `GET /api/reservations/{reservationId}` - Get reservation details (GUEST or OWNER)

## Status Workflow

```
    ┌─────────────────────────────────────────────┐
    │        PENDING (Initial Status)              │
    │   (Guest creates reservation)               │
    └─────────────────────────────────────────────┘
           │
           │ Staff: /start
           │
           ▼
    ┌─────────────────────────────────────────────┐
    │      IN_PROGRESS                             │
    │   (Staff processing service)                 │
    └─────────────────────────────────────────────┘
           │
           │ Staff: /complete
           │
           ▼
    ┌─────────────────────────────────────────────┐
    │      DONE                                    │
    │   (Service completed)                        │
    └─────────────────────────────────────────────┘

Alternative Paths:
  PENDING/IN_PROGRESS → BLOCKED (Staff: /block) → PENDING (Guest/Staff/Owner: /unblock)
  PENDING/IN_PROGRESS/BLOCKED → CANCELLED (Guest: DELETE)
```

## Validation Rules

### Date & Time Validation
- Date must be in `yyyy-MM-dd` format
- Time must be in `HH:mm` format
- Date must be available in service's `selectedDates` (comma-separated)
- Time must be available in service's `timeSlots` (comma-separated)
- Cannot have double bookings (same service, date, time)

### Role-Based Access Control
- **GUEST**: Can create, view own, update own (if PENDING), cancel own, unblock own
- **STAFF**: Can start (PENDING→IN_PROGRESS), complete (IN_PROGRESS→DONE), block, unblock
- **OWNER**: Can view all hotel/service reservations, unblock, start processing

### Status Transition Rules
- PENDING can be:
  - Started (PENDING → IN_PROGRESS)
  - Blocked (PENDING → BLOCKED)
  - Cancelled (PENDING → CANCELLED)
  - Updated (notes, guests count)
  
- IN_PROGRESS can be:
  - Completed (IN_PROGRESS → DONE)
  - Blocked (IN_PROGRESS → BLOCKED)
  - Cancelled (IN_PROGRESS → CANCELLED)
  
- BLOCKED can be:
  - Unblocked (BLOCKED → PENDING)
  - Cancelled (BLOCKED → CANCELLED)
  
- DONE cannot be:
  - Modified
  - Cancelled
  - Blocked
  
- CANCELLED cannot be:
  - Modified
  - Reopened

## API Response Format

All endpoints return a `UnifiedResponse` object:
```json
{
  "messages": [],
  "errors": [],
  "data": {
    "id": "uuid",
    "guestId": "uuid",
    "guestEmail": "guest@example.com",
    "serviceName": "Breakfast",
    "serviceId": "uuid",
    "hotelName": "Grand Hotel",
    "hotelId": "uuid",
    "reservationDate": "2026-02-14",
    "reservationTime": "09:00",
    "status": "PENDING",
    "numberOfGuests": 2,
    "notes": "Special dietary requirements",
    "createdAt": 1707932874077,
    "updatedAt": 1707932874077
  },
  "success": true
}
```

## Security Features

1. **Authentication Required**: All endpoints require JWT token
2. **Role-Based Authorization**: Endpoints protected with `@PreAuthorize`
3. **Guest Isolation**: Guests can only access their own reservations
4. **Double-Booking Prevention**: Queries check for existing reservations
5. **Audit Trail**: All changes tracked with user ID and timestamp
6. **Input Validation**: All DTOs validated with Jakarta validation annotations

## Error Handling

Common error scenarios:
- Invalid date/time format → 400 Bad Request
- Double booking attempt → 400 Bad Request
- Unauthorized access → 403 Forbidden
- Reservation not found → 404 Not Found
- Invalid status transition → 400 Bad Request
- Only guest can modify own reservation → 403 Forbidden
- Invalid role for operation → 403 Forbidden

## Database Schema

### Reservations Table
```sql
CREATE TABLE reservations (
  id UUID PRIMARY KEY,
  guest_id UUID NOT NULL REFERENCES app_users(id),
  service_id UUID NOT NULL REFERENCES services(id),
  hotel_id UUID NOT NULL REFERENCES hotels(uuid),
  reservation_date VARCHAR(255) NOT NULL,
  reservation_time VARCHAR(255) NOT NULL,
  status VARCHAR(255) NOT NULL,
  number_of_guests INTEGER,
  notes TEXT,
  created_by VARCHAR(255),
  created_at BIGINT,
  updated_by VARCHAR(255),
  updated_at BIGINT
);
```

## Files Created/Modified

### New Files
1. [Entity/Reservation.java](Entity/Reservation.java)
2. [DTO/CreateReservationDTO.java](DTO/CreateReservationDTO.java)
3. [DTO/ReservationDTO.java](DTO/ReservationDTO.java)
4. [DTO/UpdateReservationStatusDTO.java](DTO/UpdateReservationStatusDTO.java)
5. [Repositery/ReservationRepository.java](Repositery/ReservationRepository.java)
6. [Service/ReservationService.java](Service/ReservationService.java)
7. [Controller/ReservationController.java](Controller/ReservationController.java)

## Testing Endpoints

### Create Reservation (Guest)
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
```

### Start Reservation (Staff)
```
POST /api/reservations/{reservationId}/start
Authorization: Bearer {staff_token}
```

### Complete Reservation (Staff)
```
POST /api/reservations/{reservationId}/complete
Authorization: Bearer {staff_token}
```

### Block Reservation (Staff)
```
POST /api/reservations/{reservationId}/block
Authorization: Bearer {staff_token}
Content-Type: application/json

{
  "reason": "Guest did not show up"
}
```

### Unblock Reservation (Guest/Staff/Owner)
```
POST /api/reservations/{reservationId}/unblock
Authorization: Bearer {user_token}
```

### Cancel Reservation (Guest)
```
DELETE /api/reservations/{reservationId}
Authorization: Bearer {guest_token}
```

### Get My Reservations (Guest)
```
GET /api/reservations/my-reservations
Authorization: Bearer {guest_token}
```

### Get Hotel Reservations (Owner)
```
GET /api/reservations/hotel/{hotelId}
Authorization: Bearer {owner_token}
```

## Notes

- Hotel uses `uuid` as primary key, not `id`
- Service ID is UUID (not hotel ID)
- All timestamps are in milliseconds (Unix epoch)
- Dates must be provided in `yyyy-MM-dd` format
- Times must be provided in `HH:mm` format (24-hour)
- Staff members manage reservations for their hotel
- Owners can view all reservations for their hotels and services
- Guests can only see and manage their own reservations
