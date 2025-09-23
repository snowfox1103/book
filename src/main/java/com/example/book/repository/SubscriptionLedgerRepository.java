package com.example.book.repository;

import com.example.book.domain.finance.SubscriptionLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface SubscriptionLedgerRepository extends JpaRepository<SubscriptionLedger, Long> {
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Transactional
  @Query(value = """
  INSERT IGNORE INTO subscription_ledger
  (userNo, subId, catId, amount, chargeDate, createdAt)
  VALUES (:userNo, :subId, :catId, :amount, :chargeDate, NOW(6))
""", nativeQuery = true)
  int insertIfAbsent(@Param("userNo") Long userNo,
                     @Param("subId") Long subId,
                     @Param("catId") Long catId,
                     @Param("amount") Long amount,
                     @Param("chargeDate") LocalDate chargeDate);

  // 3개월 추이(ledger 기준)
  @Query(value = """
  SELECT DATE_FORMAT(chargeDate, '%Y-%m') AS ym,
         COALESCE(SUM(amount), 0)         AS total
  FROM subscription_ledger
  WHERE userNo = :userNo
    AND chargeDate >= DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 2 MONTH), '%Y-%m-01')
  GROUP BY ym
  ORDER BY ym
""", nativeQuery = true)
  List<Object[]> getThreeMonthSummary(@Param("userNo") Long userNo);
}
