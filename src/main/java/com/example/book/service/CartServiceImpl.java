package com.example.book.service;

import com.example.book.domain.pointshop.Cart;
import com.example.book.domain.pointshop.Items;
import com.example.book.domain.user.Users;
import com.example.book.dto.CartDTO;
import com.example.book.dto.CartResponseDTO;
import com.example.book.repository.CartRepository;
import com.example.book.repository.ItemsRepository;
import com.example.book.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService{
  private final UsersRepository usersRepository;
  private final ItemsRepository itemsRepository;
  private final CartRepository cartRepository;

  @Override
  @Transactional
  public void updateCart(Long userNo, Long itemId, int count) {
    Users user = usersRepository.findByUserNo(userNo)
      .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없음"));
    Items item = itemsRepository.findByItemId(itemId)
      .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없음"));

    // 장바구니에 같은 상품이 있는지 확인
    Cart cart = cartRepository.findByUsers_UserNoAndItems_ItemId(user.getUserNo(), item.getItemId())
      .orElse(Cart.builder()
        .users(user)
        .items(item)
        .itemCount(0)
        .build());

    if (count <= 0) {
      // 수량 0이면 장바구니에서 삭제
      if (cart.getCartId() != null) {
        cartRepository.delete(cart);
      }
    } else {
      // 수량 업데이트
      cart.setItemCount(count);
      cartRepository.save(cart);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public List<Cart> getCart(Long userNo) {
    return cartRepository.findByUsers_UserNo(userNo);
  }

  @Override
  @Transactional
  public void updateCart(Long userNo, CartDTO dto) {
    Users users = usersRepository.findById(userNo)
      .orElseThrow(() -> new RuntimeException("사용자 없음"));
    Items items = itemsRepository.findById(dto.getItemId())
      .orElseThrow(() -> new RuntimeException("아이템 없음"));

    Cart cart = cartRepository.findByUsersAndItems(users, items)
      .orElse(Cart.builder()
        .users(users)
        .items(items)
        .itemCount(0)
        .build());

    cart.setItemCount(dto.getCount());
    cartRepository.save(cart);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CartResponseDTO> getCartList(Long userNo) {
    Users user = usersRepository.findById(userNo)
      .orElseThrow(() -> new RuntimeException("사용자 없음"));

    List<Cart> carts = cartRepository.findAllByUsers(user);

    return carts.stream()
      .map(cart -> new CartResponseDTO(
        cart.getItems().getItemId(),
        cart.getItems().getItemName(),
        cart.getItems().getItemPrice(),
        cart.getItemCount()
      ))
      .toList();
  }

  @Override
  @Transactional
  public void deleteCartItem(Long userNo, Long itemId) {
    Users user = usersRepository.findById(userNo)
      .orElseThrow(() -> new RuntimeException("사용자 없음"));

    Items item = itemsRepository.findById(itemId)
      .orElseThrow(() -> new RuntimeException("아이템 없음"));

    cartRepository.findByUsersAndItems(user, item)
      .ifPresent(cartRepository::delete);
  }
}
