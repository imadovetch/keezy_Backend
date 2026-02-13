# 🌐 KEEZY BACK - ALL URLS & ENDPOINTS

## 🎯 QUICK ACCESS

### Main URLs (Copy & Paste)

```
🎨 Swagger UI
http://localhost:9090/swagger-ui.html

📄 API Documentation JSON
http://localhost:9090/v3/api-docs

💾 H2 Database Console
http://localhost:9090/h2-console

❤️ Application Health
http://localhost:9090/actuator
```

---

## 📍 COMPLETE URL LIST

### Application Access
| Name | URL | Purpose |
|------|-----|---------|
| Swagger UI | http://localhost:9090/swagger-ui.html | Interactive API docs |
| Alternative Swagger | http://localhost:9090/swagger-ui/index.html | Same as above |
| OpenAPI JSON | http://localhost:9090/v3/api-docs | Raw API specification |
| OpenAPI YAML | http://localhost:9090/v3/api-docs.yaml | YAML format (if enabled) |

### Database
| Name | URL | Purpose |
|------|-----|---------|
| H2 Console | http://localhost:9090/h2-console | Database browser |
| H2 API | http://localhost:9090/h2-console/api | H2 REST API |

### Health & Monitoring
| Name | URL | Purpose |
|------|-----|---------|
| Actuator | http://localhost:9090/actuator | Health endpoints |
| Actuator Health | http://localhost:9090/actuator/health | Application health |
| Actuator Env | http://localhost:9090/actuator/env | Environment info |
| Actuator Metrics | http://localhost:9090/actuator/metrics | Performance metrics |

---

## 🔐 API ENDPOINT URLS

### Authentication Endpoints

#### Register New User
```
POST http://localhost:9090/api/auth/register
```

#### Login
```
POST http://localhost:9090/api/auth/login
```

#### Create Staff Account
```
POST http://localhost:9090/api/auth/create-staff
(Requires Authentication Header)
```

---

### Hotel Endpoints

#### Create Hotel
```
POST http://localhost:9090/api/hotels
(Requires Authentication Header)
```

#### Get All Hotels
```
GET http://localhost:9090/api/hotels
(Requires Authentication Header)
```

#### Get Hotel by ID
```
GET http://localhost:9090/api/hotels/{hotelId}
(Requires Authentication Header)
```

Example:
```
GET http://localhost:9090/api/hotels/123e4567-e89b-12d3-a456-426614174000
```

#### Get User's Hotels
```
GET http://localhost:9090/api/hotels/owner/my-hotels
(Requires Authentication Header)
```

#### Create Hotel from Opera Property
```
POST http://localhost:9090/api/hotels/from-opera/{operaPropertyId}
(Requires Authentication Header)
```

Example:
```
POST http://localhost:9090/api/hotels/from-opera/12345
```

#### Update Hotel
```
PUT http://localhost:9090/api/hotels/{hotelId}
(Requires Authentication Header)
```

#### Delete Hotel
```
DELETE http://localhost:9090/api/hotels/{hotelId}
(Requires Authentication Header - Admin)
```

---

### User Endpoints

#### Get All Users
```
GET http://localhost:9090/api/users
(Requires Authentication Header - Admin only)
```

#### Get User by ID
```
GET http://localhost:9090/api/users/{userId}
(Requires Authentication Header)
```

Example:
```
GET http://localhost:9090/api/users/123e4567-e89b-12d3-a456-426614174000
```

#### Get User by Email
```
GET http://localhost:9090/api/users/email/{email}
(Requires Authentication Header)
```

Example:
```
GET http://localhost:9090/api/users/email/user@example.com
```

#### Get Users by Role
```
GET http://localhost:9090/api/users/role/{roleName}
(Requires Authentication Header)
```

Example:
```
GET http://localhost:9090/api/users/role/ADMIN
GET http://localhost:9090/api/users/role/USER
GET http://localhost:9090/api/users/role/STAFF
```

#### Update User
```
PUT http://localhost:9090/api/users/{userId}
(Requires Authentication Header)
```

#### Delete User
```
DELETE http://localhost:9090/api/users/{userId}
(Requires Authentication Header - Admin)
```

#### Enable User
```
PATCH http://localhost:9090/api/users/{userId}/enable
(Requires Authentication Header)
```

#### Disable User
```
PATCH http://localhost:9090/api/users/{userId}/disable
(Requires Authentication Header)
```

---

## 📋 COMPLETE ENDPOINT TABLE

| Method | Endpoint | Auth Required | Role Required |
|--------|----------|---------------|---------------|
| POST | /api/auth/register | ❌ | - |
| POST | /api/auth/login | ❌ | - |
| POST | /api/auth/create-staff | ✅ | USER |
| POST | /api/hotels | ✅ | USER |
| GET | /api/hotels | ✅ | USER |
| GET | /api/hotels/{id} | ✅ | USER |
| GET | /api/hotels/owner/my-hotels | ✅ | USER |
| POST | /api/hotels/from-opera/{id} | ✅ | USER |
| PUT | /api/hotels/{id} | ✅ | USER |
| DELETE | /api/hotels/{id} | ✅ | ADMIN |
| GET | /api/users | ✅ | ADMIN |
| GET | /api/users/{id} | ✅ | USER |
| GET | /api/users/email/{email} | ✅ | USER |
| GET | /api/users/role/{role} | ✅ | USER |
| PUT | /api/users/{id} | ✅ | USER |
| DELETE | /api/users/{id} | ✅ | ADMIN |
| PATCH | /api/users/{id}/enable | ✅ | USER |
| PATCH | /api/users/{id}/disable | ✅ | USER |

---

