package com.example.book.repository;

import com.example.book.domain.pointshop.Cart;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends CrudRepository<Cart, Long> {
  Optional<Cart> findByUsers_UserNoAndItems_ItemId(Long userNo, Long itemId);
  List<Cart> findByUsers_UserNo(Long userNo);
}
