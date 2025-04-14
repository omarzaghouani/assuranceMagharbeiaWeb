package com.example.Assurance.Controlleur;

import com.example.Assurance.Entity.RiskAssessment;
import com.example.Assurance.Service.RiskAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/risk-assessments")
@CrossOrigin(origins = "*")
public class RiskAssessmentController {

    @Autowired
    private RiskAssessmentService riskAssessmentService;

    @PostMapping("/assess/{contractId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RiskAssessment> assessRisk(
            @PathVariable Long contractId,
            @RequestBody Map<String, Object> riskFactors) {
        try {
            RiskAssessment assessment = riskAssessmentService.assessRisk(contractId, riskFactors);
            return ResponseEntity.ok(assessment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/contract/{contractId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT_PHYSIQUE', 'CLIENT_MORAL')")
    public ResponseEntity<RiskAssessment> getRiskAssessment(@PathVariable Long contractId) {
        return riskAssessmentService.getRiskAssessment(contractId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/contract/{contractId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRiskAssessment(@PathVariable Long contractId) {
        riskAssessmentService.deleteRiskAssessment(contractId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/standalone")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT_PHYSIQUE', 'CLIENT_MORAL')")
    public ResponseEntity<RiskAssessment> createStandaloneAssessment(@RequestBody Map<String, Object> assessmentData) {
        try {
            System.out.println("Received standalone assessment request: " + assessmentData);
            RiskAssessment assessment = riskAssessmentService.createStandaloneAssessment(assessmentData);
            return ResponseEntity.ok(assessment);
        } catch (Exception e) {
            System.err.println("Error creating standalone assessment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RiskAssessment>> getAllRiskAssessments() {
        List<RiskAssessment> assessments = riskAssessmentService.getAllRiskAssessments();
        return ResponseEntity.ok(assessments);
    }
    
    @GetMapping("/user/{userId}/latest")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT_PHYSIQUE', 'CLIENT_MORAL')")
    public ResponseEntity<RiskAssessment> getLatestUserRiskAssessment(@PathVariable String userId) {
        return riskAssessmentService.getLatestUserAssessment(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 