# Keezy Backend - Guide de Démarrage

## Vue d'ensemble
Keezy Backend est une application Spring Boot pour gérer les hôtels, les utilisateurs et les authentifications JWT.

## Architecture

### Entités
- **AppUser** : Utilisateur de l'application (Admin, User, Staff)
- **Role** : Rôles d'utilisateur (ADMIN, USER, STAFF)
- **Hotel** : Hôtels créés par les utilisateurs

### Rôles et permissions

| Rôle | Description | Permissions |
|------|-------------|-------------|
| **ADMIN** | Administrateur | Gérer tous les utilisateurs, supprimer les hôtels d'autres utilisateurs |
| **USER** | Utilisateur normal | Créer des hôtels, créer des comptes staff |
| **STAFF** | Membres du personnel | Créé par les utilisateurs normaux |

## Démarrage de l'application

### Via Maven
```bash
cd W:\Outside\Kezzy_New\keezy_back
.\mvnw.cmd spring-boot:run
```

### Via IDE (IntelliJ IDEA)
1. Ouvrir le projet dans IntelliJ IDEA
2. Cliquer sur le bouton "Run" ▶️ en haut à droite
3. Ou utiliser : **Shift + F10**

### Configuration requise
- Java 17+
- Maven 3.6+

## Accès aux services

Une fois l'application démarrée sur `http://localhost:8080` :

### Swagger UI (Documentation API)
- **URL**: http://localhost:8080/swagger-ui.html
- **API Docs JSON**: http://localhost:8080/v3/api-docs

### H2 Console (Base de données)
- **URL**: http://localhost:8080/h2-console
- **Username**: sa
- **Password**: (laissez vide)
- **JDBC URL**: jdbc:h2:mem:testdb

## Endpoints disponibles

### Authentification (`/api/auth`)

#### 1. **Enregistrement d'un nouvel utilisateur**
```
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+33612345678"
}

Réponse (201):
{
  "id": "uuid",
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "roleName": "USER"
}
```

#### 2. **Connexion et obtention du JWT**
```
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}

Réponse (200):
{
  "accessToken": "eyJhbGc...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "user": {
    "id": "uuid",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "roleName": "USER"
  }
}
```

#### 3. **Créer un compte staff**
```
POST /api/auth/create-staff
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "email": "staff@hotel.com",
  "password": "staffpass123",
  "firstName": "Jane",
  "lastName": "Smith",
  "phone": "+33698765432"
}

Réponse (201):
{
  "id": "uuid",
  "email": "staff@hotel.com",
  "firstName": "Jane",
  "lastName": "Smith",
  "roleName": "STAFF"
}
```

### Gestion des utilisateurs (`/api/users`)

#### 1. **Obtenir les informations de l'utilisateur**
```
GET /api/users/{userId}
Authorization: Bearer {accessToken}
```

#### 2. **Obtenir un utilisateur par email**
```
GET /api/users/email/{email}
Authorization: Bearer {accessToken}
```

#### 3. **Lister tous les utilisateurs** (Admin only)
```
GET /api/users
Authorization: Bearer {accessToken}
```

#### 4. **Lister les utilisateurs par rôle**
```
GET /api/users/role/{roleName}
Authorization: Bearer {accessToken}

Exemple: /api/users/role/STAFF
```

#### 5. **Mettre à jour un utilisateur**
```
PUT /api/users/{userId}
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+33612345678"
}
```

#### 6. **Désactiver un utilisateur** (Admin only)
```
PATCH /api/users/{userId}/disable
Authorization: Bearer {accessToken}
```

#### 7. **Activer un utilisateur** (Admin only)
```
PATCH /api/users/{userId}/enable
Authorization: Bearer {accessToken}
```

#### 8. **Supprimer un utilisateur** (Admin only)
```
DELETE /api/users/{userId}
Authorization: Bearer {accessToken}
```

### Gestion des hôtels (`/api/hotels`)

#### 1. **Créer un hôtel avec les informations complètes**
```
POST /api/hotels
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "name": "Hotel Paradise",
  "addressLine1": "123 Main Street",
  "city": "Paris",
  "country": "France",
  "postalCode": "75001",
  "timezone": "Europe/Paris",
  "phone": "+33123456789",
  "email": "hotel@paradise.com",
  "operaPropertyId": "PARIS001" (optionnel)
}

Réponse (201):
{
  "uuid": "uuid",
  "operaPropertyId": "PARIS001",
  "name": "Hotel Paradise",
  "addressLine1": "123 Main Street",
  "city": "Paris",
  "country": "France",
  "postalCode": "75001",
  "timezone": "Europe/Paris",
  "phone": "+33123456789",
  "email": "hotel@paradise.com",
  "ownerId": "userId",
  "ownerEmail": "user@example.com",
  "createdAt": 1707257064000
}
```

#### 2. **Créer un hôtel à partir d'un Opera Property ID**
```
POST /api/hotels/from-opera/{operaPropertyId}
Authorization: Bearer {accessToken}

Exemple: POST /api/hotels/from-opera/PARIS001
```

