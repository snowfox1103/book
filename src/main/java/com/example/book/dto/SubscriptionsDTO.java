package com.example.book.dto;

import com.example.book.domain.finance.Categories;
import com.example.book.domain.user.Users;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubscriptionsDTO {
  @NotNull
  private Long userNo; //users fk

  @NotNull
  private Long catId; // categories fk

  @NotBlank
  private String subTitle;

  @NotNull
  @Min(0)
  private Long subAmount;

  @Min(1)
  @Max(31)
  private int subPayDate;

  private boolean subNotice;

  @NotNull
  private String subPeriodUnit;

  @Min(1)
  private int subPeriodValue;

  @JsonProperty("isSub")
  private boolean isSub;
}
