package com.example.book.dto;

import lombok.Value;

import java.util.List;

@Value
public class ThreeMonthTrendDTO {
  List<String> labels;  // ["2025-07","2025-08","2025-09"]
  List<Long> amounts;   // [120000, 80000, 150000]
}
