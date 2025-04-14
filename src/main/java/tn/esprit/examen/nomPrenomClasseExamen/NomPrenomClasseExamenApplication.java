package tn.esprit.examen.nomPrenomClasseExamen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Contrat;
import tn.esprit.examen.nomPrenomClasseExamen.entities.ContractStatus;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.ContratRepository;

import java.util.Date;

@EnableAspectJAutoProxy
@EnableScheduling
@SpringBootApplication
@EnableDiscoveryClient
public class NomPrenomClasseExamenApplication {

    private static final Logger log = LoggerFactory.getLogger(NomPrenomClasseExamenApplication.class);

    private final ContratRepository contratRepository;

    public NomPrenomClasseExamenApplication(ContratRepository contratRepository) {
        this.contratRepository = contratRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(NomPrenomClasseExamenApplication.class, args);
    }

    @Bean
    ApplicationRunner init() {
        return (args) -> {
            log.info("Initialisation des contrats...");

            // Sauvegarde des contrats avec un email valide
            contratRepository.save(new Contrat("Service Financier", new Date(), new Date(System.currentTimeMillis() + 100000000L), ContractStatus.ONGOING, "client1@example.com"));
            contratRepository.save(new Contrat("Service Info", new Date(), new Date(System.currentTimeMillis() + 100000000L), ContractStatus.COMPLETED, "client2@example.com"));
            contratRepository.save(new Contrat("Service RH", new Date(), new Date(System.currentTimeMillis() + 100000000L), ContractStatus.CANCELLED, "client3@example.com"));

            // Affichage de tous les contrats
            contratRepository.findAll().forEach(contrat -> log.info("Contrat ajouté : {}", contrat));
        };
    }
}
