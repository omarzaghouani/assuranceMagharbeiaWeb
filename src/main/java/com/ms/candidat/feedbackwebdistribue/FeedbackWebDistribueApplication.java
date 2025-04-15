package com.ms.candidat.feedbackwebdistribue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableEurekaServer
public class FeedbackWebDistribueApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeedbackWebDistribueApplication.class, args);
    }

    @Autowired
    private FeedBackRepo feedBackRepo;

    @Bean
    ApplicationRunner init() {
        return (args) -> {
            feedBackRepo.save(new FeedBack(LocalDateTime.now(), "Service rapide et efficace."));
            feedBackRepo.save(new FeedBack(LocalDateTime.now().minusDays(1), "Manque de clarté dans l’explication."));
            feedBackRepo.save(new FeedBack(LocalDateTime.now().minusDays(2), "Très satisfait du support client."));
            feedBackRepo.save(new FeedBack(LocalDateTime.now().minusDays(3), "Temps de traitement un peu long."));

            feedBackRepo.findAll().forEach(fb -> {
                System.out.println("Feedback ID: " + fb.getFeedbackId());
                System.out.println("Date: " + fb.getSubmissionDate());
                System.out.println("Description: " + fb.getDescription());
                System.out.println("----------------------------------");
            });
        };
    }
}
