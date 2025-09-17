package com.example.book.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequest {
  @Column(nullable = false)
  private String currentPassword;

  @Column(nullable = false)
  private String newPassword;

  @Column(nullable = false)
  private String confirmPassword;
}
