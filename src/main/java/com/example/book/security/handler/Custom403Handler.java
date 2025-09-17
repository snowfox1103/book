package com.example.book.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@Log4j2
public class Custom403Handler implements AccessDeniedHandler {
  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
    throws IOException, ServletException {
    String accept = request.getHeader("Accept");
    String contentType = request.getHeader("Content-Type");
    String xrw = request.getHeader("X-Requested-With");

    boolean isJson =
      (accept != null && accept.contains("application/json")) ||
        (contentType != null && contentType.contains("application/json")) ||
        "XMLHttpRequest".equalsIgnoreCase(xrw);

    log.info("ACCESS DENIED. isJson: {}", isJson);

    if (isJson) {
      // API/Ajax: 403 + JSON 본문
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      response.setContentType("application/json;charset=UTF-8");
      response.getWriter().write("{\"error\":\"ACCESS_DENIED\"}");
    } else {
      // 브라우저(HTML): 로그인 페이지로 리다이렉트 (상태코드는 302)
      response.sendRedirect("/users/login?error=ACCESS_DENIED");
    }
  }
}
