package com.example.Assurance.Service;

import com.example.Assurance.Entity.AuthRequest;
import com.example.Assurance.Entity.Role;
import com.example.Assurance.Entity.User;
import com.example.Assurance.Repository.UserRepository;
import com.example.Assurance.Service.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public String login(String username, String password) {
        try {
            System.out.println("Tentative de connexion pour l'utilisateur: " + username);
            
            // Vérifier si l'utilisateur existe d'abord
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + username));
                
            if (!user.isEnabled()) {
                throw new RuntimeException("Compte désactivé");
            }

            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // Extract role and ensure it has the ROLE_ prefix
            String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(a -> {
                    String authority = a.getAuthority();
                    System.out.println("Authority trouvée: " + authority);
                    return authority;
                })
                .orElseThrow(() -> new RuntimeException("Aucun rôle trouvé pour l'utilisateur"));
                
            System.out.println("Génération du token pour l'utilisateur: " + username + " avec le rôle: " + role);
            String token = jwtUtil.generateToken(username, role);
            System.out.println("Token généré avec succès");
            return token;
        } catch (Exception e) {
            System.err.println("Échec de la connexion pour l'utilisateur: " + username);
            System.err.println("Erreur: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public String register(AuthRequest request) {
        System.out.println("=== Début de l'inscription ===");
        System.out.println("Username: " + request.username());
        System.out.println("Email: " + request.email());
        System.out.println("Role: " + request.role());
        System.out.println("Password: " + (request.password() != null ? "[PRESENT]" : "[NULL]"));
        System.out.println("TwoFactorCode: " + request.twoFactorCode());
        System.out.println("=====================================");

        // Validation des champs
        if (request.username() == null || request.username().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom d'utilisateur est requis");
        }
        if (request.password() == null || request.password().trim().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe est requis");
        }
        if (request.email() == null || request.email().trim().isEmpty()) {
            throw new IllegalArgumentException("L'email est requis");
        }
        if (request.role() == null) {
            throw new IllegalArgumentException("Le rôle est requis");
        }

        // Validation du rôle
        Role role;
        try {
            role = Role.valueOf(request.role().toString().toUpperCase());
            System.out.println("Rôle validé: " + role);
        } catch (IllegalArgumentException e) {
            System.err.println("Rôle invalide: " + request.role());
            throw new IllegalArgumentException("Le rôle doit être l'une des valeurs suivantes: ADMIN, AGENT, CLIENT_PHYSIQUE, CLIENT_MORAL");
        }

        // Vérifier si l'utilisateur existe déjà
        User existingUser = userRepository.findByUsernameOrEmail(request.username(), request.email());
        if (existingUser != null) {
            System.out.println("Utilisateur existant trouvé: " + existingUser.getUsername());
            throw new IllegalArgumentException("Un utilisateur avec ce nom d'utilisateur ou cet email existe déjà");
        }

        try {
            // Créer un nouvel utilisateur
            User newUser = new User();
            newUser.setUsername(request.username());
            newUser.setPassword(passwordEncoder.encode(request.password()));
            newUser.setEmail(request.email());
            newUser.setRole(role);
            newUser.setIs2fa_enabled(false);
            newUser.setBackup_codes_generated(false);
            newUser.setFailed2faattempts(0);
            newUser.setEnabled(true);

            System.out.println("Nouvel utilisateur créé avec succès");
            System.out.println("Tentative de sauvegarde dans la base de données...");

            // Sauvegarder l'utilisateur
            User savedUser = userRepository.save(newUser);
            System.out.println("Utilisateur sauvegardé avec succès. ID: " + savedUser.getIdUser());

            // Générer le token JWT
            String token = jwtUtil.generateToken(savedUser.getUsername(), savedUser.getRole().name());
            System.out.println("Token JWT généré avec succès");

            return token;
        } catch (Exception e) {
            System.err.println("Erreur lors de l'inscription: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'inscription: " + e.getMessage());
        }
    }

    public void updatePassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }
    }

    private String generateToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);  // Generate URL-safe token
    }

    public void createPasswordResetToken(String email, String token) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec cet email: " + email));

        if (user == null) {
            throw new UserNotFoundException("Utilisateur non trouvé avec cet email" +email);
        }

        // If the user already has a valid reset token, consider notifying or not overwriting
        if (user.getResetTokenExpiry() != null && user.getResetTokenExpiry().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Un token de réinitialisation valide existe déjà.");
        }

        // Generate a token if not passed
        if (token == null || token.isEmpty()) {
            token = generateToken();
        }

        LocalDateTime expiryDate = LocalDateTime.now().plus(24, ChronoUnit.HOURS);

        user.setResetToken(token);
        user.setResetTokenExpiry(expiryDate);

        userRepository.save(user);
    }

    public String generate2FACode(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Générer un code à 6 chiffres
        String code = String.format("%06d", new Random().nextInt(1000000));
        
        // Sauvegarder le code dans le champ twoFactorSecret
        user.setTwoFactorSecret(code);
        userRepository.save(user);

        // Envoyer le code par email
        emailService.send2FACodeEmail(user.getEmail(), user.getUsername(), code);

        return code;
    }

    public boolean verify2FACode(String username, String code) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier si le code correspond au code stocké
        boolean isValid = code.equals(user.getTwoFactorSecret());
        
        // Si le code est valide, le supprimer pour éviter la réutilisation
        if (isValid) {
            user.setTwoFactorSecret(null);
            userRepository.save(user);
        }

        return isValid;
    }

    public void enable2FA(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIs2fa_enabled(true);
        userRepository.save(user);
        emailService.send2FAEnabledEmail(user.getEmail(), user.getUsername());
    }

    public void disable2FA(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIs2fa_enabled(false);
        user.setTwoFactorSecret(null);
        userRepository.save(user);
        emailService.send2FADisabledEmail(user.getEmail(), user.getUsername());
    }

    public boolean verify2FA(String username, String code) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return code.equals(user.getTwoFactorSecret());
    }

    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtUtil.generateToken(userDetails.getUsername(), 
            userDetails.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("USER"));
    }

    public Authentication authenticate(String username, String password) {
        System.out.println("=== Début de l'authentification ===");
        System.out.println("Tentative d'authentification pour l'utilisateur: " + username);
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            System.out.println("Authentification réussie pour l'utilisateur: " + username);
            System.out.println("=== Fin de l'authentification ===");
            return authentication;
        } catch (AuthenticationException e) {
            System.out.println("Échec de l'authentification pour l'utilisateur: " + username);
            System.out.println("Raison: " + e.getMessage());
            System.out.println("=== Fin de l'authentification ===");
            throw e;
        }
    }

    public void resetFailed2FAAttempts(String username) {
        System.out.println("=== Début de la réinitialisation des tentatives 2FA ===");
        System.out.println("Réinitialisation pour l'utilisateur: " + username);
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        user.setFailed2faattempts(0);
        userRepository.save(user);
        
        System.out.println("Compteur de tentatives réinitialisé pour l'utilisateur: " + username);
        System.out.println("=== Fin de la réinitialisation des tentatives 2FA ===");
    }

    public String generateNew2FACode(String username) {
        System.out.println("=== Début de la génération du code 2FA ===");
        System.out.println("Génération pour l'utilisateur: " + username);
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        String newCode = generateRandomCode();
        user.setTwoFactorSecret(newCode);
        userRepository.save(user);
        
        System.out.println("Nouveau code 2FA généré pour l'utilisateur: " + username);
        System.out.println("=== Fin de la génération du code 2FA ===");
        
        return newCode;
    }

    private String generateRandomCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000); // Génère un code à 6 chiffres
        return String.valueOf(code);
    }
}
