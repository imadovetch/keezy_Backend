# 🎯 Résumé visuel - Keezy Backend

## 📦 Ce qui a été livré

```
W:\Outside\Kezzy_New\keezy_back/
│
├── 📄 QUICK_START.md                    ⭐ À LIRE EN PREMIER
├── 📄 RESUME_IMPLEMENTATION.md          ✅ Résumé de ce qui a été fait
├── 📄 IMPLEMENTATION_GUIDE.md           📚 Documentation complète des endpoints
├── 📄 ARCHITECTURE.md                   🏗️ Détails techniques
├── 📄 CONFIGURATION.md                  ⚙️ Variables et configuration
├── 📄 Keezy_API.postman_collection.json 🧪 Collection pour tester
│
├── src/main/java/org/bloomberg/keezy_back/
│   ├── Controller/                      🎮 3 contrôleurs REST
│   │   ├── AuthenticationController.java
│   │   ├── UserController.java
│   │   └── HotelController.java
│   │
│   ├── Service/                         ⚙️ Logique métier
│   │   ├── AuthenticationService.java
│   │   ├── UserService.java
│   │   ├── HotelService.java
│   │   ├── JwtTokenProvider.java
│   │   └── DataInitializationService.java
│   │
│   ├── Entity/                          📊 3 entités
│   │   ├── AppUser.java
│   │   ├── Role.java
│   │   └── Hotel.java
│   │
│   ├── DTO/                             📨 7 DTOs
│   │   ├── RegisterDTO.java
│   │   ├── LoginDTO.java
│   │   ├── AuthResponseDTO.java
│   │   ├── UserDTO.java
│   │   ├── HotelDTO.java
│   │   ├── CreateStaffDTO.java
│   │   └── ErrorResponseDTO.java
│   │
│   ├── Repositery/                      💾 3 repositories
│   │   ├── AppUserRepository.java
│   │   ├── RoleRepository.java
│   │   └── HotelRepository.java
│   │
│   ├── Security/                        🔐 Sécurité JWT
│   │   ├── JwtAuthenticationFilter.java
│   │   └── CustomUserDetailsService.java
│   │
│   ├── Mapper/                          🔄 Conversion DTO/Entity
│   │   ├── UserMapper.java
│   │   └── HotelMapper.java
│   │
│   ├── Exception/                       ⚠️ Gestion d'erreurs
│   │   └── GlobalExceptionHandler.java
│   │
│   ├── config/                          ⚙️ Configuration
│   │   ├── SecurityConfig.java
│   │   ├── ModelMapperConfig.java
│   │   └── SwaggerConfig.java
│   │
│   └── KeezyBackApplication.java        🚀 Application principale
│
├── pom.xml                              📦 Dépendances Maven
└── src/main/resources/
    └── application.properties            ⚙️ Configuration
```

## 🔄 Flux complet utilisateur

```
┌─────────────────────────────────────────────────────────────────┐
│ 1. UTILISATEUR NOUVEAU                                          │
│    POST /api/auth/register                                      │
│    ├─ Crée automatiquement avec rôle USER                       │
│    ├─ Password hashé en BCrypt                                  │
│    └─ Email unique                                              │
└─────────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│ 2. CONNEXION                                                    │
│    POST /api/auth/login                                         │
│    ├─ Valide email + password                                   │
│    ├─ Génère JWT Token (24 heures)                              │
│    └─ Retourne: accessToken + user info                         │
└─────────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│ 3. CRÉER HÔTEL                                                  │
│    POST /api/hotels (Authorization: Bearer {token})             │
│    ├─ Remplis informations hôtel                                │
│    ├─ Hôtel associé au propriétaire                             │
│    └─ Peut être créé par USER ou ADMIN                          │
└─────────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│ 4. CRÉER COMPTES STAFF                                          │
│    POST /api/auth/create-staff (Authorization: Bearer {token})  │
│    ├─ USER/ADMIN crée un compte STAFF                           │
│    ├─ Partage email et password au personnel                    │
│    └─ Staff peut se connecter séparément                        │
└─────────────────────────────────────────────────────────────────┘
```

## 🎯 Cas d'utilisation

### USER - Propriétaire d'hôtel
```
Enregistrement
    ├─ Register: email, password, firstName, lastName, phone
    └─ Rôle: USER

Connexion
    ├─ Login: email, password
    └─ JWT Token reçu

Gestion hôtel
    ├─ Créer hôtel: infos complètes ou Opera ID
    ├─ Voir mes hôtels: GET /api/hotels/owner/my-hotels
    ├─ Modifier mon hôtel
    └─ Supprimer mon hôtel

Gestion personnel
    ├─ Créer compte staff: email, password
    ├─ Partager identifiants avec staff
    └─ Le staff peut se connecter
```

### STAFF - Membre du personnel
```
Connexion
    ├─ Login: email (créé par USER), password (créé par USER)
    └─ JWT Token reçu

Limitations
    ├─ ❌ Pas de création d'hôtel
    ├─ ❌ Pas de création de staff
    └─ ✅ Peut uniquement voir son profil
```

### ADMIN - Administrateur
```
Permissions complètes
    ├─ Voir tous les utilisateurs
    ├─ Supprimer n'importe quel utilisateur
    ├─ Activer/Désactiver les utilisateurs
    ├─ Voir tous les hôtels
    └─ Supprimer n'importe quel hôtel
```

## 📊 Endpoints groupés

### 🔓 Publics (pas de JWT requis)
```
POST   /api/auth/register          Enregistrement
POST   /api/auth/login             Connexion
GET    /swagger-ui.html            Documentation
GET    /h2-console/**              Base de données
GET    /v3/api-docs                OpenAPI JSON
```

