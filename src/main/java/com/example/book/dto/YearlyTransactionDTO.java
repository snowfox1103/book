package com.example.book.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class YearlyTransactionDTO {

    private int month;            // 몇 월인지

    private Long totalIncome;     // 월별 총 수입
    private Long totalExpense;    // 월별 총 지출

    private Long budgetAmount;    // 월별 예산 금액
    private Long budgetUsed;      // 월별 사용 금액
}
