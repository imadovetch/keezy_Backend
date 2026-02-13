# ✅ Keezy Back - Configuration Complete

## 📦 What Has Been Configured

### 1. **✅ Swagger/OpenAPI Documentation**
- **Status:** Fully configured and ready
- **UI Location:** http://localhost:9090/swagger-ui.html
- **JSON Docs:** http://localhost:9090/v3/api-docs
- **Annotations Added:** All controllers documented with @Operation and @ApiResponse

### 2. **✅ H2 Database**
- **Status:** Embedded in-memory database configured
- **Console URL:** http://localhost:9090/h2-console
- **Login:** User: `sa`, Password: (empty)
- **Tables Created:**
  - `app_users` - User accounts
  - `roles` - User roles (ADMIN, USER, STAFF)
  - `hotels` - Hotel management

### 3. **✅ JWT Authentication**
- **Implementation:** Custom JWT implementation (no external JJWT dependency)
- **Token Expiration:** 24 hours
- **Secret Key:** Configured in `application.properties`
- **Filter:** JwtAuthenticationFilter integrated

### 4. **✅ Spring Security**
- **Public Endpoints:**
  - `/api/auth/register` - User registration
  - `/api/auth/login` - User login
  - `/swagger-ui.html` - Swagger UI
  - `/v3/api-docs/**` - API documentation
  - `/h2-console/**` - H2 database console

- **Protected Endpoints:** All other endpoints require JWT token
- **Admin Endpoints:** User deletion and management require ADMIN role

### 5. **✅ DTOs & Mappers**
- **UserDTO** - User information transfer
- **HotelDTO** - Hotel information transfer
- **AuthResponseDTO** - Login response with JWT token
- **LoginDTO** - Login credentials
- **RegisterDTO** - User registration
- **CreateStaffDTO** - Staff creation
- **Manual Mappers:** UserMapper, HotelMapper (no external dependency needed)

### 6. **✅ Services Implemented**
- **AuthenticationService** - Register, login, staff creation
- **HotelService** - CRUD operations for hotels
- **UserService** - User management (get, update, delete, enable/disable)
- **JwtTokenProvider** - Token generation and validation
- **CustomUserDetailsService** - Spring Security integration
- **DataInitializationService** - Initialize roles on startup

### 7. **✅ Repositories**
- **AppUserRepository** - User data access
- **HotelRepository** - Hotel data access
- **RoleRepository** - Role data access

---

## 🚀 How to Start the Application

### Option 1: PowerShell Script (Recommended)
```powershell
cd W:\Outside\Kezzy_New\keezy_back
.\run.ps1
```

### Option 2: Maven Wrapper
```bash
cd W:\Outside\Kezzy_New\keezy_back
.\mvnw.cmd spring-boot:run
```

### Option 3: IntelliJ IDEA
1. Open the project in IntelliJ IDEA
2. Right-click on `KeezyBackApplication.java`
3. Select "Run 'KeezyBackApplication'"

---

## 📖 Documentation Files Created

| File | Purpose |
|------|---------|
| **SWAGGER_GUIDE.md** | Complete Swagger API usage guide |
| **QUICK_START_SWAGGER.md** | Quick reference for testing API |
| **run.ps1** | PowerShell script to run the application |

---

## 🔐 User Roles & Permissions

### **ADMIN** (Administrator)
- ✅ Create hotels
- ✅ Create staff accounts
- ✅ View all users
- ✅ Delete users
- ✅ Manage hotels

### **USER** (Normal User)
- ✅ Create own hotels
- ✅ Create staff accounts
- ✅ View own hotels
- ✅ Update own hotels
- ✅ Delete own hotels

### **STAFF** (Staff Member)
- ✅ Login with provided credentials
- ✅ Limited operations based on hotel assignment

---

## 📊 API Endpoints Summary

### Authentication (`/api/auth`)
```
POST   /api/auth/register      - Register new user
POST   /api/auth/login         - Login and get JWT token
POST   /api/auth/create-staff  - Create staff account
```

### Hotels (`/api/hotels`)
```
POST   /api/hotels                      - Create new hotel
GET    /api/hotels                      - Get all hotels
GET    /api/hotels/{id}                 - Get hotel by ID
GET    /api/hotels/owner/my-hotels      - Get user's hotels
POST   /api/hotels/from-opera/{id}      - Create hotel from Opera property ID
PUT    /api/hotels/{id}                 - Update hotel
DELETE /api/hotels/{id}                 - Delete hotel
```

