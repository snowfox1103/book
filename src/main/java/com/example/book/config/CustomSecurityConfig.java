package com.example.book.config;

import com.example.book.security.CustomUserDetailsService;
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
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        log.info("------------configure-------------");
//        http.formLogin(withDefaults());
        http.formLogin(httpSecurityFormLoginConfigurer ->
                httpSecurityFormLoginConfigurer.loginPage("/member/login"));
        http.csrf(httpSecurityCsrfConfigurer ->
                httpSecurityCsrfConfigurer.disable());

        http.rememberMe(httpSecurityRememberMeConfigurer ->
                httpSecurityRememberMeConfigurer
                .key("12345678")
                .tokenRepository(persistentTokenRepository())
                .userDetailsService(userDetailsService)
                .tokenValiditySeconds(60*60*24*30)); //토큰이 유지되는 시간, 1달로 설정

        http.exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(accessDeniedHandler()));

        http.oauth2Login(httpSecurityOAuth2LoginConfigurer ->
//                httpSecurityOAuth2LoginConfigurer.loginPage("/member/login"));
                httpSecurityOAuth2LoginConfigurer.successHandler(authenticationSuccessHandler()));
        return http.build();
    }
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(){
        return new CustomSocialLoginSuccessHandler(passwordEncoder());
    }
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        log.info("------------web configure-------------");
        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }
    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return new Custom403Handler();
    }
}
