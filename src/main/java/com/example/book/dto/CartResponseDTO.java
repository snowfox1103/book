package com.example.book.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartResponseDTO {
  private Long itemId;
  private String itemName;
  private Long itemPrice;
  private int itemCount;
}
