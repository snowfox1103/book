package com.example.book.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class totalBudgetDTO {
    private Long totalBudgetId;

    private Integer tbYear = LocalDate.now().getYear();

    private Integer tbMonth = LocalDate.now().getMonthValue();

    private Long totalAmount;

    private Boolean budOver;

    private Boolean budReduction;

    private Boolean budNotice;
}
