# Reservation System - Architecture & Flow

## System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    ReservationController                     │
│  (/api/reservations - 11 RESTful endpoints)                 │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ @Autowired
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   ReservationService                         │
│  (Business logic, validation, status management)            │
│  - createReservation()                                      │
│  - startReservation() / completeReservation()               │
│  - blockReservation() / unblockReservation()                │
│  - cancelReservation()                                      │
│  - Comprehensive validation methods                         │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ Uses repositories & services
                              ▼
        ┌─────────────────────────────────────┐
        │    ReservationRepository (JPA)      │
        │    AppUserRepository                │
        │    HotelRepository                  │
        │    ServiceRepository                │
        └─────────────────────────────────────┘
                              │
                              │ Maps to entities
                              ▼
        ┌─────────────────────────────────────┐
        │         Entity Models                │
        │  - Reservation                      │
        │  - AppUser                          │
        │  - Hotel                            │
        │  - Service                          │
        └─────────────────────────────────────┘
                              │
                              │ JPA/Hibernate
                              ▼
        ┌─────────────────────────────────────┐
        │       Database (Reservations)        │
        │  - guest_id (FK)                    │
        │  - service_id (FK)                  │
        │  - hotel_id (FK)                    │
        │  - reservation_date                 │
        │  - reservation_time                 │
        │  - status (PENDING/IN_PROGRESS/...) │
        └─────────────────────────────────────┘
```

## Data Flow Diagram

### Create Reservation Flow
```
Guest sends request
   │
   ├─→ CreateReservationDTO
   │
   ├─→ ReservationController.createReservation()
   │
   ├─→ SecurityContextHolder gets guestId
   │
   ├─→ ReservationService.createReservation()
   │
   ├─→ Validations:
   │   ├─ Guest exists & has GUEST role
   │   ├─ Service exists
   │   ├─ Hotel exists
   │   ├─ Service belongs to hotel
   │   ├─ Date format valid
   │   ├─ Time format valid
   │   ├─ Date available in service
   │   ├─ Time slot available in service
   │   └─ No double booking
   │
   ├─→ Create Reservation entity
   │   └─ status = "PENDING"
   │
   ├─→ Save to database
   │
   └─→ Return ReservationDTO
      └─ HTTP 201 Created
```

### Status Transition Flow
```
Staff/Owner requests status change
   │
   ├─→ UpdateReservationStatusDTO (optional reason)
   │
   ├─→ ReservationController.startReservation()
   │
   ├─→ ReservationService.startReservation()
   │
   ├─→ Validations:
   │   ├─ Reservation exists
   │   ├─ Staff user exists
   │   ├─ Staff has STAFF role
   │   └─ Current status is PENDING
   │
   ├─→ Update reservation
   │   └─ status = "IN_PROGRESS"
   │   └─ updatedBy = staffId
   │   └─ updatedAt = currentTimestamp
   │
   ├─→ Save to database
   │
   └─→ Return updated ReservationDTO
      └─ HTTP 200 OK
```

## Role-Based Access Matrix

```
┌────────────────────┬──────────┬───────┬───────┐
│   Endpoint         │  GUEST   │ STAFF │ OWNER │
├────────────────────┼──────────┼───────┼───────┤
│ POST /             │    ✓     │   -   │   -   │
│ GET /my            │    ✓     │   -   │   -   │
│ GET /my-active     │    ✓     │   -   │   -   │
│ GET /{id}          │   ✓*     │   -   │  ✓    │
│ PUT /{id}          │   ✓*     │   -   │   -   │
│ DELETE /{id}       │   ✓*     │   -   │   -   │
│ POST /{id}/start   │    -     │   ✓   │   -   │
│ POST /{id}/complete│    -     │   ✓   │   -   │
│ POST /{id}/block   │    -     │   ✓   │   -   │
│ POST /{id}/unblock │   ✓*     │   ✓   │  ✓    │
│ GET /hotel/{id}    │    -     │   -   │  ✓    │
│ GET /service/{id}  │    -     │   -   │  ✓    │
└────────────────────┴──────────┴───────┴───────┘
* Own reservations only
```

## Status State Machine

```
                   ┌──────────────┐
                   │   PENDING    │◄──────────┐
                   │ (Guest waits)│           │
                   └───────┬──────┘           │
                           │                  │
         ┌─────────────────┼─────────────────┐│
         │                 │                 ││
         │ (Staff starts)  │ (Staff blocks)  ││ (Unblock)
         │                 │                 ││
         ▼                 ▼                 ▼│
    ┌──────────────┐  ┌────────────┐  ┌──────────────┐
    │ IN_PROGRESS  │  │  BLOCKED   │  │  (back to)   │
    │(Serving)     │  │ (Problem)  │  │   PENDING    │
    └──────┬───────┘  └────────────┘  └──────────────┘
           │
           │ (Staff completes)
           ▼
       ┌──────────────┐
       │    DONE      │
       │  (Complete)  │
       └──────────────┘
       
    ┌──────────────────────────────────────────────┐
    │ CANCELLED (from any state except DONE)       │
    │ (Guest cancels)                              │
    └──────────────────────────────────────────────┘
