# 📚 Keezy Back - Swagger & API Guide

## 🚀 Getting Started

### 1. **Start the Application**

The application runs on **Port 9090** by default.

**Option A: Using PowerShell Script**
```powershell
cd W:\Outside\Kezzy_New\keezy_back
.\run.ps1
```

**Option B: Using Maven Wrapper**
```bash
cd W:\Outside\Kezzy_New\keezy_back
.\mvnw.cmd spring-boot:run
```

**Option C: Using IntelliJ IDEA**
- Right-click on `KeezyBackApplication.java`
- Select "Run 'KeezyBackApplication'"

---

## 📖 Accessing Swagger UI

Once the application is running, access Swagger at:

### **URL: http://localhost:9090/swagger-ui.html**

Or use the OpenAPI JSON:
- **http://localhost:9090/v3/api-docs**

### **Expected Output:**
You should see the Swagger UI interface with:
- ✅ Authentication endpoints
- ✅ User management endpoints
- ✅ Hotel management endpoints
- ✅ All documented with request/response schemas

---

## 🔐 Authentication Flow

### **Step 1: Register a New User**

**Endpoint:** `POST /api/auth/register`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "SecurePassword123!",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890"
}
```

**Response (201 Created):**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890",
  "roleId": "role-123",
  "roleName": "USER",
  "enabled": true,
  "createdAt": 1707300000000
}
```

### **Step 2: Login to Get JWT Token**

**Endpoint:** `POST /api/auth/login`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "SecurePassword123!"
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "optional-refresh-token",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "user": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "roleId": "role-123",
    "roleName": "USER"
  }
}
```

---

## 🏨 Creating a Hotel

### **Endpoint:** `POST /api/hotels`

**Headers:**
```
Authorization: Bearer <accessToken>
Content-Type: application/json
```

**Request Body (Option 1 - With Property ID):**
```json
{
  "operaPropertyId": "1234567",
  "name": "Luxury Resort",
  "city": "New York",
  "country": "USA"
}
```

**Request Body (Option 2 - Manual Entry):**
```json
{
  "name": "Luxury Resort",
  "city": "New York",
  "country": "USA",
  "address": "123 Main Street",
  "zipCode": "10001",
  "phoneNumber": "+1-212-555-0100",
  "email": "hotel@example.com"
}
```

**Response (201 Created):**
```json
{
  "id": "hotel-123",
  "operaPropertyId": "1234567",
  "name": "Luxury Resort",
  "city": "New York",
  "country": "USA",
  "createdBy": "user@example.com",
  "createdAt": 1707300000000
}
```

---

## 👥 User Management (Staff Accounts)

### **Step 1: Create Staff Member**

**Endpoint:** `POST /api/auth/create-staff`

**Headers:**
```
Authorization: Bearer <accessToken>
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "staff@example.com",
  "password": "StaffPassword123!",
  "firstName": "Jane",
  "lastName": "Smith"
}
```

**Response (201 Created):**
```json
{
  "id": "staff-123",
  "email": "staff@example.com",
  "firstName": "Jane",
  "lastName": "Smith",
  "roleId": "role-staff",
  "roleName": "STAFF",
  "enabled": true
}
```

### **Staff Login**
Staff members can login using `/api/auth/login` with their email and password.

---

## 🛠️ Admin Operations

### **User Roles:**
- **ADMIN** - Can manage all users and delete accounts
- **USER** - Can create hotels and staff accounts
- **STAFF** - Created by users, has limited permissions

### **Delete User (Admin Only)**

**Endpoint:** `DELETE /api/users/{userId}`

**Headers:**
```
Authorization: Bearer <admin-accessToken>
```

**Response (204 No Content)** on success

---

## 📊 Database Access

### **H2 Database Console**

Access the H2 Database Console at:

**URL:** http://localhost:9090/h2-console

**Login Credentials:**
- **Driver Class:** `org.h2.Driver`
- **JDBC URL:** `jdbc:h2:mem:testdb`
- **User Name:** `sa`
- **Password:** (leave empty)

---

## 🔍 API Endpoints Summary

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/auth/register` | Register new user | ❌ |
| POST | `/api/auth/login` | Login user & get token | ❌ |
| POST | `/api/auth/create-staff` | Create staff account | ✅ |
| POST | `/api/hotels` | Create hotel | ✅ |
| GET | `/api/hotels` | Get all hotels | ✅ |
| GET | `/api/hotels/{id}` | Get hotel by ID | ✅ |
| PUT | `/api/hotels/{id}` | Update hotel | ✅ |
| DELETE | `/api/hotels/{id}` | Delete hotel | ✅ (ADMIN) |
| GET | `/api/users` | Get all users | ✅ (ADMIN) |
| DELETE | `/api/users/{userId}` | Delete user | ✅ (ADMIN) |

---

## ⚙️ Configuration

### **application.properties**

```properties
# Server
server.port=9090

# Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop

# Swagger
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/v3/api-docs

# JWT
jwt.secret=KeezySecretKeyForJWTTokenGenerationAndValidation12345678901234567890
jwt.expiration=86400000 # 24 hours in milliseconds
```

---

## 🐛 Troubleshooting

### **Swagger returns 403 error:**
- ✅ **Solution:** The endpoint is now configured to allow public access. Ensure SecurityConfig includes `/v3/api-docs/**` in permitted URLs.

### **JWT Token invalid or expired:**
- ✅ **Solution:** Get a new token by logging in again. Token expires after 24 hours.

### **H2 Database connection refused:**
- ✅ **Solution:** H2 is in-memory. Data is lost on application restart. Use persistent database in production.

### **Port 9090 already in use:**
- ✅ **Solution:** Change port in `application.properties`:
  ```properties
  server.port=9091
  ```

---

## 📚 Testing with Swagger UI

1. **Register:** Use `/api/auth/register` to create an account
2. **Login:** Use `/api/auth/login` to get a JWT token
3. **Authorize:** Click the "Authorize" button and paste: `Bearer <your-token>`
4. **Test Protected Endpoints:** All endpoints with a lock icon require authentication

---

## 🚀 Next Steps

1. ✅ Start the application
2. ✅ Access Swagger at http://localhost:9090/swagger-ui.html
3. ✅ Register and login to get JWT token
4. ✅ Test hotel creation and user management endpoints
5. ✅ Check H2 console for database records

Happy coding! 🎉

