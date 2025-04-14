package com.example.Assurance.Entity;

public record AuthRequest(
    Long idUser,
    String username,
    String password,
    String email,
    Role role,
    String twoFactorCode
) {
    public static AuthRequest of(String username, String password) {
        return new AuthRequest(null, username, password, null, null, null);
    }
}
