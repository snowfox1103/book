package com.example.book.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartDTO {
  private Long itemId;
  private int count;
}
