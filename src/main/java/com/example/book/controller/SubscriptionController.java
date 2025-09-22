package com.example.book.controller;

import com.example.book.domain.finance.Categories;
import com.example.book.domain.finance.Subscriptions;
import com.example.book.dto.SubscriptionsDTO;
import com.example.book.security.dto.UsersSecurityDTO;
import com.example.book.service.CategoriesService;
import com.example.book.service.SubscriptionsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/subscriptions")
public class SubscriptionController {
  private final SubscriptionsService subscriptionsService;
  private final CategoriesService categoriesService;

  @GetMapping("/subMain")
  public String subscriptionsMainGet(@AuthenticationPrincipal UsersSecurityDTO authUser, Model model) {
    log.info("subscriptionsMain get ....................");
    Long userNo = authUser.getUserNo();

    // 유저 카테고리 리스트 + 기본 카테고리 리스트
    List<Categories> categories = categoriesService.getCategoriesForUser(userNo);
    model.addAttribute("categories", categories);

    // 유저 정기결제 리스트 조회
    List<Subscriptions> subs = subscriptionsService.getSubscriptions(userNo);
    model.addAttribute("subscriptions", subs);

    Map<String, Long> categorySummary = subscriptionsService.getCategorySummary(userNo);
    model.addAttribute("categorySummary", categorySummary);

    return "/subscriptions/subMain";
  }

  @PostMapping("/addSub")
  public ResponseEntity<?> addSubscription(
    @RequestBody SubscriptionsDTO dto,
    @AuthenticationPrincipal UsersSecurityDTO authUser) {
    // 로그인 유저 번호를 강제로 DTO에 세팅
    dto.setUserNo(authUser.getUserNo());

    try {
      subscriptionsService.addSubscription(dto);

      return ResponseEntity.ok(Map.of("message", "정기 결제가 추가되었습니다."));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }
  }

  @DeleteMapping("/deleteSub/{id}")
  @ResponseBody
  public ResponseEntity<?> deleteSubscription(
    @PathVariable("id") Long subId,
    @AuthenticationPrincipal UsersSecurityDTO authUser) {

    Long userNo = authUser.getUserNo();
    subscriptionsService.deleteSubscription(userNo, subId);

    return ResponseEntity.ok(Map.of("message", "삭제 성공"));
  }

  @PutMapping("/modifySub/{id}")
  public ResponseEntity<?> updateSubscription(@PathVariable("id") Long subId,
                                              @RequestBody SubscriptionsDTO dto,
                                              @AuthenticationPrincipal UsersSecurityDTO authUser) {
    dto.setUserNo(authUser.getUserNo());
    try {
      subscriptionsService.updateSubscription(subId, dto);
      return ResponseEntity.ok(Map.of("message", "정기 결제가 수정되었습니다."));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }
  }
}
