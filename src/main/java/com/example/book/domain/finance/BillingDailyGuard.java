package com.example.book.domain.finance;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="billing_daily_guard")
@Getter
@NoArgsConstructor
public class BillingDailyGuard {
  @Id
  private Long userNo;

  @Column(nullable=false)
  private LocalDate lastRunDate;

  @Column(nullable=false)
  private LocalDateTime updatedAt;
}
