package com.example.book.repository;

import com.example.book.domain.finance.BillingDailyGuard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;

public interface BillingDailyGuardRepository extends JpaRepository<BillingDailyGuard, Long> {

  @Query(value = "SELECT lastRunDate FROM billing_daily_guard WHERE userNo = :userNo FOR UPDATE", nativeQuery = true)
  Date lockAndGet(@Param("userNo") Long userNo);

  @Modifying
  @Query(value = """
    INSERT INTO billing_daily_guard (userNo, lastRunDate, updatedAt)
    VALUES (:userNo, :today, NOW(6))
    ON DUPLICATE KEY UPDATE lastRunDate = :today, updatedAt = NOW(6)
  """, nativeQuery = true)
  int upsertToday(@Param("userNo") Long userNo, @Param("today") java.time.LocalDate today);
}
