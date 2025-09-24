package com.example.book.service;

import com.example.book.domain.finance.Categories;
import com.example.book.domain.finance.SubPeriodUnit;
import com.example.book.domain.finance.Subscriptions;
import com.example.book.domain.user.Users;
import com.example.book.dto.SubscriptionsDTO;
import com.example.book.repository.CategoriesRepository;
import com.example.book.repository.SubscriptionsRepository;
import com.example.book.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SubscriptionsServiceImpl implements SubscriptionsService {
  private final SubscriptionsRepository subscriptionsRepository;
  private final UsersRepository usersRepository;
  private final CategoriesRepository categoriesRepository;

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

  @Override
  @Transactional(readOnly = true)
  public List<Subscriptions> getDueAlertsInWindow(Long userNo, LocalDate today) {
    return subscriptionsRepository.findDueAlertsInWindow(userNo, today);
  }

  @Override
  @Transactional
  public void markAlertsShown(List<Long> ids) {
    if (ids == null || ids.isEmpty()) return;
    subscriptionsRepository.markNotified(ids, LocalDateTime.now());
  }
}
