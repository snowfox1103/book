package com.example.book.controller;

import com.example.book.service.ItemsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/pointshop")
public class PointShopController {
  private final ItemsService itemsService;

  @GetMapping("/pointShop")
  public String pointShop(Model model) {
    model.addAttribute("items", itemsService.getAllItems());

    return "pointshop/pointShop";
  }
}
