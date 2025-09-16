package com.example.book.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class EmailVerificationToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String token;
  private LocalDateTime expiryDate;

  @OneToOne
  @JoinColumn(name = "userNo")
  private Users users;

  public EmailVerificationToken(Users users, String token) {
    this.users = users;
    this.token = token;
    this.expiryDate = LocalDateTime.now().plusHours(24); // 24시간 유효
  }
}
