package com.example.book.repository;

import com.example.book.domain.MemberRole;
import com.example.book.domain.Users;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.IntStream;
@Log4j2
@SpringBootTest
public class UsersRepositoryTests {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Test
    public void insertUsers(){
        IntStream.rangeClosed(1,100).forEach(i->{
            Users users = Users.builder()
                    .realName("kim"+i)
                    .userId("user"+i)
                    .password(passwordEncoder.encode("1111"))
                    .email("email"+i+"@naver.com")
                    .role(MemberRole.USER)
                    .build();
            usersRepository.save(users);
        });
    }
    @Test
    public void testSelect(){
        Long uno = 22L;
        Optional<Users> result = usersRepository.findById(uno);
        Users users = result.orElseThrow();
        log.info(users);
    }
    @Test
    public void testUpdate(){

    }
}
