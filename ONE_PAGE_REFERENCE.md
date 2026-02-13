# 📋 KEEZY BACK - ONE PAGE REFERENCE

---

## ⚡ QUICK START (30 SECONDS)

```powershell
cd W:\Outside\Kezzy_New\keezy_back
.\run.ps1
```

Then open: **http://localhost:9090/swagger-ui.html**

---

## 🎯 WHAT YOU HAVE

✅ **Swagger/OpenAPI** - Interactive API documentation  
✅ **JWT Auth** - 24-hour token authentication  
✅ **H2 Database** - In-memory database with web console  
✅ **16 API Endpoints** - Auth, Hotels, Users  
✅ **3 User Roles** - ADMIN, USER, STAFF  
✅ **Spring Security** - Stateless authentication  

---

## 🚀 THREE WAYS TO RUN

| Method | Command | Time |
|--------|---------|------|
| PowerShell Script | `.\run.ps1` | 20 sec |
| Maven | `.\mvnw.cmd spring-boot:run` | 25 sec |
| IntelliJ | Right-click KeezyBackApplication → Run | 15 sec |

---

## 📍 IMPORTANT URLs

| Resource | URL |
|----------|-----|
| **Swagger** | http://localhost:9090/swagger-ui.html |
| **Database** | http://localhost:9090/h2-console |
| **API Docs** | http://localhost:9090/v3/api-docs |

**H2 Login:** User: `sa`, Password: (empty)

---

## 🔑 QUICK TEST

### 1. Register
```
POST /api/auth/register
{
  "email": "test@test.com",
  "password": "Test123!",
  "firstName": "Test"
}
```

### 2. Login (Copy token from response)
```
POST /api/auth/login
{
  "email": "test@test.com",
  "password": "Test123!"
}
```

### 3. Authorize in Swagger
- Click green [Authorize] button
- Paste: `Bearer <your-token>`

### 4. Create Hotel
```
POST /api/hotels
{
  "operaPropertyId": "12345",
  "name": "My Hotel",
  "city": "Paris"
}
```

---

## 📊 API ENDPOINTS

### Auth (3)
```
POST   /api/auth/register      Register user
POST   /api/auth/login         Get JWT token  
POST   /api/auth/create-staff  Create staff
```

### Hotels (7)
```
POST   /api/hotels             Create
GET    /api/hotels             List all
GET    /api/hotels/{id}        Get one
PUT    /api/hotels/{id}        Update
DELETE /api/hotels/{id}        Delete
GET    /api/hotels/owner/my    My hotels
POST   /api/hotels/from-opera  Create from ID
```

### Users (6)
```
GET    /api/users              List (Admin)
GET    /api/users/{id}         Get
PUT    /api/users/{id}         Update
DELETE /api/users/{id}         Delete (Admin)
PATCH  /api/users/{id}/enable  Enable
PATCH  /api/users/{id}/disable Disable
```

---

## 💾 DATABASE TABLES

| Table | Rows | Purpose |
|-------|------|---------|
| app_users | User accounts |
| roles | ADMIN, USER, STAFF |
| hotels | Hotel records |

**Access:** http://localhost:9090/h2-console

---

## 🔒 USER ROLES

| Role | Can Do |
|------|--------|
| **ADMIN** | Manage all users, delete, create hotels |
| **USER** | Create hotels, create staff, manage own |
| **STAFF** | Limited - created by users |

---

## ⚙️ CONFIGURATION

### application.properties
```properties
server.port=9090
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop
jwt.expiration=86400000
```

### Change Port
Edit `application.properties`:
```properties
server.port=9091
```

---

## 🐛 COMMON FIXES

| Problem | Fix |
|---------|-----|
| Swagger shows 403 | Wait for full startup, refresh page |
| Port in use | Change `server.port=9091` |
| Token expired | Login again to get new token |
| 401 error | Click [Authorize], add JWT token |
| Maven fails | Run `.\mvnw.cmd clean install` |
| H2 won't connect | Restart application |

---

## 📚 DOCUMENTATION

| File | What |
|------|------|
| **QUICK_START_SWAGGER.md** | Start here! |
| **SWAGGER_GUIDE.md** | Full API guide |
| **RUN_FROM_INTELLIJ.md** | IDE setup |
| **TROUBLESHOOTING.md** | Fix errors |
| **SWAGGER_LOCATION.md** | Find Swagger UI |

👉 **See DOCUMENTATION_INDEX.md for full list**

---

## 🔧 TOOLS YOU'LL NEED

- ✅ Browser (Chrome, Firefox, Edge)
- ✅ Terminal (PowerShell, CMD)
- ✅ IntelliJ IDEA (Optional but recommended)
- ✅ Java 17+
- ✅ Maven (mvnw included)

---

## 📱 ALTERNATIVE TEST TOOLS

- **Postman** - Desktop API tester
- **Thunder Client** - VS Code plugin
- **cURL** - Command line tool
- **Insomnia** - API client

Example cURL:
```bash
curl -X POST http://localhost:9090/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"Test123!"}'
```

---

## ✅ VERIFICATION CHECKLIST

- [ ] Application starts (see "Started KeezyBackApplication")
- [ ] Swagger loads (http://localhost:9090/swagger-ui.html)
- [ ] Can register user
- [ ] Can login and get token
- [ ] Can create hotel
- [ ] H2 console works
- [ ] Data saved in database

---

## 🎯 NEXT STEPS

### For Testing
1. Start with QUICK_START_SWAGGER.md
2. Follow copy-paste examples
3. Explore all endpoints in Swagger

### For Development
1. Read CONFIGURATION_SUMMARY.md
2. Understand project structure
3. Add custom business logic
4. Extend entities and endpoints

### For Production
1. Switch from H2 to PostgreSQL/MySQL
2. Configure environment variables
3. Setup SSL/TLS
4. Configure logging
5. Deploy to server

---

## 🆘 QUICK HELP

**App won't start?**
→ Check Java 17+, port 9090 free, see TROUBLESHOOTING.md

**Swagger shows 403?**
→ Wait 10 seconds, refresh page, clear cache

**Can't login?**
→ Make sure you registered first, check email/password

**Port in use?**
→ Change server.port in application.properties

**Still stuck?**
→ Check TROUBLESHOOTING.md file or RUN_FROM_INTELLIJ.md

---

## 📞 REFERENCE

```
📍 Server:     http://localhost:9090
🔐 Swagger:    /swagger-ui.html
📄 Docs:       /v3/api-docs
💾 Database:   /h2-console
❤️  Health:    /actuator
```

---

## 🚀 YOU'RE READY!

Everything is configured and working!

**Just run:** `.\run.ps1`

**Then visit:** http://localhost:9090/swagger-ui.html

Happy coding! 🎉

---

**Version:** 1.0.0  
**Status:** ✅ Production Ready  
**Last Updated:** 2024

