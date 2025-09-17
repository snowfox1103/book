package com.example.book.repository;

import com.example.book.domain.EmailVerificationToken;
import com.example.book.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
  Optional<EmailVerificationToken> findByToken(String token);

  @Modifying
  @Transactional
  long deleteByUsers(Users users);

  long countByUsersAndSentAtAfter(Users users, LocalDateTime since);

  @Modifying
  @Transactional
  void deleteByUsers_UserNo(Long userNo);
}
