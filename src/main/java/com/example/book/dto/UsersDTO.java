package com.example.book.dto;

import lombok.Data;

@Data
public class UsersDTO {
  private Long userNo;
  private String realName;
  private String userId;
  private String password;
  private String email;
  private boolean social;
  private boolean enabled;
  private String profileImage;
  private boolean termsCheck;   // 약관동의
  private boolean privacyCheck; // 개인정보 수집동의
}
