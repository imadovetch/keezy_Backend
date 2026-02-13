# 🚀 Running Keezy Back from IntelliJ IDEA

## Method 1: Run Directly from IDE (Recommended)

### Step 1: Open the Project
1. Open IntelliJ IDEA
2. Open project folder: `W:\Outside\Kezzy_New\keezy_back`

### Step 2: Run the Application
1. Locate the file: `KeezyBackApplication.java`
   - Path: `src/main/java/org/bloomberg/keezy_back/KeezyBackApplication.java`

2. Right-click on the file
3. Select **"Run 'KeezyBackApplication'"**

### Step 3: Wait for Startup
In the console, you should see:
```
Started KeezyBackApplication in X.XXX seconds (JVM running for Y.YYY)
```

### Step 4: Access Swagger
Open: **http://localhost:9090/swagger-ui.html**

---

## Method 2: Run from Maven Plugin

### Step 1: Open Maven View
1. Go to Menu: **View → Tool Windows → Maven**
2. On the right side, you'll see Maven panel

### Step 2: Find Spring Boot Goal
1. Expand: `keezy_back → Plugins → spring-boot`
2. Double-click on `spring-boot:run`

### Step 3: Application Starts
The console shows startup logs. Wait for:
```
Started KeezyBackApplication
```

### Step 4: Access Swagger
Open: **http://localhost:9090/swagger-ui.html**

---

## Method 3: Using Maven Run Configuration

### Step 1: Create Run Configuration
1. Go to: **Run → Edit Configurations...**
2. Click **"+Add New Configuration"**
3. Select **Maven**

### Step 2: Configure
- **Name:** Keezy Run
- **Run:** `spring-boot:run`
- **Working directory:** `$PROJECT_DIR$`

### Step 3: Run
1. Click the **Run** button (green play icon)
2. Or press **Shift + F10**

---

## Method 4: Using Spring Boot Run Configuration

### Step 1: Create Run Configuration
1. Go to: **Run → Edit Configurations...**
2. Click **"+Add New Configuration"**
3. Select **Spring Boot**

### Step 2: Configure
- **Name:** Keezy Spring Boot
- **Main class:** `org.bloomberg.keezy_back.KeezyBackApplication`
- **VM options:** (leave empty)
- **Working directory:** `$PROJECT_DIR$`

### Step 3: Run
1. Click **Run** (green play icon)
2. Or press **Shift + F10**

---

## 🔧 Troubleshooting in IntelliJ

### Issue: "Cannot find class"
**Solution:** IntelliJ hasn't indexed the project yet
```
1. Go to: File → Invalidate Caches... → Invalidate and Restart
2. Wait for reindexing
3. Try running again
```

### Issue: "Maven dependency error"
**Solution:** Refresh Maven dependencies
```
1. Open Maven panel (View → Tool Windows → Maven)
2. Right-click project name
3. Select "Reload Projects"
4. Wait for dependencies to download
```

### Issue: "Port 9090 already in use"
**Solution:** Change the port
```
1. Edit: src/main/resources/application.properties
2. Change: server.port=9090 to server.port=9091
3. Run again
```

### Issue: "Cannot connect to database"
**Solution:** H2 database is in-memory
```
- No action needed, H2 starts automatically
- Access console at: http://localhost:9090/h2-console
```

### Issue: "Spring Boot Configuration not available"
**Solution:** Install Spring Boot plugin
```
1. Go to: File → Settings → Plugins
2. Search for "Spring Boot"
3. Install "Spring Boot" plugin
4. Restart IntelliJ
```

---

## 🐛 Debugging the Application

### Enable Debug Mode
1. Right-click `KeezyBackApplication.java`
2. Select **"Debug 'KeezyBackApplication'"**
3. Set breakpoints by clicking on line numbers

### View Application Logs
1. The console at bottom shows all logs
2. Look for `Started KeezyBackApplication` message
3. Check for ERROR or WARN messages

### Common Log Messages

**Good startup:**
```
Started KeezyBackApplication in 3.456 seconds
```

**Error example:**
```
Error creating bean 'securityConfig'
Check if all dependencies are installed
```

---

## ⏹️ Stopping the Application

### Method 1: IDE Stop Button
1. In the Run console at bottom
2. Click the red **Stop** button (square icon)

### Method 2: Keyboard Shortcut
- Press **Ctrl + F2**

### After Stop
- Give it 2-3 seconds to shut down
- Console shows: `Process finished with exit code 0`

---

## 📊 Checking Application Health

### 1. Via Swagger UI
Open: http://localhost:9090/swagger-ui.html
- If page loads, application is running ✅

### 2. Via Actuator
Open: http://localhost:9090/actuator
Response shows:
```json
{
  "status": "UP",
  "components": {...}
}
```

### 3. Via Console
Look for in IntelliJ console:
```
Started KeezyBackApplication in X.XXX seconds
```

---

## 🔄 Restarting Application

### Quick Restart
1. Click the **Restart** button (circular arrow icon) in console
2. Or press **Ctrl + F5**

### Full Restart
1. Stop the application (Ctrl + F2)
2. Wait 2 seconds
3. Run again (Shift + F10)

---

## 📂 Project Files to Know

| File | Purpose |
|------|---------|
| `KeezyBackApplication.java` | Main entry point |
| `application.properties` | Server configuration |
| `pom.xml` | Maven dependencies |
| `SecurityConfig.java` | Security setup |
| `SwaggerConfig.java` | Swagger setup |

---

## 🌍 Environment Variables (Optional)

To set custom values, create a **Run Configuration**:

1. **Run → Edit Configurations...**
2. Find your configuration
3. Click **Environment variables**
4. Add variables:
```
SERVER_PORT=9090
JWT_SECRET=YourSecretKey
JWT_EXPIRATION=86400000
```

---

## 📋 Checklist for Successful Run

- [ ] Project opens in IntelliJ IDEA
- [ ] Maven dependencies downloaded (no red squiggles)
- [ ] `KeezyBackApplication.java` found in src/main/java
- [ ] Run button is available (green play icon)
- [ ] Click Run and see startup logs
- [ ] Console shows "Started KeezyBackApplication"
- [ ] Open http://localhost:9090/swagger-ui.html
- [ ] Swagger UI loads successfully
- [ ] Try POST /api/auth/register

---

## 🎯 What Happens When You Run

```
1. IntelliJ compiles Java code
   ↓
2. Maven downloads dependencies
   ↓
3. Spring Boot starts embedded Tomcat server
   ↓
4. Application loads on port 9090
   ↓
5. Swagger UI available at localhost:9090/swagger-ui.html
   ↓
6. H2 database initialized in-memory
   ↓
7. DataInitializationService creates default ROLES
   ↓
8. Application ready for API calls
```

---

## 💡 Pro Tips

### Faster Development
- Use **Ctrl + F5** for quick restart
- Check **Run in the background** in Run Configuration
- Enable **Hot reload** (IntelliJ Ultimate feature)

### Better Logging
- Add to `application.properties`:
```properties
logging.level.root=INFO
logging.level.org.bloomberg.keezy_back=DEBUG
```

### IntelliJ Keyboard Shortcuts
- **Shift + F10** - Run application
- **Shift + F9** - Debug application
- **Ctrl + F2** - Stop application
- **Ctrl + F5** - Rerun application
- **Alt + 9** - Open Run console

---

## 🎉 Success!

Once you see this in console:
```
Started KeezyBackApplication in 3.456 seconds (JVM running for 5.234)
```

Open this URL: **http://localhost:9090/swagger-ui.html**

You're done! 🚀

