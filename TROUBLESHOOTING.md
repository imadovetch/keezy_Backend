# 🐛 Keezy Back - Troubleshooting Guide

## 🚨 Common Problems & Solutions

---

## Problem 1: "Failed to load API definition" in Swagger

### ❌ Error Message
```
Fetch error
response status is 403 /v3/api-docs
```

### ✅ Solution

**Step 1:** Check if application is running
```
Look for: "Started KeezyBackApplication"
in the console
```

**Step 2:** Wait for full startup (10-15 seconds)

**Step 3:** Clear browser cache
```
Ctrl + Shift + Delete → Clear all
```

**Step 4:** Refresh page
```
F5 or Ctrl + R
```

**Step 5:** Try incognito mode
```
Ctrl + Shift + N (Chrome/Edge)
Ctrl + Shift + P (Firefox)
```

**Step 6:** Check if all dependencies are loaded
```
Look for: "Started KeezyBackApplication in X.XXX seconds"
Should be 3-5 seconds, not error message
```

---

## Problem 2: Port 9090 Already in Use

### ❌ Error Message
```
Address already in use: bind
Port 9090 already in use
```

### ✅ Solution A: Change Port

1. Edit file: `src/main/resources/application.properties`
2. Find line: `server.port=9090`
3. Change to: `server.port=9091` (or any free port)
4. Save and restart

### ✅ Solution B: Kill Process Using Port

**PowerShell:**
```powershell
# Find process using port 9090
netstat -ano | findstr :9090

# Kill the process (replace PID with actual number)
taskkill /PID <PID> /F
```

**Find Free Port:**
```powershell
# Check if port is free
Test-NetConnection -ComputerName localhost -Port 9090
```

---

## Problem 3: "Cannot resolve symbol" - Missing Dependencies

### ❌ Error Message
```
Cannot resolve symbol 'ModelMapperConfig'
Cannot resolve symbol 'SwaggerConfig'
```

### ✅ Solution

**Step 1:** Refresh Maven Cache
```
View → Tool Windows → Maven
Right-click project → Reload Projects
```

**Step 2:** Invalidate IntelliJ Cache
```
File → Invalidate Caches... → Invalidate and Restart
```

**Step 3:** Manual Maven Update
```powershell
cd W:\Outside\Kezzy_New\keezy_back
.\mvnw.cmd clean
.\mvnw.cmd install
```

**Step 4:** Check pom.xml
- Open `pom.xml`
- Verify no red squiggles
- If red, right-click → Maven → Reload project

---

## Problem 4: Maven Build Fails

### ❌ Error Message
```
BUILD FAILURE
[ERROR] Failed to execute goal
```

### ✅ Solution

**Step 1:** Clean and rebuild
```powershell
cd W:\Outside\Kezzy_New\keezy_back
.\mvnw.cmd clean
.\mvnw.cmd compile
```

**Step 2:** Check for syntax errors
- Look for red underlines in Java files
- Fix any compilation errors shown

**Step 3:** Force update dependencies
```powershell
.\mvnw.cmd clean install -U
```

**Step 4:** Delete cache
```powershell
# Delete Maven cache
Remove-Item -Force -Recurse $ENV:USERPROFILE\.m2\repository
.\mvnw.cmd install
```

---

## Problem 5: Application Starts but Swagger Doesn't Load

### ❌ Error Signs
- Application shows "Started successfully"
- But http://localhost:9090/swagger-ui.html shows blank page
- Or shows 404 error

### ✅ Solution

**Step 1:** Check application.properties
```
Make sure these lines exist:
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/v3/api-docs
```

**Step 2:** Verify SwaggerConfig.java exists
```
File: src/main/java/org/bloomberg/keezy_back/config/SwaggerConfig.java
Should have @Configuration and @Bean annotations
```

**Step 3:** Check dependency in pom.xml
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.1.0</version>
</dependency>
```

**Step 4:** Restart application completely
```
Ctrl + F2 (Stop)
Wait 3 seconds
Shift + F10 (Run)
```

---

## Problem 6: 401 Unauthorized When Calling API

### ❌ Error Message
```
{
  "error": "Unauthorized",
  "status": 401
}
```

### ✅ Solution

**For Swagger UI:**
1. Click the green "Authorize" button
2. Paste: `Bearer <your-jwt-token>`
3. Click Authorize
4. Now try the endpoint again

**For Postman/cURL:**
```
Add header:
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**To Get Token:**
1. Login via `POST /api/auth/login`
2. Copy the `accessToken` value
3. Use in Authorization header

