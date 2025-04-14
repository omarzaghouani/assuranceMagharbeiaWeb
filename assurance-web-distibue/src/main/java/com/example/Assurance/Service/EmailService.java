package com.example.Assurance.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.Assurance.Repository.UserRepository;
import java.util.Random;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserRepository userRepository;

    public String generate2FACode(String username) {
        try {
            logger.info("Génération d'un code 2FA pour l'utilisateur : {}", username);
            
            // Générer un code à 6 chiffres
            String code = String.format("%06d", new Random().nextInt(1000000));
            
            // Sauvegarder le code dans la base de données
            userRepository.findByUsername(username).ifPresent(user -> {
                user.setTwoFactorSecret(code);
                userRepository.save(user);
            });
            
            // Envoyer le code par email
            userRepository.findByUsername(username).ifPresent(user -> 
                send2FACodeEmail(user.getEmail(), username, code)
            );
            
            logger.info("Code 2FA généré et envoyé avec succès");
            return code;
        } catch (Exception e) {
            logger.error("Erreur lors de la génération du code 2FA", e);
            throw new RuntimeException("Erreur lors de la génération du code 2FA", e);
        }
    }

    public boolean verify2FACode(String username, String code) {
        try {
            logger.info("Vérification du code 2FA pour l'utilisateur : {}", username);
            
            return userRepository.findByUsername(username)
                .map(user -> {
                    boolean isValid = code.equals(user.getTwoFactorSecret());
                    if (isValid) {
                        // Réinitialiser le code après une vérification réussie
                        user.setTwoFactorSecret(null);
                        userRepository.save(user);
                    }
                    return isValid;
                })
                .orElse(false);
        } catch (Exception e) {
            logger.error("Erreur lors de la vérification du code 2FA", e);
            return false;
        }
    }

    public void send2FAEnabledEmail(String to, String username) {
        try {
            logger.info("Tentative d'envoi d'email de confirmation 2FA à : {}", to);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Activation de l'authentification à deux facteurs");
            
            String emailContent = String.format(
                "Bonjour %s,\n\n" +
                "L'authentification à deux facteurs (2FA) a été activée pour votre compte.\n\n" +
                "Pour vous connecter, vous devrez maintenant utiliser votre application d'authentification " +
                "pour générer un code à 6 chiffres en plus de votre nom d'utilisateur et mot de passe.\n\n" +
                "Si vous n'avez pas activé la 2FA, veuillez contacter immédiatement le support.\n\n" +
                "Cordialement,\n" +
                "L'équipe Assurance Maghrebia",
                username
            );
            
            message.setText(emailContent);
            mailSender.send(message);
            logger.info("Email de confirmation 2FA envoyé avec succès à : {}", to);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email de confirmation 2FA à : {}", to, e);
            throw new RuntimeException("Erreur lors de l'envoi de l'email de confirmation 2FA", e);
        }
    }

    public void send2FACodeEmail(String to, String username, String code) {
        try {
            logger.info("Tentative d'envoi d'email avec code 2FA à : {}", to);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Code de vérification 2FA");
            
            String emailContent = String.format(
                "Bonjour %s,\n\n" +
                "Voici votre code de vérification 2FA : %s\n\n" +
                "Ce code est valable pendant 5 minutes.\n" +
                "Si vous n'avez pas demandé ce code, veuillez ignorer cet email.\n\n" +
                "Cordialement,\n" +
                "L'équipe Assurance Maghrebia",
                username,
                code
            );
            
            message.setText(emailContent);
            mailSender.send(message);
            logger.info("Email avec code 2FA envoyé avec succès à : {}", to);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email avec code 2FA à : {}", to, e);
            throw new RuntimeException("Erreur lors de l'envoi de l'email avec code 2FA", e);
        }
    }

    public void send2FADisabledEmail(String to, String username) {
        try {
            logger.info("Tentative d'envoi d'email de confirmation de désactivation 2FA à : {}", to);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Désactivation de l'authentification à deux facteurs");
            
            String emailContent = String.format(
                "Bonjour %s,\n\n" +
                "L'authentification à deux facteurs (2FA) a été désactivée pour votre compte.\n\n" +
                "Vous pourrez désormais vous connecter à votre compte avec uniquement votre nom " +
                "d'utilisateur et votre mot de passe.\n\n" +
                "Si vous n'avez pas désactivé la 2FA, veuillez contacter immédiatement le support.\n\n" +
                "Cordialement,\n" +
                "L'équipe Assurance Maghrebia",
                username
            );
            
            message.setText(emailContent);
            mailSender.send(message);
            logger.info("Email de confirmation de désactivation 2FA envoyé avec succès à : {}", to);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email de confirmation de désactivation 2FA à : {}", to, e);
            throw new RuntimeException("Erreur lors de l'envoi de l'email de confirmation de désactivation 2FA", e);
        }
    }

    private void sendEmail(String to, String subject, String content) {
        try {
            logger.info("Tentative d'envoi d'email à : {}", to);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            
            message.setText(content);
            mailSender.send(message);
            logger.info("Email envoyé avec succès à : {}", to);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email à : {}", to, e);
            throw new RuntimeException("Erreur lors de l'envoi de l'email", e);
        }
    }
} 