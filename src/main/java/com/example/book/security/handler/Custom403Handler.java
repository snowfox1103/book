package com.example.book.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.io.PrintWriter;

@Log4j2
public class Custom403Handler implements AccessDeniedHandler {
  @Override
  public void handle(HttpServletRequest request,
                     HttpServletResponse response,
                     AccessDeniedException accessDeniedException) throws IOException {

    String ajaxHeader = request.getHeader("X-Requested-With");
    boolean isAjax = "XMLHttpRequest".equals(ajaxHeader);

    if (isAjax) {
      // ---- JSON 응답 ----
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      response.setContentType("application/json;charset=UTF-8");

      try (PrintWriter writer = response.getWriter()) {
        writer.write("{\"error\":\"ACCESS_DENIED\"}");
        writer.flush();
      }

      return; // ★ JSON 보냈으면 여기서 완전히 종료
    }

    // ---- 일반 브라우저 요청 ----
    // 여기서는 Spring Boot 기본 에러 페이지로 빠지지 않도록 직접 리다이렉트
    response.sendRedirect("/users/login?error=ACCESS_DENIED");
  }
}
