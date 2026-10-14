package tn.esprit.tpautoloc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.tpautoloc.domain.*;
import tn.esprit.tpautoloc.domain.enums.*;
import tn.esprit.tpautoloc.exception.ResourceNotFoundException;
import tn.esprit.tpautoloc.service.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Atelier 4 - Part 2 : tests des services (IoC / injection de dépendances).
 * On passe uniquement par la couche Service : aucun repository n'est injecté ici.
 */
@SpringBootTest
class nServiceTests {

    @Autowired
    private IAgenceService agenceService;

    @Autowired
    private IClientService clientService;

    @Autowired
    private IVehiculeService vehiculeService;

    // Partie 5 : trois services choisis pour l'exercice autonome
    @Autowired
    private IEmployeService employeService;

    @Autowired
    private IEquipementService equipementService;

    @Autowired
    private IMaintenanceService maintenanceService;

    // ------------------------------------------------------------------
    // Partie 2 : AgenceService
    // ------------------------------------------------------------------
    @Test
    void testAgenceService() {
        long avant = agenceService.count();

        // create()
        Agence agence = new Agence();
        agence.setNom("Agence Sfax Centre");
        agence.setVille("Sfax");
        agence.setAdresse("Route de Tunis, km 2");
        agence.setTelephone("74000000");

        Agence saved = agenceService.create(agence);
        System.out.println("Agence enregistrée : id=" + saved.getIdAgence()
                + ", nom=" + saved.getNom() + ", ville=" + saved.getVille());
        assertNotNull(saved.getIdAgence());
        assertEquals("Sfax", saved.getVille());
        Long id = saved.getIdAgence();

        // findAll()
        List<Agence> agences = agenceService.findAll();
        agences.forEach(a -> System.out.println("Agence : " + a.getIdAgence() + " - " + a.getNom()));
        assertFalse(agences.isEmpty());

        // findById()
        Agence found = agenceService.findById(id);
        System.out.println("findById : " + found.getNom());
        assertNotNull(found);
        assertEquals("Agence Sfax Centre", found.getNom());

        // count()
        long apres = agenceService.count();
        System.out.println("Nombre d'agences : " + apres);
        assertEquals(avant + 1, apres);

        // update()
        Agence modif = new Agence();
        modif.setNom("Agence Sfax Nord");
        modif.setVille("Sfax");
        modif.setAdresse("Avenue Hedi Chaker");
        modif.setTelephone("74111111");
        Agence updated = agenceService.update(id, modif);
        System.out.println("Après modification : " + updated.getNom() + ", " + updated.getAdresse());
        assertEquals("Agence Sfax Nord", updated.getNom());
        assertEquals("74111111", agenceService.findById(id).getTelephone());

        // deleteById() + vérification
        agenceService.deleteById(id);
        System.out.println("Agence supprimée : " + id);
        assertFalse(agenceService.existsById(id));
        assertThrows(ResourceNotFoundException.class, () -> agenceService.findById(id));
        assertEquals(avant, agenceService.count());
    }

