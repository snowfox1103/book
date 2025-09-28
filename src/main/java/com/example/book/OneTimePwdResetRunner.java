package com.example.book;

import com.example.book.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev") // 로컬에서만 돌리고 싶으면 유지, 전체에서 한번만 돌릴거면 이 줄 잠시 지워도 됨
@RequiredArgsConstructor
public class OneTimePwdResetRunner implements ApplicationRunner {

    private final UsersRepository usersRepository;

    @Override
    public void run(ApplicationArguments args) {
        String userId = "admin";     // 초기화할 계정
        String raw    = "pass1234";  // 새 비번(로그인 폼에서 입력할 원문)

        usersRepository.findByUserId(userId).ifPresent(u -> {
            u.setSocial(false); // getWithRoles가 social=false만 찾으므로 보정
            u.setPassword(new BCryptPasswordEncoder().encode(raw));
            System.out.println("[reset] password updated for " + userId);
        });
    }
}
