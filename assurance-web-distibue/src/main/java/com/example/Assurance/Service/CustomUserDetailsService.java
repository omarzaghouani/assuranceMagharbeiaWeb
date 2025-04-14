package com.example.Assurance.Service;

import com.example.Assurance.Entity.User;
import com.example.Assurance.Repository.UserRepository;
import com.example.Assurance.Security.UserDetailsImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("=== Début du chargement de l'utilisateur ===");
        System.out.println("Recherche de l'utilisateur: " + username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println("Utilisateur non trouvé: " + username);
                    return new UsernameNotFoundException("User not found");
                });
        
        System.out.println("Utilisateur trouvé: " + user.getUsername());
        System.out.println("Rôle de l'utilisateur: " + user.getRole());
        System.out.println("Compte activé: " + user.isEnabled());
        System.out.println("2FA activée: " + user.isIs2fa_enabled());
        
        // Create a SimpleGrantedAuthority for the user's role
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
        System.out.println("Authority créée: " + authority.getAuthority());
        
        // Return UserDetails with the role authority
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            Collections.singletonList(authority)
        );
        
        System.out.println("UserDetails créé avec succès");
        System.out.println("=== Fin du chargement de l'utilisateur ===");
        
        return userDetails;
    }
}