### 🔒 Protégés (JWT requis)
```
POST   /api/auth/create-staff      Créer staff (USER/ADMIN)

GET    /api/users/{id}             Lire utilisateur
GET    /api/users                  Tous (ADMIN)
GET    /api/users/role/{role}      Par rôle (ADMIN)
PUT    /api/users/{id}             Mettre à jour
PATCH  /api/users/{id}/disable     Désactiver (ADMIN)
PATCH  /api/users/{id}/enable      Activer (ADMIN)
DELETE /api/users/{id}             Supprimer (ADMIN)

POST   /api/hotels                 Créer hôtel
POST   /api/hotels/from-opera/{id} Créer à partir Opera
GET    /api/hotels/{id}            Détails hôtel
GET    /api/hotels                 Tous (ADMIN)
GET    /api/hotels/owner/my-hotels Mes hôtels
PUT    /api/hotels/{id}            Mettre à jour
DELETE /api/hotels/{id}            Supprimer
```

## 🔐 Sécurité en 3 couches

```
┌──────────────────────────────────────────┐
│ 1. AUTHENTIFICATION (JWT)                │
│    - Email + Password → Token            │
│    - Token inclus dans Authorization     │
│    - Validé à chaque requête             │
└──────────────────────────────────────────┘
                    ↓
┌──────────────────────────────────────────┐
│ 2. AUTORISATION (Rôles)                  │
│    - ADMIN : Tous les droits             │
│    - USER : Ses propres ressources       │
│    - STAFF : Lecture seule               │
└──────────────────────────────────────────┘
                    ↓
┌──────────────────────────────────────────┐
│ 3. PASSWORD (BCrypt)                     │
│    - Salt aléatoire                      │
│    - 10 iterations                       │
│    - Jamais en clair                     │
└──────────────────────────────────────────┘
```

## 📈 Base de données

### Tables créées automatiquement

```
┌─────────────────┐
│ app_users       │
├─────────────────┤
│ id (UUID)       │
│ email*          │
│ password        │
│ firstName       │
│ lastName        │
│ phone           │
│ role_id (FK)    │
│ enabled         │
│ created_at      │
│ updated_at      │
└─────────────────┘

┌─────────────────┐
│ roles           │
├─────────────────┤
│ id (UUID)       │
│ name*           │
│ description     │
└─────────────────┘

┌──────────────────┐
│ hotels          │
├──────────────────┤
│ uuid (UUID)     │
│ name            │
│ email*          │
│ addressLine1    │
│ city            │
│ country         │
│ postalCode      │
│ timezone        │
│ phone           │
│ owner_id (FK)   │
│ operaPropertyId │
│ created_at      │
│ updated_at      │
└──────────────────┘
```

## 🌟 Features principales

| Feature | Status | Details |
|---------|--------|---------|
| Authentification JWT | ✅ | 24h, HMAC-SHA256 |
| Enregistrement | ✅ | Email unique, password BCrypt |
| Connexion | ✅ | Retourne JWT token |
| Rôles (3) | ✅ | ADMIN, USER, STAFF |
| CRUD Utilisateurs | ✅ | Complet avec permissions |
| CRUD Hôtels | ✅ | Complet avec permissions |
| Création Staff | ✅ | Par USER/ADMIN |
| Opera Property ID | ✅ | Optionnel, création rapide |
| Validation | ✅ | DTOs validés |
| Audit | ✅ | createdBy, updatedBy, timestamps |
| Swagger UI | ✅ | Documentation interactive |
| H2 Console | ✅ | Base de données visuelle |
| Gestion erreurs | ✅ | Réponses structurées |
| Mappers | ✅ | DTO ↔ Entity automatique |

## 🚀 Démarrage en 3 étapes

```
1️⃣ Ouvrir le projet
   └─ File → Open → W:\Outside\Kezzy_New\keezy_back

2️⃣ Attendre Maven
   └─ "Indexing..." doit disparaître

3️⃣ Lancer l'application
   └─ Shift + F10 (ou bouton Run ▶️)
```

## 🎮 Teste immédiatement

### Swagger UI
```
http://localhost:8080/swagger-ui.html
└─ Interface visuelle pour tester tous les endpoints
```

### Postman
```
Importer: Keezy_API.postman_collection.json
└─ Collection prête à l'emploi
```

### curl
```bash
# 1. Enregistrement
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123",...}'

# 2. Connexion
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

# 3. Créer hôtel (remplacer TOKEN)
curl -X POST http://localhost:8080/api/hotels \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{...}'
```

## ❓ FAQs

**Q: Où sont les données stockées ?**
R: En mémoire H2 (reinitalisée au redémarrage). Voir `CONFIGURATION.md` pour persistance.

**Q: Token expiré, comment faire ?**
R: Se reconnecter via `/api/auth/login` pour nouveau token.

**Q: Comment créer un admin ?**
R: Enregistrer normalement (rôle USER), puis modifier la base via H2 Console.

**Q: Peut-on changer le port 8080 ?**
R: Oui, dans `application.properties`: `server.port=8081`

**Q: Swagger c'est quoi ?**
R: Documentation visuelle interactive pour tester l'API.

## 📚 Documentation

| Document | Pour |
|----------|------|
| **QUICK_START.md** | Commencer immédiatement ⭐ |
| **IMPLEMENTATION_GUIDE.md** | Tous les endpoints détaillés |
| **ARCHITECTURE.md** | Comprendre le design |
| **CONFIGURATION.md** | Variables et propriétés |
| **RESUME_IMPLEMENTATION.md** | Vue d'ensemble |

---

## ✨ Vous êtes prêt !

L'application est **entièrement fonctionnelle** et peut être lancée maintenant.

👉 **Commencez par lire QUICK_START.md**

Bon développement ! 🎉

