# 🚀 KEEZY BACKEND - COMMANDES PRATIQUES

## Démarrage rapide

### Compiler le projet
```powershell
cd W:\Outside\Kezzy_New\keezy_back
.\mvnw.cmd clean compile
```

### Lancer l'application
```powershell
cd W:\Outside\Kezzy_New\keezy_back
.\mvnw.cmd spring-boot:run
```

### Lancer avec un port custom
```powershell
# Modifier application.properties
# server.port=9090
# Puis:
.\mvnw.cmd spring-boot:run
```

## Vérifications

### Vérifier que ça compile
```powershell
.\mvnw.cmd clean compile
# Résultat attendu: BUILD SUCCESS
```

### Vérifier que l'app démarre
```powershell
.\mvnw.cmd spring-boot:run
# Résultat attendu: 
# Started KeezyBackApplication in X.XXX seconds
# Tomcat started on port(s): 9090 (http)
```

### Vérifier Swagger UI
```bash
curl -s -o /dev/null -w "%{http_code}" http://localhost:9090/swagger-ui.html
# Résultat attendu: 200
```

### Vérifier H2 Console
```bash
curl -s -o /dev/null -w "%{http_code}" http://localhost:9090/h2-console
# Résultat attendu: 200
```

## Tests API

### Enregistrement
```bash
curl -X POST http://localhost:9090/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }'
```

### Connexion
```bash
curl -X POST http://localhost:9090/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

### Créer un hôtel
```bash
# Remplacez TOKEN par le accessToken de la réponse précédente
curl -X POST http://localhost:9090/api/hotels \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Hotel",
    "addressLine1": "123 Main St",
    "city": "Paris",
    "country": "France",
    "postalCode": "75001",
    "timezone": "Europe/Paris",
    "phone": "+33123456789",
    "email": "hotel@test.com"
  }'
```

### Lister les hôtels (mes hôtels)
```bash
curl -X GET http://localhost:9090/api/hotels/owner/my-hotels \
  -H "Authorization: Bearer TOKEN"
```

### Créer un compte staff
```bash
curl -X POST http://localhost:9090/api/auth/create-staff \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "staff@test.com",
    "password": "staffpass123",
    "firstName": "John",
    "lastName": "Staff"
  }'
```

## Gestion des erreurs

### Port déjà en utilisation
```powershell
# Trouver le processus utilisant le port
netstat -ano | findstr :9090

# Tuer le processus (remplacez PID)
taskkill /PID <PID> /F

# Ou changer de port dans application.properties
# server.port=8888
```

### Problèmes de compilation
```powershell
# Nettoyer complètement
.\mvnw.cmd clean install

# Forcer la re-téléchargement des dépendances
.\mvnw.cmd -U clean compile
```

### Base de données vide/problème
```bash
# H2 Console:
# http://localhost:9090/h2-console
# Query:
SELECT * FROM app_users;
SELECT * FROM roles;
SELECT * FROM hotels;
```

## Configuration

### Changer le port
Modifiez `src/main/resources/application.properties`:
```properties
server.port=8888  # Changer de 9090 à 8888 (exemple)
```

### Changer le JWT secret
Modifiez `src/main/resources/application.properties`:
```properties
jwt.secret=VOTRE_CLÉ_SECRÈTE_FORTE_DE_32_CARACTÈRES_MINIMUM
jwt.expiration=86400000  # 24 heures en ms
```

### Changer la base de données
Pour utiliser PostgreSQL au lieu de H2:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/keezy
spring.datasource.username=postgres
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
```

## Debugging

### Activer les logs détaillés
Modifiez `src/main/resources/application.properties`:
```properties
logging.level.root=INFO
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.data=DEBUG
```

### Voir les requêtes SQL
```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

## IntelliJ IDEA

### Ouvrir le projet
```
File → Open → W:\Outside\Kezzy_New\keezy_back
```

### Configurer le JDK
```
File → Project Structure → Project
Project SDK: Java 17 (ou +)
```

### Lancer l'app
```
Shift + F10
ou
Run → Run 'KeezyBackApplication'
```

### Recharger Maven
```
Maven Tool → Reload All Maven Projects
ou
Right-click pom.xml → Maven → Reload Project
```

## Git (si utilisé)

```bash
# Ajouter les fichiers
git add .

# Commit
git commit -m "Fix: Keezy Backend finalisé et fonctionnel"

# Push
git push origin main
```

## FAQ Rapide

**Q: Où est Swagger UI?**  
R: http://localhost:9090/swagger-ui.html

**Q: Comment obtenir un JWT?**  
R: POST /api/auth/login avec email et password

**Q: Comment créer un hôtel?**  
R: POST /api/hotels avec Authorization header

**Q: Où est la base de données?**  
R: H2 Console à http://localhost:9090/h2-console

**Q: Quel est le port par défaut?**  
R: 9090 (modifiable dans application.properties)

**Q: Comment créer un staff?**  
R: POST /api/auth/create-staff (nécessite JWT de USER/ADMIN)

---

*Dernière mise à jour: 2026-02-07*

