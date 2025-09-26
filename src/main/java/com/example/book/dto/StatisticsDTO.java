package com.example.book.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatisticsDTO {
    private Long categoryId;
    private String categoryName;

    private Long budgetAmount;   // 설정금액
    private Long budgetUsed;     // 사용금액

    private Long totalIncome;    // 입금 합계
    private Long totalExpense;   // 지출 합계

    private int year;
    private int month;
}