    // ------------------------------------------------------------------
    // Partie 3 : ClientService
    // ------------------------------------------------------------------
    @Test
    void testClientService() {
        long avant = clientService.count();

        Client c1 = new Client();
        c1.setNom("Ben Salah");
        c1.setPrenom("Mohamed");
        c1.setEmail("mohamed.bensalah@example.com");
        c1.setTelephone("21111111");
        c1.setNumPermis("S-2001");
        c1.setDateInscription(LocalDate.now());

        Client c2 = new Client();
        c2.setNom("Jlassi");
        c2.setPrenom("Yasmine");
        c2.setEmail("yasmine.jlassi@example.com");
        c2.setTelephone("21222222");
        c2.setNumPermis("S-2002");
        c2.setDateInscription(LocalDate.now());

        // create() x2
        Client s1 = clientService.create(c1);
        Client s2 = clientService.create(c2);
        System.out.println("Clients enregistrés : " + s1.getIdClient() + ", " + s2.getIdClient());
        assertNotNull(s1.getIdClient());
        assertNotNull(s2.getIdClient());
        assertNotEquals(s1.getIdClient(), s2.getIdClient());

        // findAll()
        List<Client> clients = clientService.findAll();
        clients.forEach(c -> System.out.println("Client : " + c.getIdClient()
                + " - " + c.getNom() + " " + c.getPrenom()));
        assertTrue(clients.size() >= 2);

        // findById()
        Client found = clientService.findById(s1.getIdClient());
        System.out.println("findById : " + found.getNom() + " " + found.getPrenom());
        assertEquals("Ben Salah", found.getNom());
        assertEquals("mohamed.bensalah@example.com", found.getEmail());

        // count()
        long apres = clientService.count();
        System.out.println("Nombre de clients : " + apres);
        assertEquals(avant + 2, apres);

        // update()
        Client modif = new Client();
        modif.setNom("Ben Salah");
        modif.setPrenom("Mohamed");
        modif.setEmail("m.bensalah@example.com");
        modif.setTelephone("21333333");
        modif.setNumPermis("S-2001");
        modif.setDateInscription(found.getDateInscription());
        Client updated = clientService.update(s1.getIdClient(), modif);
        System.out.println("Après modification : " + updated.getEmail() + ", " + updated.getTelephone());
        assertEquals("m.bensalah@example.com", updated.getEmail());

        // deleteById() + vérification
        clientService.deleteById(s1.getIdClient());
        System.out.println("Client supprimé : " + s1.getIdClient());
        assertFalse(clientService.existsById(s1.getIdClient()));
        assertThrows(ResourceNotFoundException.class, () -> clientService.findById(s1.getIdClient()));

        // nettoyage du second client
        clientService.deleteById(s2.getIdClient());
        assertEquals(avant, clientService.count());
    }

    // ------------------------------------------------------------------
    // Partie 4 : VehiculeService
    // ------------------------------------------------------------------
    @Test
    void testVehiculeService() {
        long avant = vehiculeService.count();

        // immatriculation unique (colonne unique en base) : suffixe horodaté
        String immat = "SVC-" + System.currentTimeMillis();

        Vehicule v = new Vehicule();
        v.setImmatriculation(immat);
        v.setMarque("Peugeot");
        v.setModele("208");
        v.setCategorie(CategorieVehicule.CITADINE);
        v.setTarifJournalier(new BigDecimal("75.00"));
        v.setStatut(StatutVehicule.DISPONIBLE);

        // create()
        Vehicule saved = vehiculeService.create(v);
        Long id = saved.getIdVehicule();
        System.out.println("Véhicule enregistré : id=" + id + ", "
                + saved.getMarque() + " " + saved.getModele() + " (" + saved.getImmatriculation() + ")");
        assertNotNull(id);

        // findAll()
        vehiculeService.findAll().forEach(x -> System.out.println("Véhicule : "
                + x.getIdVehicule() + " - " + x.getImmatriculation()));
        assertFalse(vehiculeService.findAll().isEmpty());

        // findById()
        Vehicule found = vehiculeService.findById(id);
        System.out.println("findById : " + found.getMarque() + " " + found.getModele());
        assertEquals("Peugeot", found.getMarque());
        assertEquals(CategorieVehicule.CITADINE, found.getCategorie());

        // count()
        long apres = vehiculeService.count();
        System.out.println("Nombre de véhicules : " + apres);
        assertEquals(avant + 1, apres);

        // update()
        Vehicule modif = new Vehicule();
        modif.setImmatriculation(immat);
        modif.setMarque("Peugeot");
        modif.setModele("208");
        modif.setCategorie(CategorieVehicule.CITADINE);
        modif.setTarifJournalier(new BigDecimal("90.00"));
        modif.setStatut(StatutVehicule.LOUE);
        Vehicule updated = vehiculeService.update(id, modif);
        System.out.println("Après modification : " + updated.getStatut() + ", " + updated.getTarifJournalier());
        assertEquals(StatutVehicule.LOUE, updated.getStatut());
        assertEquals(0, new BigDecimal("90.00").compareTo(updated.getTarifJournalier()));

        // deleteById() + vérification
        vehiculeService.deleteById(id);
        System.out.println("Véhicule supprimé : " + id);
        assertFalse(vehiculeService.existsById(id));
        assertThrows(ResourceNotFoundException.class, () -> vehiculeService.findById(id));
        assertEquals(avant, vehiculeService.count());
    }

