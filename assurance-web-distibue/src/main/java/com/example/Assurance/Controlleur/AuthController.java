package com.example.Assurance.Controlleur;

import com.example.Assurance.Entity.AuthRequest;
import com.example.Assurance.Entity.ResetPasswordRequest;
import com.example.Assurance.Entity.User;
import com.example.Assurance.Entity.Role;
import com.example.Assurance.Repository.UserRepository;
import com.example.Assurance.Service.AuthService;
import com.example.Assurance.Service.QRCodeGenerator;
import com.example.Assurance.Service.TokenService;
import com.example.Assurance.Service.UserService;
import com.example.Assurance.Service.JwtUtil;
import com.example.Assurance.Service.EmailService;
import com.example.Assurance.Payload.Response.LoginResponse;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.*;
import com.google.zxing.qrcode.*;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Random;
import java.util.List;
import java.util.stream.Collectors;

import com.example.Assurance.Security.UserDetailsImpl;
import com.example.Assurance.Payload.Request.Verify2FARequest;
import com.example.Assurance.Payload.Response.JwtResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthController {

  private final AuthService authService;
  private final TokenService tokenService;
  private final JavaMailSender mailSender;
  private final UserService userService;
  private final QRCodeGenerator qrCodeGenerator;
  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final EmailService emailService;
  private final AuthenticationManager authenticationManager;

  private final GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();

  // Constructor Injection for all dependencies
  @Autowired
  public AuthController(AuthService authService,
                        TokenService tokenService,
                        JavaMailSender mailSender,
                        UserService userService,
                        QRCodeGenerator qrCodeGenerator,
                        JwtUtil jwtUtil,
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        EmailService emailService,
                        AuthenticationManager authenticationManager) {
    this.authService = authService;
    this.tokenService = tokenService;
    this.mailSender = mailSender;
    this.userService = userService;
    this.qrCodeGenerator = qrCodeGenerator;
    this.jwtUtil = jwtUtil;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.emailService = emailService;
    this.authenticationManager = authenticationManager;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
    System.out.println("=== Début de la tentative de connexion ===");
    System.out.println("Données reçues: " + request);
    
    String username = request.get("username");
    String password = request.get("password");
    String twoFactorCode = request.get("twoFactorCode");
    
    if (username == null || password == null) {
      return ResponseEntity.badRequest().body("Username and password are required");
    }

    try {
      // Vérifier si l'utilisateur existe
      User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

      System.out.println("Utilisateur trouvé: " + user.getUsername());
      System.out.println("2FA activée: " + user.isIs2fa_enabled());

      // Vérifier si le compte est activé
      if (!user.isEnabled()) {
        return ResponseEntity.status(401).body("Account is disabled");
      }

      // Si 2FA est activé
      if (user.isIs2fa_enabled()) {
        System.out.println("2FA est activée pour l'utilisateur: " + username);
        // Si le code 2FA n'est pas fourni
        if (twoFactorCode == null || twoFactorCode.isEmpty()) {
          System.out.println("Code 2FA non fourni, génération d'un nouveau code");
          // Générer et envoyer un nouveau code 2FA
          String code = emailService.generate2FACode(username);
          LoginResponse response = new LoginResponse(null, null, null, null, null, true, "2FA code sent to your email");
          System.out.println("Réponse envoyée: " + response);
          return ResponseEntity.ok(response);
        }

        // Vérifier le code 2FA
        if (!emailService.verify2FACode(username, twoFactorCode)) {
          // Incrémenter le compteur de tentatives échouées
          user.setFailed2faattempts(user.getFailed2faattempts() + 1);
          userRepository.save(user);
          return ResponseEntity.status(401).body("Invalid 2FA code");
        }
      }

      // Authentifier l'utilisateur
      Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(username, password)
      );
      SecurityContextHolder.getContext().setAuthentication(authentication);

      // Générer le token JWT
      String token = jwtUtil.generateToken(username, user.getRole().name());
      
      // Créer la réponse
      LoginResponse response = new LoginResponse(
        token,
        user.getIdUser(),
        user.getUsername(),
        user.getEmail(),
        user.getRole().name(),
        user.isIs2fa_enabled(),
        "Login successful"
      );

      System.out.println("Réponse finale: " + response);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      System.err.println("Erreur lors de la connexion: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.status(401).body("Authentication failed: " + e.getMessage());
    }
  }
  
  private void sendEmail(String to, String subject, String text) {
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(to);
      message.setSubject(subject);
      message.setText(text);
      mailSender.send(message);
      System.out.println("Email envoyé avec succès à: " + to);
    } catch (Exception e) {
      System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
    }
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody AuthRequest request) {
    System.out.println("=== Début de la requête d'inscription ===");
    System.out.println("Username: " + request.username());
    System.out.println("Email: " + request.email());
    System.out.println("Role: " + request.role());
    System.out.println("Password: " + (request.password() != null ? "[PRESENT]" : "[NULL]"));
    System.out.println("TwoFactorCode: " + request.twoFactorCode());
    System.out.println("=====================================");

    try {
        // Validation des champs
        if (request.username() == null || request.username().trim().isEmpty()) {
            System.out.println("Erreur: Le nom d'utilisateur est requis");
            return ResponseEntity.badRequest().body(Map.of("error", "Le nom d'utilisateur est requis"));
        }
        if (request.password() == null || request.password().trim().isEmpty()) {
            System.out.println("Erreur: Le mot de passe est requis");
            return ResponseEntity.badRequest().body(Map.of("error", "Le mot de passe est requis"));
        }
        if (request.email() == null || request.email().trim().isEmpty()) {
            System.out.println("Erreur: L'email est requis");
            return ResponseEntity.badRequest().body(Map.of("error", "L'email est requis"));
        }
        if (request.role() == null) {
            System.out.println("Erreur: Le rôle est requis");
            return ResponseEntity.badRequest().body(Map.of("error", "Le rôle est requis"));
        }

        // Validation du rôle
        try {
            Role.valueOf(request.role().toString().toUpperCase());
            System.out.println("Rôle validé: " + request.role());
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur: Rôle invalide: " + request.role());
            return ResponseEntity.badRequest().body(Map.of("error", "Le rôle doit être l'une des valeurs suivantes: ADMIN, AGENT, CLIENT_PHYSIQUE, CLIENT_MORAL"));
        }

        String token = authService.register(request);
        System.out.println("Inscription réussie");
        return ResponseEntity.ok(Map.of("token", token));
    } catch (IllegalArgumentException e) {
        System.err.println("Erreur de validation: " + e.getMessage());
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
        System.err.println("Erreur lors de l'inscription: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("error", "Erreur lors de l'inscription: " + e.getMessage()));
    }
  }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestBody String email) {
        String token = UUID.randomUUID().toString();
        System.out.println("hedha"+email);// Générer un token unique
        authService.createPasswordResetToken(email, token);  // Créer et enregistrer le token pour l'utilisateur

        String resetLink = "http://localhost:4200/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Réinitialisation de votre mot de passe");
        message.setText("Cliquez sur le lien suivant pour réinitialiser votre mot de passe : " + resetLink);

        mailSender.send(message);

        return "Un e-mail avec le lien de réinitialisation a été envoyé.";
    }

