package com.example.book.controller;

import com.example.book.domain.finance.Categories;
import com.example.book.domain.qna.Qna;
import com.example.book.domain.user.Users;
import com.example.book.dto.CategoryRequestDTO;
import com.example.book.dto.EmailChangeRequestDTO;
import com.example.book.dto.PasswordChangeRequestDTO;
import com.example.book.repository.EmailVerificationTokenRepository;
import com.example.book.repository.UsersRepository;
import com.example.book.security.dto.UsersSecurityDTO;
import com.example.book.service.CategoriesService;
import com.example.book.service.QnaService;
import com.example.book.service.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/mypage")
@PreAuthorize("isAuthenticated()")
public class MyPageController {
  private final UsersService usersService;
  private final UsersRepository usersRepository;
  private final EmailVerificationTokenRepository tokenRepository;
  private final CategoriesService categoriesService;
  private final QnaService qnaService;

  @GetMapping("/myPage")
  public String myPageGet(@AuthenticationPrincipal UsersSecurityDTO authUser, Model model) {
    log.info("myPageGet success ........... ");
    Users users = usersRepository.findByUserId(authUser.getUserId())
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    List<Categories> categories = categoriesService.categoriesList(users);
    model.addAttribute("categories", categories);
    model.addAttribute("user", users);

    List<Qna> myInquiries = qnaService.getRecentInquiries(users.getUserNo());
    model.addAttribute("myInquiries", myInquiries);

    return "mypage/myPage";
  }

  @Transactional
  @PostMapping("/userUnregister")
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

  @PostMapping("/userPasswordModify")
  public ResponseEntity<?> changePassword(@AuthenticationPrincipal UsersSecurityDTO principal,
                                          @Valid @RequestBody PasswordChangeRequestDTO passwordChangeRequestDTO) {
    usersService.changePassword(principal.getUserId(), passwordChangeRequestDTO);

    return ResponseEntity.ok().build();
  }

  @PostMapping("/userEmailModify")
  public ResponseEntity<?> chagneEmail(@AuthenticationPrincipal UsersSecurityDTO principal,
                                       @Valid @RequestBody EmailChangeRequestDTO emailChangeRequestDTO) {
    log.info(principal.getUserId());
    log.info(emailChangeRequestDTO.getNewEmail());
    usersService.changeEmail(principal.getUserId(), emailChangeRequestDTO);

    return ResponseEntity.ok().build();
  }

  @PostMapping("/category")
  public ResponseEntity<?> addCategoryPost(@RequestBody CategoryRequestDTO req,
                                           @AuthenticationPrincipal UsersSecurityDTO authUser) {
    Long userNo = authUser.getUserNo(); // 현재 로그인한 유저 번호
    boolean exists = categoriesService.existsByUserNoAndCatName(userNo, req.getCategoryName());

    if (exists) {
      return ResponseEntity.badRequest().body(Map.of("message", "이미 같은 이름의 카테고리가 존재합니다."));
    }

    categoriesService.addCategory(userNo, req.getCategoryName());
    return ResponseEntity.ok("카테고리가 추가되었습니다.");
  }

  @DeleteMapping("/category/{id}")
  @ResponseBody
  public ResponseEntity<?> deleteCategory(
    @PathVariable("id") Long catId,
    @AuthenticationPrincipal UsersSecurityDTO authUser) {

    Long userNo = authUser.getUserNo();
    categoriesService.deleteCategory(userNo, catId);

    return ResponseEntity.ok(Map.of("message", "삭제 성공"));
  }

  @PutMapping("/category/{id}")
  @ResponseBody
  public ResponseEntity<?> updateCategory(@PathVariable("id") Long catId,
                                          @RequestBody CategoryRequestDTO req,
                                          @AuthenticationPrincipal UsersSecurityDTO authUser) {
    Long userNo = authUser.getUserNo();

    if (categoriesService.existsByUserNoAndCatName(userNo, req.getCategoryName())) {
      return ResponseEntity.badRequest()
        .body(Map.of("message", "이미 같은 이름의 카테고리가 존재합니다."));
    }

    categoriesService.updateCategory(catId, req.getCategoryName());
    return ResponseEntity.ok(Map.of("message", "수정 성공"));
  }

  @PostMapping("/profileImage")
  public String uploadProfileImage(@RequestParam("profileImage") MultipartFile file,
                                   @AuthenticationPrincipal UsersSecurityDTO authUser) throws IOException {
    if (file.isEmpty()) {
      return "redirect:/mypage/myPage?error=emptyFile";
    }

    // 저장할 파일명 (UUID 붙여서 중복 방지)
    String savedFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

    // 외부 uploads 폴더 (재시작 없이 접근 가능)
    String uploadDir = System.getProperty("user.dir") + "/uploads";
    Path savePath = Paths.get(uploadDir, savedFileName);

    // 폴더 없으면 생성
    Files.createDirectories(savePath.getParent());

    // 파일 저장
    file.transferTo(savePath.toFile());

    // DB에 저장
    Users user = usersRepository.findByUserId(authUser.getUserId())
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    user.setProfileImage(savedFileName);  // DB에는 파일명만 저장
    usersRepository.save(user);

    log.info("Saved profile image: " + savedFileName);
    return "redirect:/mypage/myPage";
  }
}
