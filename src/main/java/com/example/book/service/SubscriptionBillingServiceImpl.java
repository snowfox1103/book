package com.example.book.service;

import com.example.book.domain.finance.Subscriptions;
import com.example.book.repository.BillingDailyGuardRepository;
import com.example.book.repository.SubscriptionLedgerRepository;
import com.example.book.repository.SubscriptionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SubscriptionBillingServiceImpl implements SubscriptionBillingService {
  private final SubscriptionsRepository subsRepo;
  private final SubscriptionLedgerRepository ledgerRepo;
  private final BillingDailyGuardRepository guardRepo;

  private static final java.time.ZoneId KST = java.time.ZoneId.of("Asia/Seoul");

  @Override
  @Transactional
  public int ensurePostedForToday(Long userNo) {
    LocalDate today = LocalDate.now(KST);

    // 1) 가드 행을 잠그고(없으면 null) 오늘 이미 했는지 확인
    Date last = guardRepo.lockAndGet(userNo);
    if (last != null && !last.toLocalDate().isBefore(today)) {
      return 0; // 오늘 이미 처리했으므로 스킵
    }

    // 2) 실제 처리: 결제일 지난 항목 ledger 적재 + nextPayDate 롤오버
    List<Subscriptions> due = subsRepo.findDueByUser(userNo, today);
    int inserted = 0;
    for (Subscriptions s : due) {
      Long catId = (s.getCategories() != null) ? s.getCategories().getCatId() : null;
      Long amount = (s.getSubAmount() != null) ? s.getSubAmount() : 0L;
      inserted += ledgerRepo.insertIfAbsent(userNo, s.getSubId(), catId, amount, s.getNextPayDate());
      s.rollToNext(); // 다음 회차로 이동 + 알림 리셋
    }

    // 3) 가드 갱신(오늘로)
    guardRepo.upsertToday(userNo, today);

    return inserted;
  }

  /** 3개월 추이 – 누락 달 0 채우기 */
  @Transactional
  @Override
  public Map<String, Object> threeMonthTrend(Long userNo) {
    var cur = YearMonth.from(LocalDate.now(KST));
    var window = List.of(cur.minusMonths(2), cur.minusMonths(1), cur);

    // ledger에서 7,8,9월 데이터 가져오기
    Map<String, Long> db = new HashMap<>();
    for (Object[] r : ledgerRepo.getThreeMonthSummary(userNo)) {
      db.put((String) r[0], ((Number) r[1]).longValue());
    }

    List<String> labels = new ArrayList<>();
    List<Long> amounts = new ArrayList<>();

    for (YearMonth ym : window) {
      String key = ym.toString(); // "YYYY-MM"
      labels.add(key);

      if (ym.equals(cur)) {
        // 현재 달은 ledger 대신 "활성 구독 총액" 사용
        Long curTotal = subsRepo.getActiveTotal(userNo);
        amounts.add(curTotal != null ? curTotal : 0L);
      } else {
        amounts.add(db.getOrDefault(key, 0L));
      }
    }

    return Map.of("labels", labels, "amounts", amounts);
  }
}
