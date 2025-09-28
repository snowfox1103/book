package com.example.book.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class UsersServiceImplTests {
  @Autowired
  private UsersService usersService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Test
  void isTure() {
//    String dbPassword = passwordEncoder.encode("tjwja457@gmail.compassword");
    System.out.println(passwordEncoder.matches("tjwja457@gmail.compassword", "{bcrypt}$2a$10$p8YWE7JdRcqzIfWfaXbitOqPvZDYpsMh.nT3W7paRq29KluOHK5q2"));
  }
}