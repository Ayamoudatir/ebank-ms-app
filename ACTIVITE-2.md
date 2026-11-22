# Activité 2 — Micro-services e-commerce

Le projet conserve les services bancaires de l’activité 1 et ajoute les composants e-commerce demandés : inventaire, facturation, configuration centralisée et interface Angular.

## Rôle de chaque service

- `customer-service` stocke les clients et expose ses endpoints métiers `/customers` ainsi que Spring Data REST sous `/api/customers`.
- `inventory-service` stocke les produits et expose `/products` ainsi que Spring Data REST sous `/api/products`.
- `billing-service` stocke les factures et leurs lignes. Il utilise OpenFeign pour demander à Customer et Inventory les informations du client et des produits.
- `discovery-service` (Eureka) tient le registre des services. Les clients Feign et les routes `lb://...` utilisent les noms enregistrés, pas les ports.
- `gateway-service` expose les routes fixes `/customers/**`, `/products/**`, `/bills/**` et `/accounts/**`. La découverte automatique ajoute aussi des routes à partir des services inscrits dans Eureka.
- `config-service` distribue les paramètres depuis `config-repo/`, placé à la racine du projet et initialisé comme dépôt Git séparé, comme dans la vidéo du professeur. `application.properties` contient les réglages partagés; chaque fichier `<service>.properties` contient la configuration propre au service et `gateway-service.yml` les routes de la Gateway.
- Le dépôt comprend aussi les profils de démonstration `customer-service-dev.properties` et `customer-service-prod.properties`. `GET /test/config1` lit les valeurs globales; `GET /test/config2` lit `customer.params.x/y`. Après un commit dans `config-repo`, `POST http://localhost:8057/actuator/refresh` actualise les valeurs du contrôleur annoté `@RefreshScope`.
- `angular-client` fournit le formulaire de création d’une facture; son serveur de développement relaie les appels API vers Gateway via `/gateway`.
- `ebank-service` reste présent comme service de l’activité 1.

## Démarrage

Chaque service est un projet Maven indépendant. Démarre les commandes dans des terminaux distincts, dans cet ordre :

1. `discovery-service`: `./mvnw spring-boot:run` — port 8761, tableau Eureka `http://localhost:8761`.
2. `config-service`: `./mvnw spring-boot:run` depuis `config-service` — port 9999. Le dépôt de configuration séparé doit être présent dans `config-repo/` à la racine du projet (voir son `README.md`).
3. `customer-service`: `./mvnw spring-boot:run` — port 8057.
4. `inventory-service`: `./mvnw spring-boot:run` — port 8061.
5. `billing-service`: `./mvnw spring-boot:run` — port 8062.
6. `gateway-service`: `./mvnw spring-boot:run` — port 8888.
7. Interface: depuis `angular-client`, `npm install` puis `npm start` — port 4200.

Sur macOS/Linux, `./mvnw` est utilisable. Sous Windows, utilise `mvnw.cmd spring-boot:run`.

## Essais REST

Les routes suivantes passent par Gateway :

- `GET http://localhost:8888/customers`
- `GET http://localhost:8888/products`
- `GET http://localhost:8888/api/customers` et `GET http://localhost:8888/api/products` (endpoints Spring Data REST du cours)
- `GET http://localhost:8888/CUSTOMER-SERVICE/customers/1` (route dynamique Eureka comme dans la vidéo)
- `POST http://localhost:8888/products` avec JSON `{"name":"Clavier","price":350,"quantity":12}`
- `POST http://localhost:8888/bills` avec JSON `{"customerId":1,"productItems":[{"productId":"ID_DU_PRODUIT","quantity":2}]}`
- `GET http://localhost:8888/bills/1`

Le POST facture vérifie le client et les produits via Feign, prend le prix courant de l’inventaire, enregistre les lignes avec la facture, puis le GET enrichit le résultat avec les objets client et produit.

## H2

Les consoles H2 sont disponibles sur les services de données, par exemple `http://localhost:8061/h2-console` pour Inventory. URL JDBC : `jdbc:h2:mem:inventory-db`; utilisateur `sa`; mot de passe vide. La base mémoire disparaît à l’arrêt du service. Chaque service conserve son URL JDBC; l’utilisateur, le mot de passe vide, `ddl-auto` et la console H2 sont centralisés dans `config-repo/application.properties`, comme dans la vidéo.

## Concepts à présenter

Eureka est l’annuaire; Gateway est le point d’entrée HTTP; les services métier gardent leurs propres données; Feign effectue des appels HTTP déclaratifs en résolvant les noms via Eureka; Config Server lit les propriétés depuis un dépôt Git indépendant. Les identifiants client et produit des factures sont des références entre services, pas des relations JPA inter-services.
