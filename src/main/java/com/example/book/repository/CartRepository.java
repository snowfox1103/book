package com.example.book.repository;

import com.example.book.domain.pointshop.Cart;
import com.example.book.domain.pointshop.Items;
import com.example.book.domain.user.Users;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends CrudRepository<Cart, Long> {
  Optional<Cart> findByUsers_UserNoAndItems_ItemId(Long userNo, Long itemId);
  List<Cart> findByUsers_UserNo(Long userNo);
  Optional<Cart> findByUsersAndItems(Users users, Items items);
  List<Cart> findAllByUsers(Users users);

}
