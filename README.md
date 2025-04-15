# 📄 Microservice Contrat – Projet Microservices

## 🚀 Description

Ce projet fait partie d'une architecture microservices. Le **microservice Contrat** est responsable de la gestion des contrats dans le système, y compris leur création, mise à jour, suppression et consultation.

Il s'intègre avec d'autres microservices comme :
- 🎓 `microservice-client` : pour gérer les clients
- 📦 `microservice-produit` : pour les produits/services associés aux contrats

## 🛠️ Stack Technique

- Langage : Java / Spring Boot
- Base de données : MySQL / PostgreSQL
- Communication : REST API / OpenFeign
- Service Discovery : Eureka (ou Consul)
- Configuration : Spring Cloud Config
- Sécurité : Spring Security / JWT (si applicable)
- CI/CD : GitHub Actions (optionnel)

## 📁 Structure du projet

```bash
contrat-service/
├── src/
│   ├── main/
│   │   ├── java/com/monapp/contrat/
│   │   ├── resources/
│   │   │   └── application.yml
├── pom.xml
