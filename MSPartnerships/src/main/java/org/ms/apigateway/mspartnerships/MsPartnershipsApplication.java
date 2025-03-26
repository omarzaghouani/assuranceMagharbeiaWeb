package org.ms.apigateway.mspartnerships;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class MsPartnershipsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsPartnershipsApplication.class, args);
    }

    @Autowired
    private PartnerRepository partnerRepository;

    @Bean
    ApplicationRunner init() {
        return (args) -> {
            // save
            partnerRepository.save(new Partner(null, "Tech Solutions Inc.", "Technology", "tech@example.com", "+1234567890"));
            partnerRepository.save(new Partner(null, "HealthCare Partners", "Healthcare", "health@example.com", "+1987654321"));
            partnerRepository.save(new Partner(null, "EduGlobal", "Education", "edu@example.com", "+1122334455"));
            // fetch
            partnerRepository.findAll().forEach(System.out::println);
        };
    }
}