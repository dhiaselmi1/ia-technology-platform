# Guide de test — IA-Technology

Ce document décrit **comment lancer l'application** et **quoi tester** pour valider que toutes les fonctionnalités (CRUD, sécurité, IA, dark mode, uploads, etc.) fonctionnent de bout en bout.

---

## 1. Pré-requis

| Composant | Version | Vérification |
|---|---|---|
| Java | 17+ | `java -version` |
| Maven | 3.9+ | `mvn -v` |
| Node.js | 20+ | `node -v` |
| npm | 10+ | `npm -v` |
| Python | 3.10+ | `python --version` |
| PostgreSQL | 14+ | `psql --version` |

## 2. Préparation de la base de données

```sql
CREATE DATABASE ia_technology_db;
```

Connexion par défaut (surchargeable via env) :
- user : `postgres` / password : `dhia`
- url : `jdbc:postgresql://localhost:5432/ia_technology_db`

```bash
export DB_PASSWORD=votre_mot_de_passe   # si différent
export JWT_SECRET=un-secret-de-plus-de-256-bits-pour-hs256
export SEED_ENABLED=true                 # désactivable en prod
```

Hibernate (`ddl-auto=update`) crée automatiquement les tables au démarrage : `users`, `audit_logs`, `domains`, `researchers`, `publications` (avec `image_url`, `updated_at`), `news` (avec `image_url`).

## 3. Démarrage des 3 services

**Trois terminaux** :

### Terminal 1 — Backend Spring Boot (port 8080)
```bash
cd backend
mvn spring-boot:run
```
Au démarrage, le seeder peuple : **8 domaines, 12 chercheurs, 24 publications** (étalées sur 18 mois), **10 actualités**, 8 utilisateurs.

### Terminal 2 — Service IA Python FastAPI (port 8001)
```bash
cd ai-service
python -m venv venv
.\venv\Scripts\activate          # Windows
pip install -r requirements.txt  # inclut sentence-transformers (~80 MB téléchargés au 1er appel)
uvicorn main:app --reload --port 8001
```

### Terminal 3 — Frontend Angular (port 4200)
```bash
cd ia-technology-frontend
npm install
ng serve
```

Ouvrir **http://localhost:4200**.

---

## 4. Comptes de test

| Email | Mot de passe | Rôle |
|---|---|---|
| `admin@ia-technology.com` | `admin123` | ADMIN |
| `moderator@ia-technology.com` | `moderator123` | MODERATEUR |
| `franck@example.com` | `user123` | MODERATEUR |
| `alice@example.com` | `user123` | UTILISATEUR |
| `bob@example.com` | `user123` | UTILISATEUR |
| `clara@example.com` | `user123` | UTILISATEUR |
| `david@example.com` | `user123` | UTILISATEUR (compte inactif — pour tester le message "compte inactif") |
| `erin@example.com` | `user123` | UTILISATEUR |

---

## 5. Authentification & rôles

### 5.1 Inscription
- [ ] Aller sur `/auth/register` → bouton **"← Retour à l'accueil"** visible en haut à gauche
- [ ] Remplir le formulaire avec un email **non utilisé** + mot de passe (≥ 6 caractères) → soumettre
- [ ] Redirection automatique vers `/auth/login` avec un bandeau vert *"Compte créé avec succès"*
- [ ] Le compte est créé en rôle UTILISATEUR — pas de session auto, il faut se connecter

### 5.2 Connexion
- [ ] `/auth/login` → bouton retour à l'accueil + toggle dark mode visibles
- [ ] Cocher **"Rester connecté"** → connexion → déconnexion → revenir sur `/auth/login` : email + mot de passe **pré-remplis automatiquement**
- [ ] Tenter de se connecter avec `david@example.com` → message *"Votre compte est inactif. Contactez l'administrateur."*
- [ ] Tenter avec mot de passe erroné → *"Email ou mot de passe incorrect"*