### Users (`/api/users`)
```
GET    /api/users/{id}                  - Get user by ID
GET    /api/users/email/{email}         - Get user by email
GET    /api/users                       - Get all users (Admin)
GET    /api/users/role/{roleName}       - Get users by role
PUT    /api/users/{id}                  - Update user
DELETE /api/users/{id}                  - Delete user (Admin)
PATCH  /api/users/{id}/disable          - Disable user
PATCH  /api/users/{id}/enable           - Enable user
```

---

## 🛠️ Configuration Files

### `application.properties`
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
jwt.expiration=86400000
```

---

## 📝 Example Workflows

### 1. **Register & Login**
1. Call `POST /api/auth/register` with email and password
2. Call `POST /api/auth/login` with credentials
3. Copy the `accessToken` from response
4. Use token in `Authorization: Bearer <token>` header

### 2. **Create Hotel**
1. Login to get JWT token
2. Call `POST /api/hotels` with hotel details
3. Or call `POST /api/hotels/from-opera/{operaPropertyId}`

### 3. **Create Staff Account**
1. Login as a USER
2. Call `POST /api/auth/create-staff` with staff details
3. Share email and password with staff member
4. Staff can login with their credentials

### 4. **Manage Users (Admin Only)**
1. Login as ADMIN
2. Call `GET /api/users` to list all users
3. Call `DELETE /api/users/{userId}` to remove users
4. Call `PATCH /api/users/{userId}/disable` to disable users

---

## ⚠️ Important Notes

### Database
- **H2 is in-memory** - Data is lost when application restarts
- For production, use persistent database (PostgreSQL, MySQL, etc.)

### JWT Token
- Token expires after **24 hours** (86,400,000 ms)
- Each login generates a new token
- Token is validated on every protected endpoint request

### Security
- Passwords are hashed with **BCrypt**
- CSRF protection disabled (stateless API)
- Session management set to STATELESS
- All sensitive endpoints require authentication

### Swagger
- All public endpoints accessible without authentication
- Protected endpoints show lock icon in Swagger UI
- Click "Authorize" button to add JWT token

---

## 🔄 Project Structure

```
src/main/java/org/bloomberg/keezy_back/
├── KeezyBackApplication.java          # Main application entry point
├── config/
│   ├── SecurityConfig.java            # Spring Security configuration
│   ├── SwaggerConfig.java             # Swagger/OpenAPI configuration
│   └── ModelMapperConfig.java         # Bean configuration
├── Controller/
│   ├── AuthenticationController.java  # Auth endpoints
│   ├── HotelController.java           # Hotel endpoints
│   └── UserController.java            # User endpoints
├── Service/
│   ├── AuthenticationService.java     # Auth business logic
│   ├── HotelService.java              # Hotel business logic
│   ├── UserService.java               # User business logic
│   ├── JwtTokenProvider.java          # JWT implementation
│   └── CustomUserDetailsService.java  # Security details service
├── Entity/
│   ├── AppUser.java                   # User entity
│   ├── Hotel.java                     # Hotel entity
│   └── Role.java                      # Role entity
├── DTO/
│   ├── UserDTO.java
│   ├── HotelDTO.java
│   ├── AuthResponseDTO.java
│   ├── LoginDTO.java
│   ├── RegisterDTO.java
│   └── CreateStaffDTO.java
├── Mapper/
│   ├── UserMapper.java                # DTO mapping
│   └── HotelMapper.java
├── Repository/
│   ├── AppUserRepository.java
│   ├── HotelRepository.java
│   └── RoleRepository.java
└── resources/
    └── application.properties          # Configuration
```

---

## ✅ Testing Checklist

- [ ] Start application with `.\run.ps1`
- [ ] Open Swagger UI at http://localhost:9090/swagger-ui.html
- [ ] Register a new user via `/api/auth/register`
- [ ] Login and get JWT token via `/api/auth/login`
- [ ] Create a hotel via `POST /api/hotels`
- [ ] Create staff account via `/api/auth/create-staff`
- [ ] Access H2 console at http://localhost:9090/h2-console
- [ ] Verify data in database tables
- [ ] Test unauthorized access (without token)
- [ ] Test admin operations

---

## 📞 Support

For detailed API usage, see:
- **SWAGGER_GUIDE.md** - Comprehensive API guide
- **QUICK_START_SWAGGER.md** - Quick reference

All endpoints are documented in Swagger UI with:
- Request/Response schemas
- HTTP status codes
- Parameter descriptions
- Example requests

---

## 🎉 Ready to Use!

Your Keezy Back API is now fully configured and ready to use.

**Next Step:** Start the application and access Swagger at **http://localhost:9090/swagger-ui.html**

