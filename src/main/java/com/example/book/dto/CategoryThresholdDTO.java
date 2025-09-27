package com.example.book.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryThresholdDTO {
  private Long catId;
  private String catName;
  private Integer threshold;
}
