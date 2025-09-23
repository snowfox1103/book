package com.example.book.service;

import com.example.book.domain.pointshop.Cart;

import java.util.List;

public interface CartService {
  void updateCart(Long userNo, Long itemId, int count);
  List<Cart> getCart(Long userNo);
}
