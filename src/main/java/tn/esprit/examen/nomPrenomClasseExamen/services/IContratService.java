package tn.esprit.examen.nomPrenomClasseExamen.services;



import tn.esprit.examen.nomPrenomClasseExamen.entities.Contrat;

import java.util.List;
import java.util.Optional;

public interface IContratService {
    // Create or update contract
    Contrat addContract(Contrat contrat);

    // Get all contracts
    List<Contrat> getAllContracts();

    // Get contract by ID
    Optional<Contrat> getContractById(Long id);
    Contrat updateContract(Long id, Contrat updatedContract);
    // Delete contract
    void deleteContract(Long id);
    long countContractsByStatus(String status);
    void notifyClientsAboutExpiringContracts();
    List<Contrat> getExpiringContracts();
    Optional<Contrat> findByNumber(String number);

}
