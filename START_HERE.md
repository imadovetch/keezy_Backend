# 🎯 KEEZY BACK - EVERYTHING YOU NEED TO KNOW

## 🚀 START HERE - READ THIS FIRST (2 minutes)

### What is Keezy Back?
A **complete REST API** with:
- ✅ **Swagger UI** for testing endpoints
- ✅ **JWT Authentication** for secure login
- ✅ **H2 Database** for data storage
- ✅ **User Management** with roles
- ✅ **Hotel Management** with full CRUD

---

## 🎬 QUICK START (30 SECONDS)

### Step 1: Open PowerShell
```powershell
cd W:\Outside\Kezzy_New\keezy_back
```

### Step 2: Run the Application
```powershell
.\run.ps1
```

### Step 3: Wait for Message
Look for: **"Started KeezyBackApplication"** in console

### Step 4: Open Swagger
Click: **http://localhost:9090/swagger-ui.html**

### Done! 🎉
You now have a working API with interactive documentation.

---

## 📚 WHICH FILE TO READ?

### I have 2 minutes
→ Read: **PROJECT_OVERVIEW.md**

### I have 5 minutes  
→ Read: **QUICK_START_SWAGGER.md**

### I have 15 minutes
→ Read: **SWAGGER_GUIDE.md**

### I need to fix something
→ Read: **TROUBLESHOOTING.md**

### I need all URLs
→ Read: **ALL_URLS_ENDPOINTS.md**

### I want everything
→ Read: **DOCUMENTATION_INDEX.md**

---

## 🌐 IMPORTANT URLS

```
🎨 Swagger UI:          http://localhost:9090/swagger-ui.html
💾 Database Console:    http://localhost:9090/h2-console
📄 API Documentation:   http://localhost:9090/v3/api-docs
❤️  Health Check:        http://localhost:9090/actuator
```

**H2 Login:** User: `sa`, Password: (empty)

---

## 🔑 FIRST TEST

### 1. Register User
```
POST /api/auth/register
{
  "email": "test@test.com",
  "password": "Test123!",
  "firstName": "Test"
}
```

### 2. Login
```
POST /api/auth/login
{
  "email": "test@test.com",
  "password": "Test123!"
}
```
→ Copy the `accessToken`

### 3. Authorize Swagger
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

## 📊 WHAT'S INCLUDED

### 16 REST Endpoints
- 3 for Authentication
- 7 for Hotels
- 6 for Users

### 3 User Roles
- **ADMIN** - Full access
- **USER** - Create hotels
- **STAFF** - Limited access

### 3 Database Tables
- **app_users** - User accounts
- **roles** - User roles
- **hotels** - Hotel data

### 14 Documentation Files
- Getting started guides
- Complete API reference
- Troubleshooting
- Quick references

---

## ✅ WHAT'S BEEN DONE

- ✅ Swagger fully configured
- ✅ H2 database ready
- ✅ JWT authentication working
- ✅ All endpoints created
- ✅ Security configured
- ✅ Complete documentation
- ✅ Power script included
- ✅ No additional setup needed

---

## 💻 RECOMMENDED SETUP

### IntelliJ IDEA (Recommended)
1. Right-click **KeezyBackApplication.java**
2. Select **"Run 'KeezyBackApplication'"**
3. See logs in console
4. Open Swagger when ready

### PowerShell (Simple)
1. Run `.\run.ps1`
2. Wait for startup message
3. Open Swagger URL

### Maven Command
```bash
.\mvnw.cmd spring-boot:run
```

---

## 🎯 FEATURES YOU GET

### Authentication
- User registration
- User login (JWT token)
- Staff account creation
- Secure password hashing
- 24-hour token expiration

### Hotels
- Create hotels
- List all hotels
- Get hotel details
- Update hotels
- Delete hotels
- Create from Opera ID

### Users (Admin)
- View all users
- Get user details
- Search by email
- Filter by role
- Enable/disable users
- Delete users

---

## 🔒 SECURITY

- ✅ Passwords hashed with BCrypt
- ✅ JWT tokens for authentication
- ✅ Role-based access control
- ✅ Stateless API design
- ✅ Public endpoints whitelisted
- ✅ Protected endpoints require token

---

## 📖 DOCUMENTATION MAP

```
PROJECT_OVERVIEW.md
   ↓
QUICK_START_SWAGGER.md
   ↓
SWAGGER_GUIDE.md
   ↓
Specialized Guides:
   - RUN_FROM_INTELLIJ.md
   - TROUBLESHOOTING.md
   - ALL_URLS_ENDPOINTS.md
   - And 7 more...

👉 Start with PROJECT_OVERVIEW.md
```

---

## 🚨 IF SOMETHING DOESN'T WORK

### Swagger shows error
→ Wait 10 seconds, refresh page

### Can't login
→ Make sure you registered first

### Port 9090 in use
→ Change in application.properties

### H2 console doesn't load
→ Restart application

### More problems?
→ Read **TROUBLESHOOTING.md**

---

## ⚡ KEYBOARD SHORTCUTS

| Action | Command |
|--------|---------|
| Start | `.\run.ps1` |
| Stop | `Ctrl + C` |
| Refresh Swagger | `F5` |
| Open Swagger | `http://localhost:9090/swagger-ui.html` |

---

## 📋 QUICK CHECKLIST

- [ ] Started application with `.\run.ps1`
- [ ] See "Started KeezyBackApplication" in console
- [ ] Opened http://localhost:9090/swagger-ui.html
- [ ] Registered a test user
- [ ] Logged in and got token
- [ ] Created a hotel
- [ ] Checked H2 database

---

## 🎓 LEARNING PATH

### Day 1: Get It Running
1. Run `.\run.ps1`
2. Test register & login
3. Create a hotel
4. Explore Swagger

### Day 2: Understand It
1. Read CONFIGURATION_SUMMARY.md
2. Study project structure
3. Review security setup
4. Check database tables

### Day 3+: Extend It
1. Add custom features
2. Extend entities
3. Create new endpoints
4. Build frontend

---

## 🎊 YOU'RE ALL SET!

Everything is ready. No additional installation needed.

**Next Step:** Run `.\run.ps1` and start testing!

---

## 📞 SUPPORT

### Quick answer?
→ See: **ONE_PAGE_REFERENCE.md**

### Getting started?
→ See: **QUICK_START_SWAGGER.md**

### API details?
→ See: **SWAGGER_GUIDE.md**

### Fix an issue?
→ See: **TROUBLESHOOTING.md**

### Find all docs?
→ See: **DOCUMENTATION_INDEX.md**

---

## 🏆 SUCCESS INDICATORS

When everything is working correctly:

✅ Swagger UI loads  
✅ Can register user  
✅ Can login and get token  
✅ Can create hotel  
✅ H2 console shows data  

---

## 🚀 FINAL REMINDER

```
THIS IS YOUR CHECKLIST:

1. ✅ Read PROJECT_OVERVIEW.md (3 min)
2. ✅ Run .\run.ps1 (20 sec)
3. ✅ Open http://localhost:9090/swagger-ui.html
4. ✅ Test register → login → create hotel
5. ✅ Read next documentation file

YOU ARE READY TO GO! 🎉
```

---

**Status:** ✅ COMPLETE  
**Time to First Working API:** 5 minutes  
**Documentation Files:** 14  
**Lines of Code:** 2000+  
**Endpoints:** 16  

**Ready to use immediately!** 🚀

Start now: `.\run.ps1`

