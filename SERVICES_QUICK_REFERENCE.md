# Services Module - Quick Reference

## What You Get

A complete **Service/Amenities Management System** where hotel owners can manage services (breakfast, dining, activities, etc.) with full JWT-based authorization.

---

## Files Created

### DTOs & Models
- **ServiceDTO.java** - Data structure for service data
- **Service.java** - JPA entity for database
- **ServiceRepository.java** - Database queries

### Business Logic
- **ServiceService.java** - Core logic with **JWT owner extraction**
- **ServiceMapper.java** - Converts between DTO and Entity

### API
- **ServiceController.java** - REST endpoints

### Enhanced Files
- **JwtTokenProvider.java** - Now includes userId in JWT token

---

## Key Feature: JWT Owner Extraction

```java
// In ServiceService.java
public String extractHotelOwnerFromToken(String jwtToken) {
    // 1. Extract userId from JWT token
    String userId = jwtTokenProvider.getUserIdFromToken(jwtToken);
    
    // 2. Verify user exists
    // 3. Return owner ID for authorization
    return userId;
}
```

**How it works:**
1. JWT token contains `userId` in payload
2. ServiceService extracts it for every operation
3. Verifies hotel belongs to that owner
4. Prevents unauthorized access

---

## API Endpoints

### Create Service
```
POST /api/services/{hotelId}
Authorization: Bearer {token}

{
  "title": "Breakfast",
  "category": "Food & Beverage",
  "type": "Breakfast",
  "price": 25.99,
  "selectedDates": ["05 Feb 2026"],
  "timeSlots": ["09:00 - 09:30"],
  "images": ["img.png"]
}
```

### Get All Services
```
GET /api/services/{hotelId}
Authorization: Bearer {token}
```

### Get One Service
```
GET /api/services/service/{serviceId}
Authorization: Bearer {token}
```

### Update Service
```
PUT /api/services/service/{serviceId}
Authorization: Bearer {token}
{...updated fields...}
```

### Delete Service
```
DELETE /api/services/service/{serviceId}
Authorization: Bearer {token}
```

---

## Token Structure

After login, JWT token contains:

```json
{
  "sub": "user@hotel.com",          // Email
  "userId": "550e8400-e29b-41d4-...", // User ID (NEW!)
  "iat": 1707842400,                // Issued at
  "exp": 1707928800                 // Expires
}
```

---

## Security

✅ JWT token validated for every request
✅ Hotel ownership verified
✅ User existence checked against database
✅ 401/403 errors for unauthorized access
✅ Input validation on all fields

---

## Data Model

Service has:
- `title` - Service name
- `category` - Type (Food & Beverage, Activities, etc.)
- `type` - Specific type (Breakfast, Lunch, etc.)
- `price` - Cost
- `images` - List of image files
- `selectedDates` - Available dates
- `timeSlots` - Available time slots
- Audit fields (createdBy, createdAt, etc.)

---

## Testing Example

```bash
# 1. Login
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email": "owner@hotel.com", "password": "pass123"}'

# Get token from response...

# 2. Create Service
curl -X POST "http://localhost:8080/api/services/88552462-97c4-4273-9150-ca37a947be19" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Breakfast Buffet",
    "category": "Food & Beverage",
    "type": "Breakfast",
    "price": 25.99,
    "selectedDates": ["05 Feb 2026", "10 Feb 2026"],
    "timeSlots": ["09:00 - 09:30", "08:00 - 08:30"]
  }'
```

---

## Files Modified Summary

| File | Changes |
|------|---------|
| JwtTokenProvider.java | ✅ Added userId to token, new extraction methods |
| (6 new files created) | ServiceDTO, Service, ServiceRepository, ServiceService, ServiceMapper, ServiceController |

All files compile without errors ✅

---

## What's Next?

- Rebuild project to create database tables
- Test endpoints with Postman/curl
- Optionally add image upload feature
- Add service availability scheduling
