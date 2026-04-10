# Mini Projet – Développement Web Avancé

**Module :** Développement Web Avancé  
**Enseignant :** F. OUESLATI  
**Niveau :** 2ING-IDL  
**Année Universitaire :** 2025-2026

---

## 1. Contexte et Objectifs

Le projet consiste à concevoir une **application web professionnelle** pour la société **IA-Technology**, dédiée à la gestion et à la diffusion des travaux scientifiques. La plateforme vise à :

- Centraliser les informations sur les chercheurs et leurs publications.
- Valoriser les projets en IA (NLP, Vision par ordinateur, Cybersécurité, etc.).
- Fournir un environnement interactif, sécurisé et évolutif.

---

## 2. Architecture Technique

### Backend
- Framework : **Spring Boot** (Java)
- API REST sécurisée
- ORM : **JPA / Hibernate**
- Base de données : **MySQL** ou **PostgreSQL**

### Frontend
- Framework : **Angular**
- Architecture modulaire
- Communication via services REST
- Interface **responsive** (desktop et mobile)

### Sécurité
- Authentification via **JWT**
- Gestion des rôles : Admin, Modérateur, Utilisateur
- Protection contre CSRF, XSS et autres vulnérabilités

---

## 3. Description Fonctionnelle

### 3.1 Espace Administrateur
- **Chercheurs** : ajout, modification, suppression, consultation des profils, recherche par nom ou domaine.
- **Domaines** : création, modification, classification.
- **Publications** : ajout, modification, suppression, association à des chercheurs, upload (PDF, DOI).
- **Comptes** : création, suppression, attribution des rôles.

### 3.2 Espace Modérateur
- Gestion du contenu de la page d'accueil.
- Publication d'actualités et d'annonces.
- Mise en avant des projets récents.

### 3.3 Espace Utilisateur
- **Gestion de compte** : inscription, authentification, modification des informations personnelles.
- **Recherche & consultation** : par domaine, par chercheur, par mots-clés ; téléchargement des publications.
- **Accès libre** : consultation de la page d'accueil sans authentification.

---

## 4. Exigences Non Fonctionnelles

- Interface ergonomique et intuitive
- Temps de réponse optimisé
- Architecture évolutive et maintenable
- Sécurité des données
- Journalisation des actions
- Documentation technique complète

---

## 5. Module IA *(Optionnel – Mention Excellent)*

Un module d'Intelligence Artificielle peut être intégré pour décrocher la mention Excellent. Exemples :

- Classification automatique des publications
- Extraction automatique de mots-clés
- Système de recommandation
- Recherche sémantique intelligente

Le module doit être intégré à l'architecture existante, exposé via une API dédiée et documenté (modèle, métriques, résultats).

---

## 6. Livrables Attendus

- Code source complet (Backend + Frontend)
- Présentation finale avec démonstration
