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

    private String category;       // "healthcare", "auto", etc.
    private Integer popularityScore; // 1-100 scale
    private Double discount;       // 10.5 for 10.5% off

    // Add a convenience constructor (without id)
    public Package(String name, String description, Double price,
                   String insuranceType, int duration,String category,Integer popularityScore,Double discount) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.insuranceType = insuranceType;
        this.duration = duration;
        this.category = category;
        this.popularityScore = popularityScore;
        this.discount = discount;
    }

}
