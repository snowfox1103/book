package com.example.book.dto;

import com.example.book.domain.finance.InOrOut;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionsDTO {
    private Long transId;

    @NotNull
    private Long userNo;
    @NotEmpty
    private String transTitle;
    @NotNull
    @Positive
    private Long transAmount;
    @NotNull
    @PastOrPresent
    private LocalDate transDate;
    @NotNull
    private InOrOut transInOut;

    private Long transCategory; //카테고리 id

    private String transMemo;

    private Long subId;
}
