# Architecture et Configuration - Détails Techniques

## 🏗️ Architecture générale

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT (Postman, React, etc.)             │
└────────────────────────────────┬────────────────────────────────┘
                                  │
                                  ▼
                    ┌──────────────────────────────┐
                    │    REST API Controllers      │
                    │  (/api/auth, /api/users...)  │
                    └──────────────────────────────┘
                                  │
                    ┌─────────────┼─────────────┐
                    │             │             │
                    ▼             ▼             ▼
        ┌──────────────────┐  ┌──────────────┐  ┌──────────────┐
        │  Auth Service    │  │ User Service │  │Hotel Service │
        │ (JWT, Login)     │  │ (CRUD Users) │  │ (CRUD Hotels)│
        └──────────────────┘  └──────────────┘  └──────────────┘
                    │             │             │
                    └─────────────┼─────────────┘
                                  │
                    ┌─────────────▼──────────────┐
                    │   JPA Repositories        │
                    │ (UserRepo, HotelRepo...)   │
                    └────────────┬────────────────┘
                                  │
                    ┌─────────────▼──────────────┐
                    │   H2 In-Memory Database    │
                    │  (Tables: app_users,      │
                    │   hotels, roles)          │
                    └────────────────────────────┘
```

## 🔒 Flux d'authentification JWT

```
1. User sends credentials (email + password)
   │
   ▼
2. AuthenticationManager validates credentials
   │
   ▼
3. If valid: JwtTokenProvider generates JWT token
   │
   ▼
4. Return: {accessToken, expiresIn, user}
   │
   ▼
5. Client includes token in Authorization header
   │
   ▼
6. JwtAuthenticationFilter intercepts request
   │
   ▼
7. Filter validates token signature and expiration
   │
   ▼
8. If valid: Set authentication in SecurityContext
   │
   ▼
9. Request proceeds to protected endpoint
```

## 📊 Modèle de données

### Table: `roles`
```sql
CREATE TABLE roles (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT
);

-- Données initiales:
-- ADMIN   | "Administrator - Can manage all users and hotels"
-- USER    | "Normal User - Can create hotels and staff accounts"
-- STAFF   | "Staff Member - Created by users"
```

### Table: `app_users`
```sql
CREATE TABLE app_users (
    id VARCHAR(36) PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    phone VARCHAR(20),
    role_id VARCHAR(36) NOT NULL,
    enabled BOOLEAN DEFAULT true,
    created_by VARCHAR(255),
    created_at BIGINT,
    updated_by VARCHAR(255),
    updated_at BIGINT,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Index for faster lookups
CREATE INDEX idx_email ON app_users(email);
CREATE INDEX idx_role ON app_users(role_id);
```

### Table: `hotels`
```sql
CREATE TABLE hotels (
    uuid VARCHAR(36) PRIMARY KEY,
    opera_property_id VARCHAR(255) UNIQUE,
    name VARCHAR(255) NOT NULL,
    owner_id VARCHAR(36) NOT NULL,
    address_line1 VARCHAR(255),
    city VARCHAR(255),
    country VARCHAR(255),
    postal_code VARCHAR(20),
    timezone VARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(255) UNIQUE NOT NULL,
    created_by VARCHAR(255),
    created_at BIGINT,
    updated_by VARCHAR(255),
    updated_at BIGINT,
    FOREIGN KEY (owner_id) REFERENCES app_users(id)
);

-- Index for faster lookups
CREATE INDEX idx_owner ON hotels(owner_id);
CREATE INDEX idx_opera_id ON hotels(opera_property_id);
CREATE INDEX idx_email ON hotels(email);
```

## 🔐 Flux de sécurité Spring

```
HTTP Request
    │
    ▼
SecurityFilterChain
    │
    ├─ CSRF Filter (disabled)
    │
    ├─ JwtAuthenticationFilter (custom)
    │   ├─ Extract JWT from Authorization header
    │   ├─ Validate JWT signature
    │   ├─ Validate JWT expiration
    │   └─ Load user details
    │
    ├─ Authorization Filter
    │   └─ Check if user has required role
    │
    ▼
Controller
```

## 🛠️ Configuration JWT

```properties
# Secret key pour signer les tokens (au moins 32 caractères)
jwt.secret=KeezySecretKeyForJWTTokenGenerationAndValidation12345678901234567890

# Durée de vie du token (en millisecondes)
# 86400000 = 24 heures
jwt.expiration=86400000
```

### Structure du JWT Token

```
Header:
{
  "alg": "HS256",
  "typ": "JWT"
}

Payload:
{
  "sub": "user@example.com",
  "iat": 1707257064,
  "exp": 1707343464
}

Signature:
HMAC_SHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret_key
)
```

## 📋 Rôles et permissions détaillées

### ADMIN
- ✅ Lire tous les utilisateurs
- ✅ Supprimer n'importe quel utilisateur
- ✅ Activer/Désactiver les utilisateurs
- ✅ Voir tous les hôtels
- ✅ Supprimer n'importe quel hôtel

### USER
- ✅ Lire son propre profil
- ✅ Mettre à jour son profil
- ✅ Créer des hôtels
- ✅ Voir ses propres hôtels
- ✅ Mettre à jour ses propres hôtels
- ✅ Supprimer ses propres hôtels
- ✅ Créer des comptes staff
- ❌ Modifier les utilisateurs d'autres personnes
- ❌ Voir les hôtels d'autres personnes

### STAFF
- ✅ Lire son propre profil
- ❌ Ne peut pas créer d'hôtels
- ❌ Ne peut pas créer d'autres comptes staff

## 🔄 Flux de création d'hôtel

```
1. USER calls POST /api/hotels with HotelDTO
   │
   ▼
