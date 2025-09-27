package com.example.book.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BudgetAlertDTO {
  private Long id;          // budgetId
  private String category;  // 카테고리명
  private Long amount;      // 설정 예산
  private Long current;     // 현재 사용 금액
  private int rate;         // 사용률 %
  private int threshold;    // 사용자 설정 알림 비율 (%)
}