    // ------------------------------------------------------------------
    // Partie 5 : exercice autonome (Employe, Equipement, Maintenance)
    // ------------------------------------------------------------------
    @Test
    void testEmployeService() {
        Employe e = new Employe();
        e.setNom("Mansouri");
        e.setPrenom("Hichem");
        e.setRole(RoleEmploye.MANAGER);

        Employe saved = employeService.create(e);
        Long id = saved.getIdEmploye();
        System.out.println("Employé enregistré : " + id + " - " + saved.getNom() + " (" + saved.getRole() + ")");
        assertNotNull(id);

        employeService.findAll().forEach(x -> System.out.println("Employé : "
                + x.getIdEmploye() + " - " + x.getNom() + " " + x.getPrenom()));
        Employe found = employeService.findById(id);
        assertEquals(RoleEmploye.MANAGER, found.getRole());
        assertTrue(employeService.existsById(id));

        employeService.deleteById(id);
        System.out.println("Employé supprimé : " + id);
        assertFalse(employeService.existsById(id));
        assertThrows(ResourceNotFoundException.class, () -> employeService.findById(id));
    }

    @Test
    void testEquipementService() {
        Equipement e = new Equipement();
        e.setLibelle("Siège bébé");

        Equipement saved = equipementService.create(e);
        Long id = saved.getIdEquipement();
        System.out.println("Équipement enregistré : " + id + " - " + saved.getLibelle());
        assertNotNull(id);

        equipementService.findAll().forEach(x -> System.out.println("Équipement : "
                + x.getIdEquipement() + " - " + x.getLibelle()));
        assertEquals("Siège bébé", equipementService.findById(id).getLibelle());
        assertTrue(equipementService.existsById(id));

        equipementService.deleteById(id);
        System.out.println("Équipement supprimé : " + id);
        assertFalse(equipementService.existsById(id));
        assertThrows(ResourceNotFoundException.class, () -> equipementService.findById(id));
    }

    @Test
    void testMaintenanceService() {
        Maintenance m = new Maintenance();
        m.setDateDebut(LocalDate.now());
        m.setDateFin(LocalDate.now().plusDays(3));
        m.setDescription("Changement des pneus");

        Maintenance saved = maintenanceService.create(m);
        Long id = saved.getIdMaintenance();
        System.out.println("Maintenance enregistrée : " + id + " - " + saved.getDescription());
        assertNotNull(id);

        maintenanceService.findAll().forEach(x -> System.out.println("Maintenance : "
                + x.getIdMaintenance() + " - " + x.getDescription()));
        assertEquals("Changement des pneus", maintenanceService.findById(id).getDescription());
        assertTrue(maintenanceService.existsById(id));

        maintenanceService.deleteById(id);
        System.out.println("Maintenance supprimée : " + id);
        assertFalse(maintenanceService.existsById(id));
        assertThrows(ResourceNotFoundException.class, () -> maintenanceService.findById(id));
    }

    // ------------------------------------------------------------------
    // Bonus : la couche Service applique ses règles de validation
    // ------------------------------------------------------------------
    @Test
    void testValidationService() {
        // nom obligatoire
        assertThrows(IllegalArgumentException.class, () -> agenceService.create(new Agence()));

        // un nouvel objet ne doit pas déjà avoir d'identifiant
        Agence avecId = new Agence();
        avecId.setIdAgence(1L);
        avecId.setNom("Agence X");
        assertThrows(IllegalArgumentException.class, () -> agenceService.create(avecId));

        // id inexistant
        assertThrows(ResourceNotFoundException.class, () -> clientService.findById(999999L));
    }
}
