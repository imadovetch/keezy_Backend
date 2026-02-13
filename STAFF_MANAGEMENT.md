# Staff Management System - Complete Implementation

## Overview
A dedicated staff management system with JWT-based hotel owner verification. Hotel owners can create, read, update, and delete staff members for their hotels.

---

## Key Features

✅ **JWT-Based Ownership Verification**
- Extracts hotel owner ID from JWT token
- Verifies hotel ownership before allowing staff operations
- Prevents unauthorized access to other hotel's staff

✅ **Complete CRUD Operations**
- Create staff members
- List all staff for a hotel
- Get individual staff details
- Update staff information
- Delete staff members

✅ **Hotel Association**
- Staff must be associated with a hotel
- Only hotel owner can manage their staff
- Tracks which owner created each staff member

---

## API Endpoints

### Create Staff Member
```
POST /api/staff/{hotelId}
Authorization: Bearer {token}

Payload:
{
  "email": "ibouderoua63@gmail.com",
  "firstName": "imad",
  "lastName": "bouderoua",
  "password": "dadadadada",
  "phone": "0620783344",
  "photo": "",
  "hotelId": "70970-232-3232-2323-232"
}

Response (201 Created):
{
  "errors": [],
  "infos": [],
  "data": {
    "id": "staff-uuid",
    "email": "ibouderoua63@gmail.com",
    "firstName": "imad",
    "lastName": "bouderoua",
    "phone": "0620783344",
    "role": "STAFF"
  },
  "authorized": true
}
```

### Get All Staff for Hotel
```
GET /api/staff/{hotelId}
Authorization: Bearer {token}

Response (200 OK):
{
  "errors": [],
  "infos": [],
  "data": [
    {
      "id": "staff-uuid-1",
      "email": "staff1@hotel.com",
      "firstName": "John",
      "lastName": "Doe",
      "role": "STAFF"
    },
    {
      "id": "staff-uuid-2",
      "email": "staff2@hotel.com",
      "firstName": "Jane",
      "lastName": "Smith",
      "role": "STAFF"
    }
  ],
  "authorized": true
}
```

### Get Staff Details
```
GET /api/staff/member/{staffId}
Authorization: Bearer {token}

Response (200 OK):
{
  "errors": [],
  "infos": [],
  "data": {
    "id": "staff-uuid",
    "email": "ibouderoua63@gmail.com",
    "firstName": "imad",
    "lastName": "bouderoua",
    "phone": "0620783344",
    "role": "STAFF",
    "createdBy": "owner-uuid",
    "createdAt": 1707842500000
  },
  "authorized": true
}
```

### Update Staff Member
```
PUT /api/staff/member/{staffId}
Authorization: Bearer {token}

Payload (all fields optional):
{
  "firstName": "imad",
  "lastName": "bouderoua",
  "password": "newpassword",
  "phone": "0620783344"
}

Response (200 OK):
{
  "errors": [],
  "infos": [],
  "data": {
    "id": "staff-uuid",
    "email": "ibouderoua63@gmail.com",
    "firstName": "imad",
    "lastName": "bouderoua",
    "phone": "0620783344",
    "role": "STAFF"
  },
  "authorized": true
}
```

### Delete Staff Member
```
DELETE /api/staff/member/{staffId}
Authorization: Bearer {token}

Response (204 No Content)
```

---

## Security Features

### Hotel Owner Verification
```
1. Extract userId from JWT token
2. Verify user exists in database
3. Load hotel by ID
4. Check: hotel.owner.id == extractedUserId
5. If not owner: Throw "Unauthorized" exception
```

### Staff Ownership Tracking
```
- createdBy: Set to hotel owner ID when staff is created
- When fetching staff: Verify createdBy == owner ID from token
- Prevents access to other hotel's staff
```

### Authorization Flow
```
StaffController
    ↓
getJwtFromRequest() → Extract "Bearer {token}"
    ↓
StaffService.createStaff(dto, jwtToken)
    ↓
StaffService.extractHotelOwnerFromToken(jwtToken)
    ├─ Extract userId from token
    ├─ Verify user exists
    └─ Return owner ID
    ↓
Verify hotel.owner.id == owner ID
    ↓
Create staff with createdBy = owner ID
```

---

## Updated DTOs

### CreateStaffDTO
```java
{
  "email": "string (required, valid email)",
  "firstName": "string (required)",
  "lastName": "string (required)",
  "password": "string (required, min 6 chars)",
  "phone": "string (optional)",
  "photo": "string (optional, base64)",
  "hotelId": "string (required)"
}
```

