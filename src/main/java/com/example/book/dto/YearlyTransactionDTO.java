package com.example.book.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YearlyTransactionDTO {
    private int month;
    private Long totalIncome;
    private Long totalExpense;
}
