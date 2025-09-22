package com.example.book.controller;

import com.example.book.domain.user.EmailVerificationToken;
import com.example.book.domain.user.Users;
import com.example.book.dto.*;
import com.example.book.repository.EmailVerificationTokenRepository;
import com.example.book.repository.UsersRepository;
import com.example.book.service.EmailService;
import com.example.book.service.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

//@RestController
@Controller
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

  //, consumes=MediaType.APPLICATION_JSON_VALUE
  @PostMapping(value="/resend")
  public ResponseEntity<?> resendPost(@RequestBody @Valid ResendRequestDTO req) {
    log.info("HIT /users/resend with {}", req.getEmail());
    // 존재/미인증 사용자에게만 재발송.
    // 이메일 존재 여부는 절대 응답으로 노출하지 않음(계정 추측 방지).
    try {
      usersService.resend(req); // 내부에서 enabled 확인 + 토큰 재발송
    } catch (Exception ignored) {
      // 일부러 무시: "있든 없든 보냈다"로 응답
    }
    return ResponseEntity.noContent().build(); // 204
  }

  @PostMapping("/idSearch")
  public ResponseEntity<?> idSearch(@RequestBody @Valid IdSearchRequestDTO req) {
    log.info("id search Post ...............");
    usersService.idSearch(req);

    return ResponseEntity.ok().build();
  }

  @PostMapping("/pwSearch")
  public ResponseEntity<?> pwSearch(@RequestBody @Valid PwSearchRequestDTO req) {
    log.info("pwSearch Post ...............");
    usersService.pwSearch(req); //user의 email + "password"로 비밀번호 변경 후 메일로 보냄

    return ResponseEntity.ok().build();
  }

  @GetMapping("/searchAndResend")
  public void searchAndResend() {
    log.info("searchAndResend get...............");
  }
}