### 5.3 Persistance de session
- [ ] Se connecter, recharger la page (F5) → toujours connecté, le menu utilisateur affiche le **nom** (et non l'email)
- [ ] Le menu utilisateur (avatar + nom + flèche) s'affiche dans toutes les navbars (home, search, dashboards, profile…)

### 5.4 Profil personnel
- [ ] Connecté → menu utilisateur → **Mon profil** (`/user/profile`)
- [ ] Modifier le nom et l'email → **Enregistrer** → menu navbar mis à jour immédiatement
- [ ] Cliquer sur l'icône caméra de l'avatar → uploader une image → l'avatar change partout (navbar, dashboards, sidebars)
- [ ] Section *"Changer mon mot de passe"* (collapsible) : ancien + nouveau + confirmation → tester avec mauvais ancien mot de passe → message d'erreur clair

---

## 6. Administration

### 6.1 Dashboard admin (`/admin/dashboard`)
- [ ] **6 cartes KPI** en haut : Chercheurs, Publications (avec badge ↑↓ % de croissance mensuelle), Domaines, Utilisateurs (actifs/inactifs), Pubs ce mois, Actualités
- [ ] **Graphique linéaire** "Publications dans le temps" (12 mois) — courbe non plate grâce au seeding étalé
- [ ] **Doughnut** "Utilisateurs par rôle"
- [ ] **Bar chart** "Publications par domaine"
- [ ] **Bar horizontal** "Top 6 chercheurs"
- [ ] **Widget "Sujets émergents"** (IA #6) — termes en croissance via TF-IDF sur 120 jours
- [ ] **Activité récente** — 8 dernières entrées des audit logs
- [ ] **Inscriptions utilisateurs** — bar chart 12 mois

### 6.2 CRUD Utilisateurs (`/admin/users`)
- [ ] Tableau avec username, email, rôle (badge coloré), statut (Actif/Inactif), date d'inscription
- [ ] Toggle activer/désactiver via le switch → notification + statut mis à jour
- [ ] Modifier le rôle d'un utilisateur via la modale dédiée
- [ ] Supprimer un utilisateur

### 6.3 CRUD Domaines, Chercheurs, Publications
- [ ] Créer / éditer / supprimer dans chaque section
- [ ] Filtres et recherche live fonctionnels

### 6.4 Upload d'images (couvertures publications + actualités)
- [ ] Éditer une publication ou actualité → *"Choisir une image"*
- [ ] Sélectionner un fichier image → upload via `POST /api/files/images` → preview au format **16:9**
- [ ] Bouton ✕ pour retirer l'image avant sauvegarde

### 6.5 Upload de PDF (manuscrits)
- [ ] Éditer une publication → section *"Manuscrit PDF (max 10 MB)"* → *"Choisir un PDF"*
- [ ] PDFs de test fournis dans `sample-pdfs/` :
  - `quantum-error-correction-surface-codes.pdf`
  - `contrastive-pretraining-code-understanding.pdf`
  - `adversarial-robustness-vlm.pdf`
  - `diffusion-synthetic-medical-imaging.pdf`
- [ ] Le PDF apparaît en bandeau rouge avec son nom + lien cliquable
- [ ] Sauvegarder → l'icône PDF apparaît dans la table → cliquer → ouvre le PDF dans un nouvel onglet
- [ ] Sur la fiche publique `/publications/{id}` → bouton **"Télécharger"** fonctionnel

---

## 7. Modérateur

### 7.1 Espace modérateur (`/moderator/news`)
- [ ] Sidebar : **Actualités** + **Publications** + **Accueil**
- [ ] CRUD actualités avec éditeur riche (Quill via PrimeNG)
- [ ] Upload d'image dans le formulaire actualité (16:9)
- [ ] Toggle "À la une" en un clic
- [ ] Filtre Toutes / À la une

### 7.2 Publications (modérateur)
- [ ] Aller sur `/moderator/publications` → même UI que l'admin mais avec sidebar modérateur
- [ ] Le modérateur peut créer/éditer/supprimer des publications (rôle étendu)

---

## 8. Espace public

### 8.1 Page d'accueil (`/`)
- [ ] **Actualités** en première section (6 cartes max, avec image si fournie)
- [ ] **Barre Q&A IA** (IA #7) — voir section IA ci-dessous
- [ ] **Hero** + image
- [ ] **Stats strip** (publications / chercheurs / domaines)
- [ ] **Domaines** (cartes)
- [ ] **Publications récentes** : grille de tuiles avec couverture 16:9, badge domaine, date, titre + abstract clampés, footer auteur + CTA "Lire →"

### 8.2 Recherche publications (`/search`)
- [ ] Toggle **Mots-clés / Sémantique IA** (IA #1)
- [ ] Mode mots-clés : recherche client par titre/abstract/auteur/domaine
- [ ] Mode sémantique : encode la requête via sentence-transformers, retourne les publications par similarité, affiche un badge violet *"86%"* sur chaque résultat
- [ ] Filtres domaine et chercheur s'appliquent dans les deux modes
- [ ] Couvertures à taille standardisée (160 × 100, ratio 16:10)

### 8.3 Liste chercheurs (`/researchers`)
- [ ] Recherche live + filtre domaine
- [ ] Sur chaque carte chercheur : bouton **"✨ Profil IA"** (IA #8)
- [ ] Cliquer → expansion qui charge `/api/ai/researcher-profile/{id}` :
  - **Sujets d'expertise** (top mots-clés agrégés via TF-IDF)
  - **Chercheurs au profil proche** (top 3 par cosine similarity sur centroïdes d'embeddings) avec score %
- [ ] Bouton "Masquer" pour replier

### 8.4 Détail publication (`/publications/{id}`)
- [ ] Couverture en ratio 21:9 (max 360 px)
- [ ] Titre, badge domaine, date, auteur
- [ ] Section **"Mots-clés (extraction IA)"** — visible si connecté (TF-IDF via le service Python)
- [ ] Bouton **Télécharger** si PDF attaché

---

## 9. Fonctionnalités IA — détail des tests

### 9.1 Service Python (`http://localhost:8001`)
- [ ] `GET /health` → `{"status":"ok"}`
- [ ] `POST /keywords` body `{"text":"...","n":5}` → top-N TF-IDF
- [ ] `POST /recommend` → top-N par cosine
- [ ] `POST /semantic-search` → embeddings via `sentence-transformers/all-MiniLM-L6-v2` (lazy-load au 1er appel)
- [ ] `POST /researcher-profile` → keywords + chercheurs similaires
- [ ] `POST /ask` → retrieval sémantique + meilleure phrase de l'abstract

### 9.2 Backend Spring (relais)
| Méthode | URL | Auth | Fonction |
|---|---|---|---|
| POST | `/api/ai/keywords?text=...&n=10` | auth | extraction mots-clés |
| GET | `/api/ai/recommend/{id}?topN=5` | auth | recommandations TF-IDF |
| GET | `/api/ai/semantic-search?q=...&topN=10` | public | **recherche sémantique** |
| POST | `/api/ai/ask?question=...&topN=5` | public | **Q&A** |
| GET | `/api/ai/researcher-profile/{id}` | public | **profil chercheur** |

### 9.3 Tests UI

#### Scénario A — Recherche sémantique (`/search`)
Bascule le toggle sur **"Sémantique"** puis tape ces requêtes (les mots ne sont **pas** dans les titres → prouve la compréhension du sens) :

| Requête | Doit remonter |
|---|---|
| `quantum computing` | #14 Quantum Approximate Optimization, #23 Quantum Error Correction, #3 Side-Channel Attacks on Post-Quantum |
| `deep learning for healthcare` | #2 Medical Image Segmentation, #7 Federated Learning for Healthcare, #21 Diffusion Models for Synthetic Medical |
| `attaques sur la cryptographie` (FR !) | #3 Side-Channel Attacks, #12 Lattice-Based Cryptography |
| `large language models` | #16 LLM Fine-Tuning for Legal, #24 Adversarial Robustness in Vision-Language Models |

✅ Critères : badge avec score (%), résultats différents du toggle "mot-clé", la 1ère requête prend ~30 s (téléchargement modèle), les suivantes sont instantanées.

#### Scénario B — Q&A / RAG-lite (page d'accueil)
Barre **"Posez une question"** en haut :

| Question | Source attendue |
|---|---|
| `What are surface codes used for?` | Publi #23 |
| `How does federated learning preserve privacy?` | Publi #7 |
| `Why use diffusion models for medical imaging?` | Publi #21 |
| `What is adversarial robustness?` | Publi #24 |

✅ Critères : titre publi + **phrase extraite** (pas l'abstract entier) + score de pertinence.

#### Scénario C — Profil chercheur (`/researchers`)
Cliquer **"Profil IA"** sur :
- **#1 Dhia Selmi** → keywords liés à ses travaux + 2-3 chercheurs similaires
- **#7 Mehdi Ayadi** ou **#10 Imen Gharbi**

✅ Critères : la zone s'étend, mots-clés affichés en badges, cartes des chercheurs proches avec nom + domaine + % similarité.

#### Scénario D — Recommandations (page détail publication)
Ouvrir la publication, scroll en bas → section **"Publications similaires"** :
- Sur **#23 Quantum Error Correction** → recommande #14 (Quantum Optimization), #3 (Post-Quantum Crypto)
- Sur **#7 Federated Learning** → recommande #2 (Medical Imaging), #21 (Synthetic Medical)

✅ Critères : 3-4 cartes triées par similarité, cliquer une carte ouvre la publication suggérée.

#### Scénario E — Extraction de mots-clés (curl direct)
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@ia.tn","password":"password123"}' | jq -r .token)

curl -X POST "http://localhost:8080/api/ai/keywords?text=Quantum%20error%20correction%20with%20surface%20codes%20enables%20fault-tolerant%20quantum%20computing&n=5" \
  -H "Authorization: Bearer $TOKEN"
```
✅ Retourne 5 termes type `quantum`, `error correction`, `surface codes`, `fault-tolerant`, `quantum computing`.

#### Tests bonus — robustesse
| Test | Comportement attendu |
|---|---|
| Couper le service Python puis lancer recherche sémantique | Liste vide, **pas de 500** côté frontend |
| Recherche sémantique avec `q=""` | Erreur 400 propre |
| Q&A hors sujet (`"What's the weather?"`) | Aucun résultat ou scores < 0.2 (filtré) |
| 2ème recherche sémantique sur le même corpus | Beaucoup plus rapide (cache d'embeddings) |

#### Scénario F — IA générative : Résumé express + Vulgarisation (Groq)
**Pré-requis** : fichier `ai-service/.env` avec `GROQ_API_KEY=gsk_...` (gitignored). Service Python redémarré.

Sur la page détail d'une publication (`/publication/{id}`), section **Résumé**, deux boutons :
- ⚡ **Résumé express** → `mode=tldr` → 2-3 phrases techniques dans la langue source
- 🎓 **Expliquer simplement** → `mode=vulgarize` → 3-4 phrases en français accessible

Tester sur :
| Publication | Résumé express attendu | Vulgarisation attendue |
|---|---|---|
| #23 Quantum Error Correction with Surface Codes | Mention de "logical qubits", "2D lattice", "fault-tolerant" | Phrases simples expliquant que c'est pour fiabiliser les ordinateurs quantiques |
| #16 LLM Fine-Tuning for Legal Document Analysis | Mention "fine-tuning", "domain-specific", "legal" | Explication grand public sur l'adaptation de l'IA au juridique |
| #2 Deep Learning Pipelines for Medical Image Segmentation | Vocabulaire `U-Net`, `segmentation` conservé | Phrases sur "aider les médecins à analyser les images" |

✅ Critères :
- 1ère génération : ~1-2s (latence Groq)
- Cadre violet/dégradé avec icône ✨ et titre "Résumé express (IA générative)"
- Cliquer l'autre bouton → la zone se met à jour (pas de doublon)
- Le résumé original (`abstractText`) reste affiché au-dessus

#### Test robustesse Groq
| Test | Comportement attendu |
|---|---|
| Supprimer/vider `GROQ_API_KEY` dans `.env`, redémarrer Python | Réponse 503 propre, message "GROQ_API_KEY not set" affiché |
| Couper internet → cliquer "Résumé express" | Message d'erreur dans la zone IA, pas de crash |
| Cliquer 2 fois rapidement | Bouton désactivé pendant la génération (cursor: progress) |

### 9.4 Tests via Postman
Importer `ia-technology.postman_collection.json` → variables `baseUrl=http://localhost:8080`, `aiUrl=http://localhost:8001` → exécuter dans l'ordre :
1. Auth → Login admin (récupère le token automatiquement)
2. AI → Keywords / Recommendations / Semantic search / Ask / Researcher profile

---

## 10. Dark mode

- [ ] Bouton ☀️/🌙 visible dans toutes les top-bars
- [ ] Cliquer → bascule instantanée (HTML `<html class="dark-mode">`)
- [ ] Persisté en `localStorage` (`iat_theme`)
- [ ] Au premier chargement, si aucune préférence sauvée, utilise `prefers-color-scheme` du système
- [ ] PrimeNG (charts, dialog, p-editor, p-toast) adopte automatiquement son thème sombre via `darkModeSelector: '.dark-mode'`

---

## 11. Sanity checks

### Backend
```bash
cd backend && mvn clean compile -DskipTests
```
→ BUILD SUCCESS attendu.

### Frontend
```bash
cd ia-technology-frontend && ng build --configuration=development
```
→ `Output location: dist/ia-technology-frontend` attendu.

### Service IA
```bash
cd ai-service && python -c "from semantic_engine import get_model; get_model(); print('model loaded')"
```
→ Premier appel télécharge le modèle (~80 MB), ensuite quasi-instantané.

---

## 12. Reset complet de la base (pour re-seeding)

```sql
TRUNCATE publications, news, researchers, domains, audit_logs RESTART IDENTITY CASCADE;
```
La table `users` reste intacte. Au prochain démarrage du backend, le seeder remplira les autres tables.

---

## 13. Endpoints principaux

| Méthode | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | public | Création de compte (UTILISATEUR) |
| POST | `/api/auth/login` | public | Login → token JWT |
| GET | `/api/users/me` | auth | Profil courant |
| PUT | `/api/users/me` | auth | Modifier profil + avatar + mdp |
| PUT | `/api/users/{id}/active?active=true` | ADMIN | Activer/désactiver |
| PUT | `/api/users/{id}/role?role=ADMIN` | ADMIN | Changer rôle |
| POST | `/api/files/images` | auth | Upload image |
| POST | `/api/files/pdf` | ADMIN/MODÉ | Upload PDF |
| GET | `/uploads/**` | public | Sert les fichiers |
| GET | `/api/stats/*` | ADMIN | KPI + tendances pour le dashboard |
| GET/POST | `/api/ai/*` | mixte | Toutes les fonctionnalités IA |

Swagger UI : **http://localhost:8080/swagger-ui.html**
