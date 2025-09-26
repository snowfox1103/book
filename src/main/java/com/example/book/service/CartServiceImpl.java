package com.example.book.service;

import com.example.book.domain.point.PointType;
import com.example.book.domain.point.UserPoint;
import com.example.book.domain.pointshop.Cart;
import com.example.book.domain.pointshop.Items;
import com.example.book.domain.user.Users;
import com.example.book.dto.CartDTO;
import com.example.book.dto.CartResponseDTO;
import com.example.book.repository.CartRepository;
import com.example.book.repository.ItemsRepository;
import com.example.book.repository.UserPointRepository;
import com.example.book.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService{
  private final UsersRepository usersRepository;
  private final ItemsRepository itemsRepository;
  private final CartRepository cartRepository;
  private final EmailService emailService;
  private final UserPointRepository userPointRepository;

//  @Override
//  @Transactional
//  public void updateCart(Long userNo, Long itemId, int count) {
//    Users user = usersRepository.findByUserNo(userNo)
//      .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없음"));
//    Items item = itemsRepository.findByItemId(itemId)
//      .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없음"));
//
//    // 장바구니에 같은 상품이 있는지 확인
//    Cart cart = cartRepository.findByUsers_UserNoAndItems_ItemId(user.getUserNo(), item.getItemId())
//      .orElse(Cart.builder()
//        .users(user)
//        .items(item)
//        .itemCount(0)
//        .build());
//
//    if (count <= 0) {
//      // 수량 0이면 장바구니에서 삭제
//      if (cart.getCartId() != null) {
//        cartRepository.delete(cart);
//      }
//    } else {
//      // 수량 업데이트
//      cart.setItemCount(count);
//      cartRepository.save(cart);
//    }
//  }

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

  @Override
  @Transactional
  public void checkout(Long userNo, List<CartResponseDTO> items) {
    Users user = usersRepository.findById(userNo)
      .orElseThrow(() -> new RuntimeException("유저 없음"));

    // 1. 차감할 포인트 계산
    long usedPoint = items.stream()
      .mapToLong(i -> i.getItemPrice() * i.getItemCount())
      .sum();

    if (user.getBalance() < usedPoint) {
      throw new RuntimeException("포인트 부족");
    }

    // 2. 유저 포인트 차감
    user.setBalance(user.getBalance() - usedPoint);
    usersRepository.save(user);

    // 3. UserPoint 테이블에 사용 내역 저장
    UserPoint userPoint = UserPoint.builder()
      .userNo(user.getUserNo())
      .budId(null) // 예산(budget) 연동 필요 없다면 null
      .pointStartDate(LocalDateTime.now())
      .pointAmount(usedPoint)
      .pointType(PointType.USE)
      .pointReason("포인트샵 결제")
      .build();
    userPointRepository.save(userPoint);

    // 4. 이메일 발송
    String itemList = items.stream()
      .map(i -> i.getItemName() + " x " + i.getItemCount())
      .collect(Collectors.joining("\n"));
    emailService.sendItems(user.getEmail(), "포인트샵 구매 내역", itemList);

    // 5. 장바구니 비우기
    cartRepository.deleteByUsers_UserNo(userNo);
  }
}