---

## Files Created/Modified

### New Files:
1. `StaffService.java` - Business logic with JWT-based hotel owner verification
2. `StaffController.java` - REST endpoints for staff CRUD operations

### Updated Files:
1. `CreateStaffDTO.java` - Enhanced with hotelId, phone, photo fields

---

## How It Works

### Step 1: Hotel Owner Logs In
```bash
POST /api/auth/login
{
  "email": "owner@hotel.com",
  "password": "password123"
}
```
Response includes JWT token with:
- sub: owner@hotel.com
- userId: "550e8400-e29b-41d4-a716-446655440000"

### Step 2: Create Staff for Hotel
```bash
POST /api/staff/70970-232-3232-2323-232
Authorization: Bearer eyJhbGc...

{
  "email": "ibouderoua63@gmail.com",
  "firstName": "imad",
  "lastName": "bouderoua",
  "password": "dadadadada",
  "phone": "0620783344",
  "hotelId": "70970-232-3232-2323-232"
}
```

### Step 3: Service Processing
1. Extract owner ID from JWT token
2. Verify hotel exists
3. Verify hotel belongs to owner
4. Check email not already used
5. Get STAFF role from database
6. Create AppUser with STAFF role
7. Set createdBy = owner ID
8. Return created staff details

### Step 4: Response
```json
{
  "data": {
    "id": "new-staff-uuid",
    "email": "ibouderoua63@gmail.com",
    "firstName": "imad",
    "lastName": "bouderoua",
    "role": "STAFF",
    "createdBy": "550e8400-e29b-41d4-a716-446655440000"
  }
}
```

---

## Validation

✅ **JWT Token**
- Required on all endpoints
- Must be valid and not expired

✅ **Hotel Ownership**
- Hotel must exist
- Must belong to token owner

✅ **Staff Email**
- Must be valid email format
- Must not already exist in system

✅ **Required Fields**
- email: Required, valid email
- firstName: Required
- lastName: Required
- password: Required, min 6 characters
- hotelId: Required

---

## Error Handling

| Error | Status | Message |
|-------|--------|---------|
| Missing JWT token | 401 | Missing or invalid Authorization header |
| Hotel not found | 400 | Hotel not found with ID: {hotelId} |
| Not hotel owner | 400 | Unauthorized: you are not the owner of this hotel |
| Email exists | 400 | Email already exists |
| Staff not found | 400 | Staff not found |
| Invalid role | 400 | STAFF role not found |

---

## Database Schema

### app_users Table (Staff Member)
```sql
INSERT INTO app_users (
  id, email, password, first_name, last_name, phone, role_id, 
  enabled, created_by, created_at
) VALUES (
  'uuid', 'ibouderoua63@gmail.com', 'hashed_password', 'imad', 'bouderoua',
  '0620783344', 'staff_role_id', true, 'owner_uuid', 1707842500000
);
```

---

## Complete Example: Create Staff with cURL

```bash
curl -X POST "http://localhost:8080/api/staff/70970-232-3232-2323-232" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJvd25lckBob3RlbC5jb20iLCJ1c2VySWQiOiI1NTBlODQwMC1lMjliLTQxZDQtYTcxNi00NDY2NTU0NDAwMDAiLCJpYXQiOjE3MDc4NDI0MDAsImV4cCI6MTcwNzkyODgwMH0.xyz..." \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ibouderoua63@gmail.com",
    "firstName": "imad",
    "lastName": "bouderoua",
    "password": "dadadadada",
    "phone": "0620783344",
    "photo": "",
    "hotelId": "70970-232-3232-2323-232"
  }' \
  -w "\nHTTP Status: %{http_code}\n"
```

Expected Response (201):
```json
{
  "errors": [],
  "infos": [],
  "data": {
    "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
    "email": "ibouderoua63@gmail.com",
    "firstName": "imad",
    "lastName": "bouderoua",
    "phone": "0620783344",
    "role": "STAFF",
    "createdBy": "550e8400-e29b-41d4-a716-446655440000",
    "createdAt": 1707842500000
  },
  "authorized": true
}
```

---

## Summary

✅ Dedicated staff management system
✅ JWT-based hotel owner verification
✅ Complete CRUD operations
✅ Proper authorization and validation
✅ Clean separation from authentication
✅ Extensible architecture