@PostMapping("/reset-password")
public ResponseEntity<Object> resetPassword(@RequestBody ResetPasswordRequest request) {
  if (tokenService.isValidToken(request.getToken())) {
    authService.updatePassword(request.getToken(), request.getPassword());
    tokenService.invalidateToken(request.getToken());
    return ResponseEntity.ok(new MessageResponse("Le mot de passe a été réinitialisé avec succès."));
  } else {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Token invalide ou expiré."));
  }
}
  @PostMapping("/double/{username}")
  public ResponseEntity<Map<String, String>> generate2FA(@PathVariable String username) {
    // Vérifie si l'utilisateur existe
    var user = userService.findUserByUsername(username);
    if (user == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(Collections.singletonMap("error", "Utilisateur non trouvé."));
    }

    try {
      // Générer un code 2FA
      String twoFactorCode = generateTwoFactorCode();
      user.setTwoFactorSecret(twoFactorCode);
      user.setIs2fa_enabled(true);
      userRepository.save(user);

      // Envoyer un email avec le code
      emailService.send2FAEnabledEmail(user.getEmail(), user.getUsername());
      return ResponseEntity.ok(Map.of(
        "message", "Un code 2FA a été envoyé à votre email."
      ));
    } catch (Exception e) {
      // Si l'envoi d'email échoue, on désactive la 2FA
      user.setIs2fa_enabled(false);
      user.setTwoFactorSecret(null);
      userRepository.save(user);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("error", "Erreur lors de l'envoi du code 2FA. Veuillez réessayer."));
    }
  }

  public class MessageResponse {
    private String message;

    // constructor, getter, setter
    public MessageResponse(String message) {
      this.message = message;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }
  }
  @GetMapping("/username/{username}")
  public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
    try {
      User user = userService.findUserByUsername(username);
      return new ResponseEntity<>(user, HttpStatus.OK);
    } catch (RuntimeException e) {
      return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(HttpServletResponse response) {
    Cookie cookie = new Cookie("JWT", null);
    cookie.setHttpOnly(true);
    cookie.setSecure(true);  // Assurez-vous d'activer ce paramètre en production
    cookie.setPath("/");
    cookie.setMaxAge(0);  // Supprimer le cookie en définissant son âge à 0
    response.addCookie(cookie);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/enable-2fa")
  public ResponseEntity<?> enable2FA(@RequestBody Map<String, String> request) {
    try {
      String username = request.get("username");
      if (username == null || username.trim().isEmpty()) {
        return ResponseEntity.badRequest()
          .body(Map.of("error", "Le nom d'utilisateur est requis"));
      }

      User user = userService.findUserByUsername(username);
      if (user == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(Map.of("error", "Utilisateur non trouvé"));
      }

      if (user.isIs2fa_enabled()) {
        return ResponseEntity.badRequest()
          .body(Map.of("error", "La 2FA est déjà activée pour cet utilisateur"));
      }

      // Générer un code 2FA
      String twoFactorCode = generateTwoFactorCode();
      user.setTwoFactorSecret(twoFactorCode);
      user.setIs2fa_enabled(true);
      userRepository.save(user);

      try {
        // Envoyer un email de confirmation avec le code
        emailService.send2FAEnabledEmail(user.getEmail(), user.getUsername());
      } catch (Exception e) {
        // Si l'envoi d'email échoue, on désactive la 2FA
        user.setIs2fa_enabled(false);
        user.setTwoFactorSecret(null);
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Erreur lors de l'envoi de l'email de confirmation. La 2FA n'a pas été activée."));
      }

      return ResponseEntity.ok(Map.of(
        "message", "2FA activée avec succès. Un code a été envoyé à votre email."
      ));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("error", "Erreur lors de l'activation de la 2FA : " + e.getMessage()));
    }
  }

  @PostMapping("/verify-2fa")
  public ResponseEntity<?> verify2FA(@RequestBody Map<String, String> request) {
    try {
      String username = request.get("username");
      String password = request.get("password");
      String code = request.get("code");
      
      if (username == null || password == null || code == null) {
        return ResponseEntity.badRequest()
          .body(new MessageResponse("Tous les champs sont requis (username, password, code)"));
      }
      
      User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));

      if (!user.isEnabled()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new MessageResponse("Compte désactivé. Veuillez contacter l'administrateur."));
      }

      if (!user.isIs2fa_enabled()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new MessageResponse("2FA n'est pas activé pour cet utilisateur."));
      }

      String storedCode = user.getTwoFactorSecret();
      if (storedCode == null || storedCode.isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new MessageResponse("Aucun code 2FA n'a été généré. Veuillez vous reconnecter."));
      }

      boolean isValid = code.equals(storedCode);
      System.out.println("Received code: " + code);
      System.out.println("Stored code: " + storedCode);
      System.out.println("Is valid: " + isValid);

      if (!isValid) {
        user.setFailed2faattempts(user.getFailed2faattempts() + 1);
        userRepository.save(user);

        if (user.getFailed2faattempts() >= 3) {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new MessageResponse("Trop de tentatives échouées. Veuillez réinitialiser votre 2FA."));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new MessageResponse("Code 2FA invalide. Tentatives restantes: " + 
            (3 - user.getFailed2faattempts())));
      }

      user.setFailed2faattempts(0);
      user.setTwoFactorSecret(null);
      userRepository.save(user);

      Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
          username,
          password
        )
      );

      SecurityContextHolder.getContext().setAuthentication(authentication);
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      String role = userDetails.getAuthorities().stream()
        .findFirst()
        .map(a -> a.getAuthority().replace("ROLE_", ""))
        .orElse("USER");
      String jwt = jwtUtil.generateToken(username, role);

      List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

      return ResponseEntity.ok(new JwtResponse(jwt,
        user.getIdUser(),
        user.getUsername(),
        user.getEmail(),
        roles));
    } catch (AuthenticationException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new MessageResponse("Nom d'utilisateur ou mot de passe incorrect"));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new MessageResponse("Une erreur est survenue lors de la vérification 2FA: " + e.getMessage()));
    }
  }

  @PostMapping("/disable-2fa")
  public ResponseEntity<?> disable2FA(@RequestBody Map<String, String> request) {
    try {
        String username = request.get("username");
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Le nom d'utilisateur est requis"));
        }

        User user = userService.findUserByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Utilisateur non trouvé"));
        }

        if (!user.isIs2fa_enabled()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "La 2FA n'est pas activée pour cet utilisateur"));
        }

        // Désactiver la 2FA
        user.setIs2fa_enabled(false);
        user.setTwoFactorSecret(null);
        user.setFailed2faattempts(0);
        userRepository.save(user);

        try {
            // Envoyer un email de confirmation
            emailService.send2FADisabledEmail(user.getEmail(), user.getUsername());
        } catch (Exception e) {
            // Si l'envoi d'email échoue, on continue car la 2FA est déjà désactivée
            System.out.println("Erreur lors de l'envoi de l'email de confirmation pour la désactivation de la 2FA: " + e.getMessage());
        }

        return ResponseEntity.ok(Map.of(
            "message", "2FA désactivée avec succès"
        ));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("error", "Erreur lors de la désactivation de la 2FA : " + e.getMessage()));
    }
  }

  private String generateTwoFactorCode() {
    // Générer un code à 6 chiffres
    Random random = new Random();
    int code = 100000 + random.nextInt(900000);
    return String.format("%06d", code);
  }

}



