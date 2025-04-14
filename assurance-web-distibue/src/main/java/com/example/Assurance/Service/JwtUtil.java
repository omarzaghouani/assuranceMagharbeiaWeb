package com.example.Assurance.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username, String role) {
        try {
            System.out.println("Génération du token JWT pour: " + username);
            System.out.println("Rôle reçu: " + role);
            
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", role);
            
            String token = createToken(claims, username);
            System.out.println("Token JWT généré avec succès");
            return token;
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du token JWT: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            final String role = extractRole(token);
            System.out.println("Validating token for user: " + username);
            System.out.println("Token role: " + role);
            System.out.println("User authorities: " + userDetails.getAuthorities());
            
            // Normalize usernames for comparison
            String normalizedTokenUsername = username.toLowerCase().trim();
            String normalizedUserDetailsUsername = userDetails.getUsername().toLowerCase().trim();
            boolean usernameMatches = normalizedTokenUsername.equals(normalizedUserDetailsUsername);
            
            boolean tokenNotExpired = !isTokenExpired(token);
            
            // Add ROLE_ prefix to token role for comparison
            String normalizedTokenRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
            boolean roleMatches = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(normalizedTokenRole));
            
            System.out.println("Username matches: " + usernameMatches);
            System.out.println("Token not expired: " + tokenNotExpired);
            System.out.println("Role matches: " + roleMatches);
            
            if (!usernameMatches) {
                System.out.println("Username mismatch - Token: " + normalizedTokenUsername + ", UserDetails: " + normalizedUserDetailsUsername);
            }
            if (!tokenNotExpired) {
                System.out.println("Token has expired");
            }
            if (!roleMatches) {
                System.out.println("Role mismatch - Token role: " + normalizedTokenRole + ", User authorities: " + userDetails.getAuthorities());
            }
            
            return usernameMatches && tokenNotExpired && roleMatches;
        } catch (Exception e) {
            System.err.println("Error validating token: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
