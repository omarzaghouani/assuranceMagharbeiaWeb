package com.example.demoerikaserveur;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer // pour activer le serveur Eureka
public class DemoerikaserveurApplication {
    

    public static void main(String[] args) {
        SpringApplication.run(DemoerikaserveurApplication.class, args);
    }

}
