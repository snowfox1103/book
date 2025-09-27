package com.example.book.dto;

import lombok.Data;

@Data
public class ThresholdDTO {
  private Long catId;       // Categories.catId → Budgets.budCategory
  private Integer threshold;
}