```

## Validation Pipeline

```
Input (CreateReservationDTO)
   │
   ├─→ Bean Validation
   │   ├─ @NotBlank on required fields
   │   ├─ @Pattern on date (yyyy-MM-dd)
   │   ├─ @Pattern on time (HH:mm)
   │   ├─ @Min/@Max on numberOfGuests
   │   └─ Returns 400 if fails
   │
   ├─→ Service Layer Validation
   │   ├─ Guest exists & has role
   │   ├─ Service exists
   │   ├─ Hotel exists
   │   ├─ Service belongs to hotel
   │   ├─ Date format valid
   │   ├─ Time format valid
   │   ├─ Date available
   │   ├─ Time available
   │   ├─ No double booking
   │   └─ Throws IllegalArgumentException if fails
   │
   ├─→ Create entity
   │
   └─→ HTTP 201 response
```

## Database Schema

```
reservations
├─ id (UUID) [PK]
├─ guest_id (UUID) [FK → app_users.id]
├─ service_id (UUID) [FK → services.id]
├─ hotel_id (UUID) [FK → hotels.uuid]
├─ reservation_date (VARCHAR) - yyyy-MM-dd
├─ reservation_time (VARCHAR) - HH:mm
├─ status (VARCHAR) - PENDING|IN_PROGRESS|DONE|BLOCKED|CANCELLED
├─ number_of_guests (INTEGER)
├─ notes (TEXT)
├─ created_by (VARCHAR)
├─ created_at (BIGINT) - milliseconds
├─ updated_by (VARCHAR)
└─ updated_at (BIGINT) - milliseconds

Relationships:
app_users (1) ──────┐
                    ├──→ reservations
services (1) ───────┤
                    │
hotels (1) ──────────┘
```

## API Request/Response Flow

```
CLIENT REQUEST
   │
   ├─→ GET /api/reservations/my-reservations
   │   Header: Authorization: Bearer {JWT}
   │   Method: GET
   │   Path: /api/reservations/my-reservations
   │
   ├─→ Spring Security
   │   ├─ Validates JWT
   │   ├─ Extracts guestId
   │   ├─ Checks role (GUEST required)
   │   └─ Sets SecurityContext
   │
   ├─→ ReservationController
   │   ├─ @GetMapping binding
   │   ├─ Gets authentication
   │   ├─ Extracts guestId
   │   └─ Calls service
   │
   ├─→ ReservationService
   │   ├─ Validates guest exists
   │   ├─ Queries database
   │   └─ Maps to DTOs
   │
   ├─→ Response builder
   │   ├─ Sets messages[]
   │   ├─ Sets errors[]
   │   ├─ Sets data (List<ReservationDTO>)
   │   └─ Sets success = true
   │
   └─→ HTTP 200 OK
      {
        "messages": [],
        "errors": [],
        "data": [...],
        "success": true
      }
```

## Security Layers

```
Layer 1: Transport
└─ HTTPS (configured in deployment)

Layer 2: Authentication
├─ JWT token in Authorization header
├─ Token validation in SecurityConfig
└─ Expiration checking

Layer 3: Authorization
├─ @PreAuthorize annotations
├─ Role checking (GUEST, STAFF, OWNER)
└─ 403 Forbidden if unauthorized

Layer 4: Data Validation
├─ Bean validation (format)
├─ Business rule validation
└─ 400 Bad Request if invalid

Layer 5: Access Control
├─ Guest can only access own reservations
├─ Staff can only access their hotel's reservations
└─ Owner can access all their hotels/services

Layer 6: Audit
├─ createdBy tracks original creator
├─ updatedBy tracks last modifier
├─ Timestamps in milliseconds
└─ Status changes logged
```

## Component Dependencies

```
ReservationController
├─ ReservationService (injected)
└─ SecurityContextHolder (static)

ReservationService
├─ ReservationRepository
├─ AppUserRepository
├─ HotelRepository
└─ ServiceRepository

Repositories
└─ JpaRepository (Spring Data)

Entities
├─ AppUser
├─ Hotel
├─ Service
└─ Reservation
```

## Error Handling Flow

```
Exception thrown
   │
   ├─→ IllegalArgumentException
   │   ├─ Caught in controller
   │   ├─ Message extracted
   │   ├─ Added to errors[]
   │   └─ HTTP 400 returned
   │
   ├─→ @PreAuthorize fails
   │   ├─ AccessDenied exception
   │   ├─ Handled by Spring Security
   │   └─ HTTP 403 returned
   │
   ├─→ Resource not found
   │   ├─ Optional.empty() returned
   │   ├─ IllegalArgumentException thrown
   │   └─ HTTP 404 returned
   │
   └─→ Validation fails
       ├─ MethodArgumentNotValidException
       ├─ Field errors extracted
       └─ HTTP 400 returned
```

---

**This architecture ensures:**
- ✅ Separation of concerns
- ✅ Single responsibility
- ✅ Testability
- ✅ Security
- ✅ Scalability
- ✅ Maintainability
