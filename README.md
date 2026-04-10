# IA-Technology — Plateforme de Gestion des Travaux Scientifiques

## 📋 Table des Matières
1. [Vue d'ensemble](#vue-densemble)
2. [Architecture](#architecture)
3. [Installation & Démarrage](#installation--démarrage)
4. [Fonctionnalités](#fonctionnalités)
5. [Guide d'utilisation](#guide-dutilisation)
6. [API REST](#api-rest)
7. [Module IA](#module-ia)
8. [Structure du projet](#structure-du-projet)
9. [Sécurité](#sécurité)
10. [Tests](#tests)

---

## 🎯 Vue d'ensemble

**IA-Technology** est une plateforme web complète pour la gestion et la diffusion des travaux scientifiques. Elle centralise les profils de chercheurs, leurs publications, les domaines de recherche et les actualités, avec un module IA intégré pour l'extraction de mots-clés et les recommandations intelligentes.

### Stack Technique
- **Backend** : Spring Boot 3.2.3 + Java 17 + PostgreSQL
- **Frontend** : Angular 19 + PrimeNG 19 (Lara theme)
- **AI Service** : Python FastAPI + scikit-learn
- **Authentification** : JWT (jjwt 0.12.3)
- **Base de données** : PostgreSQL 12+

### Rôles & Permissions
- **ADMIN** : Gestion complète (chercheurs, domaines, publications, utilisateurs)
- **MODERATEUR** : Gestion des actualités et mise en avant
- **UTILISATEUR** : Consultation, recherche, téléchargement

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Angular Frontend (4200)                   │
│  ┌──────────────┬──────────────┬──────────────┬────────────┐ │
│  │ Public Home  │ Auth Module  │ Admin Space  │ User Space │ │
│  │ (Hero, News) │ (Login/Reg)  │ (Dashboard)  │ (Search)   │ │
│  └──────────────┴──────────────┴──────────────┴────────────┘ │
└────────────────────────┬─────────────────────────────────────┘
                         │ JWT Bearer Token
                         ▼
┌─────────────────────────────────────────────────────────────┐
│              Spring Boot Backend (8080)                      │
│  ┌──────────────┬──────────────┬──────────────┬────────────┐ │
│  │ Auth API     │ CRUD APIs    │ Search API   │ AI API     │ │
│  │ (JWT)        │ (Researchers)│ (Global)     │ (Keywords) │ │
│  │              │ (Domains)    │              │ (Recommend)│ │
│  │              │ (Publications)               │            │ │
│  │              │ (News, Users)                │            │ │
│  └──────────────┴──────────────┴──────────────┴────────────┘ │
└────────────────────────┬─────────────────────────────────────┘
                         │ SQL
                         ▼
┌─────────────────────────────────────────────────────────────┐
│              PostgreSQL Database                             │
│  ┌──────────────┬──────────────┬──────────────┬────────────┐ │
│  │ users        │ researchers  │ publications │ news       │ │
│  │ domains      │ audit_logs   │              │            │ │
│  └──────────────┴──────────────┴──────────────┴────────────┘ │
└─────────────────────────────────────────────────────────────┘

                         │ HTTP
                         ▼
┌─────────────────────────────────────────────────────────────┐
│           FastAPI AI Microservice (8001)                     │
│  ┌──────────────┬──────────────────────────────────────────┐ │
│  │ /keywords    │ /recommend                               │ │
│  │ (TF-IDF)     │ (Cosine Similarity)                      │ │
│  └──────────────┴──────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

---

## 🚀 Installation & Démarrage

### Prérequis
- Java 17+
- Node.js 18+
- Python 3.9+
- PostgreSQL 12+
- Git

### 1. Cloner le repository
```bash
git clone https://github.com/dhiaselmi1/ia-technology-platform.git
cd ia-technology-platform
```

### 2. Configurer PostgreSQL
```sql
CREATE DATABASE ia_technology_db;
```

### 3. Configurer le Backend
```bash
cd backend

# Éditer application.properties
# spring.datasource.password=YOUR_PASSWORD

# Compiler
mvn clean compile

# Démarrer
mvn spring-boot:run
```
✅ Backend disponible sur `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

### 4. Configurer le Microservice IA
```bash
cd ai-service

# Installer les dépendances
pip install -r requirements.txt

# Démarrer
uvicorn main:app --reload --port 8001
```
✅ IA Service disponible sur `http://localhost:8001`

### 5. Configurer le Frontend
```bash
cd ia-technology-frontend

# Installer les dépendances
npm install

# Démarrer
ng serve
```
✅ Frontend disponible sur `http://localhost:4200`

---

## ✨ Fonctionnalités

### 🔐 Authentification & Sécurité
- **Inscription** : Création de compte avec validation email
- **Connexion** : JWT stateless, token valide 24h
- **Rôles** : ADMIN, MODERATEUR, UTILISATEUR
- **Protection** : Guards Angular + @PreAuthorize Spring
- **Audit** : Logging de toutes les actions (CREATE, UPDATE, DELETE)

### 👥 Gestion des Chercheurs (Admin)
- ✅ Créer/Modifier/Supprimer chercheurs
- ✅ Associer à un domaine
- ✅ Ajouter bio et photo
- ✅ Recherche par nom

### 📚 Gestion des Domaines (Admin)
- ✅ Créer/Modifier/Supprimer domaines
- ✅ Catégorisation
- ✅ Description détaillée

### 📄 Gestion des Publications (Admin)
- ✅ Créer/Modifier/Supprimer publications
- ✅ Associer chercheur + domaine
- ✅ Upload PDF (stockage local `/uploads`)
- ✅ DOI unique
- ✅ Recherche par titre/abstract

### 📰 Gestion des Actualités (Modérateur)
- ✅ Créer/Modifier/Supprimer actualités
- ✅ Éditeur rich text (p-editor)
- ✅ Mise en avant (featured toggle)
- ✅ Image d'illustration

### 👤 Gestion des Utilisateurs (Admin)
- ✅ Créer/Supprimer utilisateurs
- ✅ Attribuer rôles
- ✅ Activer/Désactiver comptes

### 🔍 Recherche Globale (Tous)
- ✅ Recherche par mot-clé (publications + chercheurs)
- ✅ Filtrer par domaine
- ✅ Filtrer par chercheur
- ✅ Résultats en temps réel

### 🏠 Page d'Accueil (Public)
- ✅ Hero section avec barre de recherche
- ✅ Publications récentes (6 dernières)
- ✅ Actualités (3 dernières)
- ✅ Accès sans authentification

### 🤖 Module IA
- ✅ **Extraction de mots-clés** : TF-IDF sur titre + abstract
- ✅ **Recommandations** : Cosine similarity entre publications
- ✅ Intégration FastAPI transparente
- ✅ Gestion d'erreurs gracieuse

---

## 📖 Guide d'utilisation

### Scénario 1 : S'inscrire et se connecter
1. Aller à `http://localhost:4200/auth/register`
2. Remplir : username, email, password (min 6 caractères)
3. Cliquer "S'inscrire"
4. Redirection automatique vers home
5. Navbar affiche votre username + rôle (UTILISATEUR par défaut)

### Scénario 2 : Admin — Créer un domaine
1. Se connecter avec compte ADMIN
2. Cliquer sur l'icône ⚙️ (Admin) dans la navbar
3. Aller à "Domaines"
4. Cliquer "Ajouter"
5. Remplir : Nom, Description, Catégorie
6. Cliquer "Sauvegarder"
7. ✅ Domaine apparaît dans la table

### Scénario 3 : Admin — Créer un chercheur
1. Aller à Admin → Chercheurs
2. Cliquer "Ajouter"
3. Remplir : Prénom, Nom, Email, Bio, Domaine
4. Cliquer "Sauvegarder"
5. ✅ Chercheur apparaît dans la table

### Scénario 4 : Admin — Créer une publication
1. Aller à Admin → Publications
2. Cliquer "Ajouter"
3. Remplir : Titre, Abstract, DOI, Chercheur, Domaine
4. Uploader PDF (optionnel)
5. Cliquer "Sauvegarder"
6. ✅ Publication apparaît dans la table

### Scénario 5 : Modérateur — Publier une actualité
1. Se connecter avec compte MODERATEUR
2. Cliquer sur l'icône ✏️ (Modérateur) dans la navbar
3. Cliquer "Ajouter"
4. Remplir : Titre, Contenu (rich text)
5. Toggle "À la une" (optionnel)
6. Cliquer "Sauvegarder"
7. ✅ Actualité apparaît sur la home

### Scénario 6 : Utilisateur — Rechercher des publications
1. Se connecter avec compte UTILISATEUR
2. Aller à User → Recherche
3. Entrer une requête (ex: "machine learning")
4. Sélectionner un domaine (optionnel)
5. Cliquer "Rechercher"
6. ✅ Résultats affichés en deux colonnes (publications + chercheurs)

### Scénario 7 : Public — Consulter la home
1. Aller à `http://localhost:4200` (sans authentification)
2. Voir la hero section avec barre de recherche
3. Voir les 6 publications récentes
4. Voir les 3 actualités récentes
5. Cliquer "Voir toutes les publications" pour recherche avancée

---

## 🔌 API REST

### Authentification
```
POST   /api/auth/register          # Inscription
POST   /api/auth/login             # Connexion → JWT token
```

### Domaines
```
GET    /api/domains                # Lister tous
GET    /api/domains/{id}           # Détail
POST   /api/domains                # Créer [ADMIN]
PUT    /api/domains/{id}           # Modifier [ADMIN]
DELETE /api/domains/{id}           # Supprimer [ADMIN]
```

### Chercheurs
```
GET    /api/researchers            # Lister tous
GET    /api/researchers/{id}       # Détail
GET    /api/researchers/search?q=  # Recherche par nom
GET    /api/researchers/domain/{id}# Par domaine
POST   /api/researchers            # Créer [ADMIN]
PUT    /api/researchers/{id}       # Modifier [ADMIN]
DELETE /api/researchers/{id}       # Supprimer [ADMIN]
```

### Publications
```
GET    /api/publications           # Lister tous
GET    /api/publications/{id}      # Détail
GET    /api/publications/search?q= # Recherche
GET    /api/publications/researcher/{id}
GET    /api/publications/domain/{id}
POST   /api/publications           # Créer [ADMIN]
PUT    /api/publications/{id}      # Modifier [ADMIN]
DELETE /api/publications/{id}      # Supprimer [ADMIN]
```

### Actualités
```
GET    /api/news                   # Lister tous
GET    /api/news/featured          # À la une
GET    /api/news/{id}              # Détail
POST   /api/news                   # Créer [MODERATEUR]
PUT    /api/news/{id}              # Modifier [MODERATEUR]
DELETE /api/news/{id}              # Supprimer [MODERATEUR]
```

### Utilisateurs
```
GET    /api/users                  # Lister tous [ADMIN]
GET    /api/users/{id}             # Détail [ADMIN]
POST   /api/users                  # Créer [ADMIN]
PUT    /api/users/{id}/role        # Changer rôle [ADMIN]
DELETE /api/users/{id}             # Supprimer [ADMIN]
```

### Recherche Globale
```
GET    /api/search?q=&domain=&researcher=
# Retourne : { publications: [], researchers: [] }
```

### IA
```
POST   /api/ai/keywords            # Extraire mots-clés
# Body: { text: string, n: int }
# Response: { keywords: string[] }

GET    /api/ai/recommend/{id}?topN=5
# Retourne : { recommendations: [{id, text, similarity_score}] }
```

---

## 🤖 Module IA

### Extraction de Mots-clés (TF-IDF)
```bash
curl -X POST http://localhost:8080/api/ai/keywords \
  -H "Content-Type: application/json" \
  -d '{"text": "Machine learning et deep learning", "n": 5}'

# Response:
# { "keywords": ["machine learning", "deep learning", "learning"] }
```

### Recommandations (Cosine Similarity)
```bash
curl -X GET "http://localhost:8080/api/ai/recommend/1?topN=5" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Response:
# {
#   "recommendations": [
#     {"id": 2, "text": "...", "similarity_score": 0.85},
#     {"id": 3, "text": "...", "similarity_score": 0.72}
#   ]
# }
```

### Endpoints FastAPI (Direct)
```
POST   http://localhost:8001/keywords
POST   http://localhost:8001/recommend
GET    http://localhost:8001/health
```

---

## 📁 Structure du Projet

```
ia-technology-platform/
│
├── backend/                                    # Spring Boot
│   ├── pom.xml
│   └── src/main/java/com/iatechnology/
│       ├── config/
│       │   ├── CorsConfig.java
│       │   ├── SecurityConfig.java
│       │   └── RestTemplateConfig.java
│       ├── controller/
│       │   ├── AuthController.java
│       │   ├── DomainController.java
│       │   ├── ResearcherController.java
│       │   ├── PublicationController.java
│       │   ├── NewsController.java
│       │   ├── UserController.java
│       │   ├── SearchController.java
│       │   └── AiController.java
│       ├── service/
│       │   ├── AuthService.java
│       │   ├── DomainService.java
│       │   ├── ResearcherService.java
│       │   ├── PublicationService.java
│       │   ├── NewsService.java
│       │   ├── UserService.java
│       │   ├── AuditLogService.java
│       │   └── AiService.java
│       ├── repository/
│       │   ├── UserRepository.java
│       │   ├── DomainRepository.java
│       │   ├── ResearcherRepository.java
│       │   ├── PublicationRepository.java
│       │   ├── NewsRepository.java
│       │   └── AuditLogRepository.java
│       ├── model/
│       │   ├── User.java
│       │   ├── Role.java
│       │   ├── Domain.java
│       │   ├── Researcher.java
│       │   ├── Publication.java
│       │   ├── News.java
│       │   └── AuditLog.java
│       ├── dto/
│       │   ├── auth/
│       │   │   ├── LoginRequest.java
│       │   │   ├── RegisterRequest.java
│       │   │   └── AuthResponse.java
│       │   ├── ai/
│       │   │   ├── KeywordsRequest.java
│       │   │   ├── KeywordsResponse.java
│       │   │   ├── RecommendationsResponse.java
│       │   │   └── RecommendationItem.java
│       │   ├── DomainDTO.java
│       │   ├── ResearcherDTO.java
│       │   ├── PublicationDTO.java
│       │   ├── NewsDTO.java
│       │   └── UserDTO.java
│       ├── security/
│       │   ├── JwtTokenProvider.java
│       │   ├── JwtAuthFilter.java
│       │   └── UserDetailsServiceImpl.java
│       ├── exception/
│       │   └── GlobalExceptionHandler.java
│       └── IaTechnologyApplication.java
│   └── src/main/resources/
│       └── application.properties
│
├── ia-technology-frontend/                    # Angular
│   ├── package.json
│   ├── angular.json
│   ├── tsconfig.json
│   └── src/
│       ├── app/
│       │   ├── app.config.ts
│       │   ├── app.routes.ts
│       │   ├── app.component.ts
│       │   ├── app.component.html
│       │   ├── core/
│       │   │   ├── guards/
│       │   │   │   ├── auth.guard.ts
│       │   │   │   └── role.guard.ts
│       │   │   ├── interceptors/
│       │   │   │   └── jwt.interceptor.ts
│       │   │   └── services/
│       │   │       ├── auth.service.ts
│       │   │       └── api.service.ts
│       │   └── modules/
│       │       ├── public/
│       │       │   ├── public.routes.ts
│       │       │   └── home/
│       │       │       ├── home.component.ts
│       │       │       ├── home.component.html
│       │       │       └── home.component.scss
│       │       ├── auth/
│       │       │   ├── auth.routes.ts
│       │       │   ├── login/
│       │       │   │   ├── login.component.ts
│       │       │   │   ├── login.component.html
│       │       │   │   └── login.component.scss
│       │       │   └── register/
│       │       │       ├── register.component.ts
│       │       │       ├── register.component.html
│       │       │       └── register.component.scss
│       │       ├── admin/
│       │       │   ├── admin.routes.ts
│       │       │   ├── dashboard/
│       │       │   ├── researchers/
│       │       │   ├── domains/
│       │       │   ├── publications/
│       │       │   └── users/
│       │       ├── moderator/
│       │       │   ├── moderator.routes.ts
│       │       │   └── news/
│       │       └── user/
│       │           ├── user.routes.ts
│       │           ├── profile/
│       │           └── search/
│       └── styles.scss
│
├── ai-service/                                # Python FastAPI
│   ├── main.py
│   ├── keyword_extractor.py
│   ├── recommender.py
│   └── requirements.txt
│
└── README.md
```

---

## 🔒 Sécurité

### Authentification
- ✅ JWT stateless (pas de session serveur)
- ✅ Token valide 24h
- ✅ Refresh token non implémenté (à ajouter en production)
- ✅ Password hashing : BCrypt

### Autorisation
- ✅ @PreAuthorize sur tous les endpoints sensibles
- ✅ Guards Angular (authGuard, roleGuard)
- ✅ Rôles : ADMIN, MODERATEUR, UTILISATEUR

### Protection CSRF
- ✅ Désactivée (JWT stateless, pas de cookies)

### Protection XSS
- ✅ Validation DTOs (@Valid)
- ✅ Angular sanitization automatique

### Audit
- ✅ Logging de toutes les actions (CREATE, UPDATE, DELETE)
- ✅ Table `audit_logs` avec username, timestamp, détails

### À améliorer (Production)
- [ ] HTTPS obligatoire
- [ ] Rate limiting
- [ ] Refresh tokens
- [ ] 2FA
- [ ] CORS plus restrictif
- [ ] Secrets en variables d'environnement

---

## 🧪 Tests

### Test 1 — Authentification
```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@example.com","password":"password123"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"password123"}'

# Response: { "token": "eyJhbGc...", "role": "UTILISATEUR", "username": "john" }
```

### Test 2 — CRUD Domaines
```bash
# Create
curl -X POST http://localhost:8080/api/domains \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Machine Learning","description":"AI & ML","category":"IA"}'

# Read
curl -X GET http://localhost:8080/api/domains \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Update
curl -X PUT http://localhost:8080/api/domains/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Deep Learning","description":"Updated","category":"IA"}'

# Delete
curl -X DELETE http://localhost:8080/api/domains/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Test 3 — Recherche Globale
```bash
curl -X GET "http://localhost:8080/api/search?q=machine%20learning&domain=1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Response:
# {
#   "publications": [...],
#   "researchers": [...],
#   "publicationsByDomain": [...],
#   "researchersByDomain": [...]
# }
```

### Test 4 — Module IA
```bash
# Keywords
curl -X POST http://localhost:8080/api/ai/keywords \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"text":"Deep learning neural networks","n":5}'

# Recommendations
curl -X GET "http://localhost:8080/api/ai/recommend/1?topN=5" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Swagger UI
Accéder à `http://localhost:8080/swagger-ui.html` pour tester tous les endpoints interactivement.

---

## 📊 Statistiques du Projet

| Composant | Fichiers | Lignes |
|-----------|----------|--------|
| Backend Java | 42 | ~3500 |
| Frontend Angular | 35+ | ~2500 |
| AI Service Python | 3 | ~150 |
| **Total** | **80+** | **~6150** |

---

## 🎓 Apprentissages Clés

### Backend
- Spring Boot 3.2 avec Java 17
- JWT stateless authentication
- JPA/Hibernate ORM
- Spring Security 6
- RestTemplate pour appels externes

### Frontend
- Angular 19 standalone components
- PrimeNG 19 (Lara theme)
- Signals pour state management
- Lazy loading des routes
- JWT interceptor

### AI
- TF-IDF pour extraction de mots-clés
- Cosine similarity pour recommandations
- FastAPI microservice
- Intégration REST

---

## 📝 Licence

Ce projet est fourni à titre éducatif pour le cours "Développement Web Avancé" (2ING-IDL, 2025-2026).

---

## 👨‍💻 Auteur

Développé par l'équipe IA-Technology pour la gestion des travaux scientifiques.

**Dernière mise à jour** : Avril 2026
