package tn.esprit.tpautoloc.service;

import java.util.List;
import tn.esprit.tpautoloc.domain.Paiement;

/**
 * Lecture seule : les paiements sont créés, modifiés et supprimés via le Contrat.
 */
public interface IPaiementService {

    Paiement findById(Long id);

    List<Paiement> findAll();
}
