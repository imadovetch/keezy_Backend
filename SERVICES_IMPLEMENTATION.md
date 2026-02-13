# Services Module Implementation - Complete

## Project Overview
**Keezy Backend** is a hotel management API built with Spring Boot that provides:
- User authentication and authorization with JWT
- Hotel management
- Service/amenities management (NEW)
- Role-based access control

---

## What Was Added

### 1. **ServiceDTO** - Data Transfer Object
**File:** `src/main/java/org/bloomberg/keezy_back/DTO/ServiceDTO.java`

Handles service data exchange with the following fields:
```json
{
  "title": "Breakfast",
  "category": "Food & Beverage",
  "type": "Breakfast",
  "subcategory": "Continental",
  "description": "Our famous breakfast buffet",
  "hotelId": "88552462-97c4-4273-9150-ca37a947be19",
  "price": 25.99,
  "images": ["image1.png", "image2.png"],
  "selectedDates": ["05 Feb 2026", "10 Feb 2026"],
  "timeSlots": ["09:00 - 09:30", "08:00 - 08:30"]
}
```

---

### 2. **Service Entity** - Database Model
**File:** `src/main/java/org/bloomberg/keezy_back/Entity/Service.java`

JPA entity representing a hotel service in the database with:
- Relationship to Hotel (ManyToOne)
- Support for images, dates, and time slots (stored as CSV)
- Audit fields (createdBy, createdAt, etc.)

---

### 3. **Enhanced JwtTokenProvider**
**File:** `src/main/java/org/bloomberg/keezy_back/Service/JwtTokenProvider.java`

**Key Enhancements:**
- ✅ **Added `userId` to JWT payload** - Now includes user ID when generating tokens
- ✅ **`getUserIdFromToken(String token)`** - Extracts userId directly from token
- ✅ **`getAllClaimsFromToken(String token)`** - Extracts all JWT claims for flexibility

**How it works:**
```
1. When token is generated, it now includes:
   - sub: user email
   - userId: user ID (UUID)
   - iat: issued at time
   - exp: expiration time

2. JWT Token structure:
   header.payload.signature
   
   Where payload contains:
   {
     "sub": "user@example.com",
     "userId": "550e8400-e29b-41d4-a716-446655440000",
     "iat": 1707842400,
     "exp": 1707928800
   }
```

---

### 4. **ServiceService** - Core Business Logic
**File:** `src/main/java/org/bloomberg/keezy_back/Service/ServiceService.java`

**Key Feature: Hotel Owner Extraction from JWT Token**

The `extractHotelOwnerFromToken(String jwtToken)` method:
1. Extracts `userId` from JWT token
2. Falls back to extracting email if userId not available
3. Verifies the user exists in database
4. Returns the owner ID for authorization checks

**Method Endpoints:**
- `createService(ServiceDTO, jwtToken)` - Create new service
  - Verifies hotel belongs to token owner
  - Validates all required fields
  - Returns created service with audit info

- `getHotelServices(hotelId, jwtToken)` - Get all services for a hotel
  - Ownership verification
  - Returns list of all services

- `getService(serviceId, jwtToken)` - Get specific service
  - Full ownership verification
  
- `updateService(serviceId, ServiceDTO, jwtToken)` - Update service
  - Incremental updates
  - Audit trail (updatedBy, updatedAt)

- `deleteService(serviceId, jwtToken)` - Delete service
  - Permanent deletion with ownership check

---

### 5. **ServiceRepository** - Data Access
**File:** `src/main/java/org/bloomberg/keezy_back/Repositery/ServiceRepository.java`

Provides database queries:
- `findByHotelId()` - Get all services for a hotel
- `findByHotelIdAndCategory()` - Filter by category
- `findByIdAndHotelId()` - Get specific service with hotel verification
- `countByHotelId()` - Count services per hotel

---

### 6. **ServiceMapper** - Entity/DTO Conversion
**File:** `src/main/java/org/bloomberg/keezy_back/Mapper/ServiceMapper.java`

Handles bidirectional conversion between:
- Service Entity ↔ ServiceDTO
- List<String> ↔ CSV strings (for images, dates, time slots)

---

### 7. **ServiceController** - REST API Endpoints
**File:** `src/main/java/org/bloomberg/keezy_back/Controller/ServiceController.java`

**REST Endpoints:**

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/services/{hotelId}` | Create new service |
| GET | `/api/services/{hotelId}` | List all services |
| GET | `/api/services/service/{serviceId}` | Get one service |
| PUT | `/api/services/service/{serviceId}` | Update service |
| DELETE | `/api/services/service/{serviceId}` | Delete service |

**Authorization:** All endpoints require JWT token in `Authorization: Bearer {token}` header

---

## How JWT-Based Owner Extraction Works

### Step 1: User Login
```
POST /api/auth/login
{
  "email": "owner@hotel.com",
  "password": "password123"
}

