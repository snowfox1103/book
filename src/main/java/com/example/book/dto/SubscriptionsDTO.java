package com.example.book.dto;

import com.example.book.domain.Categories;
import com.example.book.domain.Users;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SubscriptionsDTO {
  private Long userNo; //users fk
  private Long catId; // categories fk
  private String subTitle;
  private Long subAmount;
  private int subPayDate;
  private boolean subNotice;
  private String subPeriodUnit;
  private int subPeriodValue;

  @JsonProperty("isSub")
  private boolean isSub;
}
