package com.example.book.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequestDTO {
  private String currentPassword;
  private String newPassword;
  private String confirmPassword;
  private boolean termsCheck;
  private boolean privacyCheck;
}
