package com.example.Assurance.Controlleur;

import com.example.Assurance.Entity.Contract;
import com.example.Assurance.Service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@CrossOrigin(origins = "*")
public class ContractController {

    @Autowired
    private ContractService contractService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Contract> createContract(@RequestBody Contract contract) {
        Contract newContract = contractService.createContract(contract);
        return ResponseEntity.ok(newContract);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT_PHYSIQUE', 'CLIENT_MORAL')")
    public ResponseEntity<Contract> getContract(@PathVariable Long id) {
        return contractService.getContract(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT_PHYSIQUE', 'CLIENT_MORAL')")
    public ResponseEntity<List<Contract>> getUserContracts(@PathVariable Long userId) {
        List<Contract> contracts = contractService.getUserContracts(userId);
        return ResponseEntity.ok(contracts);
    }

    @GetMapping("/user/{userId}/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT_PHYSIQUE', 'CLIENT_MORAL')")
    public ResponseEntity<List<Contract>> getUserActiveContracts(@PathVariable Long userId) {
        List<Contract> contracts = contractService.getUserActiveContracts(userId);
        return ResponseEntity.ok(contracts);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Contract> updateContract(@PathVariable Long id, @RequestBody Contract contractDetails) {
        Contract updatedContract = contractService.updateContract(id, contractDetails);
        if (updatedContract == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedContract);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteContract(@PathVariable Long id) {
        if (contractService.deleteContract(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Contract>> getAllContracts() {
        List<Contract> contracts = contractService.getAllContracts();
        return ResponseEntity.ok(contracts);
    }
} 