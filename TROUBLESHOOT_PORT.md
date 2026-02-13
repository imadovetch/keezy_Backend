# 🔧 RÉSOLUTION: PORT DÉJÀ EN UTILISATION

## Problème

Vous recevez cette erreur au démarrage:
```
Web server failed to start. Port 9090 was already in use.
```

## Solutions

### Solution 1: Tuer le processus utilisant le port (RAPIDE ✅)

#### Windows PowerShell

```powershell
# Trouver le processus
netstat -ano | findstr :9090

# Résultat:
# TCP    0.0.0.0:9090           0.0.0.0:0              LISTENING       12345

# Tuer le processus (remplacez 12345 par le PID)
taskkill /PID 12345 /F

# Vérifier que le port est libre
netstat -ano | findstr :9090
# (aucun résultat = port libre ✅)

# Relancer l'application
.\mvnw.cmd spring-boot:run
```

#### Windows CMD

```cmd
# Trouver le processus
netstat -ano | findstr :9090

# Tuer le processus
taskkill /PID 12345 /F

# Relancer
mvnw.cmd spring-boot:run
```

### Solution 2: Changer de port (ALTERNATIF)

#### Fichier `application.properties`

Trouvez/modifiez cette ligne:
```properties
server.port=9090
```

Changez-la en:
```properties
server.port=8888
```

Ou utilisez un autre port (8081, 3000, 5000, etc.)

#### Relancer l'application

```powershell
.\mvnw.cmd spring-boot:run
# Maintenant elle écoute sur http://localhost:8888
```

### Solution 3: Forcer le port dans les commandes Maven

```powershell
# Démarrer sur le port 9091
.\mvnw.cmd spring-boot:run -Dspring-boot.run.arguments="--server.port=9091"
```

---

## Vérifier si le port est libre

```powershell
# Vérifier le port 9090
netstat -ano | findstr :9090

# Résultats possibles:
# - Aucun résultat = port libre ✅
# - TCP ... LISTENING = port occupé ❌

# Vérifier plusieurs ports
netstat -ano | findstr :8080
netstat -ano | findstr :8081
netstat -ano | findstr :9090
```

---

## Ports recommandés

```
Disponibles généralement:
8888, 8889, 9000, 9090, 9091, 3000, 5000, 5001

À ÉVITER:
3306 (MySQL)
5432 (PostgreSQL)
27017 (MongoDB)
6379 (Redis)
```

---

## QuickFix (copier-coller rapide)

### Windows PowerShell
```powershell
# 1. Trouver les processus utilisant les ports courants
Write-Host "Ports en utilisation:"
netstat -ano | findstr ":9090\|:8080\|:8081"

# 2. Tuer un processus spécifique (remplacez PID)
taskkill /PID YOUR_PID /F

# 3. Relancer l'app
cd W:\Outside\Kezzy_New\keezy_back
.\mvnw.cmd spring-boot:run
```

---

## Diagnostique complet

```powershell
# Script complet de diagnostique

# 1. Vérifier Java
Write-Host "=== Java ==="
java -version

# 2. Vérifier les ports
Write-Host "=== Ports en utilisation ==="
netstat -ano | findstr "LISTENING" | findstr ":8\|:9"

# 3. Vérifier Maven
Write-Host "=== Maven ==="
.\mvnw.cmd -v

# 4. Compiler
Write-Host "=== Compilation ==="
.\mvnw.cmd clean compile
```

---

## FAQ - Port

**Q: Quel port par défaut?**  
R: 9090 (dans `application.properties`)

**Q: Comment savoir quel port utiliser?**  
R: Vérifiez avec `netstat -ano | findstr :PORT_NUMBER`

**Q: Puis-je utiliser n'importe quel port?**  
R: Oui, 1024-65535 (évitez les ports système < 1024)

**Q: L'app démarre sur quel port après changement?**  
R: Le port que vous avez configuré (ex: 8888)

**Q: Comment changer le port sans modifier le fichier?**  
R: `.\mvnw.cmd spring-boot:run --server.port=8888`

---

## Si ça ne marche pas encore

```powershell
# Option nucléaire: fermer tous les Java
taskkill /F /IM java.exe

# Vérifier que tous les ports sont libres
netstat -ano | findstr "LISTENING"

# Relancer l'app
cd W:\Outside\Kezzy_New\keezy_back
.\mvnw.cmd spring-boot:run
```

---

*Version: 1.0*  
*Dernière mise à jour: 2026-02-07*

