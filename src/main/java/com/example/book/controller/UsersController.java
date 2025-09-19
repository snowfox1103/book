package com.example.book.controller;

import com.example.book.domain.user.EmailVerificationToken;
import com.example.book.domain.user.Users;
import com.example.book.dto.EmailChangeRequest;
import com.example.book.dto.PasswordChangeRequest;
import com.example.book.dto.UsersDTO;
import com.example.book.repository.EmailVerificationTokenRepository;
import com.example.book.repository.UsersRepository;
import com.example.book.security.dto.UsersSecurityDTO;
import com.example.book.service.EmailService;
import com.example.book.service.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Controller
//@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/users")
public class UsersController {
  private final UsersService usersService;
  private final EmailService emailService;
  private final EmailVerificationTokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final UsersRepository usersRepository;
  private final ModelMapper modelMapper;

  @GetMapping("/login")
  public String loginGET(@RequestParam(value = "error", required = false) String error,
                         @RequestParam(value = "logout", required = false) String logout,
                         Model model) {
    log.info("login get .................");
    log.info("logout: " + logout);
    if (logout != null) {
      log.info("user logout..............");
    }

    if (error != null) {
      log.info("login error..............");
      model.addAttribute("errorMsg", "아이디 또는 비밀번호가 잘못되었습니다.");
    }

    return "/users/login"; // login.html 경로
  }

  @GetMapping("/userRegister")
  public void joinGet() {
    log.info("userRegister get.........");
  }

  @PostMapping("/userRegister")
  public String userRegisterPost(UsersDTO usersDTO, RedirectAttributes redirectAttributes) {
    log.info("userRegister post.........");
    log.info(usersDTO);
    try {
      Users users = usersService.register(usersDTO);

      emailService.sendVerificationEmail(users);
      redirectAttributes.addFlashAttribute("message", "Checking Email");

      return "redirect:/users/login";
    } catch(UsersService.userIdExistsException e) {
      redirectAttributes.addFlashAttribute("error", "userId");

      return "redirect:/users/userRegister";
    } catch(UsersService.emailExistsException e) {
      redirectAttributes.addFlashAttribute("error", "email");

      return "redirect:/users/userRegister";
    }
  }

  @GetMapping("/verify")
  public String verifyEmail(@RequestParam String token, RedirectAttributes redirectAttributes) {
    var result = emailService.verify(token);
    Optional<EmailVerificationToken> verificationToken = tokenRepository.findByToken(token);

    switch (result) {
      case SUCCESS -> {
        Users users = verificationToken.get().getUsers();
        users.enable(); // 엔티티 메서드로 enabled=true 설정(세터 대신)
        usersRepository.save(users);
        tokenRepository.deleteByUsers(users);

        redirectAttributes.addFlashAttribute("msg", "이메일 인증이 완료되었습니다. 로그인해 주세요.");
        return "redirect:/users/login";
      }
      case EXPIRED -> {
        // 만료: 토큰으로 유저를 알아낼 수 있으면 자동 재발송까지 처리
        boolean resent = emailService.resendByTokenIfPossible(token);
        redirectAttributes.addFlashAttribute("msg", resent
          ? "토큰이 만료되어 새 인증 메일을 보냈습니다. 메일함을 확인해 주세요."
          : "토큰이 만료되었습니다. 이메일을 입력하여 인증 메일을 다시 받아주세요.");
        return resent ? "redirect:/users/login" : "redirect:/users/resend";
      }
      case INVALID -> {
        redirectAttributes.addFlashAttribute("msg", "유효하지 않은 링크입니다. 이메일을 입력하면 인증 메일을 다시 보낼 수 있어요.");
        return "redirect:/users/resend";
      }
      case ALREADY_USED, ALREADY_VERIFIED -> {
        redirectAttributes.addFlashAttribute("msg", "이미 인증된 계정입니다. 바로 로그인해 주세요.");
        return "redirect:/users/login";
      }
    }

    redirectAttributes.addFlashAttribute("msg", "인증 처리 중 문제가 발생했습니다.");

    return "redirect:/users/login";
  }

  @GetMapping("/resend")
  public String resendPage() {
    return "users/resend"; // 이메일 입력 form 있는 뷰
  }

  @PostMapping("/resend")
  public String resend(@RequestParam String email, RedirectAttributes redirectAttributes) {
    emailService.resendByEmailSilently(email);
    redirectAttributes.addFlashAttribute("msg", "입력한 주소로(존재한다면) 인증 메일을 보냈습니다.");

    return "redirect:/users/login";
  }

  @GetMapping("/userUnregister")
  @PreAuthorize("isAuthenticated()")
  public void unregisterGet(UsersDTO usersDTO) {
    log.info("unregister get...............");
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping("/userUnregister")
  @Transactional
  public String unregisterPost(@AuthenticationPrincipal UsersSecurityDTO principal,
                               HttpServletRequest request,
                               HttpServletResponse response,
                               RedirectAttributes redirectAttributes) {
    // 1) 현재 로그인한 사용자 로드 (DTO 신뢰 X)
    Users users = usersRepository.findByUserId(principal.getUserId())
      .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));
    Long userNo = users.getUserNo();

    // 2) 자식 데이터 먼저 삭제 (토큰/구독 등)
    tokenRepository.deleteByUsers_UserNo(userNo);
//    subscriptionsRepository.deleteByUsers_UserNo(userNo);
    // 다른 연관도 있으면 같은 방식으로

    // 3) 사용자 삭제
    usersRepository.deleteByUserId(users.getUserId()); // 또는 usersRepository.deleteByUserId(user.getUserId());

    // 4) 로그아웃 처리
    new SecurityContextLogoutHandler().logout(request, response, null);

    // 5) 메시지/리다이렉트
    redirectAttributes.addFlashAttribute("msg", "탈퇴가 완료되었습니다.");

    return "redirect:/users/login?unregistered";
  }

  @GetMapping("/userPasswordModify")
  @PreAuthorize("isAuthenticated()")
  public void changePasswordGet() {log.info("changePassword get...............");}

  @PostMapping("/userPasswordModify")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> changePassword(@AuthenticationPrincipal UsersSecurityDTO principal,
                                          @Valid @RequestBody PasswordChangeRequest passwordChangeRequest) {
    usersService.changePassword(principal.getUserId(), passwordChangeRequest);

    return ResponseEntity.ok().build();
  }

  @GetMapping("/userEmailModify")
  public void changeEmailGet() {
    log.info("changeEmail get...............");
  }

  @PostMapping("/userEmailModify")
  public ResponseEntity<?> chagneEmail(@AuthenticationPrincipal UsersSecurityDTO principal,
                                       @Valid @RequestBody EmailChangeRequest emailChangeRequest) {
    log.info(principal.getUserId());
    log.info(emailChangeRequest.getNewEmail());
    usersService.changeEmail(principal.getUserId(), emailChangeRequest);

    return ResponseEntity.ok().build();
  }
}