## 🔌 PORT CONFIGURATION

### Current Configuration
```
Server Port: 9090
Host: localhost
Base URL: http://localhost:9090
```

### Change Port
Edit: `src/main/resources/application.properties`
```properties
server.port=9091
```

Then all URLs become:
```
http://localhost:9091/... instead of http://localhost:9090/...
```

---

## 🔑 AUTHENTICATION HEADER FORMAT

### For Protected Endpoints

Add this header to all requests:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Example with cURL
```bash
curl -X GET http://localhost:9090/api/users/123 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

### Example with Postman
1. Select **Authorization** tab
2. Type: **Bearer Token**
3. Token: `<paste your JWT token>`

### Example with Fetch (JavaScript)
```javascript
fetch('http://localhost:9090/api/hotels', {
  method: 'GET',
  headers: {
    'Authorization': 'Bearer YOUR_JWT_TOKEN_HERE',
    'Content-Type': 'application/json'
  }
})
```

---

## 📲 REQUEST EXAMPLES

### Register User
```
POST http://localhost:9090/api/auth/register

Headers:
Content-Type: application/json

Body:
{
  "email": "user@example.com",
  "password": "Password123!",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890"
}
```

### Login
```
POST http://localhost:9090/api/auth/login

Headers:
Content-Type: application/json

Body:
{
  "email": "user@example.com",
  "password": "Password123!"
}
```

### Create Hotel (Authenticated)
```
POST http://localhost:9090/api/hotels

Headers:
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

Body:
{
  "operaPropertyId": "12345",
  "name": "Luxury Hotel",
  "city": "Paris",
  "country": "France"
}
```

### Get All Hotels (Authenticated)
```
GET http://localhost:9090/api/hotels

Headers:
Authorization: Bearer <JWT_TOKEN>
```

---

## 🗂️ OPTIONAL QUERY PARAMETERS

### Pagination (If Implemented)
```
GET http://localhost:9090/api/hotels?page=0&size=10
```

### Sorting (If Implemented)
```
GET http://localhost:9090/api/hotels?sort=name,asc
```

### Filtering (Custom Implementation)
```
GET http://localhost:9090/api/hotels?city=Paris&country=France
```

---

## 📊 HTTP STATUS CODES

| Code | Meaning | Example |
|------|---------|---------|
| 200 | OK | GET success |
| 201 | Created | POST success |
| 204 | No Content | DELETE success |
| 400 | Bad Request | Invalid JSON |
| 401 | Unauthorized | Missing token |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource doesn't exist |
| 500 | Server Error | Application error |

---

## 🔗 BROWSER BOOKMARKS

Save these URLs in your browser bookmarks:

```
Swagger UI
http://localhost:9090/swagger-ui.html

H2 Database
http://localhost:9090/h2-console

API Docs
http://localhost:9090/v3/api-docs

Health Check
http://localhost:9090/actuator/health
```

---

## 🚀 QUICK TEST URLS (Click to Test)

### 1. Check if Server is Running
Open in browser: http://localhost:9090/actuator/health

Expected response:
```json
{"status":"UP"}
```

### 2. See API Documentation
Open in browser: http://localhost:9090/swagger-ui.html

You should see: Swagger UI interface with all endpoints

### 3. Access Database
Open in browser: http://localhost:9090/h2-console

Login with:
- User: sa
- Password: (empty)

---

## 💻 COMMAND LINE TESTING

### Check Server Status (PowerShell)
```powershell
Invoke-WebRequest -Uri "http://localhost:9090/actuator/health"
```

### Register User (PowerShell)
```powershell
$body = @{
    email = "test@example.com"
    password = "Test123!"
    firstName = "Test"
    lastName = "User"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:9090/api/auth/register" `
    -Method POST `
    -Headers @{"Content-Type"="application/json"} `
    -Body $body
```

### Login (PowerShell)
```powershell
$body = @{
    email = "test@example.com"
    password = "Test123!"
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:9090/api/auth/login" `
    -Method POST `
    -Headers @{"Content-Type"="application/json"} `
    -Body $body

$response.Content | ConvertFrom-Json
```

---

## 🌍 EXTERNAL ACCESS (Localhost Only)

### Current
```
http://localhost:9090/swagger-ui.html
```

### Won't Work From Other Machines
```
http://127.0.0.1:9090/swagger-ui.html (No)
http://YOUR_IP:9090/swagger-ui.html (No)
http://YOUR_COMPUTER_NAME:9090/swagger-ui.html (No)
```

### For Remote Access (Production)
1. Configure firewall
2. Use reverse proxy (Nginx)
3. Deploy to server with public IP

---

## 📚 DOCUMENTATION LINKS

- **SWAGGER_GUIDE.md** - See this for detailed endpoint documentation
- **QUICK_START_SWAGGER.md** - Quick examples with copy-paste code
- **TROUBLESHOOTING.md** - Fix connection issues
- **ONE_PAGE_REFERENCE.md** - Printable quick reference

---

## ✅ ACCESSIBILITY CHECKLIST

- [x] Swagger UI loads? → http://localhost:9090/swagger-ui.html
- [x] H2 Console works? → http://localhost:9090/h2-console
- [x] Can register? → POST /api/auth/register
- [x] Can login? → POST /api/auth/login
- [x] Token received? → Check login response
- [x] Can create hotel? → POST /api/hotels (with token)

---

## 🎯 REMEMBER

**Base URL:** `http://localhost:9090`

**All URLs in this file are clickable** - Just add protocol:
- `http://localhost:9090/swagger-ui.html`
- `http://localhost:9090/h2-console`
- `http://localhost:9090/api/auth/login`

**Happy Testing! 🚀**

