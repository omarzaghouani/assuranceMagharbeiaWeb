package com.example.Assurance.Service;

import com.example.Assurance.Entity.User;
import com.example.Assurance.Repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + id));
    }

    public User getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        return user;
    }

    public User findUserByUsername(String username) {
        try {
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " does not exist in the database."));
        } catch (Exception e) {
            System.err.println("Error finding user by username: " + e.getMessage());
            e.printStackTrace();
            throw new UsernameNotFoundException("Error finding user: " + e.getMessage());
        }
    }

    public String get2FASecret(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent()) {
            System.out.println("Utilisateur trouvé : " + username);
            System.out.println("Code 2FA : " + user.get().getTwoFactorSecret());
            return user.get().getTwoFactorSecret();
        } else {
            System.out.println("Utilisateur non trouvé !");
            return null;
        }
    }
}
