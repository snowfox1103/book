package com.example.book.security;

import com.example.book.domain.user.MemberRole;
import com.example.book.domain.user.Users;
import com.example.book.repository.UsersRepository;
import com.example.book.security.dto.UsersSecurityDTO;
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

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
  private final UsersRepository usersRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    log.info("userRequest..............");
    log.info(userRequest);
    log.info("oauth2 user...................................");

    ClientRegistration clientRegistration = userRequest.getClientRegistration(); //클라이언트 등록정보를 가져옴
    String clientName = clientRegistration.getClientName(); //클라이언트 이름을 가져옴
    log.info("Name: " + clientName);

    OAuth2User oAuth2User = super.loadUser(userRequest); //OAuth2 사용자 정보를 로드
    Map<String, Object> paramMap = oAuth2User.getAttributes(); //사용자 속성 정보를 맵 형태로 가져옴

    String email = null;

    switch(clientName) {
      case "kakao":
        email = getKakaoEmail(paramMap);
        break;
    }
    log.info("=============================");
    log.info(email);
    log.info("=============================");

//    return oAuth2User;
    return generateDTO(email, paramMap);
  }

  private UsersSecurityDTO generateDTO(String email, Map<String, Object> params) {
    Optional<Users> result = usersRepository.findByEmail(email); // 이메일을 기준으로 데이터베이스에서 회원 정보를 조회

    if (result.isEmpty()) { // 데이터베이스에 해당 이메일을 가진 사용자가 없다면
      // 새로운 회원 추가 (userid는 이메일 주소, password는 "1111"로 설정)
      Users users = Users.builder()
        .realName("임시사용자") //이름은 임시사용자로 설정
        .userId(email) // 회원 ID로 이메일 사용
        .password(passwordEncoder.encode("1111")) // 기본 패스워드 설정 (암호화)
        .email(email) // 이메일 설정
        .social(true) // 소셜 로그인 사용자로 설정
        .enabled(true)
        .role(MemberRole.USER) // 기본 권한(ROLE_USER) 부여
        .build();

      usersRepository.save(users); // 회원 정보 저장

      UsersSecurityDTO usersSecurityDTO = new UsersSecurityDTO(users.getUserNo(), users.getRealName(), email, "1111", email, true, true,
        Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))); // MemberSecurityDTO 생성 및 반환

      usersSecurityDTO.setProps(params); // 추가 속성 설정

      return usersSecurityDTO;
    } else {
      Users users = result.get(); // 기존 회원 정보 가져오기
      // UserSecurityDTO 생성 및 반환
      UsersSecurityDTO usersSecurityDTO =
        new UsersSecurityDTO(
          users.getUserNo(),
          users.getRealName(),
          users.getUserId(), // 회원 ID
          users.getPassword(), // 암호화된 비밀번호
          users.getEmail(), // 이메일
          users.isSocial(), // 소셜 로그인 여부
          users.isEnabled(), //이메일 인증 여부
          java.util.List.of(new SimpleGrantedAuthority("ROLE_" + users.getRole().name()))
        );

      return usersSecurityDTO;
    }
  }

  private String getKakaoEmail(Map<String, Object> paramMap) {
    log.info("KAKAO.........................................");
    Object value = paramMap.get("kakao_account");
    log.info(value);

    LinkedHashMap accountMap = (LinkedHashMap) value;
    String email = (String) accountMap.get("email");
    log.info("email........" + email);

    return email;
  }
}
