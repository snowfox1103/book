package com.example.book.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

@Log4j2
public class Custom401Handler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {

        log.info("---- UNAUTHORIZED(401) :: uri={} ----", request.getRequestURI());

        // AJAX/JSON 요청 식별 (null-safe)
        String contentType = request.getHeader("Content-Type");
        String accept = request.getHeader("Accept");
        String xrw = request.getHeader("X-Requested-With");
        boolean isJson = (contentType != null && contentType.startsWith("application/json"))
                || (accept != null && accept.contains("application/json"))
                || "XMLHttpRequest".equalsIgnoreCase(xrw);

        if (isJson) {
            // API/AJAX 요청은 JSON으로 401 응답
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"UNAUTHORIZED\",\"message\":\"로그인이 필요합니다.\"}");
            return;
        }

        // 그 외(브라우저 페이지)는 Spring Boot 기본 에러 매핑을 활용 → templates/error/401.html 렌더링
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
