# Dépôt local de configuration

Ce dossier se trouve dans le projet `ebank-ms-app`, comme dans la vidéo du professeur. Il reste un dépôt Git séparé, lu par `config-service` avec Spring Cloud Config.

- `application.properties` contient les réglages partagés.
- `<nom-du-service>.properties` contient les réglages propres à un service.
- `customer-service-dev.properties` et `customer-service-prod.properties` illustrent les profils `dev` et `prod` de la vidéo.
- `gateway-service.yml` contient les routes statiques de la Gateway.

Les valeurs de démonstration sont `global.params.p1=456`, `global.params.p2=224`, `customer.params.x/y=11/22` par défaut, `88/99` en `dev` et `33/44` en `prod`. Les propriétés H2 communes sont dans `application.properties`; chaque service de données garde sa propre URL JDBC.

Pour observer les propriétés chargées :

- `GET http://localhost:9999/application/default` : propriétés communes.
- `GET http://localhost:9999/customer-service/default` : propriétés communes et client.
- `GET http://localhost:9999/customer-service/dev` ou `/prod` : surcharge par profil.
- `GET http://localhost:8057/test/config1` : `global.params.p1` et `global.params.p2`.
- `GET http://localhost:8057/test/config2` : paramètres `customer.params.x` et `.y`.

Pour démarrer le client avec un profil : `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev` (ou `prod`).
Après un commit de configuration, `POST http://localhost:8057/actuator/refresh` recharge les propriétés utilisées par le contrôleur annoté `@RefreshScope`.

Le dépôt démarre sur la branche `main`. Pour vérifier la configuration exposée :
`http://localhost:9999/customer-service/default` ou
`http://localhost:9999/gateway-service/default`.

Après modification d’un fichier, enregistrer la nouvelle version dans ce dépôt avec
`git add` puis `git commit`. Pour recharger une configuration déjà utilisée par un
service, envoyer `POST http://localhost:<port-du-service>/actuator/refresh`.
