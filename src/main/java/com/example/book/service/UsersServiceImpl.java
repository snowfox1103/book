package com.example.book.service;

import com.example.book.domain.MemberRole;
import com.example.book.domain.Users;
import com.example.book.dto.EmailChangeRequest;
import com.example.book.dto.PasswordChangeRequest;
import com.example.book.dto.UsersDTO;
import com.example.book.repository.EmailVerificationTokenRepository;
import com.example.book.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {
  private final ModelMapper modelMapper;
  private final UsersRepository usersRepository;
  private final PasswordEncoder passwordEncoder;
  private final EmailService emailService;
  private final EmailVerificationTokenRepository tokenRepository;

  @Override
  public Users register(UsersDTO usersDTO) throws userIdExistsException, emailExistsException {
    String userId = usersDTO.getUserId();
    boolean existId = usersRepository.existsByUserId(userId);

    if (existId) {
      throw new userIdExistsException();
    }
    Optional<Users> byEmail = usersRepository.findByEmail(usersDTO.getEmail());
    if (byEmail.isPresent()) {
      Users users = byEmail.get();
      if (Boolean.TRUE.equals(users.isEnabled())) { // 이미 인증완료된 계정
        throw new emailExistsException();
      } else { // 미인증 계정: 정보 갱신 후 토큰 재발급 & 재전송
        users.applyRegistration(
          usersDTO.getRealName(),
          usersDTO.getUserId(),
          passwordEncoder.encode(usersDTO.getPassword())
        );

        usersRepository.save(users); // enabled=false 유지
        emailService.sendVerificationEmail(users); // 내부에서 기존 토큰 삭제+신규 발급

        return users;
      }
    }
    Users newUsers = Users.builder()
      .realName(usersDTO.getRealName())
      .userId(usersDTO.getUserId())
      .email(usersDTO.getEmail())
      .password(passwordEncoder.encode(usersDTO.getPassword()))
      .role(MemberRole.USER)
      .social(false)
      .enabled(false)   // 중요: 미인증 상태
      .build();

    log.info("============================");
    log.info(newUsers);
    log.info(newUsers.getRole());

    usersRepository.save(newUsers);

    return newUsers;
  }

  @Transactional
  @Override
  public void unRegister(UsersDTO usersDTO) {
    String userId = usersDTO.getUserId();

    log.info("unRegister userId: " + userId);
    usersRepository.deleteByUserId(userId);
  }

  @Override
  public void passwordModify(UsersDTO usersDTO) {
    String userId = usersDTO.getUserId();
    Users users = modelMapper.map(usersDTO, Users.class);

    log.info("changePassword userId: " + userId);
    users.changePassword(passwordEncoder.encode(usersDTO.getPassword()));
    usersRepository.save(users);
  }

  @Override
  public void emailModify(UsersDTO usersDTO) throws emailExistsException {
    String userId = usersDTO.getUserId();
    Users users = modelMapper.map(usersDTO, Users.class);

    log.info("changeEmail userId: " + userId);
    users.changeEmail(usersDTO.getEmail());
    usersRepository.save(users);
  }

  @Override
  public void changePassword(String userId, PasswordChangeRequest req) {
    Users users = usersRepository.findByUserId(userId)
      .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

    if (!passwordEncoder.matches(req.getCurrentPassword(), users.getPassword())) {
      throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
    }
    if (!req.getNewPassword().equals(req.getConfirmPassword())) {
      throw new IllegalArgumentException("새 비밀번호와 확인이 일치하지 않습니다.");
    }
    if (passwordEncoder.matches(req.getNewPassword(), users.getPassword())) {
      throw new IllegalArgumentException("새 비밀번호는 기존과 달라야 합니다.");
    }
    log.info("changePassword success ........... ");

    users.changePassword(passwordEncoder.encode(req.getNewPassword())); // 엔티티 메서드 사용

    usersRepository.save(users);
  }

  @Override
  @Transactional
  public void changeEmail(String userId, EmailChangeRequest req) {
    Users users = usersRepository.findByUserId(userId)
      .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

    if (!users.getEmail().equals(req.getCurrentEmail())) {
      throw new IllegalArgumentException("현재 이메일이 일치하지 않습니다.");
    }
    if (usersRepository.existsByEmail(req.getNewEmail())) {
      throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
    }

    users.changeEmail(req.getNewEmail()); // 엔티티 도메인 메서드 사용(Setter 지양)
    log.info("changeEmail success ........... ");

    // (선택) 새 이메일 인증 정책
     users.disable(); //게정 비활성화
      log.info("changeEnable success ........... ");
     tokenRepository.deleteByUsers_UserNo(users.getUserNo());
     log.info("delete success ........... ");
     emailService.sendVerificationEmail(users);
     log.info("email send success ........... ");
  }
}
