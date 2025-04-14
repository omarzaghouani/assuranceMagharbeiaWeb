package tn.esprit.examen.nomPrenomClasseExamen.services;

import tn.esprit.examen.nomPrenomClasseExamen.entities.Reclamation;
import tn.esprit.examen.nomPrenomClasseExamen.entities.StatutReclamation;

import java.util.List;

public interface ReclamationService {
    Reclamation createReclamation(Reclamation reclamation);
    List<Reclamation> getAllReclamations();
    Reclamation updateReclamation(int id, Reclamation updatedReclamation);
    void deleteReclamation(int id);
    Reclamation updateStatutReclamation(int id, StatutReclamation statutReclamation);
    Reclamation getRecById(int id);

}
