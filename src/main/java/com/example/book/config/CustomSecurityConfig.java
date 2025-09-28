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
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
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

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    //로그인 안되는 오류로 추가 0928 석준영
    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService); // 네 CustomUserDetailsService
        p.setPasswordEncoder(passwordEncoder);
        return p;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("------------------configure----------------------");
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/trans/**", "/budget/**")// 필요하면 특정 경로만 예외
                )
//      .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error", "/css/**", "/js/**", "/images/**", "/assets/**", "/webjars/**", "/fragments/**").permitAll()
                        // 2.2 화면들
                        .requestMatchers("/users/login", "/users/checkUserId", "/users/checkEmail", "/users/userRegister", "/users/verify",
                                "/users/searchAndResend").permitAll()
                        // 2.3 공개 API (resend/id/pw)
                        .requestMatchers("/users/resend", "/users/idSearch", "/users/pwSearch").permitAll()
                        // 2.4 그 외는 인증
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout
                        .logoutUrl("/users/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            // 세션을 완전히 무효화
                            request.getSession().invalidate();
                            // 새 세션 강제 생성
                            request.getSession(true);

                            // 리다이렉트
                            response.sendRedirect("/users/login?logout");
                        })
                        .permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/users/login")
                        .loginProcessingUrl("/users/login")
                        .usernameParameter("username") // login이 안 되서 추가함 0928 석준영 >> username으로 수정 0928 조덕진
                        .passwordParameter("password") // login이 안 되서 추가함 0928 석준영
                        .failureHandler(authFailureHandler())
                        .defaultSuccessUrl("/mainPage/mainpage", true) //0925 조덕진 로그인 성공시 메인페이지로 이동하도록 수정

                )
                .rememberMe(httpSecurityRememberMeConfigurer -> { //자동 로그인 기능 처리하는 부분
                    httpSecurityRememberMeConfigurer
                            .key("12345678")
                            .tokenRepository(persistentTokenRepository())
                            .userDetailsService(userDetailsService)
                            .tokenValiditySeconds(60 * 60 * 24 * 30);
                })
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(new Custom403Handler()) // 등록
                )
                .oauth2Login(httpSecurityOauth2LoginConfigurer -> {
//      httpSecurityOauth2LoginConfigurer.loginPage("/member/login");
                    httpSecurityOauth2LoginConfigurer.successHandler(authenticationSuccessHandler());
                });

        return http.build();
    }
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        log.info("===== Security configure =====");
//
//        // CSRF – 필요 시 다시 켜자 (H2 콘솔 쓰면 .ignoringRequestMatchers("/h2-console/**") 추가)

    /// /    http.csrf(csrf -> csrf.disable()); 로그인 기능 활성화로 주석처리 0918 석준영
//
//        // 폼 로그인
//        http.formLogin(form -> form
//                .loginPage("/users/login")
//                .loginProcessingUrl("/users/login")
//                .usernameParameter("userId")
//                .passwordParameter("password")
//                .defaultSuccessUrl("/", true)
//                .permitAll()
//        );
//
//        // OAuth2 로그인
//        http.oauth2Login(oauth2 -> oauth2
//                .loginPage("/users/login")
//                .successHandler(authenticationSuccessHandler())
//        );
//
//        // 401/403 처리
//        http.exceptionHandling(ex -> ex
//                .authenticationEntryPoint(new Custom401Handler()) // 401
//                .accessDeniedHandler(accessDeniedHandler())       // 403
//        );
//
//        // remember-me
//        http.rememberMe(remember -> remember
//                .key("12345678")
//                .tokenRepository(persistentTokenRepository())
//                .userDetailsService(userDetailsService)
//                .tokenValiditySeconds(60 * 60 * 24 * 30)
//        );
//
//        // 인가 규칙
//        http.authorizeHttpRequests(auth -> auth
//                // 정적/에러/로그인 허용
//                .requestMatchers("/error/**", "/users/login", "/css/**", "/js/**", "/images/**").permitAll()
//
//                // 공지: 읽기 허용, 쓰기/수정/삭제는 ADMIN
//                .requestMatchers(HttpMethod.GET, "/notice/**").permitAll()
//                .requestMatchers("/notice/write", "/notice/*/edit", "/notice/*/delete").hasRole("ADMIN")
//
//                // 관리자 페이지
//                .requestMatchers("/admin/**").hasRole("ADMIN")
//
//                // QnA: 로그인 필요(팀 규칙대로). 테스트 중이면 .permitAll()로 잠깐 바꿔도 됨
//                .requestMatchers("/qna/**").authenticated()
//
//                // 그 외
//                .anyRequest().permitAll()
//        );
//
//        return http.build();
//    }
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
    AuthenticationFailureHandler authFailureHandler() {
        return (request, response, ex) -> {
            ex.printStackTrace(); // 에러 확인용
            String code = "bad";
            if (ex instanceof org.springframework.security.authentication.DisabledException) {
                code = "disabled";
            }
            response.sendRedirect("/users/login?error=" + code);
        };
    }
}