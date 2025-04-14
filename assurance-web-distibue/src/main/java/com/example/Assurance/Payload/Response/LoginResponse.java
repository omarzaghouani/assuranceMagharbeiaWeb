package com.example.Assurance.Payload.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private Long id;
    private String username;
    private String email;
    private String role;
    private boolean is2faEnabled;
    private String message;
} 