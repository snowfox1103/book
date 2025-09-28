package com.example.book.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatisticsDTO {

    private Long categoryId;      // 카테고리 ID
    private String categoryName;  // 카테고리 이름

    private Long totalExpense;    // 총 지출 금액 (도넛 차트용)

    private Long budgetAmount;    // 예산 금액
    private Long budgetUsed;      // 실제 사용 금액

    private int year;             // 해당 연도
    private int month;            // 해당 월
}
