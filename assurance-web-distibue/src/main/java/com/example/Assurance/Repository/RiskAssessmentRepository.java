package com.example.Assurance.Repository;

import com.example.Assurance.Entity.RiskAssessment;
import com.example.Assurance.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RiskAssessmentRepository extends JpaRepository<RiskAssessment, Long> {
    Optional<RiskAssessment> findByContractId(Long contractId);
    void deleteByContractId(Long contractId);
    List<RiskAssessment> findByUserIdUser(Long userId);
    List<RiskAssessment> findByUserUsername(String username);
    Optional<RiskAssessment> findTopByUserOrderByAssessmentDateDesc(User user);
    List<RiskAssessment> findByIsStandaloneTrue();
} 