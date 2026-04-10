# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**IA-Technology** — a web platform for managing scientific works (researchers, publications, domains, news). Three-service architecture:

| Service | Tech | Port |
|---|---|---|
| `backend/` | Spring Boot 3.2.3 + Java 17 + PostgreSQL | 8080 |
| `ia-technology-frontend/` | Angular 19 + PrimeNG 19 (SSR enabled) | 4200 |
| `ai-service/` | Python FastAPI + scikit-learn | 8001 |

## Commands

### Backend (Spring Boot)
```bash
cd backend
mvn spring-boot:run          # Start dev server
mvn clean package            # Build JAR
mvn test                     # Run tests
```

### Frontend (Angular)
```bash
cd ia-technology-frontend
ng serve                     # Start dev server
ng build                     # Production build
ng test --watch=false        # Run tests once
ng generate component modules/admin/researchers/researchers  # Example codegen
```

### AI Service (FastAPI)
```bash
cd ai-service
pip install -r requirements.txt
uvicorn main:app --reload --port 8001
```

## Prerequisites

1. PostgreSQL running locally — create the database:
   ```sql
   CREATE DATABASE ia_technology_db;
   ```
2. Update `backend/src/main/resources/application.properties` with your actual DB password.
3. Install `@primeng/themes` in the frontend: `npm install @primeng/themes`

## Architecture

### Request Flow
```
Angular (4200) ──JWT──► Spring Boot (8080) ──► PostgreSQL (5432)
                                │
                                └──► FastAPI AI service (8001)
```

### Backend Package Structure
```
com.iatechnology/
├── config/       SecurityConfig, CorsConfig, JwtConfig
├── controller/   REST controllers (one per entity + AuthController + AiController)
├── service/      Business logic + FileStorageService + AiService
├── repository/   JPA repositories with custom query methods
├── model/        JPA entities: User, Researcher, Domain, Publication, News
├── dto/          Request/Response DTOs
├── security/     JwtTokenProvider, JwtAuthFilter, UserDetailsServiceImpl
└── exception/    GlobalExceptionHandler (@RestControllerAdvice)
```

### Frontend Module Structure
```
src/app/
├── app.config.ts          Global providers: PrimeNG Aura theme, animations, HTTP + JWT interceptor
├── app.routes.ts          Lazy-loaded routes with role guards
├── core/
│   ├── guards/            authGuard (checks token), roleGuard (checks role from localStorage)
│   ├── interceptors/      jwtInterceptor — attaches Bearer token to every request
│   └── services/          AuthService (signals-based), ApiService (single HTTP abstraction)
└── modules/
    ├── public/            Home page — accessible without auth
    ├── auth/              Login + Register
    ├── admin/             Dashboard, Researchers, Domains, Publications, Users CRUD
    ├── moderator/         News/announcements management
    └── user/              Profile + Search
```

### Key Conventions

**Angular (standalone components, no NgModules):**
- All components are standalone — import PrimeNG components directly in each component's `imports` array.
- Use `loadComponent()` for leaf routes, `loadChildren()` for route groups.
- `AuthService.isAuthenticated` is an Angular signal — use `authService.isAuthenticated()` in templates.
- `ApiService` is the only class that calls `HttpClient` — all other services inject `ApiService`.
- JWT token stored in `localStorage` under key `iat_token`; user info (role, username) under `iat_user`.

**Backend:**
- `app.jwt.secret` and `app.jwt.expiration` are read via `@Value` in `JwtTokenProvider`.
- `app.upload.dir=./uploads` — PDF files are stored here; serve them via a dedicated endpoint.
- Spring Boot calls the AI service via `RestTemplate` using `app.ai.service.url`.
- Swagger UI available at `http://localhost:8080/swagger-ui.html` once backend is running.
- `ddl-auto=update` — Hibernate auto-creates/updates tables on startup; no migration files needed during dev.

**Roles:** `ADMIN`, `MODERATEUR`, `UTILISATEUR` — stored as enum in the `User` entity and in JWT claims.

### AI Service Endpoints
- `POST /keywords` — `{ text: string, n: int }` → `{ keywords: string[] }` (TF-IDF)
- `POST /recommend` — `{ target_text, corpus: [{id, text}], top_n }` → `{ recommendations }` (cosine similarity)
- `GET /health` — health check

---

## Implementation Progress

### ✅ Phase 1 — Setup & Configuration (DONE)
**Backend:**
- `backend/pom.xml` — Spring Boot 3.2.3, JWT (jjwt 0.12.3), PostgreSQL, Lombok, SpringDoc OpenAPI
- `IaTechnologyApplication.java` — main entry point
- `application.properties` — datasource, JWT secret, file upload path, AI service URL
- `config/CorsConfig.java` — allows `http://localhost:4200`

**AI Service:**
- `ai-service/main.py` — FastAPI app with `/keywords`, `/recommend`, `/health`
- `ai-service/keyword_extractor.py` — TF-IDF via scikit-learn
- `ai-service/recommender.py` — cosine similarity via scikit-learn
- `ai-service/requirements.txt`

**Frontend (Angular 19 + PrimeNG 19):**
- `app.config.ts` — PrimeNG Aura theme, `provideAnimationsAsync`, `provideHttpClient` + `jwtInterceptor`
- `styles.scss` — PrimeIcons + PrimeFlex imports, global reset
- `app.routes.ts` — lazy-loaded routes for all 5 modules with guards
- `core/guards/auth.guard.ts` — functional guard, redirects to `/auth/login`
- `core/guards/role.guard.ts` — functional guard, checks role from localStorage
- `core/interceptors/jwt.interceptor.ts` — attaches `Authorization: Bearer <token>`
- `core/services/auth.service.ts` — login/register/logout, signals-based auth state
- `core/services/api.service.ts` — single HTTP abstraction (get/post/put/delete/upload)
- `modules/*/routes.ts` — skeleton route files for public, auth, admin, moderator, user

---

### ⏳ Phase 2 — Backend: Security & Authentication (TODO)
- `User` entity + `Role` enum (ADMIN, MODERATEUR, UTILISATEUR)
- `JwtTokenProvider` — generate & validate JWT
- `JwtAuthFilter` — Spring Security filter
- `SecurityConfig` — public vs protected endpoints
- `AuthController` — `POST /api/auth/register` + `POST /api/auth/login`
- Audit logging via AOP

### ⏳ Phase 3 — Backend: Entities & CRUD (TODO)
- JPA entities: `Domain`, `Researcher`, `Publication`, `News`, `AuditLog`
- Repositories with custom search methods
- REST controllers + services for all entities
- `FileStorageService` — PDF upload/download to `./uploads`
- Global search endpoint: `GET /api/search?q=&domain=&researcher=`

### ⏳ Phase 4 — Frontend: Auth Module (TODO)
- Login and Register pages (PrimeNG forms)
- Route protection wired to guards

### ⏳ Phase 5 — Frontend: All Functional Spaces (TODO)
- Public home page (hero, recent publications, news)
- Admin space: dashboard + CRUD for researchers, domains, publications, users
- Moderator space: news editor, featured toggle
- User space: profile, advanced search, PDF download

### ⏳ Phase 6 — AI Module Integration (TODO)
- Spring Boot `AiService` calling FastAPI via RestTemplate
- `AiController` exposing `/api/ai/keywords` and `/api/ai/recommend/{id}`
- Frontend AI features wired to publication detail pages

### ⏳ Phase 7 — Finalization (TODO)
- Swagger/OpenAPI documentation review
- Security hardening (input validation, XSS, CSRF)
- End-to-end test scenarios with Postman collection
