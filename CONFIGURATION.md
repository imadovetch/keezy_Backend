# Configuration - Variables et Propriétés

## 📄 Fichier application.properties

Créé et configuré à : `src/main/resources/application.properties`

```properties
spring.application.name=keezy_back

# ===========================
# H2 Database Configuration
# ===========================
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2 Console Configuration
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# ===========================
# JPA Configuration
# ===========================
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# ===========================
# Swagger/OpenAPI Configuration
# ===========================
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.operations-sorter=method
springdoc.swagger-ui.tags-sorter=alpha

# ===========================
# JWT Configuration
# ===========================
jwt.secret=KeezySecretKeyForJWTTokenGenerationAndValidation12345678901234567890
jwt.expiration=86400000
```

## 🔐 Variables JWT

### jwt.secret
- **Description** : Clé secrète pour signer les tokens JWT
- **Valeur actuelle** : `KeezySecretKeyForJWTTokenGenerationAndValidation12345678901234567890`
- **Recommandation** : Minimum 32 caractères pour HS256
- **Production** : Utiliser une variable d'environnement

### jwt.expiration
- **Description** : Durée de vie du token en millisecondes
- **Valeur actuelle** : `86400000` (24 heures)
- **Formules** :
  - 1 heure = 3600000 ms
  - 24 heures = 86400000 ms
  - 7 jours = 604800000 ms

## 🗄️ Variables H2

### spring.datasource.url
- **Description** : URL de connexion à la base de données
- **Valeur** : `jdbc:h2:mem:testdb`
- **Mode** : En mémoire (réinitialisée à chaque redémarrage)
- **Alternative** : `jdbc:h2:file:./data/testdb` pour persistance

### spring.jpa.hibernate.ddl-auto
- **Description** : Comportement DDL à l'application
- **Valeur** : `create-drop` (crée et supprime à chaque démarrage)
- **Options** :
  - `create` : Crée les tables
  - `create-drop` : Crée et supprime au démarrage/arrêt
  - `update` : Modifie les tables existantes
  - `validate` : Valide sans modifications
  - `none` : Aucune action

## 🌐 Variables Swagger

### springdoc.swagger-ui.enabled
- **Description** : Active/désactive l'interface Swagger UI
- **Valeur** : `true`

### springdoc.swagger-ui.path
- **Description** : Chemin pour accéder à Swagger UI
- **Valeur** : `/swagger-ui.html`
- **URL complète** : `http://localhost:8080/swagger-ui.html`

### springdoc.api-docs.path
- **Description** : Chemin pour la documentation OpenAPI JSON
- **Valeur** : `/v3/api-docs`
- **URL complète** : `http://localhost:8080/v3/api-docs`

## 🔧 Configuration en fonction du profil

### Développement (par défaut)

```properties
# application.properties
spring.jpa.show-sql=false
spring.jpa.hibernate.ddl-auto=create-drop
jwt.expiration=86400000
```

### Production (optionnel)

Créer `application-prod.properties` :

```properties
# Base de données PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/keezy
spring.datasource.username=keezy_user
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate

# Désactiver H2 Console
spring.h2.console.enabled=false

# JWT - Variables d'environnement
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION:86400000}

# Swagger - Désactiver en production
springdoc.swagger-ui.enabled=false
```

Lancer avec :
```bash
.\mvnw.cmd spring-boot:run -Dspring.profiles.active=prod
```

## 📌 Variables d'environnement (Recommandé)

Pour ne pas exposer les secrets dans le code source :

```bash
# Windows PowerShell
$env:JWT_SECRET = "VotreCléSecrèteForte32Caractères"
$env:JWT_EXPIRATION = "86400000"
$env:DB_PASSWORD = "motdepasse"

# Linux/Mac
export JWT_SECRET="VotreCléSecrèteForte32Caractères"
export JWT_EXPIRATION="86400000"
export DB_PASSWORD="motdepasse"
```

### Accéder aux variables dans Spring

```java
@Value("${jwt.secret:${JWT_SECRET:default_value}}")
private String jwtSecret;
```

## 🔄 Propriétés dynamiques par environnement

### Développement
```properties
logging.level.root=INFO
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.security=DEBUG
```

### Test
```properties
logging.level.org.springframework=WARN
```

### Production
```properties
logging.level.root=WARN
logging.level.org.springframework.security=ERROR
```

## 🔗 Endpoints par défaut

| Endpoint | URL | Public |
|----------|-----|--------|
| Swagger UI | http://localhost:8080/swagger-ui.html | ✅ |
| API Docs | http://localhost:8080/v3/api-docs | ✅ |
| H2 Console | http://localhost:8080/h2-console | ✅ |
| Actuator | http://localhost:8080/actuator | ✅ |
| Auth | http://localhost:8080/api/auth/** | ✅ |
| Users | http://localhost:8080/api/users/** | ❌ |
| Hotels | http://localhost:8080/api/hotels/** | ❌ |

## 📋 Checklist de configuration

- [x] H2 Database configurée
- [x] JWT configuré (24h)
- [x] Spring Security configuré
- [x] Swagger/OpenAPI configuré
- [x] Rôles initialisés au démarrage
- [x] Password encoding (BCrypt)
- [x] CORS non configuré (à ajouter si nécessaire)
- [x] SSL/TLS non configuré (à ajouter pour production)

## 🚀 Configuration pour production

1. **Changer la base de données**
   ```properties
   spring.datasource.url=jdbc:postgresql://prod-db:5432/keezy
   spring.jpa.hibernate.ddl-auto=validate
   ```

2. **Secrets sécurisés**
   ```properties
   jwt.secret=${JWT_SECRET_ENV}
   spring.datasource.password=${DB_PASSWORD_ENV}
   ```

3. **Logs**
   ```properties
   logging.level.root=WARN
   logging.file.name=/var/log/keezy/app.log
   ```

4. **Performance**
   ```properties
   server.tomcat.threads.max=200
   server.tomcat.threads.min-spare=10
   ```

5. **HTTPS**
   ```properties
   server.ssl.key-store-type=PKCS12
   server.ssl.key-store=classpath:keystore.p12
   server.ssl.key-store-password=${SSL_PASSWORD}
   ```

## 🧪 Configuration pour tests

Créer `application-test.properties` :

```properties
# Base H2 dédiée aux tests
spring.datasource.url=jdbc:h2:mem:testdb;MODE=MYSQL
spring.jpa.hibernate.ddl-auto=create-drop

# Logs réduits
logging.level.root=WARN

# Désactiver Swagger en test
springdoc.swagger-ui.enabled=false
```

## 💾 Sauvegarde de la base de données

### Mode fichier (au lieu de mémoire)

```properties
# application.properties
spring.datasource.url=jdbc:h2:file:./data/keezy_db
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=update
```

La base sera sauvegardée dans le dossier `./data/`

### Sauvegarder les données

```sql
-- Exporter depuis H2 Console
CALL CSVWRITE('data_backup.csv', 'SELECT * FROM app_users');
```

## 📊 Réglages de performance

```properties
# Connection Pool
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5

# Lazy loading
spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true

# Query optimization
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
```

## 📞 Support

- **Questions JWT** : Voir `ARCHITECTURE.md`
- **Endpoints** : Voir `IMPLEMENTATION_GUIDE.md`
- **Démarrage** : Voir `QUICK_START.md`

