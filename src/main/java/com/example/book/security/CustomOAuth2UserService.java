package com.example.book.security;
import com.example.book.domain.Member;
import com.example.book.domain.MemberRole;
import com.example.book.repository.MemberRepository;
import com.example.book.security.dto.MemberSecurityDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("userRequest....");
        log.info(userRequest);
        //       return super.loadUser(userRequest);
        log.info("oAuth2User-------------------------------------");
        // 클라이언트 등록 정보를 가져옴
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        // 클라이언트 이름을 가져옴
        String clientName = clientRegistration.getClientName();
        log.info("NAME: " + clientName);
        // OAuth2 사용자 정보를 로드
        OAuth2User oAuth2User = super.loadUser(userRequest);
        // 사용자 속성 정보를 맵 형태로 가져옴
        Map<String, Object> paramMap = oAuth2User.getAttributes();
//        // 사용자 속성 정보 출력
//        paramMap.forEach((k, v) -> {
//            log.info("-------------------------------------");
//            log.info(k + ":" + v);
//        });
//        // 최종적으로 OAuth2User 객체 반환
//        return oAuth2User;
        String email = null;
        switch (clientName){
            case "kakao":
                email = getKakaoEmail(paramMap);
                break;
        }
        log.info("===============================");
        log.info(email);
        log.info("===============================");
//        return oAuth2User;
        return generateDTO(email, paramMap);
    }
    private MemberSecurityDTO generateDTO(String email, Map<String, Object> params) {
        // 이메일을 기준으로 데이터베이스에서 회원 정보를 조회
        Optional<Member> result = memberRepository.findByEmail(email);
        // 데이터베이스에 해당 이메일을 가진 사용자가 없다면
        if (result.isEmpty()) {
            // 새로운 회원 추가 (mid는 이메일 주소, 패스워드는 "1111"로 설정)
            Member member = Member.builder()
                    .mid(email) // 회원 ID로 이메일 사용
                    .mpw(passwordEncoder.encode("1111")) // 기본 패스워드 설정 (암호화)
                    .email(email) // 이메일 설정
                    .social(true) // 소셜 로그인 사용자로 설정
                    .build();
            // 기본 권한(ROLE_USER) 부여
            member.addRole(MemberRole.USER);
            // 회원 정보 저장
            memberRepository.save(member);
            // MemberSecurityDTO 생성 및 반환
            MemberSecurityDTO memberSecurityDTO =
                    new MemberSecurityDTO(email, "1111", email, false, true,
                            Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));

            // 추가 속성 설정
            memberSecurityDTO.setProps(params);
            return memberSecurityDTO;
        } else {
            // 기존 회원 정보 가져오기
            Member member = result.get();
            // MemberSecurityDTO 생성 및 반환
            MemberSecurityDTO memberSecurityDTO =
                    new MemberSecurityDTO(
                            member.getMid(), // 회원 ID
                            member.getMpw(), // 암호화된 비밀번호
                            member.getEmail(), // 이메일
                            member.isDel(), // 삭제 여부
                            member.isSocial(), // 소셜 로그인 여부
                            member.getRoleSet().stream()
                                    .map(memberRole -> new SimpleGrantedAuthority("ROLE_" + memberRole.name())) // 권한 매핑
                                    .collect(Collectors.toList()) // 리스트로 변환
                    );
            return memberSecurityDTO;
        }
    }
    private String getKakaoEmail(Map<String, Object> paramMap){
        log.info("KAKAO-----------------------------------------");
        Object value = paramMap.get("kakao_account");
        log.info(value);
        LinkedHashMap accountMap = (LinkedHashMap) value;
        String email = (String)accountMap.get("email");
        log.info("email..." + email);
        return email;
    }

}