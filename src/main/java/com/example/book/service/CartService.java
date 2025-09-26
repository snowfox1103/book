package com.example.book.service;

import com.example.book.domain.pointshop.Cart;
import com.example.book.dto.CartDTO;
import com.example.book.dto.CartResponseDTO;

import java.util.List;

public interface CartService {
//  void updateCart(Long userNo, Long itemId, int count);
  List<Cart> getCart(Long userNo);
  void updateCart(Long userNo, CartDTO dto);
  List<CartResponseDTO> getCartList(Long userNo);
  void deleteCartItem(Long userNo, Long itemId);
  void checkout(Long userNo, List<CartResponseDTO> items);
}