2. Controller extracts userId from JWT token
   │
   ▼
3. HotelService.createHotel(hotelDTO, userId)
   │
   ├─ Validate HotelDTO (via @Valid)
   │
   ├─ Check if hotel email exists
   │
   ├─ Check if Opera Property ID exists (if provided)
   │
   ├─ Load owner from database
   │
   ├─ Create Hotel entity with:
   │  ├─ owner = loaded user
   │  ├─ createdBy = user.email
   │  ├─ createdAt = System.currentTimeMillis()
   │
   ├─ Save to HotelRepository
   │
   ▼
4. HotelMapper.toDTO(savedHotel) converts to HotelDTO
   │
   ▼
5. Return 201 CREATED with HotelDTO
```

## 🔄 Flux de création de staff

```
1. USER calls POST /api/auth/create-staff with CreateStaffDTO
   │
   ▼
2. AuthenticationService.createStaff(createStaffDTO, userId)
   │
   ├─ Load current user from database
   │
   ├─ Check if user is ADMIN or USER
   │
   ├─ Check if staff email already exists
   │
   ├─ Hash password using BCryptPasswordEncoder
   │
   ├─ Load STAFF role from database
   │
   ├─ Create AppUser entity with:
   │  ├─ role = STAFF
   │  ├─ createdBy = currentUser.email
   │  ├─ createdAt = System.currentTimeMillis()
   │
   ├─ Save to AppUserRepository
   │
   ▼
3. UserMapper.toDTO(savedUser) converts to UserDTO
   │
   ▼
4. Return 201 CREATED with UserDTO
```

## 🧪 Validation des données

### DTOs avec validation

```java
@Valid
RegisterDTO {
  @Email
  @NotBlank
  email: String,
  
  @NotBlank
  @Size(min = 6)
  password: String,
  
  @NotBlank
  firstName: String,
  
  @NotBlank
  lastName: String
}
```

Erreurs de validation retournent 400 Bad Request avec détails.

## 📊 Performance et optimisations

### Lazy Loading
- Hotel → AppUser: EAGER (pour les informations du propriétaire)
- AppUser → Role: EAGER (pour les permissions)

### Indexes
- `app_users.email` : Recherche rapide par email
- `hotels.owner_id` : Recherche des hôtels d'un propriétaire
- `hotels.opera_property_id` : Recherche par Opera ID

### Caching (non implémenté actuellement)
Peut être ajouté pour :
- Utilisateurs fréquemment accédés
- Rôles (3 seulement)
- Hôtels populaires

## 🔐 Bonnes pratiques de sécurité

1. **Mot de passe**
   - Hashage BCrypt (salt + itérations)
   - Jamais stocké en clair
   - Validation minimale 6 caractères

2. **JWT Token**
   - Signature HMAC-SHA256
   - Clé secrète forte (32+ caractères)
   - Expiration 24 heures
   - Vérifié à chaque requête

3. **Authentification**
   - Credentials validés au login
   - Token inclus dans Authorization header
   - Header format: "Bearer {token}"

4. **Autorisation**
   - Vérification du rôle pour chaque endpoint
   - Vérification de propriété pour les ressources

5. **CSRF**
   - Désactivé (API REST avec JWT)
   - Non nécessaire avec tokens

## 🚀 Déploiement (Futur)

Pour la production :

1. **Base de données**
   - Remplacer H2 par PostgreSQL/MySQL
   - Configuration dans `application-prod.properties`

2. **JWT Secret**
   - Stocker en variables d'environnement
   - Ne pas committer en hardcoded

3. **CORS**
   - Configurer les domaines autorisés
   - Actuellement permissive (à améliorer)

4. **HTTPS**
   - Forcer HTTPS en production
   - Configurer certificats SSL/TLS

5. **Monitoring**
   - Logs centralisés
   - Alertes sur erreurs
   - Métriques Spring Actuator

## 📞 Support et debugging

### Enable detailed logging

Dans `application.properties` :
```properties
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.data=DEBUG
```

### H2 Console SQL

```sql
-- Voir tous les utilisateurs
SELECT * FROM app_users;

-- Voir tous les hôtels
SELECT * FROM hotels;

-- Voir tous les rôles
SELECT * FROM roles;

-- Hôtels d'un utilisateur spécifique
SELECT * FROM hotels WHERE owner_id = 'user-id';
```

