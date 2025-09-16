package com.example.book.security;

import com.example.book.domain.Member;
import com.example.book.security.dto.MemberSecurityDTO;
import com.example.book.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private PasswordEncoder passwordEncoder;
//    public CustomUserDetailsService(){
//        this.passwordEncoder = new BCryptPasswordEncoder();
//    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        log.info("loadUserByUsername: "+username);
        Optional<Member> result = memberRepository.getWithRoles(username);
        if(result.isEmpty()){
            throw new UsernameNotFoundException("username not found....");
        }
        Member member = result.get();
        MemberSecurityDTO memberSecurityDTO =
                new MemberSecurityDTO(
                        member.getMid(),
                        member.getMpw(),
                        member.getEmail(),
                        member.isDel(),
                        false,
                        member.getRoleSet()
                                .stream().map(memberRole -> new SimpleGrantedAuthority(
                                        "ROLE_"+memberRole.name())).collect(Collectors.toList())
                );
        log.info("memberSecurityDTO");
        log.info(memberSecurityDTO);
//        UserDetails userDetails = User.builder().username("user1")
//                .password("1111")
//                .password(passwordEncoder.encode("1111"))
//                .authorities("ROLE_USER")
//                .build();
//        return userDetails;
//        return null;
        return memberSecurityDTO;
    }
}
