package com.example.book.domain.finance;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_ledger")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionLedger {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ledgerId")
  private Long ledgerId;

  @Column(name = "userNo", nullable = false)
  private Long userNo;

  @Column(name = "subId", nullable = false)
  private Long subId;

  @Column(name = "catId")
  private Long catId;

  @Column(name = "amount", nullable = false)
  private Long amount;

  @Column(name = "chargeDate", nullable = false)
  private LocalDate chargeDate;

  @Column(name = "createdAt", nullable = false)
  private LocalDateTime createdAt;
}
