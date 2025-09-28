package com.example.book.service;

import com.example.book.domain.finance.Budgets;
import com.example.book.domain.user.MemberRole;
import com.example.book.domain.user.Users;
import com.example.book.dto.*;
import com.example.book.repository.BudgetsRepository;
import com.example.book.repository.EmailVerificationTokenRepository;
import com.example.book.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
  private final BudgetsRepository budgetsRepository;

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
//    Users users = modelMapper.map(usersDTO, Users.class);
    Users newUsers = Users.builder()
      .realName(usersDTO.getRealName())
      .userId(usersDTO.getUserId())
      .email(usersDTO.getEmail())
      .password(passwordEncoder.encode(usersDTO.getPassword()))
      .role(MemberRole.USER)
      .social(false)
      .enabled(false)   // 중요: 미인증 상태
      .privacyCheck(true)
      .termsCheck(true)
      .build();

//    users.changePassword(passwordEncoder.encode(usersDTO.getPassword()));
//    users.setRole(MemberRole.USER);
    log.info("============================");
    log.info(newUsers);
    log.info(newUsers.getRole());
    usersRepository.save(newUsers);

    //유저 예산 0으로 설정
    Budgets budgets = Budgets.builder()
      .userNo(newUsers.getUserNo())
      .budCategory(0L)  // 카테고리 0 (전체/미분류 같은 의미)
      .budAmount(0L)    // 설정금액
      .budCurrent(0L)   // 사용금액
      .budIsOver(false) // 처음엔 초과 아님
      .budYear(LocalDate.now().getYear())
      .budMonth(LocalDate.now().getMonthValue())
      .build();

    budgetsRepository.save(budgets);

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
  @Transactional
  public void changePassword(String userId, PasswordChangeRequestDTO req) {
    Users users = usersRepository.findByUserId(userId)
      .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

    // ✅ 소셜 로그인 + firstLogin이면 현재 비밀번호 확인 건너뛰고 변경
    if (users.isSocial() && Boolean.TRUE.equals(users.isFirstLogin())) {
      // 새 비밀번호 확인
      if (!req.getNewPassword().equals(req.getConfirmPassword())) {
        throw new IllegalArgumentException("새 비밀번호와 확인이 일치하지 않습니다.");
      }
      if (passwordEncoder.matches(req.getNewPassword(), users.getPassword())) {
        throw new IllegalArgumentException("새 비밀번호는 기존과 달라야 합니다.");
      }

      users.changePassword(passwordEncoder.encode(req.getNewPassword())); // 엔티티 메서드 사용
      // 약관 동의 값 저장 (Users 엔티티에 이런 필드가 있어야 함)
      users.setTermsCheck(true);
      users.setPrivacyCheck(true);
      users.setFirstLogin(false); // 첫 로그인 처리 완료
      usersRepository.save(users);
      return;
    }

    // ✅ 일반 사용자(또는 이미 firstLogin 처리된 사용자)
    if (!passwordEncoder.matches(req.getCurrentPassword(), users.getPassword())) {
      throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
    }
    if (!req.getNewPassword().equals(req.getConfirmPassword())) {
      throw new IllegalArgumentException("새 비밀번호와 확인이 일치하지 않습니다.");
    }
    if (passwordEncoder.matches(req.getNewPassword(), users.getPassword())) {
      throw new IllegalArgumentException("새 비밀번호는 기존과 달라야 합니다.");
    }

    users.changePassword(passwordEncoder.encode(req.getNewPassword())); // 엔티티 메서드 사용
    usersRepository.save(users);
  }

  @Override
  @Transactional
  public void changeEmail(String userId, EmailChangeRequestDTO req) {
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
//     tokenRepository.save(EmailVerificationToken.newToken(users, Duration.ofMinutes(30))); //이게 문제였네?
//     log.info("newToken insert success ........... ");
    emailService.sendVerificationEmail(users);
    log.info("email send success ........... ");
  }

  @Override
  @Transactional
  public void resend(ResendRequestDTO req) {
    String email = req.getEmail();
    log.info("resend email: " + email);
    Users users = usersRepository.findByEmail(email)
      .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

    if (users.isEnabled()) {
      throw new IllegalArgumentException("이미 인증된 계정입니다.");
    }
    log.info("find user success..........");
    tokenRepository.deleteByUsers_UserNo(users.getUserNo());
    log.info("delete success.............");
    emailService.sendVerificationEmail(users);
    log.info("email send success ............");
  }

  @Override
  public void idSearch(IdSearchRequestDTO req) {
    String email = req.getEmail();
    log.info("idSearch email: " + email);
    Users users = usersRepository.findByEmail(email)
      .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

    emailService.sendId(email);
  }

  @Override
  @Transactional
  public void pwSearch(PwSearchRequestDTO req) {
    String userId = req.getUserId();
    String email = req.getEmail();
    log.info("pwSearch userId: " + userId);
    Users users = usersRepository.findByUserId(userId)
      .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

    users.changePassword(passwordEncoder.encode(email+"password"));
    log.info("pw change success ...........");
    emailService.sendPw(email);
    log.info("pw email send success ...........");
  }

  @Override
  public String getUserIdByUserNo(Long userNo) {
    if (userNo == null) return "-";
    return usersRepository.findById(userNo)
            .map(Users::getUserId)   // Users 엔티티에 getUserId() 존재 가정
            .orElse("-");
  }

  @Override
  public int getCurrentPoint(Long userNo) {
    return usersRepository.getBalance(userNo);
  }

  @Override
  @Transactional
  public void updateBalance(Long userNo, int newBalance) {
    Users user = usersRepository.findById(userNo)
      .orElseThrow(() -> new IllegalArgumentException("유저 없음"));
    user.setBalance(newBalance);
  }
}
