package com.example.book.security.handler;

import com.example.book.security.dto.UsersSecurityDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import java.io.IOException;

@Log4j2
@RequiredArgsConstructor
public class CustomSocialLoginSuccessHandler implements AuthenticationSuccessHandler {
  private final PasswordEncoder passwordEncoder;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
    log.info("----------------------------------------------------------");
    log.info("CustomLoginSuccessHandler onAuthenticationSuccess ..........");
    log.info(authentication.getPrincipal());
    UsersSecurityDTO usersSecurityDTO = (UsersSecurityDTO) authentication.getPrincipal();
    String encodedPw = usersSecurityDTO.getPassword();

    //소셜로그인이고 회원의 패스워드가 1111이라면
    if (usersSecurityDTO.isSocial() && passwordEncoder.matches("1111", encodedPw)) {
      log.info("Should Change Password");
      log.info("Redirect to User Modify ");
      response.sendRedirect("/users/userModify");

      return;
    }

    SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);

    if (savedRequest != null) {
      String targetUrl = savedRequest.getRedirectUrl();
      response.sendRedirect(targetUrl);
      return;
    }

    response.sendRedirect("/main");
  }
}
