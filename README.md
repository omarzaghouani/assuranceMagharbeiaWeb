# 💼 Projet de Microservices - Contrat App Web Distribuée

Ce projet est une application web distribuée basée sur une architecture microservices. Il permet la gestion des contrats clients avec notification par email, filtrage par statut, et détection des contrats expirants.

## 🧱 Architecture

```plaintext
Frontend (Angular)
       |
       ↓
API Gateway (Spring Cloud Gateway) ←→ JWT Auth /Keycloak
       ↓
Eureka Server (Service Discovery)
       ↓
Microservice Contrat (Spring Boot)
       ↓
Base de données (MongoDB ou PostgreSQL)
🧩 Modules
📡 Eureka Server
Utilisé pour enregistrer et découvrir les microservices.

Port par défaut : 8761

Accès : http://localhost:8761

🚪 API Gateway
Sert de point d’entrée unique.

Configuration de routes via RouteLocator.

Sécurisé avec JWT (ReactiveJwtDecoder).

Port : 8088 (exemple)

📃 Microservice Contrat
CRUD sur les contrats

Envoi d'e-mails via Spring Mail

Notification des contrats expirants

Filtrage des contrats par statut

Exposé via /api/contrats

🛡️ Sécurité
JWT configuré sur la Gateway

Peut être étendu avec Keycloak pour une meilleure gestion des utilisateurs

💌 Notifications
Email envoyé automatiquement lors de la création d’un contrat

Notification groupée pour les contrats proches de l’expiration

🌐 Frontend
Développé avec Angular

Communique avec l’API via Gateway

Port : 4200

🐳 Docker (à intégrer)
Prochainement :

Dockerisation de chaque service

docker-compose.yml pour lancer toute l'architecture

Déploiement sur DockerHub

🔐 Authentification
Basée sur JWT (configurable avec Keycloak)

Exemple de config JWT via jwtDecoder() dans la Gateway
