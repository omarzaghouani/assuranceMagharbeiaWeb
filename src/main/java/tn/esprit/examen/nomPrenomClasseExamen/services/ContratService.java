package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.springframework.web.server.ResponseStatusException;
import tn.esprit.examen.nomPrenomClasseExamen.entities.ContractStatus;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Contrat;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.ContratRepository;


import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
@AllArgsConstructor
@Service
public class ContratService<T> implements IContratService {
     ContratRepository contractRepository;

    @Override
    public Contrat addContract(Contrat contrat) {
        return contractRepository.save(contrat);
    }

    @Override
    public List<Contrat> getAllContracts() {
        return contractRepository.findAll();
    }

    @Override
    public Optional<Contrat> getContractById(Long id) {
        return contractRepository.findById(id);
    }

    @Override
    public Contrat updateContract(Long id, Contrat updatedContract) {
        return contractRepository.findById(id).map(contract -> {
            contract.setNumber(updatedContract.getNumber());
            contract.setStartDate(updatedContract.getStartDate());
            contract.setEndDate(updatedContract.getEndDate());
            contract.setStatus(updatedContract.getStatus());
            return contractRepository.save(contract);
        }).orElseThrow(() -> new RuntimeException("Contrat non trouvé avec ID: " + id));
    }

    @Override
    public void deleteContract(Long id) {
        contractRepository.deleteById(id);
    }

    @Override
    public long countContractsByStatus(String status) {
        try {
            ContractStatus contractStatus = ContractStatus.valueOf(status.toUpperCase());
            return contractRepository.countByStatus(contractStatus);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid contract status: " + status);
        }

    }
    @Scheduled(cron = "0 0 12 * * ?")
    @Override
    public void notifyClientsAboutExpiringContracts() {




    }

    @Override
    public List<Contrat> getExpiringContracts() {
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);


        Date startDate = java.sql.Date.valueOf(today);
        Date endDate = java.sql.Date.valueOf(nextWeek);


        return contractRepository.findContractsExpiringSoon(startDate, endDate, ContractStatus.ONGOING);
    }

    @Override
    public Optional<Contrat> findByNumber(String number) {
        return contractRepository.findByNumber(number);
    }
}

