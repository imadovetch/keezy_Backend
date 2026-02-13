# Keezy Backend - Quick Start Guide

## 🚀 Lancer l'application rapidement

### Option 1 : Via IntelliJ IDEA (Recommandé)

1. **Ouvrir le projet**
   - Fichier → Ouvrir
   - Sélectionner le dossier `W:\Outside\Kezzy_New\keezy_back`

2. **Laisser Maven charger les dépendances**
   - IntelliJ téléchargera automatiquement les dépendances
   - Patientez jusqu'à ce que "Indexing..." disparaisse

3. **Lancer l'application**
   - Cliquer sur le bouton ▶️ (Run) en haut à droite
   - Ou utiliser le raccourci : **Shift + F10**
   - Ou aller à : Run → Run 'KeezyBackApplication'

4. **Vérifier que c'est démarré**
   - Chercher "Started KeezyBackApplication" dans la console
   - Vous devriez voir : "Tomcat started on port(s): 8080"

### Option 2 : Via Terminal/CMD

```bash
cd W:\Outside\Kezzy_New\keezy_back
.\mvnw.cmd spring-boot:run
```

## 📋 Premiers pas après le démarrage

### 1. Accéder à Swagger UI
```
http://localhost:8080/swagger-ui.html
```

### 2. Accéder à H2 Console (Base de données)
```
http://localhost:8080/h2-console
```

### 3. Tester avec un client REST (Postman, Insomnia, curl)

#### Exemple avec curl : S'enregistrer

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@test.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }'
```

## 🔑 Obtenir un JWT Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@test.com",
    "password": "password123"
  }'
```

Réponse :
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "user": {...}
}
```

Copiez le token pour les prochaines requêtes.

## 🏨 Créer un hôtel

```bash
curl -X POST http://localhost:8080/api/hotels \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "My Hotel",
    "addressLine1": "123 Main St",
    "city": "Paris",
    "country": "France",
    "postalCode": "75001",
    "timezone": "Europe/Paris",
    "phone": "+33123456789",
    "email": "hotel@example.com"
  }'
```

## 👥 Créer un compte staff

```bash
curl -X POST http://localhost:8080/api/auth/create-staff \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "staff@hotel.com",
    "password": "staffpass123",
    "firstName": "John",
    "lastName": "Staff"
  }'
```

## 🐛 Dépannage

### Application ne démarre pas ?

1. **Vérifier Java**
   ```bash
   java -version
   ```
   Doit être Java 17 ou plus.

2. **Vérifier si le port 8080 est libre**
   ```bash
   netstat -ano | findstr :8080
   ```

3. **Nettoyer et reconstruire**
   ```bash
   .\mvnw.cmd clean install
   ```

### Erreurs de compilation ?

```bash
.\mvnw.cmd clean compile
```

### Vérifier les logs

Regardez la fenêtre "Run" ou "Console" dans IntelliJ pour les messages d'erreur.

## 📝 Endoints principaux

| Méthode | URL | Description |
|---------|-----|-------------|
| POST | /api/auth/register | Créer un nouvel utilisateur |
| POST | /api/auth/login | Connexion (obtenir JWT) |
| POST | /api/auth/create-staff | Créer un compte staff |
| GET | /api/users | Lister tous les utilisateurs |
| POST | /api/hotels | Créer un hôtel |
| GET | /api/hotels | Lister tous les hôtels |
| GET | /api/hotels/owner/my-hotels | Mes hôtels |

## 📚 Documentation complète

Voir `IMPLEMENTATION_GUIDE.md` pour la documentation détaillée.

## ⚡ Raccourcis IntelliJ

| Raccourci | Action |
|-----------|--------|
| Shift + F10 | Lancer l'application |
| Shift + F9 | Déboguer l'application |
| Ctrl + Shift + F10 | Relancer l'application |
| Alt + F5 | Redémarrer le serveur |

## 💡 Tips

- **Swagger UI** est très utile pour tester les endpoints
- **H2 Console** permet de voir la base de données en temps réel
- Les tokens JWT expirent après **24 heures**
- Utilisez `Ctrl + Alt + L` dans IntelliJ pour formater le code

