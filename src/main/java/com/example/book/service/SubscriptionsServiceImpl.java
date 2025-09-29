package com.example.book.service;

import com.example.book.domain.finance.Categories;
import com.example.book.domain.finance.SubPeriodUnit;
import com.example.book.domain.finance.Subscriptions;
import com.example.book.domain.user.Users;
import com.example.book.dto.SubscriptionsDTO;
import com.example.book.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class SubscriptionsServiceImpl implements SubscriptionsService {
  private final SubscriptionsRepository subscriptionsRepository;
  private final UsersRepository usersRepository;
  private final CategoriesRepository categoriesRepository;
  private final BillingDailyGuardRepository guardRepository;
  private final TransactionsRepository transactionsRepository;
  private final SchedulerService schedulerService;

  @Override
  @Transactional
  public void addSubscription(SubscriptionsDTO dto) {
    Users user = usersRepository.findById(dto.getUserNo())
      .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

    Categories category = categoriesRepository.findById(dto.getCatId())
      .orElseThrow(() -> new IllegalArgumentException("잘못된 카테고리입니다."));

    Subscriptions sub = Subscriptions.builder()
      .users(user)
      .categories(category)
      .subTitle(dto.getSubTitle())
      .subAmount(dto.getSubAmount())
      .subPayDate(dto.getSubPayDate())
      .subNotice(dto.isSubNotice())
      .subPeriodUnit(SubPeriodUnit.valueOf(dto.getSubPeriodUnit())) // enum 변환
      .subPeriodValue(dto.getSubPeriodValue())
      .isSub(dto.isSub())
      .notifyWindowDays(3)        // 기본값 권장
      .anchorToMonthEnd(false)
      .build();

    sub.initNextPayDateIfNeeded(); // ★ 생성 시 필수
    subscriptionsRepository.save(sub);

    //등록 시 해당 날이 정기 결제일이면 바로 입출금 내역 추가
    if (sub.getSubPayDate() == LocalDate.now().getDayOfMonth()) {
      //중복 확인
      boolean alreadyExists = transactionsRepository.existsThisMonth(
        sub.getUsers().getUserNo(),
        sub.getSubId(),
        LocalDate.now().getYear(),
        LocalDate.now().getMonthValue()
      );
      if (!alreadyExists) {
        schedulerService.createTransactionFromSubscription(sub);
      }
    }
  }

  @Override
  public List<Subscriptions> getSubscriptions(Long userNo) {
    return subscriptionsRepository.findByUsers_UserNo(userNo);
  }

  @Override
  public void deleteSubscription(Long userNo, Long subId) {
    // 소유권 체크가 필요하면 아래와 같이:

    subscriptionsRepository.deleteByUsers_UserNoAndSubId(userNo, subId);
  }

  @Override
  @Transactional
  public void updateSubscription(Long subId, SubscriptionsDTO dto) {
    Subscriptions sub = subscriptionsRepository.findBySubId(subId)
      .orElseThrow(() -> new IllegalArgumentException("정기 결제가 존재하지 않습니다."));

    if (!sub.getUsers().getUserNo().equals(dto.getUserNo())) {
      throw new IllegalArgumentException("권한이 없습니다.");
    }

    Users users = usersRepository.findByUserNo(dto.getUserNo())
      .orElseThrow(() -> new IllegalArgumentException("유저 정보가 없습니다."));

    Categories category = categoriesRepository.findByCatId(dto.getCatId())
      .orElseThrow(() -> new IllegalArgumentException("잘못된 카테고리입니다."));

    // ✅ 엔티티에 업데이트 위임
    sub.updateFromDTO(dto, users, category);
    subscriptionsRepository.save(sub);

    // ✅ 수정 후에도 결제일이 오늘이라면 바로 입출금 내역 추가
    if (sub.getSubPayDate() == LocalDate.now().getDayOfMonth()) {
      //중복 확인
      boolean alreadyExists = transactionsRepository.existsThisMonth(
        sub.getUsers().getUserNo(),
        sub.getSubId(),
        LocalDate.now().getYear(),
        LocalDate.now().getMonthValue()
      );
      if (!alreadyExists) {
        schedulerService.createTransactionFromSubscription(sub);
      }
    }
  }

  // 카테고리별 합계
  @Override
  public Map<String, Long> getCategorySummary(Long userNo) {
    List<Object[]> result = subscriptionsRepository.sumAmountByCategory(userNo);

    Map<String, Long> summary = new LinkedHashMap<>();
    for (Object[] row : result) {
      String categoryName = (String) row[0];
      Long totalAmount = (Long) row[1];
      summary.put(categoryName, totalAmount);
    }
    return summary;
  }

//  @Override
//  @Transactional(readOnly = true)
//  public List<Subscriptions> getDueAlertsInWindow(Long userNo, LocalDate today) {
//    return subscriptionsRepository.findDueAlertsInWindow(userNo, today);
//  }
//
//  @Override
//  @Transactional
//  public void markAlertsShown(List<Long> ids) {
//    if (ids == null || ids.isEmpty()) return;
//    subscriptionsRepository.markNotified(ids, LocalDateTime.now());
//  }
private static final ZoneId KST = ZoneId.of("Asia/Seoul");

//  @Override
//  @Transactional(readOnly = true)
//  public List<Subscriptions> getDueAlertsInWindow(Long userNo, LocalDate today) {
//    // 1) guard 확인: 오늘 이미 알림 마킹 됐으면 바로 빈 배열 리턴
//    Date last = guardRepository.lockAndGet(userNo);
//    if (last != null && ((java.sql.Date) last).toLocalDate().equals(today)) {
//      return Collections.emptyList();
//    }
//
//    // 2) 결제일 임박 구독 조회 (조건 맞는 것만 리턴)
//    return subscriptionsRepository.findDueAlertsInWindowNative(userNo, java.sql.Date.valueOf(today));
//  }
  @Override
  @Transactional(readOnly = true)
  public List<Subscriptions> getDueAlertsInWindow(Long userNo, LocalDate today) {
    log.info("== Alert 조회 시작 ==");
    log.info("userNo = {}", userNo);
    log.info("today = {}", today);

    // ❌ 여기서는 guard 체크/업데이트 절대 안함
    List<Subscriptions> list = subscriptionsRepository.findDueAlertsInWindowNative(
      userNo,
      java.sql.Date.valueOf(today)
    );

    log.info("조회 결과 건수 = {}", list.size());
    for (Subscriptions s : list) {
      log.info("subId={}, nextPayDate={}, notifyWindowDays={}, lastNotifiedFor={}, subNotice={}",
        s.getSubId(), s.getNextPayDate(), s.getNotifyWindowDays(),
        s.getLastNotifiedFor(), s.isSubNotice());
    }

    return list;
  }

  @Override
  @Transactional
  public void markAlertsShown(Long userNo, List<Long> ids) {
    if (ids == null || ids.isEmpty()) return;

    // 1) Subscriptions 테이블에 "이번 회차 알림 봤음" 기록
    subscriptionsRepository.markNotified(ids, LocalDateTime.now());

    // 2) ✅ guard는 "오늘 직접 닫은 경우"에만 업데이트
    LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
    guardRepository.upsertToday(userNo, today);

    log.info("오늘({}) 알림 마킹 완료: userNo={}, ids={}", today, userNo, ids);
  }
}
