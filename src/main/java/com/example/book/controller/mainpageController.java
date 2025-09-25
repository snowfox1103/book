package com.example.book.controller;

import com.example.book.domain.finance.Categories;
import com.example.book.domain.user.Users;
import com.example.book.dto.BudgetsDTO;
import com.example.book.dto.PageRequestDTO;
import com.example.book.dto.PageResponseDTO;
import com.example.book.dto.TransactionsDTO;
import com.example.book.service.BudgetsService;
import com.example.book.service.CategoriesService;
import com.example.book.service.TransactionsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@Log4j2
@RequestMapping("/mainPage")
@RequiredArgsConstructor
public class mainpageController {
    private final TransactionsService transactionsService;
    private final BudgetsService budgetsService;
    private final CategoriesService categoriesService;
    @GetMapping("/mainpage")
    public void getMain(Users users,PageRequestDTO pageRequestDTO, Model model, TransactionsDTO transactionsDTO, BudgetsDTO budgetsDTO){
//        Long userNo = users.getUserNo();
        Long userNo = 1L;
        log.info("--------get mainpage---------");
        PageResponseDTO<TransactionsDTO> responseDTO = transactionsService.listByUser(userNo,pageRequestDTO);
        log.info(responseDTO);
        List<Categories> categories = categoriesService.categoriesList(users);
//        Long totalAmount = transactionsService.totals(userNo);
        model.addAttribute("users",userNo);
//        model.addAttribute("totals",totalAmount);
        model.addAttribute("categories", categories);
        model.addAttribute("responseDTO",responseDTO);
    }
    @GetMapping("/stastic")
    public void getstastic(Users users,PageRequestDTO pageRequestDTO, Model model, TransactionsDTO transactionsDTO, BudgetsDTO budgetsDTO){
//        Long userNo = users.getUserNo();
        Long userNo = 1L;
        log.info("--------get ---------");
        PageResponseDTO<TransactionsDTO> responseDTO = transactionsService.listByUser(userNo,pageRequestDTO);
        log.info(responseDTO);
        List<Categories> categories = categoriesService.categoriesList(users);
//        Long totalAmount = transactionsService.totals(userNo);
        model.addAttribute("users",userNo);
//        model.addAttribute("totals",totalAmount);
        model.addAttribute("categories", categories);
        model.addAttribute("responseDTO",responseDTO);
    }
}
