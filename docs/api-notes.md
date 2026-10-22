# Atelier 5 – Notes API

## A. Endpoints
| Contrôleur | Verbe et route | Service appelé | Succès | Erreurs |
|---|---|---|---|---|
| <Entité>Controller (Contrat, Agence, Employe, Vehicule, Equipement, Client, Reservation, Maintenance) | GET /api/<ressources> | findAll() | 200 | — |
| idem | GET /api/<ressources>/{id} | findById(id) | 200 | 404 |
| idem | POST /api/<ressources> | create(x) | 201 | 400 |
| idem | PUT /api/<ressources>/{id} | update(id, x) | 200 | 404, 400 |
| idem | DELETE /api/<ressources>/{id} | deleteById(id) | 204 | 404 |
| PaiementController | GET /api/paiements | findAll() | 200 | — |
| PaiementController | GET /api/paiements/{id} | findById(id) | 200 | 404 |

(Développez en une ligne par endpoint et par contrôleur pour le rendu final.)

## B. Annotations Jackson
| Association | Annotation | Justification |
|---|---|---|
| Contrat.paiements / Paiement.contrat | @JsonManagedReference / @JsonBackReference("contrat-paiements") | Composition : les paiements sont créés dans le JSON du contrat (cascade ALL) |
| Contrat.reservation | @JsonIgnore | Côté inverse du OneToOne, évite Contrat ↔ Reservation à l'infini |
| Agence.employes, Agence.vehicules | @JsonIgnore | Enfants créés seuls, désignant l'agence par id |
| Client.reservations | @JsonIgnore | Idem (pas de BackReference sur Reservation.client) |
| Vehicule.reservations, Vehicule.maintenances | @JsonIgnore | Idem |
| Equipement.vehicules | @JsonIgnore | Côté inverse du ManyToMany |

Différence : @JsonBackReference masque le côté enfant→parent à la sérialisation mais
doit être associé à un @JsonManagedReference (le lien est reconstruit à la lecture du JSON).
@JsonIgnore exclut totalement le champ, sans reconstruction du lien.

## C. Postman et Swagger
- Postman : à compléter (ex. test du 404 sur /contrats/9999, scénario de cascade Contrat/Paiement).
- Swagger : à compléter (ex. Try it out sur POST /contrats, section Schemas).

## D. Anomalies SonarQube
| Règle | Explication | Correction |
|---|---|---|
| à compléter après l'analyse | | |
| à compléter après l'analyse | | |

## E. Questions
1. Le DispatcherServlet reçoit toutes les requêtes ; le HandlerMapping retrouve la méthode à partir du verbe HTTP et de l'URL.
2. Dépendre de l'interface = couplage faible, et le contrôleur ne doit pas contourner la couche service (ni accéder au repository).
3. 201 : ressource créée ; 204 : succès sans corps. Une ressource absente lève ResourceNotFoundException, traduite en 404 par le GlobalExceptionHandler (sinon 500).
4. Les paiements sont gérés via le Contrat (cascade) : pas de POST, donc 405 Method Not Allowed.
5. Risques : couplage API/base, champs internes exposés, boucles de sérialisation, proxys LAZY. Contrat.paiements est une composition (paire Managed/Back) ; Agence.vehicules est un enfant créé seul, donc @JsonIgnore suffit.
