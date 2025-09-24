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
}
