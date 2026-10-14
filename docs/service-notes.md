# Notes Atelier 4 : couche Service, IoC et injection de dépendances

## A. Modes d'injection comparés (étape 2)

| Critère | Constructeur | Attribut (`@Autowired` sur le champ) |
|---|---|---|
| Champ `final` possible ? | Oui | Non |
| Dépendance visible dans la signature de la classe ? | Oui (paramètre du constructeur) | Non (dépendance cachée) |
| Utilisable avec `new` hors conteneur sans dépendance `null` ? | Oui : on est obligé de fournir le repository | Non : le champ reste `null` hors conteneur (NullPointerException) |
| Signalé par SonarQube for IDE ? | Non | Oui (règle S6813, à vérifier dans le panneau) |

## B. Services

| Service | Dépendances injectées | Mode | Justification |
|---|---|---|---|
| AgenceServiceImpl | IAgenceRepository | Constructeur | Champ `final` + `@RequiredArgsConstructor` ; dépendance typée par interface ; service sans état. |
| EmployeServiceImpl | IEmployeRepository | Constructeur | Idem. |
| VehiculeServiceImpl | IVehiculeRepository | Constructeur | Idem. |
| EquipementServiceImpl | IEquipementRepository | Constructeur | Idem. |
| ClientServiceImpl | IClientRepository | Constructeur | Idem. |
| ReservationServiceImpl | IReservationRepository | Constructeur | Idem. |
| ContratServiceImpl | IContratRepository | Constructeur | Idem ; les paiements sont gérés par cascade depuis le Contrat. |
| PaiementServiceImpl | IPaiementRepository | Constructeur | Idem ; lecture seule (findById, findAll). |
| MaintenanceServiceImpl | IMaintenanceRepository | Constructeur | Idem. |

## C. Messages d'erreur du conteneur (étape 3)

**Message 1 : `NoSuchBeanDefinitionException` (bean de type `IContratService` introuvable).**
Deux causes probables côté `ContratServiceImpl` : (1) l'annotation `@Service` a été oubliée ;
(2) la classe est placée en dehors du package racine scanné (hors de `tn.esprit.tpautoloc`).
Correction : ajouter `@Service` et vérifier le package.

**Message 2 : `NoUniqueBeanDefinitionException` (deux beans : `emailNotificateur` et `smsNotificateur`).**
Correction 1 : `@Primary` sur la **classe** du bean à utiliser par défaut (ex. `EmailNotificateur`).
Correction 2 : `@Qualifier("emailNotificateur")` sur le **paramètre du constructeur** de
`NotificationController`, pour un choix explicite à l'injection.

**Message 3 : dépendance circulaire (`clientServiceImpl` <-> `reservationServiceImpl`).**
Avec l'injection par constructeur, Spring doit créer chaque bean complet avant de le passer à l'autre :
`clientServiceImpl` a besoin de `reservationServiceImpl` pour exister et inversement, donc aucun des
deux ne peut être construit en premier (contrairement à l'injection par attribut/setter, qui permet de
créer l'objet puis d'injecter plus tard, et masque le problème).
Piste de conception : extraire la logique commune dans un troisième service (ou injecter directement
le repository nécessaire), de sorte que les dépendances aillent dans un seul sens.

## D. Anomalies SonarQube for IDE

| # | Règle | Explication | Correction |
|---|---|---|---|
| 1 | S6813 (à vérifier dans le panneau) | Injection par attribut avec `@Autowired` : champ non `final`, dépendance cachée. | Retour à l'injection par constructeur (`@RequiredArgsConstructor` + champ `final`). |
| 2 | S112 | Lever une `RuntimeException` générique dans `findById` : le type ne dit rien du problème. | Utiliser `ResourceNotFoundException`, exception métier explicite. |
| 3 | À compléter | À compléter après analyse du package `service` dans IntelliJ. | À compléter |

## E. Questions de compréhension

1. Le `new` de `ContratServiceImpl` est exécuté par le conteneur Spring (`ApplicationContext`) au
   démarrage, après détection de la classe annotée `@Service` par le scan. Aucun `new` n'est écrit dans notre code.
2. Un contrôleur dépend de `IContratService` (l'interface) et non de `ContratServiceImpl` pour rester
   couplé à une abstraction : on peut changer l'implémentation (ou fournir un faux service dans les
   tests) sans toucher au contrôleur.
3. Un service est un singleton partagé par tous les threads : un attribut modifiable serait partagé
   entre les requêtes. Exemple de bug : un attribut `contratCourant` rempli par `findById` ; deux
   requêtes simultanées s'écrasent mutuellement et l'une travaille sur le contrat de l'autre.
4. `update` charge d'abord l'entité existante puis recopie les attributs modifiables : passer
   directement l'objet reçu à `save` écraserait toutes les colonnes (valeurs absentes = `null`) et,
   avec `orphanRemoval`, pourrait vider la collection de paiements. Le chargement garantit aussi
   l'erreur `ResourceNotFoundException` si l'id n'existe pas.
5. `@Component` se pose sur une classe qu'on possède (détection par scan) ; `@Bean` se pose sur une
   méthode d'une classe `@Configuration` et sert pour une classe qu'on ne peut pas annoter
   (bibliothèque, JDK) ou une construction particulière. `@Primary` se pose sur la classe et désigne
   le candidat par défaut ; `@Qualifier` se pose à l'injection et choisit un bean par son nom.
   `IPaiementService` n'expose que la lecture car les paiements dépendent du Contrat (cascade +
   `orphanRemoval`) : leur création, modification et suppression passent par le Contrat.
