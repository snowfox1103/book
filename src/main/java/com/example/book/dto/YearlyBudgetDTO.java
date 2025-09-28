package com.example.book.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YearlyBudgetDTO {
    private int month;
    private long totalBudget;   // 설정된 총 예산(해당 월)
    private long totalUsed;     // 사용된 총 예산(해당 월)
}
