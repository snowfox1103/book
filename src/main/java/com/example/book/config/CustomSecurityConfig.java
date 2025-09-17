package com.example.book.config;

import com.example.book.security.CustomUserDetailsService;
import com.example.book.security.handler.Custom401Handler;
import com.example.book.security.handler.Custom403Handler;
import com.example.book.security.handler.CustomSocialLoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

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
//    http.formLogin(withDefaults());
    http.formLogin(httpSecurityFormLoginConfigurer -> {
      httpSecurityFormLoginConfigurer.loginPage("/users/login");
    });

    http.csrf(httpSecurityCsrfConfigurer -> {
      httpSecurityCsrfConfigurer.disable();
    });

    //자동 로그인 기능 처리하는 부분?
    http.rememberMe(httpSecurityRememberMeConfigurer -> {
      httpSecurityRememberMeConfigurer
        .key("12345678")
        .tokenRepository(persistentTokenRepository())
        .userDetailsService(userDetailsService)
        .tokenValiditySeconds(60*60*24*30);
    });

    //403에러를 처리하는 부분
    http.exceptionHandling( httpSecurityExceptionHandlingConfigurer -> {
      httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(accessDeniedHandler());
    });

    // (추가) 401 핸들러 0916 석준영
    http.exceptionHandling(c -> c.authenticationEntryPoint(new Custom401Handler()));

    // qna 인증용 0916 석준영
    http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/error/**", "/users/login", "/css/**", "/js/**", "/images/**").permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/qna/**").authenticated()
            .anyRequest().permitAll()
    );

    http.oauth2Login(httpSecurityOauth2LoginConfigurer -> {
//      httpSecurityOauth2LoginConfigurer.loginPage("/member/login");
      httpSecurityOauth2LoginConfigurer.successHandler(authenticationSuccessHandler());
    });

    return http.build();
  }

//  @Bean
//  public AuthenticationSuccessHandler authenticationSuccessHandler() {
//    return new CustomSocialLoginSuccessHandler(passwordEncoder());
//  }

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
}