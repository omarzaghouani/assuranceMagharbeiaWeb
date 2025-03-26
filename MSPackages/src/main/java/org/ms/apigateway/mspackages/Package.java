package org.ms.apigateway.mspackages;


import jakarta.persistence.*;
import lombok.*;
@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Package {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String description;
    private Double price;
    private String insuranceType;
    private int duration; // in months

    // Add a convenience constructor (without id)
    public Package(String name, String description, Double price,
                   String insuranceType, int duration) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.insuranceType = insuranceType;
        this.duration = duration;
    }

}
