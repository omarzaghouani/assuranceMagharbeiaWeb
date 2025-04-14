package com.example.Assurance.Payload.Request;

import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;

@Validated
public class Verify2FARequest {
  @NotBlank(message = "Le nom d'utilisateur est requis")
  private String username;

  @NotBlank(message = "Le mot de passe est requis")
  private String password;

  @NotBlank(message = "Le code de vérification est requis")
  private String code;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }
} 