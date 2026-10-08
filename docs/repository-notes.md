# Notes Atelier 3 : couche Repository

## Choix des interfaces

| Interface | Étend | Justification |
|---|---|---|
| IContratRepository | JpaRepository<Contrat, Long> | CRUD complet, findAll renvoie une List, saveAndFlush disponible. |
| IAgenceRepository | JpaRepository<Agence, Long> | Même choix pour toutes les entités : CRUD, listes, tri, pagination et flush. |
| IEmployeRepository | JpaRepository<Employe, Long> | Idem |
| IVehiculeRepository | JpaRepository<Vehicule, Long> | Idem |
| IEquipementRepository | JpaRepository<Equipement, Long> | Idem |
| IClientRepository | JpaRepository<Client, Long> | Idem |
| IReservationRepository | JpaRepository<Reservation, Long> | Idem |
| IPaiementRepository | JpaRepository<Paiement, Long> | Idem. Créé pour pouvoir lire les paiements, la création et le retrait passent par le Contrat. |
| IMaintenanceRepository | JpaRepository<Maintenance, Long> | Idem |

## Anomalies SonarQube for IDE

Non réalisé : l'installation du plugin a été dispensée par l'enseignant (téléchargement trop long).