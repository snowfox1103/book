package com.example.book.service;

import com.example.book.domain.user.EmailVerificationToken;
import com.example.book.domain.user.Users;
import com.example.book.repository.EmailVerificationTokenRepository;
import com.example.book.repository.UsersRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailService {
  private final JavaMailSender mailSender;
  private final EmailVerificationTokenRepository tokenRepo;
  private final UsersRepository usersRepo;

  @Value("${app.base-url:http://localhost:8080}")
  private String baseUrl;

  private static final Duration TOKEN_TTL = Duration.ofHours(1);
  public enum VerifyResult { SUCCESS, INVALID, EXPIRED, ALREADY_USED, ALREADY_VERIFIED }

  @Transactional
  public void sendVerificationEmail(Users users) {
    // 이미 인증되었으면 스킵
    if (Boolean.TRUE.equals(users.isEnabled())) {
      return;
    }

    tokenRepo.deleteByUsers_UserNo(users.getUserNo());// 이전 토큰 제거 후 새 토큰 발급

    EmailVerificationToken token = EmailVerificationToken.newToken(users, TOKEN_TTL);
    tokenRepo.save(token);

    // 절대경로 링크(네이버 메일에서 중요)
    String verifyUrl = baseUrl + "/users/verify?token=" + token.getToken();

    String plain = "안녕하세요, " + users.getRealName() + "님.\n"
      + "아래 링크를 눌러 이메일을 인증해 주세요.\n" + verifyUrl;

    String html =
      """
      <div style="font-family:Arial,Apple SD Gothic Neo,sans-serif;font-size:14px;line-height:1.6">
        <p>안녕하세요, <b>%s</b>님.</p>
        <p>아래 버튼을 눌러 이메일을 인증해 주세요.</p>
        <p>
          <a href="%s" style="display:inline-block;padding:10px 16px;text-decoration:none;border:1px solid #ddd;border-radius:6px;">
            이메일 인증하기
          </a>
        </p>
        <p style="color:#666">버튼이 보이지 않으면 아래 주소를 복사해 붙여넣기:</p>
        <p><a href="%s">%s</a></p>
      </div>
      """.formatted(users.getRealName(), verifyUrl, verifyUrl, verifyUrl);

    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setTo(users.getEmail());
      // 발신자 표시 이름(optional)
      helper.setFrom(new InternetAddress("your.email@gmail.com", "가계북"));
      helper.setSubject("[가계북] 이메일 인증 안내");

      // 텍스트/HTML 동시 전송(클라이언트별 호환성 Good)
      helper.setText(plain, html);

      mailSender.send(message);
    } catch (MessagingException | UnsupportedEncodingException e) {
      throw new IllegalStateException("이메일 전송 실패", e);
    }
  }

  public VerifyResult verify(String tokenStr) {
    Optional<EmailVerificationToken> opt = tokenRepo.findByToken(tokenStr);
    if (opt.isEmpty()) {
      return VerifyResult.INVALID;
    }

    EmailVerificationToken token = opt.get();
    if (token.isUsed()) {
      return VerifyResult.ALREADY_USED;
    }
    if (token.isExpired()) {
      return VerifyResult.EXPIRED;
    }

    Users users = token.getUsers();
    if (Boolean.TRUE.equals(users.isEnabled())) {
      token.setUsed(true);
      return VerifyResult.ALREADY_VERIFIED;
    }

    users.setEnabled(true);
    usersRepo.save(users);

    token.setUsed(true);
    tokenRepo.save(token);

    return VerifyResult.SUCCESS;
  }

  /** 토큰 문자열만 알고 있을 때도, 만료 상태라면 자동 재발송 (토큰이 DB에 있어야 가능) */
  public boolean resendByTokenIfPossible(String tokenStr) {
    return tokenRepo.findByToken(tokenStr)
      .map(EmailVerificationToken::getUsers)
      .map(user -> { sendVerificationEmail(user); return true; })
      .orElse(false);
  }

  /** 사용자가 이메일만 넣고 재발송 요청 */
  public void resendByEmailSilently(String email) {
    // 프라이버시 보호: 존재 여부와 상관없이 동일 응답을 주는 방식 권장
    usersRepo.findByEmail(email).ifPresent(u -> {
      if (!Boolean.TRUE.equals(u.isEnabled())) {
        sendVerificationEmail(u);
      }
    });
  }
}