---

## Problem 7: 403 Forbidden - Access Denied

### ❌ Error Message
```
{
  "error": "Forbidden",
  "status": 403
}
```

### ✅ Solution

**Reason:** You don't have the required role

**Example:**
- Trying to delete user without ADMIN role
- Only ADMIN can delete users

**Solution:**
1. Check your user's role
2. Login as ADMIN user
3. Or grant yourself the ADMIN role via H2 console

**Via H2 Console:**
```sql
-- See your role
SELECT u.email, r.roleType FROM app_users u 
JOIN roles r ON u.role_id = r.id;

-- Update role to ADMIN
UPDATE roles SET roleType = 'ADMIN' 
WHERE id = (SELECT role_id FROM app_users WHERE email = 'your@email.com');
```

---

## Problem 8: H2 Database Connection Failed

### ❌ Error Message
```
Cannot connect to database
H2 console not working
```

### ✅ Solution

**Step 1:** Verify H2 is configured
```
In application.properties:
spring.datasource.url=jdbc:h2:mem:testdb
spring.h2.console.enabled=true
```

**Step 2:** Access H2 console
```
URL: http://localhost:9090/h2-console
JDBC URL: jdbc:h2:mem:testdb
User: sa
Password: (leave empty)
```

**Step 3:** If console shows 403
- Check SecurityConfig allows H2
- Add to permitAll():
  - `/h2-console/**`

**Step 4:** Tables not showing
- In H2 console, run:
  ```sql
  SHOW TABLES;
  ```
- If empty, database wasn't initialized
- Restart application

---

## Problem 9: JWT Token Expired or Invalid

### ❌ Error Message
```
{
  "error": "Invalid token",
  "message": "Token expired"
}
```

### ✅ Solution

**Token Expired After 24 Hours:**
```
1. Login again to get new token
2. Update token in Swagger Authorize button
3. Or update Authorization header in postman
```

**Invalid Token Format:**
```
Make sure header is:
Authorization: Bearer <token>

NOT just the token, must include "Bearer "
```

**Check Token Expiration:**
```
Token lasts: 24 hours (86400000 milliseconds)
Configured in: application.properties
jwt.expiration=86400000
```

---

## Problem 10: IntelliJ "Run" Button Not Showing

### ❌ Error Signs
- No green play button
- Can't run application from IDE

### ✅ Solution

**Step 1:** Mark source as source root
```
Right-click: src/main/java
Select: Mark Directory as → Sources Root
```

**Step 2:** Open KeezyBackApplication
```
Navigate to: src/main/java/org/bloomberg/keezy_back/KeezyBackApplication.java
Right-click → Run
```

**Step 3:** Install Spring Boot Plugin
```
File → Settings → Plugins
Search: Spring Boot
Install and restart IntelliJ
```

**Step 4:** Create Run Configuration Manually
```
Run → Edit Configurations
Add → Spring Boot
Main class: org.bloomberg.keezy_back.KeezyBackApplication
```

---

## Problem 11: "The input line is too long" on Windows

### ❌ Error Message
```
The command line is too long
```

### ✅ Solution

**Use Maven Wrapper:**
```powershell
.\mvnw.cmd spring-boot:run
```

**Or create batch file:**
```batch
@echo off
cd W:\Outside\Kezzy_New\keezy_back
call mvnw.cmd spring-boot:run
```

---

## Problem 12: Compilation Error: Symbol Not Found

### ❌ Error Message
```
java: cannot find symbol
symbol: class AppUser
location: class org.bloomberg.keezy_back.Service
```

### ✅ Solution

**Step 1:** Check imports in the file
```
Should have:
import org.bloomberg.keezy_back.Entity.AppUser;
```

**Step 2:** Rebuild project
```
Build → Rebuild Project
```

**Step 3:** Clear cache
```
File → Invalidate Caches → Invalidate and Restart
```

**Step 4:** Check file exists
```
src/main/java/org/bloomberg/keezy_back/Entity/AppUser.java
```

