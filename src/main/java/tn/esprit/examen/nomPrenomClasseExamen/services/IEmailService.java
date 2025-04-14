package tn.esprit.examen.nomPrenomClasseExamen.services;

import jakarta.mail.MessagingException;

public interface IEmailService {
    void sendConfirmationEmail(String to, String subject, String text) throws MessagingException;
}
