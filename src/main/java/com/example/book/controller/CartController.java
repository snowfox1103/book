package com.example.book.controller;

import com.example.book.dto.CartDTO;
import com.example.book.dto.CartResponseDTO;
import com.example.book.security.dto.UsersSecurityDTO;
import com.example.book.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
  private final CartService cartService;

  @PostMapping("/update")
  @ResponseBody
  public void updateCart(@AuthenticationPrincipal UsersSecurityDTO users,
                         @RequestBody CartDTO dto) {
    cartService.updateCart(users.getUserNo(), dto);
  }

  @GetMapping("/list")
  @ResponseBody
  public List<CartResponseDTO> getCart(@AuthenticationPrincipal UsersSecurityDTO users) {
    return cartService.getCartList(users.getUserNo());
  }

  @DeleteMapping("/delete/{itemId}")
  @ResponseBody
  public void deleteCartItem(@AuthenticationPrincipal UsersSecurityDTO users,
                             @PathVariable Long itemId) {
    cartService.deleteCartItem(users.getUserNo(), itemId);
  }

  @PostMapping("/checkout")
  @ResponseBody
  public String checkout(@AuthenticationPrincipal UsersSecurityDTO user,
                         @RequestBody List<CartResponseDTO> items) {
    cartService.checkout(user.getUserNo(), items);
    return "success";
  }
}
