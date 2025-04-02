package org.ms.apigateway.mspackages;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) // Autorise toutes les requêtes
                .csrf(csrf -> csrf.disable()) // Désactive la protection CSRF
                .headers(headers -> headers.frameOptions().disable()); // Permet les requêtes H2-console (si besoin)

        return http.build();
    }
}
