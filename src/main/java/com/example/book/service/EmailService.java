package com.example.book.service;

import com.example.book.domain.EmailVerificationToken;
import com.example.book.domain.Users;
import com.example.book.repository.EmailVerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailService {
  private final JavaMailSender mailSender;
  private final EmailVerificationTokenRepository tokenRepo;

  public void sendVerificationEmail(Users users) {
    String token = UUID.randomUUID().toString();

    // 토큰 DB 저장
    EmailVerificationToken verificationToken = new EmailVerificationToken(users, token);
    tokenRepo.save(verificationToken);

    // 이메일 내용 작성
    String recipientAddress = users.getEmail();
    String subject = "회원가입 이메일 인증";
    String confirmationUrl = "http://localhost:8080/users/verify?token=" + token;
    String message = "아래 링크를 클릭하여 이메일 인증을 완료하세요:\n" + confirmationUrl;

    SimpleMailMessage email = new SimpleMailMessage();
    email.setTo(recipientAddress);
    email.setSubject(subject);
    email.setText(message);

    mailSender.send(email);
  }
}
