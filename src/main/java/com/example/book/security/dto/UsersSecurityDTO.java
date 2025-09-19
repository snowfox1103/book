package com.example.book.security.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;

@Getter
@Setter
@ToString
public class UsersSecurityDTO extends User implements OAuth2User {
    private final Long userNo;
    private String realName;
    private String userId;
    private String password;
    private String email;
    private String role;
    private boolean social;
    private boolean enabled;
    private java.util.Map<String, Object> props; //소셜 로그인 정보

    public UsersSecurityDTO(
            Long userNo,
            String realName,
            String userId,
            String password,
            String email,
            boolean social,
            boolean enabled,
            Collection<? extends GrantedAuthority> authorities) {
        super(userId, password, enabled, true, true, true, authorities);
        this.userNo = userNo;
        this.realName = realName;
        this.userId = userId;
        this.email = email;
        this.social = social;
        this.enabled = enabled;
        // UsersSecurityDTO가 UserDetails를 상속하고 있고, password는 부모 생성자에게 이미 세팅되어 있어서 제외함 0919 석준영
    }

    @Override
    public java.util.Map<String, Object> getAttributes() {
        return this.getProps();
    }

    @Override
    public String getName() {
        return this.userId;
    }
}
