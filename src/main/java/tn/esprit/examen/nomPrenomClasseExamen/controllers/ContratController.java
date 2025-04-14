package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
        import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Contrat;
import tn.esprit.examen.nomPrenomClasseExamen.services.IContratService;
import tn.esprit.examen.nomPrenomClasseExamen.services.IEmailService;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/contrats")
public class ContratController {

    private static final Logger log = LoggerFactory.getLogger(ContratController.class);

    @Autowired
    private IContratService contratService;

    @Autowired
    private IEmailService emailService;

    @PostMapping("/add")
    public ResponseEntity<Contrat> createContract(@RequestBody Contrat contract) {
        if (contract == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The contract cannot be null");
        }

        Contrat createdContract = contratService.addContract(contract);

        if (createdContract.getClientEmail() != null && !createdContract.getClientEmail().isEmpty()) {
            try {
                emailService.sendConfirmationEmail(
                        createdContract.getClientEmail(),
                        "Confirmation of your contract",
                        "Hello,\n\nYour contract has been successfully created.\n" +
                                "Contract number: " + createdContract.getNumber() + "\n" +
                                "Contract status: " + createdContract.getStatus() + "\n" +
                                "Contract Start Date: " + createdContract.getStartDate() + "\n" +
                                "Contract End Date: " + createdContract.getEndDate() + "\n\n" +
                                "Thank you for placing your trust in us."
                );
            } catch (MessagingException e) {
                log.error("Échec de l'envoi de l'e-mail pour le contrat {}", createdContract.getNumber(), e);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de l'envoi de l'e-mail");
            }
        } else {
            log.warn("Aucun e-mail trouvé pour le contrat {}", createdContract.getNumber());
        }

        return new ResponseEntity<>(createdContract, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Contrat>> getAllContracts() {
        log.info("✅ [GET] /api/contrats/all hit successfully !");
        List<Contrat> contrats = contratService.getAllContracts();
        return new ResponseEntity<>(contrats, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contrat> getContractById(@PathVariable Long id) {
        Optional<Contrat> contrat = contratService.getContractById(id);
        return contrat.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Contrat> updateContract(@PathVariable Long id, @RequestBody Contrat contrat) {
        try {
            Contrat updatedContract = contratService.updateContract(id, contrat);
            return new ResponseEntity<>(updatedContract, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContract(@PathVariable Long id) {
        contratService.deleteContract(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping("/count/{status}")
    public long getContractCountByStatus(@PathVariable String status) {
        return contratService.countContractsByStatus(status);
    }
    @PostMapping("/notify-expiring")
    public ResponseEntity<String> notifyExpiringContracts() {
        try {
            contratService.notifyClientsAboutExpiringContracts();
            return ResponseEntity.ok("Notifications envoyées !");
        } catch (Exception e) {
            log.error("Error while sending expiring contract notifications", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur est survenue lors de l'envoi des notifications.");
        }

    }
    @GetMapping("/expiring")
    public ResponseEntity<List<Contrat>> getExpiringContracts() {
        return ResponseEntity.ok(contratService.getExpiringContracts());
    }
    @GetMapping("/search-by-number/{number}")
    public ResponseEntity<Contrat> getContractByNumber(@PathVariable String number) {
        Optional<Contrat> contrat = contratService.findByNumber(number);
        return contrat.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
