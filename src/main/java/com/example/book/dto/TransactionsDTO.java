package com.example.book.dto;

import com.example.book.domain.finance.InOrOut;
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

    private Long userNo;

    private String transTitle;

    private Long transAmount;

    private LocalDate transDate;

    private InOrOut transInOut;

    private Long transCategory;

    private String transMemo;

    private Long subId;
}
