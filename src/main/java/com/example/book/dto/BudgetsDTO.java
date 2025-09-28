package com.example.book.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetsDTO {
    private Long budgetId;

    @NotNull
    private Long userNo;

    @NotNull
    private Long budCategory;
    @NotNull
    @Positive
    private Long budAmount;

    private Long budCurrent = 0L;

    private Boolean budIsOver = false;
    @NotNull
    private int budYear = LocalDate.now().getYear();
    @NotNull
    private int budMonth = LocalDate.now().getMonthValue();

    private Boolean budOver = false;

    private Boolean budReduction = false;

    private Boolean budNotice = false;

    private Integer budThreshold = 90;
}
