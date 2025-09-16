package com.example.book.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "roleSet")
public class Member {
    @Id
    private String mid;
    private String mpw;
    private String email;
    private boolean del;
    private boolean social; //소셜로그인

//    @Enumerated(EnumType.STRING)//0,1이 아닌 string 타입으로 생성
    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private Set<MemberRole> roleSet = new HashSet<>();
    public void changePassword(String mpw){
        this.mpw = mpw;
    }
    public void changeEmail(String email){
        this.email = email;
    }
    public void changeDel(boolean del){
        this.del = del;
    }
    public void addRole(MemberRole memberRole){
        this.roleSet.add(memberRole);
    }
    public void clearRoles(){
        this.roleSet.clear();
    }
    public void changeSocial(boolean social){
        this.social = social;
    }
}
