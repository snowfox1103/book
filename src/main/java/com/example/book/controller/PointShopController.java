package com.example.book.controller;

import com.example.book.domain.user.Users;
import com.example.book.dto.CartResponseDTO;
import com.example.book.dto.PointRowVM;
import com.example.book.repository.UserPointRepository;
import com.example.book.repository.UsersRepository;
import com.example.book.security.dto.UsersSecurityDTO;
import com.example.book.service.CartService;
import com.example.book.service.ItemsService;
import com.example.book.service.PointService;
import com.example.book.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/pointshop")
public class PointShopController {
  private final ItemsService itemsService;
  private final UsersService usersService;
  private final UsersRepository usersRepository;
  private final CartService cartService;
  private final UserPointRepository userPointRepository;

  @GetMapping("/pointShop")
  public String pointShop(@AuthenticationPrincipal UsersSecurityDTO authUser, Model model) {
    Users users = usersRepository.findByUserId(authUser.getUserId())
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    model.addAttribute("items", itemsService.getAllItems());

    int currentPoint = usersService.getCurrentPoint(users.getUserNo());
    model.addAttribute("currentPoint", currentPoint);

    return "pointshop/pointShop";
  }

  @PostMapping("/checkout")
  @ResponseBody
  public String checkout(@AuthenticationPrincipal UsersSecurityDTO user,
                         @RequestBody List<CartResponseDTO> items) {
    cartService.checkout(user.getUserNo(), items);

    return "success";
  }
}
