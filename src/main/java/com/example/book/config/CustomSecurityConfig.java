package com.example.book.config;

import com.example.book.security.CustomUserDetailsService;
import com.example.book.security.handler.Custom403Handler;
import com.example.book.security.handler.CustomSocialLoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import javax.sql.DataSource;

@Log4j2
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class CustomSecurityConfig {
  private final DataSource dataSource;
  private final CustomUserDetailsService userDetailsService;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    log.info("------------------configure----------------------");
    http
      .csrf(csrf -> csrf
        .ignoringRequestMatchers("/h2-console/**")// 필요하면 특정 경로만 예외
      )
//      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/", "/favicon.ico", "/error",
          "/css/**", "/js/**", "/images/**", "/assets/**", "/webjars/**").permitAll()
        // 2.2 화면들
        .requestMatchers("/users/login", "/users/userRegister", "/users/verify",
          "/users/searchAndResend").permitAll()
        // 2.3 공개 API (resend/id/pw)
        .requestMatchers("/users/resend", "/users/idSearch", "/users/pwSearch").permitAll()
        // 2.4 그 외는 인증
        .anyRequest().authenticated()
      )
      .logout(logout -> logout
        .logoutUrl("/users/logout")
        .logoutSuccessUrl("/users/login?logout")
        .invalidateHttpSession(true)
        .deleteCookies("JSESSIONID")
      )
      .formLogin(form -> form
        .loginPage("/users/login")
        .loginProcessingUrl("/users/login")
        .failureHandler(authFailureHandler())
        .defaultSuccessUrl("/", true)
      )
      .rememberMe(httpSecurityRememberMeConfigurer -> { //자동 로그인 기능 처리하는 부분
        httpSecurityRememberMeConfigurer
          .key("12345678")
          .tokenRepository(persistentTokenRepository())
          .userDetailsService(userDetailsService)
          .tokenValiditySeconds(60*60*24*30);
      })
      .exceptionHandling( httpSecurityExceptionHandlingConfigurer -> { //403에러를 처리하는 부분
        httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(accessDeniedHandler());
      })
      .oauth2Login(httpSecurityOauth2LoginConfigurer -> {
//      httpSecurityOauth2LoginConfigurer.loginPage("/member/login");
        httpSecurityOauth2LoginConfigurer.successHandler(authenticationSuccessHandler());
      });

    return http.build();
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    log.info("-----------------web configure-------------------");
    return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations()); //정적 자원들은 필터에서 제외한다는 뜻
  }

  @Bean
  public PersistentTokenRepository persistentTokenRepository() {
    JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
    repo.setDataSource(dataSource);

    return repo;
  }

  @Bean
  public AccessDeniedHandler accessDeniedHandler() {
    return new Custom403Handler();
  }

  @Bean
  public AuthenticationSuccessHandler authenticationSuccessHandler() {
    return new CustomSocialLoginSuccessHandler(passwordEncoder());
  }

  @Bean
  public AuthenticationFailureHandler authFailureHandler() {
    return (request, response, ex) -> {
      String code = "bad";
      if (ex instanceof org.springframework.security.authentication.DisabledException) {
        code = "disabled";
      }
      response.sendRedirect("/users/login?error=" + code);
    };
  }
}