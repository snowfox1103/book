package com.example.book.controller;

import com.example.book.domain.finance.Subscriptions;
import com.example.book.domain.finance.Categories;
import com.example.book.dto.SubscriptionsDTO;
import com.example.book.security.dto.UsersSecurityDTO;
import com.example.book.service.CategoriesService;
import com.example.book.service.SubscriptionBillingServiceImpl;
import com.example.book.service.SubscriptionsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/subscriptions")
public class SubscriptionController {
  private final SubscriptionsService subscriptionsService;
  private final CategoriesService categoriesService;
  private final SubscriptionBillingServiceImpl billingService;

  @GetMapping("/subMain")
  public String subscriptionsMainGet(@AuthenticationPrincipal UsersSecurityDTO authUser, Model model) {
    log.info("subscriptionsMain get ....................");
    Long userNo = authUser.getUserNo();

    // 새로고침을 해도 하루 1회만 실행됨
    billingService.ensurePostedForToday(userNo);

    // 유저 카테고리 리스트 + 기본 카테고리 리스트
    List<Categories> categories = categoriesService.getCategoriesForUser(userNo);
    model.addAttribute("categories", categories);

    // 유저 정기결제 리스트 조회
    List<Subscriptions> subs = subscriptionsService.getSubscriptions(userNo);
    model.addAttribute("subscriptions", subs);

    Map<String, Long> categorySummary = subscriptionsService.getCategorySummary(authUser.getUserNo());
    model.addAttribute("categorySummary", categorySummary);

//    model.addAttribute("monthlySummary", subscriptionsService.getMonthlySummary(authUser.getUserNo()));
//    Map<String, Object> monthlySummary = subscriptionsService.getMonthlySummary(userNo);
//    model.addAttribute("monthlyLabels", monthlySummary.get("labels"));
//    model.addAttribute("monthlyAmounts", monthlySummary.get("amounts"));
    Map<String, Object> trend = billingService.threeMonthTrend(userNo);
    model.addAttribute("monthlyLabels", trend.get("labels"));
    model.addAttribute("monthlyAmounts", trend.get("amounts"));

    return "subscriptions/subMain";
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
  public ResponseEntity<?> deleteSubscription(@PathVariable("id") Long subId,
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

  @GetMapping("/alerts")
  @ResponseBody
  public List<Map<String, Object>> getAlerts(@AuthenticationPrincipal UsersSecurityDTO authUser) {
    var today = java.time.LocalDate.now(java.time.ZoneId.of("Asia/Seoul"));
    var list = subscriptionsService.getDueAlertsInWindow(authUser.getUserNo(), today);

    // 프론트 친화적 형태로 변환
    List<Map<String, Object>> result = new ArrayList<>();
    for (Subscriptions s : list) {
      result.add(Map.of(
        "id", s.getSubId(),
        "title", s.getSubTitle(),
        "amount", s.getSubAmount(),
        "nextPayDate", s.getNextPayDate().toString()
      ));
    }

    return result;
  }

  // ====== ▼ 추가: 알림 노출 마킹 ======
  @PostMapping("/alerts/mark")
  @ResponseBody
  public ResponseEntity<?> markAlerts(@AuthenticationPrincipal UsersSecurityDTO authUser,
                                      @RequestBody List<Long> ids) {
    subscriptionsService.markAlertsShown(authUser.getUserNo(), ids);
    return ResponseEntity.ok(Map.of("message", "marked"));
  }
}
