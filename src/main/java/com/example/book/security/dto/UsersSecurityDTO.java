package com.example.book.security.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;
import java.util.stream.Stream;

@Getter
@Setter
@ToString
public class UsersSecurityDTO extends User implements OAuth2User {
  private Long userNo;
  private String realName;
  private String userId;
  private String password;
  private String email;
  private String role;
  private boolean social;
  private boolean enabled;
  private Map<String, Object> props;

  public UsersSecurityDTO(
          Long userNo, String realName, String userId, String password, String email,
          boolean social, boolean enabled,
          Collection<? extends GrantedAuthority> authorities
  ) {
    super(userId, password, enabled, true, true, true, authorities);
    this.userNo = userNo;
    this.realName = realName;
    this.userId = userId;
    this.password = password;
    this.email = email;
    this.social = social;
    this.enabled = enabled;
  }

  // ====== Role helpers ======

  /** 주어진 역할을 보유했는지 검사 (ex. "ADMIN" 또는 "ROLE_ADMIN" 모두 허용) */
  public boolean hasRole(String role) {
    if (role == null) return false;
    String target = role.startsWith("ROLE_") ? role : "ROLE_" + role;
    return getAuthorities() != null &&
            getAuthorities().stream().anyMatch(a -> target.equals(a.getAuthority()));
  }

  /** 주어진 역할 중 하나라도 보유했는지 검사 */
  public boolean hasAnyRole(String... roles) {
    if (roles == null || roles.length == 0) return false;
    return Stream.of(roles).anyMatch(this::hasRole);
  }

  /** 관리자 편의 메서드 */
  public boolean isAdmin() { return hasRole("ADMIN"); }

  /** 일반 사용자 편의 메서드 */
  public boolean isUser() { return hasRole("USER"); }

  @Override
  public Map<String, Object> getAttributes() {
    return this.getProps();
  }

  @Override
  public String getName() {
    return this.userId;
  }
}
