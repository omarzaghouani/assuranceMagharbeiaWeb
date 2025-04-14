package com.example.Assurance.Service;

import com.example.Assurance.Entity.Contract;
import com.example.Assurance.Entity.RiskAssessment;
import com.example.Assurance.Entity.User;
import com.example.Assurance.Repository.ContractRepository;
import com.example.Assurance.Repository.RiskAssessmentRepository;
import com.example.Assurance.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.List;

@Service
@Transactional
public class RiskAssessmentService {

    @Autowired
    private RiskAssessmentRepository riskAssessmentRepository;

    @Autowired
    private ContractRepository contractRepository;
    
    @Autowired
    private UserRepository userRepository;

    public RiskAssessment assessRisk(Long contractId, Map<String, Object> riskFactors) {
        Optional<Contract> contract = contractRepository.findById(contractId);
        if (!contract.isPresent()) {
            throw new RuntimeException("Contract not found");
        }

        // Calculer le score de risque en fonction du type de contrat
        double riskScore = calculateRiskScore(contract.get(), riskFactors);
        RiskAssessment.RiskLevel riskLevel = determineRiskLevel(riskScore);

        // Créer ou mettre à jour l'évaluation
        RiskAssessment assessment = new RiskAssessment();
        assessment.setContract(contract.get());
        assessment.setRiskScore(riskScore);
        assessment.setRiskLevel(riskLevel);
        assessment.setRiskFactors(riskFactors.toString());
        assessment.setAssessmentDate(LocalDateTime.now());

        return riskAssessmentRepository.save(assessment);
    }

    private double calculateRiskScore(Contract contract, Map<String, Object> factors) {
        double score = 0;
        
        if ("SANTE".equals(contract.getType())) {
            // Facteurs de risque pour l'assurance santé
            int age = (int) factors.get("age");
            boolean hasChronicDisease = (boolean) factors.get("hasChronicDisease");
            boolean isSmoking = (boolean) factors.get("smoking");

            score += age * 0.5; // Plus l'âge est élevé, plus le risque est grand
            if (hasChronicDisease) score += 30;
            if (isSmoking) score += 20;

        } else if ("AUTO".equals(contract.getType())) {
            // Facteurs de risque pour l'assurance auto
            int driverAge = (int) factors.get("driverAge");
            int carAge = (int) factors.get("carAge");
            int previousAccidents = (int) factors.get("previousAccidents");

            score += Math.abs(driverAge - 40) * 0.5; // L'âge optimal est autour de 40 ans
            score += carAge * 2; // Plus la voiture est vieille, plus le risque est grand
            score += previousAccidents * 15; // Chaque accident augmente significativement le risque
        }

        // Normaliser le score entre 0 et 100
        return Math.min(Math.max(score, 0), 100);
    }

    private RiskAssessment.RiskLevel determineRiskLevel(double score) {
        System.out.println("Determining risk level for score: " + score);
        if (score <= 40) {
            System.out.println("Risk level: VERT");
            return RiskAssessment.RiskLevel.VERT;
        } else if (score <= 80) {
            System.out.println("Risk level: ORANGE");
            return RiskAssessment.RiskLevel.ORANGE;
        } else {
            System.out.println("Risk level: ROUGE");
            return RiskAssessment.RiskLevel.ROUGE;
        }
    }

    public Optional<RiskAssessment> getRiskAssessment(Long contractId) {
        return riskAssessmentRepository.findByContractId(contractId);
    }

    public void deleteRiskAssessment(Long contractId) {
        riskAssessmentRepository.deleteByContractId(contractId);
    }
    