RESPONSE:
{
  "accessToken": "eyJhbGc...", // Contains userId in payload
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "user": {...}
}
```

### Step 2: Create Service with Token
```
POST /api/services/88552462-97c4-4273-9150-ca37a947be19
Authorization: Bearer eyJhbGc...

{
  "title": "Breakfast",
  "category": "Food & Beverage",
  "type": "Breakfast",
  "price": 25.99,
  "hotelId": "88552462-97c4-4273-9150-ca37a947be19",
  "selectedDates": ["05 Feb 2026"],
  "timeSlots": ["09:00 - 09:30"]
}
```

### Step 3: ServiceService Verification
```
1. extractHotelOwnerFromToken("eyJhbGc...")
   ├─ Extract userId from JWT payload
   └─ Return: "550e8400-e29b-41d4-a716-446655440000"

2. Verify hotel ownership
   ├─ Fetch hotel from DB
   └─ Check: hotel.owner.id == extractedUserId

3. If verified: Create service with createdBy = userId
   If not verified: Throw "Unauthorized" exception
```

---

## Database Schema

### `services` Table
```sql
CREATE TABLE services (
  id VARCHAR(36) PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  category VARCHAR(255) NOT NULL,
  subcategory VARCHAR(255),
  type VARCHAR(255) NOT NULL,
  description TEXT,
  hotel_id VARCHAR(36) NOT NULL,
  price DOUBLE,
  images TEXT,              -- CSV: "img1.png,img2.png"
  selected_dates TEXT,      -- CSV: "05 Feb 2026,10 Feb 2026"
  time_slots TEXT,          -- CSV: "09:00 - 09:30,08:00 - 08:30"
  created_by VARCHAR(36),
  created_at BIGINT,
  updated_by VARCHAR(36),
  updated_at BIGINT,
  FOREIGN KEY (hotel_id) REFERENCES hotel(id)
);
```

---

## Example Flow: Create Breakfast Service

### Request
```bash
curl -X POST "http://localhost:8080/api/services/88552462-97c4-4273-9150-ca37a947be19" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJvd25lckBob3RlbC5jb20iLCJ1c2VySWQiOiI1NTBlODQwMC1lMjliLTQxZDQtYTcxNi00NDY2NTU0NDAwMDAiLCJpYXQiOjE3MDc4NDI0MDAsImV4cCI6MTcwNzkyODgwMH0.xyz..." \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Continental Breakfast",
    "category": "Food & Beverage",
    "type": "Breakfast",
    "description": "Our signature breakfast buffet",
    "price": 25.99,
    "selectedDates": ["05 Feb 2026", "10 Feb 2026"],
    "timeSlots": ["09:00 - 09:30", "08:00 - 08:30"],
    "images": ["breakfast1.png", "breakfast2.png"]
  }'
```

### Response (201 Created)
```json
{
  "errors": [],
  "warnings": [],
  "data": {
    "uuid": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
    "title": "Continental Breakfast",
    "category": "Food & Beverage",
    "type": "Breakfast",
    "description": "Our signature breakfast buffet",
    "hotelId": "88552462-97c4-4273-9150-ca37a947be19",
    "price": 25.99,
    "selectedDates": ["05 Feb 2026", "10 Feb 2026"],
    "timeSlots": ["09:00 - 09:30", "08:00 - 08:30"],
    "images": ["breakfast1.png", "breakfast2.png"],
    "createdBy": "550e8400-e29b-41d4-a716-446655440000",
    "createdAt": 1707842500000
  },
  "success": true
}
```

---

## Security Features

✅ **JWT Token Extraction**
- User ID embedded in token
- Email fallback for legacy tokens

✅ **Ownership Verification**
- Every operation verifies hotel owner
- Cross-references with user database
- Prevents unauthorized access

✅ **Authorization**
- Required Bearer token header
- Returns 401 if missing/invalid
- Returns 403 if not authorized

✅ **Input Validation**
- Required fields enforced
- Price validation
- Hotel existence check

---

## Files Created/Modified

### New Files Created:
1. `ServiceDTO.java` - DTO for services
2. `Service.java` - JPA Entity
3. `ServiceRepository.java` - Data access layer
4. `ServiceService.java` - Business logic with JWT extraction
5. `ServiceMapper.java` - Entity/DTO mapping
6. `ServiceController.java` - REST endpoints

### Files Enhanced:
1. `JwtTokenProvider.java` - Added userId to token payload and extraction methods

---

## Next Steps (Optional)

1. **Image Upload** - Store base64 images and save to disk
2. **Availability Management** - Create separate table for date/time availability
3. **Service Categories** - Create service category reference table
4. **Booking Integration** - Link services to hotel bookings
5. **Analytics** - Track popular services by category/date

---

## Summary

✅ Complete service management system with JWT-based owner verification
✅ All CRUD operations secure and authorized
✅ Clean separation of concerns (DTO, Entity, Service, Controller)
✅ Comprehensive error handling
✅ Extensible architecture for future features
