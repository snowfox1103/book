package com.example.book.dto;

import lombok.Data;

@Data
public class SubscriptionsDTO {
  private Long userNo;
  private String subTitle;
  private Long subAmount;
  private int subPayDate;
  private Long subCategory; //이거 왜 bigint?
  private boolean subNotice;
  private String subPeriodUnit;
  private int subPeriodValue;
  private boolean isSub;
}