---

## Problem 13: CORS Error in Browser

### ❌ Error Message
```
Access to XMLHttpRequest blocked by CORS policy
```

### ✅ Solution

**For Testing from Browser:**
Swagger UI is already on same domain, so no issue

**For External Frontend:**
Add CORS configuration to SecurityConfig:
```java
.cors(cors -> cors.configurationSource(request -> {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
    config.setAllowedMethods(Arrays.asList("*"));
    config.setAllowedHeaders(Arrays.asList("*"));
    return config;
}))
```

---

## Problem 14: Postman "Send Request" Fails

### ❌ Error Message
```
Could not get any response
```

### ✅ Solution

**Step 1:** Check URL format
```
Correct: http://localhost:9090/api/auth/login
Incorrect: localhost:9090/api/auth/login (missing http://)
```

**Step 2:** Check Authorization header
```
Headers tab → Authorization
Type: Bearer Token
Token: <paste-your-jwt-token>
```

**Step 3:** Check JSON format
```
Headers → Content-Type: application/json
Body → Select "raw" and "JSON"
```

**Step 4:** Application running?
```
Check console shows: "Started KeezyBackApplication"
```

---

## Problem 15: Password Hashing Issues

### ❌ Error Message
```
BadCredentialsException: Bad credentials
```

### ✅ Solution

**Passwords are BCrypt hashed**
- During registration, password is encrypted
- During login, provided password is hashed and compared
- Plain text passwords won't work

**When Testing:**
1. Register with password: `Test123!`
2. Login with same password: `Test123!`
3. System automatically hashes and compares

**Direct Database Testing:**
```sql
-- DON'T: Insert plain password
INSERT INTO app_users (email, password) VALUES ('test@ex.com', 'Test123!');

-- Instead use application registration endpoint
POST /api/auth/register with email and password
```

---

## 🆘 Still Not Working?

### Check These Files Exist
- ✓ `src/main/java/org/bloomberg/keezy_back/KeezyBackApplication.java`
- ✓ `src/main/resources/application.properties`
- ✓ `src/main/java/org/bloomberg/keezy_back/config/SwaggerConfig.java`
- ✓ `src/main/java/org/bloomberg/keezy_back/config/SecurityConfig.java`
- ✓ `pom.xml` (should be valid XML)

### Check Console Log For
- ✓ "Started KeezyBackApplication"
- ✗ No ERROR messages in red
- ✗ No WARN about missing dependencies

### Try Nuclear Option
```powershell
# Stop application (Ctrl + C)

# Clean everything
cd W:\Outside\Kezzy_New\keezy_back
.\mvnw.cmd clean

# Rebuild
.\mvnw.cmd install

# Run
.\mvnw.cmd spring-boot:run
```

### Last Resort
1. Close IntelliJ completely
2. Delete `.idea` folder
3. Delete `target` folder
4. Reopen project
5. Run again

---

## 📞 Quick Reference

| Problem | Command |
|---------|---------|
| Port in use | Change `server.port=9091` in properties |
| Clear cache | `Ctrl + Shift + Delete` in browser |
| Restart IDE | `File → Invalidate Caches → Restart` |
| Rebuild Maven | `.\mvnw.cmd clean install` |
| Restart app | `Ctrl + F5` in IntelliJ |
| Get new token | Login via `/api/auth/login` |
| Check database | Open H2 console at `/h2-console` |
| View logs | Check Run console at bottom of IntelliJ |

---

## ✅ Verification Checklist

- [ ] Application starts without errors
- [ ] "Started KeezyBackApplication" in console
- [ ] Swagger loads at http://localhost:9090/swagger-ui.html
- [ ] Can register user via POST /api/auth/register
- [ ] Can login via POST /api/auth/login
- [ ] Can access H2 console at /h2-console
- [ ] Database has tables: app_users, roles, hotels
- [ ] JWT token received in login response
- [ ] Protected endpoints require Authorization header
- [ ] Swagger Authorize button works

---

## 🎯 Getting Help

1. Check **SWAGGER_GUIDE.md** for API usage
2. Check **RUN_FROM_INTELLIJ.md** for IDE setup
3. Check application console for error messages
4. Check browser console (F12) for client errors
5. Use H2 console to inspect database

---

Good luck! 🚀

