package com.example.Assurance.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "risk_assessments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RiskAssessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    @JsonIgnoreProperties({"riskAssessments", "user"})
    private Contract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"contracts", "riskAssessments"})
    private User user;

    @Column(name = "contract_type")
    private String contractType;

    @Column(name = "risk_score")
    private double riskScore; // Score de 0 à 100

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level")
    private RiskLevel riskLevel; // VERT, ORANGE, ROUGE

    @Column(name = "risk_factors", columnDefinition = "TEXT")
    private String riskFactors; // Stocké en JSON

    @Column(name = "assessment_date")
    private LocalDateTime assessmentDate;

    @Column(name = "is_standalone")
    private Boolean isStandalone = false;

    public enum RiskLevel {
        VERT,    // Risque faible
        ORANGE,  // Risque moyen
        ROUGE    // Risque élevé
    }

    @PrePersist
    protected void onCreate() {
        assessmentDate = LocalDateTime.now();
    }
} 