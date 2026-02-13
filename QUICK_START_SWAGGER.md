# ⚡ Keezy Back - Quick Start Guide

## 🎯 Start the Application (3 Steps)

### Step 1: Open PowerShell
```powershell
cd W:\Outside\Kezzy_New\keezy_back
```

### Step 2: Run the Application
```powershell
.\mvnw.cmd spring-boot:run
```

### Step 3: Open Swagger UI
Once you see `Started KeezyBackApplication` in the console:

**🔗 http://localhost:9090/swagger-ui.html**

---

## 📝 First Test (Copy & Paste)

### 1️⃣ Register User
Click on **POST /api/auth/register** and use this data:

```json
{
  "email": "test@example.com",
  "password": "Test123!",
  "firstName": "Test",
  "lastName": "User"
}
```

✅ You'll get a **UserDTO** response

---

### 2️⃣ Login to Get Token
Click on **POST /api/auth/login** and use:

```json
{
  "email": "test@example.com",
  "password": "Test123!"
}
```

✅ Copy the **accessToken** value (long string starting with `eyJ`)

---

### 3️⃣ Authorize in Swagger
1. Click the **green "Authorize"** button at the top
2. Paste in the text box: `Bearer <paste-your-token-here>`
3. Click **Authorize**
4. Close the modal

---

### 4️⃣ Create a Hotel
Click on **POST /api/hotels** and use:

```json
{
  "operaPropertyId": "12345",
  "name": "My First Hotel",
  "city": "Paris",
  "country": "France"
}
```

✅ Hotel created!

---

## 🎮 All Available Endpoints

| Feature | Endpoint | Method |
|---------|----------|--------|
| 📝 Register | `/api/auth/register` | POST |
| 🔑 Login | `/api/auth/login` | POST |
| 👥 Create Staff | `/api/auth/create-staff` | POST |
| 🏨 Create Hotel | `/api/hotels` | POST |
| 📋 List Hotels | `/api/hotels` | GET |
| 🔍 Get Hotel | `/api/hotels/{id}` | GET |
| ✏️ Update Hotel | `/api/hotels/{id}` | PUT |
| ❌ Delete Hotel | `/api/hotels/{id}` | DELETE |
| 👤 Get Users | `/api/users` | GET |
| 🗑️ Delete User | `/api/users/{id}` | DELETE |

---

## 📊 Database (H2 Console)

**🔗 http://localhost:9090/h2-console**

Login with:
- User: `sa`
- Password: (empty)

Tables:
- `app_users` - User accounts
- `roles` - User roles
- `hotels` - Hotel data

---

## ⚠️ Common Issues

### Issue: "Failed to load API definition"
**Solution:** Make sure the app is fully started. Wait for the Spring Boot console to show "Started KeezyBackApplication"

### Issue: "Port 9090 in use"
**Solution:** Change port in `src/main/resources/application.properties`:
```properties
server.port=9091
```

### Issue: "Unauthorized" when calling endpoints
**Solution:** Click the "Authorize" button and paste your JWT token

---

## 🛑 Stop the Application
Press `Ctrl + C` in the PowerShell terminal

---

## 📞 Need Help?

- **Swagger errors?** Check `SWAGGER_GUIDE.md`
- **Architecture questions?** Check `ARCHITECTURE.md`
- **More details?** Check `README_VISUAL.md`

Happy coding! 🚀

