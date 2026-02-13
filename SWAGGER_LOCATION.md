# 🎯 Where is Swagger? Visual Guide

## 📍 Swagger UI Location

Once your application is running, access Swagger at:

```
🔗 http://localhost:9090/swagger-ui.html
```

---

## 🚀 Step 1: Start Application

### Using PowerShell:
```powershell
cd W:\Outside\Kezzy_New\keezy_back
.\run.ps1
```

### Wait for this message in console:
```
Started KeezyBackApplication in X.XXX seconds
```

---

## 🌐 Step 2: Open Swagger URL

Click this link or paste in your browser:

### **http://localhost:9090/swagger-ui.html**

---

## 👀 What You Should See

### Swagger UI Interface
```
┌─────────────────────────────────────────────┐
│  Swagger UI          [Authorize] [▼]         │
├─────────────────────────────────────────────┤
│                                              │
│  Keezy Back API                             │
│  REST API documentation for Keezy           │
│  Version: 1.0.0                             │
│                                              │
│  ✓ Authentication                           │
│    POST   /api/auth/register               │
│    POST   /api/auth/login                  │
│    POST   /api/auth/create-staff           │
│                                              │
│  ✓ Hotels                                   │
│    POST   /api/hotels                       │
│    GET    /api/hotels                       │
│    GET    /api/hotels/{id}                  │
│    PUT    /api/hotels/{id}                  │
│    DELETE /api/hotels/{id}                  │
│                                              │
│  ✓ Users                                    │
│    GET    /api/users/{id}                   │
│    GET    /api/users                        │
│    DELETE /api/users/{id}                   │
│                                              │
└─────────────────────────────────────────────┘
```

---

## 🎮 Testing in Swagger

### 1. Try POST /api/auth/register
```
1. Click on "POST /api/auth/register"
2. Click "Try it out"
3. Paste this in Request body:
{
  "email": "test@example.com",
  "password": "Test123!",
  "firstName": "Test",
  "lastName": "User"
}
4. Click "Execute"
```

### 2. Copy Response & Login
```
You'll get a response like:
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "email": "test@example.com",
  ...
}
```

### 3. Login to Get Token
```
1. Click "POST /api/auth/login"
2. Click "Try it out"
3. Paste:
{
  "email": "test@example.com",
  "password": "Test123!"
}
4. Click "Execute"
5. COPY the accessToken value
```

### 4. Authorize Swagger
```
1. Click green [Authorize] button at top
2. Paste: Bearer <your-token-here>
3. Click "Authorize"
4. Now you can test protected endpoints!
```

---

## 🔑 JWT Token Flow

```
1. Register or Login
         ↓
2. Get accessToken from response
         ↓
3. Click Authorize button in Swagger
         ↓
4. Paste: Bearer <token>
         ↓
5. Access protected endpoints
         ↓
6. Token expires after 24 hours
```

---

## 📱 Alternative API Testing Tools

If Swagger UI doesn't work, use these tools:

### **Postman**
```
1. Download Postman: https://www.postman.com/downloads/
2. Import: Keezy_API.postman_collection.json
3. Set JWT token in Authorization header
```

### **cURL (Command Line)**
```bash
# Register
curl -X POST http://localhost:9090/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test123!"}'

# Login
curl -X POST http://localhost:9090/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test123!"}'

# Create Hotel (with token)
curl -X POST http://localhost:9090/api/hotels \
  -H "Authorization: Bearer <your-token>" \
  -H "Content-Type: application/json" \
  -d '{"operaPropertyId":"12345","name":"Hotel","city":"Paris"}'
```

### **JavaScript/Fetch**
```javascript
// Login
const response = await fetch('http://localhost:9090/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    email: 'test@example.com',
    password: 'Test123!'
  })
});

const data = await response.json();
const token = data.accessToken;

// Create Hotel
const hotelResponse = await fetch('http://localhost:9090/api/hotels', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    operaPropertyId: '12345',
    name: 'My Hotel',
    city: 'Paris'
  })
});
```

---

## 🔗 Other URLs

| URL | Purpose |
|-----|---------|
| http://localhost:9090/swagger-ui.html | Swagger UI |
| http://localhost:9090/v3/api-docs | OpenAPI JSON |
| http://localhost:9090/h2-console | Database Console |
| http://localhost:9090/actuator | Health Check |

---

## ❌ Common Issues & Solutions

### Issue: "Failed to load API definition"
```
✓ Check if application is running
✓ Wait 5-10 seconds for full startup
✓ Refresh the page (F5)
✓ Clear browser cache (Ctrl+Shift+Delete)
```

### Issue: 404 Not Found
```
✓ Check if port 9090 is correct
✓ Verify application started successfully
✓ Check logs for errors
```

### Issue: CORS Error
```
✓ Swagger UI is on same domain
✓ CORS is configured in SecurityConfig
✓ Try in Incognito mode
```

### Issue: Token Expired
```
✓ Login again to get new token
✓ Token lasts 24 hours
✓ Each login generates new token
```

---

## 🎯 Quick Reference

```
START:           .\run.ps1
SWAGGER:         http://localhost:9090/swagger-ui.html
DATABASE:        http://localhost:9090/h2-console
STOP:            Ctrl+C in PowerShell
LOGIN:           POST /api/auth/login
CREATE HOTEL:    POST /api/hotels
GET HOTELS:      GET /api/hotels
AUTHORIZE:       Click [Authorize] → Bearer <token>
```

---

## ✅ Success Indicators

When Swagger loads correctly, you should see:

✅ Title: "Keezy Back API"  
✅ Version: "1.0.0"  
✅ Green "Authorize" button at top right  
✅ Three sections: Authentication, Hotels, Users  
✅ Multiple endpoints listed  
✅ Request/Response schemas visible  

---

## 🎉 You're All Set!

Your API is ready. Start the app and enjoy Swagger! 🚀

```
http://localhost:9090/swagger-ui.html
```