    // Méthode pour créer une évaluation autonome (sans contrat)
    public RiskAssessment createStandaloneAssessment(Map<String, Object> assessmentData) {
        try {
            System.out.println("Creating standalone assessment with data: " + assessmentData);
            
            if (assessmentData == null) {
                throw new IllegalArgumentException("Assessment data cannot be null");
            }

            if (!assessmentData.containsKey("contractType")) {
                throw new IllegalArgumentException("Contract type is required");
            }

            if (!assessmentData.containsKey("answers")) {
                throw new IllegalArgumentException("Answers are required");
            }
            
            // Récupérer l'utilisateur authentifié
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            System.out.println("Authenticated username: " + username);
            
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (!userOpt.isPresent()) {
                throw new RuntimeException("User not found: " + username);
            }
            User user = userOpt.get();
            System.out.println("Found user: " + user.getUsername() + " with ID: " + user.getIdUser());

            // Créer l'évaluation
            RiskAssessment assessment = new RiskAssessment();
            assessment.setUser(user);
            assessment.setContractType(assessmentData.get("contractType").toString());
            
            // Convertir les réponses en JSON string de manière sécurisée
            Object answers = assessmentData.get("answers");
            if (answers instanceof List) {
                System.out.println("Processing answers: " + answers);
                assessment.setRiskFactors(answers.toString());
                
                // Calculer le score de risque
                try {
                    double riskScore = calculateRiskScore(assessmentData);
                    System.out.println("Calculated risk score: " + riskScore);
                    assessment.setRiskScore(riskScore);
                    
                    // Déterminer le niveau de risque
                    RiskAssessment.RiskLevel riskLevel = determineRiskLevel(riskScore);
                    System.out.println("Determined risk level: " + riskLevel);
                    assessment.setRiskLevel(riskLevel);
                } catch (Exception e) {
                    System.err.println("Error calculating risk score: " + e.getMessage());
                    throw new RuntimeException("Failed to calculate risk score: " + e.getMessage());
                }
            } else {
                throw new IllegalArgumentException("Answers must be a list");
            }
            
            assessment.setAssessmentDate(LocalDateTime.now());
            assessment.setIsStandalone(true);
            
            System.out.println("Saving assessment: " + assessment);
            return riskAssessmentRepository.save(assessment);
            
        } catch (Exception e) {
            System.err.println("Error in createStandaloneAssessment: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create risk assessment: " + e.getMessage(), e);
        }
    }
    
    private double calculateRiskScore(Map<String, Object> assessmentData) {
        try {
            List<Map<String, Object>> answers = (List<Map<String, Object>>) assessmentData.get("answers");
            System.out.println("Calculating risk score for answers: " + answers);
            
            if (answers == null || answers.isEmpty()) {
                throw new IllegalArgumentException("No answers provided");
            }

            double totalRiskValue = 0;
            int validAnswersCount = 0;
            
            for (Map<String, Object> answer : answers) {
                System.out.println("Processing answer: " + answer);
                if (answer.containsKey("riskValue")) {
                    try {
                        double riskValue = Double.parseDouble(answer.get("riskValue").toString());
                        // Normaliser les valeurs élevées (10-25) vers une échelle 1-3
                        double normalizedValue;
                        if (riskValue <= 10) {
                            normalizedValue = 1; // Faible risque
                        } else if (riskValue <= 20) {
                            normalizedValue = 2; // Risque moyen
                        } else {
                            normalizedValue = 3; // Risque élevé
                        }
                        
                        // Convertir en pourcentage (1=10%, 2=50%, 3=90%)
                        double percentage = 10 + ((normalizedValue - 1) * 40);
                        totalRiskValue += percentage;
                        validAnswersCount++;
                        System.out.println("Original risk value: " + riskValue + ", Normalized: " + normalizedValue + ", Percentage: " + percentage + "%");
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid risk value format: " + answer.get("riskValue"));
                    }
                }
            }
            
            if (validAnswersCount == 0) {
                throw new IllegalArgumentException("No valid risk values found in answers");
            }
            
            // Calculer la moyenne des pourcentages
            double score = totalRiskValue / validAnswersCount;
            
            System.out.println("Total risk value: " + totalRiskValue);
            System.out.println("Number of answers: " + validAnswersCount);
            System.out.println("Final score: " + score);
            
            return score;
            
        } catch (ClassCastException e) {
            System.err.println("Invalid answers format: " + e.getMessage());
            throw new IllegalArgumentException("Invalid answers format: " + e.getMessage());
        }
    }
    
    public List<RiskAssessment> getAllRiskAssessments() {
        return riskAssessmentRepository.findAll();
    }
    
    public Optional<RiskAssessment> getLatestUserAssessment(String userId) {
        Optional<User> user = userRepository.findByUsername(userId);
        return user.flatMap(u -> riskAssessmentRepository.findTopByUserOrderByAssessmentDateDesc(u));
    }
} 