#### 3. **Obtenir les détails d'un hôtel**
```
GET /api/hotels/{hotelId}
Authorization: Bearer {accessToken}
```

#### 4. **Lister tous les hôtels** (Admin only)
```
GET /api/hotels
Authorization: Bearer {accessToken}
```

#### 5. **Obtenir mes hôtels**
```
GET /api/hotels/owner/my-hotels
Authorization: Bearer {accessToken}
```

#### 6. **Mettre à jour un hôtel**
```
PUT /api/hotels/{hotelId}
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "name": "Hotel Paradise Updated",
  "addressLine1": "456 New Street",
  "city": "Paris",
  "country": "France",
  "postalCode": "75002",
  "timezone": "Europe/Paris",
  "phone": "+33987654321",
  "email": "newemail@paradise.com"
}
```

#### 7. **Supprimer un hôtel**
```
DELETE /api/hotels/{hotelId}
Authorization: Bearer {accessToken}
```

## Authentification JWT

### Utilisation du token

Tous les endpoints protégés nécessitent un header d'authentification :

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Expiration du token
- **Durée de vie**: 24 heures (configurable via `jwt.expiration` en ms)
- Après expiration, l'utilisateur doit se reconnecter

## Configuration

### Fichier `application.properties`

```properties
# H2 Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=

# JWT
jwt.secret=KeezySecretKeyForJWTTokenGenerationAndValidation12345678901234567890
jwt.expiration=86400000

# Swagger
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.path=/swagger-ui.html
```

## Exemple de workflow complet

### 1. Créer un nouvel utilisateur (USER)
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "hotel_owner@example.com",
    "password": "password123",
    "firstName": "Pierre",
    "lastName": "Dupont",
    "phone": "+33612345678"
  }'
```

### 2. Se connecter
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "hotel_owner@example.com",
    "password": "password123"
  }'
```

Récupérez le token `accessToken` de la réponse.

### 3. Créer un hôtel
```bash
curl -X POST http://localhost:8080/api/hotels \
  -H "Authorization: Bearer {accessToken}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Hotel Eiffel Tower",
    "addressLine1": "5 Avenue Anatole France",
    "city": "Paris",
    "country": "France",
    "postalCode": "75007",
    "timezone": "Europe/Paris",
    "phone": "+33123456789",
    "email": "eiffel@hotel.com"
  }'
```

### 4. Créer un compte staff
```bash
curl -X POST http://localhost:8080/api/auth/create-staff \
  -H "Authorization: Bearer {accessToken}" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "staff@eiffel.com",
    "password": "staffpass123",
    "firstName": "Marie",
    "lastName": "Martin",
    "phone": "+33698765432"
  }'
```

### 5. Le staff peut maintenant se connecter avec ses identifiants
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "staff@eiffel.com",
    "password": "staffpass123"
  }'
```

## Traçage des problèmes

### Build échoue
```bash
# Nettoyer et recompiler
.\mvnw.cmd clean compile
```

### Application ne démarre pas
1. Vérifier que le port 8080 est disponible
2. Vérifier les logs dans la console
3. Vérifier que Java 17+ est installé : `java -version`

### JWT invalide
- Vérifier que le token n'a pas expiré
- Vérifier que le header Authorization est correct : `Bearer {token}`

## Structure du projet

```
src/main/java/org/bloomberg/keezy_back/
├── config/                    # Configuration Spring
│   ├── SecurityConfig.java
│   ├── ModelMapperConfig.java
│   └── SwaggerConfig.java
├── Controller/                # API Endpoints
│   ├── AuthenticationController.java
│   ├── UserController.java
│   └── HotelController.java
├── Service/                   # Logique métier
│   ├── AuthenticationService.java
│   ├── UserService.java
│   ├── HotelService.java
│   ├── JwtTokenProvider.java
│   └── DataInitializationService.java
├── Security/                  # Sécurité
│   ├── JwtAuthenticationFilter.java
│   └── CustomUserDetailsService.java
├── Entity/                    # Modèles de données
│   ├── AppUser.java
│   ├── Role.java
│   └── Hotel.java
├── DTO/                       # Data Transfer Objects
│   ├── RegisterDTO.java
│   ├── LoginDTO.java
│   ├── AuthResponseDTO.java
│   ├── UserDTO.java
│   ├── HotelDTO.java
│   └── ErrorResponseDTO.java
├── Repositery/               # JPA Repositories
│   ├── AppUserRepository.java
│   ├── RoleRepository.java
│   └── HotelRepository.java
├── Mapper/                    # Mappers
│   ├── UserMapper.java
│   └── HotelMapper.java
├── Exception/                 # Exception Handlers
│   └── GlobalExceptionHandler.java
└── KeezyBackApplication.java  # Application principale
```

## Technologies utilisées

- **Spring Boot 4.0.2**
- **Spring Security**
- **Spring Data JPA**
- **JWT (JJWT 0.11.5)**
- **H2 Database**
- **ModelMapper**
- **Lombok**
- **Swagger/OpenAPI 2.1.0**
- **Validation (Hibernate Validator)**

## Licence

Ce projet est un système de gestion d'hôtels sécurisé avec authentification JWT.

