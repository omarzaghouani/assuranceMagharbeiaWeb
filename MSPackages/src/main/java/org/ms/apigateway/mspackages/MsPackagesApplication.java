package org.ms.apigateway.mspackages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
@EnableDiscoveryClient
public class MsPackagesApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsPackagesApplication.class, args);
    }

    @Autowired
    private PackageRepository packageRepository;

    @Bean
    @Transactional
    ApplicationRunner init() {
        return (args) -> {
            // Clear existing data (optional)
            packageRepository.deleteAll();

            // Save test packages
            packageRepository.save(new Package("Basic Package", "Basic insurance coverage", 50.0, "Health", 5,"test",1,10.00));
            packageRepository.save(new Package("Premium Package", "Premium insurance coverage", 100.0, "Life", 12,"test",1,10.00));
            packageRepository.save(new Package("Family Package", "Family insurance plan", 150.0, "Family", 12,"test",1,10.00));

            // Fetch and print all packages
            System.out.println("Initial packages saved:");
            packageRepository.findAll().forEach(p ->
                    System.out.println(p.getId() + ": " + p.getName())
            );
        };
    }
}