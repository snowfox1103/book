package com.example.book.domain.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class EmailVerificationToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String token;

  @Column(nullable = false)
  private LocalDateTime expiryDate;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "userNo")
  private Users users;

  @Column(nullable = false)
  private boolean used = false;

  @Column(nullable = false)
  private LocalDateTime sentAt = LocalDateTime.now();

  public EmailVerificationToken(Users users, String token) {
    this.users = users;
    this.token = token;
    this.expiryDate = LocalDateTime.now().plusHours(1); // 1시간 유효
  }

  public static EmailVerificationToken newToken(Users users, Duration ttl) {
    EmailVerificationToken t = new EmailVerificationToken();
    t.setUsers(users);
    t.setToken(UUID.randomUUID().toString());
    t.setExpiryDate(LocalDateTime.now().plus(ttl));
    t.setUsed(false);
    t.setSentAt(LocalDateTime.now());
    return t;
  }

  public boolean isExpired() {
    return expiryDate.isBefore(LocalDateTime.now());
  }
}
