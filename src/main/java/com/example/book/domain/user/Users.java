package com.example.book.domain.user;

import com.example.book.domain.finance.Subscriptions;
import com.example.book.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "role")
public class Users extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userNo")
    private Long userNo;

    @Column(name = "realName")
    private String realName;

    @Column(name = "userId")
    private String userId;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private MemberRole role;

    private boolean social = false;
    private boolean enabled = false;
    private String profileImage;

    @Column(name = "firstLogin")
    private boolean firstLogin;

    @Column(name="termsCheck")
    private boolean termsCheck;   // 약관동의

    @Column(name = "privacyCheck")
    private boolean privacyCheck; // 개인정보 수집동의

    @Builder.Default
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subscriptions> subscriptions = new ArrayList<>();

    public void setRole(MemberRole role) {
        this.role = role;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void changeEmail(String email) {
        this.email = email;
    }

    public void applyRegistration(String realName, String userId, String encodedPassword) {
        this.realName = realName;
        this.userId = userId;
        this.password = encodedPassword;
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    @Column(nullable = false)
    private long balance = 0L;

    public long getBalance() { return balance; }
    public void setBalance(long balance) { this.balance = balance; }

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "userNo", insertable=false, updatable=false)
//    private Users author;   // Users 엔티티 (userId, realName 등 보유)
}
