package tn.esprit.tpautoloc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.tpautoloc.domain.*;
import tn.esprit.tpautoloc.domain.enums.*;
import tn.esprit.tpautoloc.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RepositoryTests {

    @Autowired
    private IAgenceRepository agenceRepository;

    @Autowired
    private IClientRepository clientRepository;

    @Autowired
    private IVehiculeRepository vehiculeRepository;

    @Autowired
    private IEquipementRepository equipementRepository;

    @Autowired
    private IEmployeRepository employeRepository;

    @Autowired
    private IMaintenanceRepository maintenanceRepository;


    @Test
    void testAgenceRepository() {
        Agence agence = new Agence();
        agence.setNom("Agence Tunis Centre");
        agence.setVille("Tunis");
        agence.setAdresse("Avenue Habib Bourguiba");
        agence.setTelephone("71000000");


        Agence saved = agenceRepository.save(agence);
        System.out.println("Agence enregistrée : id=" + saved.getIdAgence() + ", nom=" + saved.getNom());
        assertNotNull(saved.getIdAgence());


        List<Agence> agences = agenceRepository.findAll();
        agences.forEach(a -> System.out.println("Agence : " + a.getIdAgence() + " - " + a.getNom()));
        assertFalse(agences.isEmpty());


        Long id = saved.getIdAgence();
        Optional<Agence> found = agenceRepository.findById(id);
        System.out.println("findById : " + found.map(Agence::getNom).orElse("introuvable"));
        assertTrue(found.isPresent());
        assertEquals("Tunis", found.get().getVille());


        assertTrue(agenceRepository.existsById(id));
        System.out.println("Nombre d'agences : " + agenceRepository.count());


        agenceRepository.deleteById(id);
        assertFalse(agenceRepository.existsById(id));
        assertTrue(agenceRepository.findById(id).isEmpty());
        System.out.println("Agence supprimée : " + id);
    }
    @Test
    void testClientRepository() {
        long avant = clientRepository.count();

        Client c1 = new Client();
        c1.setNom("Ben Ali");
        c1.setPrenom("Ahmed");
        c1.setEmail("ahmed.benali@example.com");
        c1.setTelephone("20111111");
        c1.setNumPermis("P-1001");
        c1.setDateInscription(LocalDate.now());

        Client c2 = new Client();
        c2.setNom("Trabelsi");
        c2.setPrenom("Sarra");
        c2.setEmail("sarra.trabelsi@example.com");
        c2.setTelephone("20222222");
        c2.setNumPermis("P-1002");
        c2.setDateInscription(LocalDate.now());

        Client s1 = clientRepository.save(c1);
        Client s2 = clientRepository.save(c2);
        System.out.println("Clients enregistrés : " + s1.getIdClient() + ", " + s2.getIdClient());

        List<Client> clients = clientRepository.findAll();
        clients.forEach(c -> System.out.println("Client : " + c.getIdClient() + " - " + c.getNom() + " " + c.getPrenom()));

        Optional<Client> found = clientRepository.findById(s1.getIdClient());
        assertTrue(found.isPresent());
        assertEquals("Ben Ali", found.get().getNom());

        assertTrue(clientRepository.existsById(s1.getIdClient()));
        assertFalse(clientRepository.existsById(999999L));

        long apres = clientRepository.count();
        System.out.println("Nombre de clients : " + apres);
        assertEquals(avant + 2, apres);
    }
    @Test
    void testVehiculeRepository() {
        Vehicule v = new Vehicule();
        v.setImmatriculation("123 TUN 4567");
        v.setMarque("Renault");
        v.setModele("Clio");
        v.setCategorie(CategorieVehicule.CITADINE);
        v.setTarifJournalier(new BigDecimal("80.00"));
        v.setStatut(StatutVehicule.DISPONIBLE);

        Vehicule saved = vehiculeRepository.save(v);
        Long id = saved.getIdVehicule();
        System.out.println("Véhicule enregistré : id=" + id + ", " + saved.getMarque() + " " + saved.getModele());
        assertNotNull(id);

        vehiculeRepository.findAll().forEach(x ->
                System.out.println("Véhicule : " + x.getIdVehicule() + " - " + x.getImmatriculation()));
        System.out.println("Nombre de véhicules : " + vehiculeRepository.count());

        // update
        saved.setStatut(StatutVehicule.LOUE);
        saved.setTarifJournalier(new BigDecimal("95.00"));
        vehiculeRepository.save(saved);

        Vehicule modifie = vehiculeRepository.findById(id).orElseThrow();
        System.out.println("Après modification : " + modifie.getStatut() + ", " + modifie.getTarifJournalier());
        assertEquals(StatutVehicule.LOUE, modifie.getStatut());
        assertEquals(0, new BigDecimal("95.00").compareTo(modifie.getTarifJournalier()));

        vehiculeRepository.deleteById(id);
        assertFalse(vehiculeRepository.existsById(id));
    }
    @Test
    void testEquipementRepository() {
        Equipement e = new Equipement();
        e.setLibelle("GPS");

        Equipement saved = equipementRepository.save(e);
        Long id = saved.getIdEquipement();
        System.out.println("Équipement enregistré : " + id + " - " + saved.getLibelle());
        assertNotNull(id);

        equipementRepository.findAll().forEach(x -> System.out.println("Équipement : " + x.getLibelle()));
        assertTrue(equipementRepository.existsById(id));

        equipementRepository.deleteById(id);
        assertFalse(equipementRepository.existsById(id));
    }

    @Test
    void testEmployeRepository() {
        Employe e = new Employe();
        e.setNom("Gharbi");
        e.setPrenom("Karim");
        e.setRole(RoleEmploye.AGENT);

        Employe saved = employeRepository.save(e);
        Long id = saved.getIdEmploye();
        System.out.println("Employé enregistré : " + id + " - " + saved.getNom() + " (" + saved.getRole() + ")");
        assertNotNull(id);

        Optional<Employe> found = employeRepository.findById(id);
        assertTrue(found.isPresent());
        assertEquals(RoleEmploye.AGENT, found.get().getRole());
        assertTrue(employeRepository.existsById(id));

        employeRepository.deleteById(id);
        assertFalse(employeRepository.existsById(id));
    }

    @Test
    void testMaintenanceRepository() {
        Maintenance m = new Maintenance();
        m.setDateDebut(LocalDate.now());
        m.setDateFin(LocalDate.now().plusDays(2));
        m.setDescription("Vidange et contrôle des freins");

        Maintenance saved = maintenanceRepository.save(m);
        Long id = saved.getIdMaintenance();
        System.out.println("Maintenance enregistrée : " + id + " - " + saved.getDescription());
        assertNotNull(id);

        maintenanceRepository.findAll().forEach(x -> System.out.println("Maintenance : " + x.getDescription()));
        assertTrue(maintenanceRepository.existsById(id));

        maintenanceRepository.deleteById(id);
        assertFalse(maintenanceRepository.existsById(id));
    }
}
