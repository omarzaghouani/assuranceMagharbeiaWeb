package tn.esprit.examen.nomPrenomClasseExamen.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Contrat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idContrat;

    private String number;
    private Date startDate;
    private Date endDate;

    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    private String email;

    // Constructeur ajouté pour correspondre à l'initialisation dans ApplicationRunner
    public Contrat(String number, Date startDate, Date endDate, ContractStatus status, String email) {
        this.number = number;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.email = email;
    }

    public String getClientEmail() {
        return email;
    }

    public void setClientEmail(String email) {
        this.email = email;
    }

    public boolean isOngoing() {
        return status == ContractStatus.ONGOING;
    }

    public boolean isCompleted() {
        return status == ContractStatus.COMPLETED;
    }
}
