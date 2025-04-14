package com.example.refundwebdistibue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
@EnableEurekaServer
public class RefundWebDistibueApplication {

    public static void main(String[] args) {
        SpringApplication.run(RefundWebDistibueApplication.class, args);
    }

    @Autowired
    private refundRepo refundRepository;

    @Bean
    ApplicationRunner init() {
        return (args) -> {
            // save
            refundRepository.save(new refund(100.0, "Pending", LocalDate.now(), null));
            refundRepository.save(new refund(200.0, "Completed", LocalDate.now().minusDays(1), LocalDate.now()));
            refundRepository.save(new refund(150.0, "Pending", LocalDate.now().minusDays(2), null));
            refundRepository.save(new refund(300.0, "Completed", LocalDate.now().minusDays(3), LocalDate.now().minusDays(1)));

            // fetch
            refundRepository.findAll().forEach(System.out::println);
        };
    }
    
    
    
}
