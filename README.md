# 🎓 E-Learning Platform

Une plateforme d'apprentissage en ligne moderne et complète, construite avec une architecture découplée. Le projet se compose d'un backend robuste développé avec **Spring Boot** (Java) et d'un frontend dynamique et élégant réalisé avec **Angular** et **Tailwind CSS**.

---

## 🏗️ Architecture du Projet

Le projet est divisé en deux parties principales :
1. **Backend** : API REST sécurisée sous Spring Boot connectée à une base de données MySQL.
2. **Frontend** : Application SPA (Single Page Application) sous Angular connectée à l'API REST.

---

## 🛠️ Technologies Utilisées

### **Backend**
*   **Langage :** Java 21
*   **Framework :** Spring Boot 4.0.2
*   **Sécurité :** Spring Security, OAuth2 Resource Server (JWT)
*   **Persistance :** Spring Data JPA / Hibernate
*   **Base de données :** MySQL (via `mysql-connector-j`)
*   **Documentation API :** Swagger UI / OpenAPI 3.0 (`springdoc-openapi`)
*   **Outils :** Project Lombok (pour la réduction du code boilerplate), Maven (gestionnaire de dépendances)

### **Frontend**
*   **Framework :** Angular 16.2.16
*   **Stylisation :** Tailwind CSS & Vanilla CSS
*   **Graphiques :** Chart.js
*   **Gestion de requêtes :** RxJS & Angular HTTP Client (avec Interceptors pour le jeton JWT)

---

## 📋 Fonctionnalités Principales

La plateforme gère trois rôles d'utilisateurs distincts avec des autorisations spécifiques :

### 👤 **Espace Étudiant (Student)**
*   **Authentification & Profil :** Inscription, connexion, et mise à jour des informations de profil.
*   **Navigation & Recherche :** Recherche et filtrage des cours par catégorie.
*   **Suivi des Cours :** Inscription aux cours, consultation des chapitres/leçons et des supports associés (PDF, documents).
*   **Évaluations :** Lecteur de Quiz dynamique (`quiz-player`) avec choix multiples et soumission directe.
*   **Progression :** Suivi de l'état de progression par leçon et par cours.
*   **Avis & Évaluations :** Notation des cours et commentaires.

### 👨‍🏫 **Espace Enseignant (Teacher)**
*   **Tableau de Bord :** Statistiques clés sur les cours, les inscriptions et la progression globale des élèves.
*   **Gestion des Cours :** Création, modification, catégorisation et publication de cours interactifs.
*   **Création de Contenu :** Gestion de sections, leçons et téléchargement de supports pédagogiques (PDF).
*   **Constructeur de Quiz :** Création de quiz interactifs (`quiz-builder`) avec questions, réponses et barème de notation.
*   **Suivi des Étudiants :** Consultation de l'avancement individuel des étudiants par cours.

### 🔑 **Espace Administrateur (Admin)**
*   **Dashboard d'administration :** Statistiques globales de la plateforme.
*   **Gestion des Utilisateurs :** Approbation et gestion des comptes utilisateurs (Enseignants et Étudiants).
*   **Gestion des Catégories :** CRUD complet des catégories de cours disponibles pour organiser la plateforme.

---

## 📂 Structure du Workspace

```text
ProjectAngular/
├── README.md               # Ce fichier d'explications
├── .gitignore              # Ignorer les dépendances et builds locaux
│
├── elearning-platform/     # REPERTOIRE BACKEND (Spring Boot)
│   └── elearning-platform/
│       ├── pom.xml         # Dépendances Maven & configuration build
│       └── src/
│           ├── main/
│           │   ├── java/com/elearning/elearning_platform/
│           │   │   ├── auth/           # Sécurité & Connexion
│           │   │   ├── user/           # Gestion des profils/rôles
│           │   │   ├── course/         # Cours, Sections, Leçons
│           │   │   ├── enrollment/     # Inscriptions aux cours
│           │   │   ├── assessment/     # Quiz et Évaluations
│           │   │   └── shared/         # Sécurité, Seeders, Gestion d'erreurs
│           │   └── resources/
│           │       └── application.properties # Configurations (Port, DB, OAuth2)
│           └── test/       # Tests unitaires et d'intégration
│
└── Frontend/               # REPERTOIRE FRONTEND (Angular)
    └── Frontend/
        ├── package.json    # Dépendances NPM & Scripts de lancement
        ├── angular.json    # Configuration Angular CLI
        └── src/
            ├── index.html  # Fichier racine HTML
            └── app/
                ├── Admin/      # Pages et Dashboard Administrateur
                ├── Teacher/    # Pages, Éditeur de Cours, Quiz Builder Enseignant
                ├── Student/    # Pages, Lecteur de Cours, Quiz Player Étudiant
                ├── Shared/     # Navbar, Footer et composants partagés par rôle
                ├── services/   # Services de communication avec l'API
                └── models/     # Modèles et interfaces TypeScript (DTOs)
```

---

## 🚀 Installation et Lancement Local

### **Prérequis**
*   **Java JDK 21** ou supérieur
*   **Node.js** (version 18 ou supérieure recommandée) et **NPM**
*   **MySQL Server** démarré localement ou à distance

---

### **Étape 1 : Configuration et Lancement du Backend**

1. Créez une base de données MySQL vide nommée `elearning_db` :
   ```sql
   CREATE DATABASE elearning_db;
   ```
2. Accédez au dossier du backend :
   ```bash
   cd elearning-platform/elearning-platform
   ```
3. Ouvrez le fichier `src/main/resources/application.properties` et configurez vos accès de base de données :
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/elearning_db?useSSL=false&serverTimezone=UTC
   spring.datasource.username=VOTRE_UTILISATEUR
   spring.datasource.password=VOTRE_MOT_DE_PASSE
   ```
4. Lancez le backend à l'aide du wrapper Maven :
   ```bash
   # Sur Windows (PowerShell)
   .\mvnw.cmd spring-boot:run
   
   # Sur Linux/macOS
   ./mvnw spring-boot:run
   ```
   *L'API sera accessible sur `http://localhost:8080`.*
   *La documentation OpenAPI Swagger sera disponible sur `http://localhost:8080/swagger-ui/index.html`.*

---

### **Étape 2 : Configuration et Lancement du Frontend**

1. Accédez au dossier du frontend :
   ```bash
   cd Frontend/Frontend
   ```
2. Installez les dépendances du projet :
   ```bash
   npm install
   ```
3. Lancez le serveur de développement Angular :
   ```bash
   npm run start
   # ou
   ng serve
   ```
4. Ouvrez votre navigateur sur `http://localhost:4200/`.

---

## 🔒 Sécurité et Authentification

La sécurité de la plateforme repose sur **Spring Security** avec **OAuth2** :
*   L'authentification génère un jeton sécurisé **JWT (JSON Web Token)**.
*   Ce jeton est stocké côté frontend et automatiquement injecté dans les en-têtes HTTP de chaque requête grâce à un **intercepteur Angular** (`auth.interceptor.ts`).
*   Des **Guards Angular** (`role.guard.ts`, `guest-only.guard.ts`) empêchent les utilisateurs non autorisés d'accéder aux tableaux de bord et pages restreintes.
