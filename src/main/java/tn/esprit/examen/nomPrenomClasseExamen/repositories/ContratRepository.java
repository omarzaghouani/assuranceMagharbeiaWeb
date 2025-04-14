package tn.esprit.examen.nomPrenomClasseExamen.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.examen.nomPrenomClasseExamen.entities.ContractStatus;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Contrat;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ContratRepository extends JpaRepository<Contrat, Long> {
    @Query("SELECT COUNT(c) FROM Contrat c WHERE c.status = :status")
    long countByStatus(@Param("status") ContractStatus status);
    @Query("SELECT c FROM Contrat c WHERE c.endDate BETWEEN :startDate AND :endDate AND c.status = :status")
    List<Contrat> findContractsExpiringSoon(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("status") ContractStatus status);
    Optional<Contrat> findByNumber(String number);

}
