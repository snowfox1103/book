package com.example.book.repository;

import com.example.book.domain.finance.Subscriptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SubscriptionsRepository extends JpaRepository<Subscriptions, Long> {
  List<Subscriptions> findByUsers_UserNo(Long userNo);
  Optional<Subscriptions> findBySubId(Long subId);

  @Modifying
  @Transactional
  @Query("DELETE FROM Subscriptions s WHERE s.users.userNo = :userNo AND s.subId = :subId")
  void deleteByUsers_UserNoAndSubId(Long userNo, Long subId);

  // 카테고리별 합계
  @Query("SELECT s.categories.catName, SUM(s.subAmount) " +
    "FROM Subscriptions s " +
    "WHERE s.users.userNo = :userNo AND s.isSub = true " +
    "GROUP BY s.categories.catName")
  List<Object[]> sumAmountByCategory(@Param("userNo") Long userNo);

  // ====== ▼ 추가: 3일 이내(D-3 ~ D-1) & 이번 회차 미노티파이드 ======
  @Query(value = """
    SELECT * 
    FROM subscription s
    WHERE s.userNo = :userNo
      AND s.subNotice = 1
      AND DATEDIFF(DATE(s.nextPayDate), :today) BETWEEN 0 AND COALESCE(s.notifyWindowDays, 3)
      AND (s.lastNotifiedFor IS NULL OR s.lastNotifiedFor <> DATE(s.nextPayDate))
""", nativeQuery = true)
  List<Subscriptions> findDueAlertsInWindowNative(@Param("userNo") Long userNo,
                                                  @Param("today") java.sql.Date today);

  // ====== ▼ 추가: 표시 후 마킹 ======
  @Modifying
  @Query("""
    update Subscriptions s
    set s.lastNotifiedFor = s.nextPayDate,
        s.lastNotifiedAt = :now
    where s.subId in :ids
  """)
  int markNotified(@Param("ids") List<Long> ids, @Param("now") LocalDateTime now);

  // (선택) 사용자 한 명만 처리할 때
  @Query("""
  select s from Subscriptions s
  where s.isSub = true
    and s.users.userNo = :userNo
    and s.nextPayDate is not null
    and s.nextPayDate <= :today
""")
  List<Subscriptions> findDueByUser(@Param("userNo") Long userNo, @Param("today") java.time.LocalDate today);

  @Query("SELECT COALESCE(SUM(s.subAmount),0) " +
    "FROM Subscriptions s " +
    "WHERE s.users.userNo = :userNo " +
    "AND s.isSub = true")
  Long getActiveTotal(@Param("userNo") Long userNo);

  //결제일 목록 불러오기
  List<Subscriptions> findBySubPayDate(int subPayDate);

  @Modifying
  @Transactional
  void deleteByUsers_UserNo(Long userNo);

  @Query("""
    SELECT SUM(s.subAmount)
    FROM Subscriptions s
    WHERE s.users.userNo = :userNo
      AND s.subPayDate > :day
""")
  Long getRemainingThisMonth(@Param("userNo") Long userNo,
                             @Param("day") int day);
}
