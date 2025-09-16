package com.example.book.controller;

import com.example.book.domain.EmailVerificationToken;
import com.example.book.domain.Users;
import com.example.book.dto.UsersDTO;
import com.example.book.repository.EmailVerificationTokenRepository;
import com.example.book.repository.UsersRepository;
import com.example.book.service.EmailService;
import com.example.book.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

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
      redirectAttributes.addFlashAttribute("error", "userid");

      return "redirect:/users/userRegister";
    } catch(UsersService.emailExistsException e) {
      redirectAttributes.addFlashAttribute("error", "email");

      return "redirect:/users/userRegister";
    }
  }

  @GetMapping("/verify")
  public String verifyEmail(@RequestParam String token) {
    Optional<EmailVerificationToken> optToken = tokenRepository.findByToken(token);

    if(optToken.isEmpty()) {
      return "redirect:/users/login?verified=invalid";
    }

    EmailVerificationToken verificationToken = optToken.get();

    if(verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
      return "redirect:/users/login?verified=expired";
    }

    Users users = verificationToken.getUsers();
    users.setEnabled(true);
    usersRepository.save(users);

    return "redirect:/users/login?verified=ok";
  }

  @GetMapping("/userUnregister")
  public void unregisterGet() {
    log.info("unregister get...............");
  }

  @GetMapping("/userModify")
  public void modifyGet() {
    log.info("modify get...............");
  }

//  @PostMapping("/userUnregister")
//  public String unregisterPost() {}
}
