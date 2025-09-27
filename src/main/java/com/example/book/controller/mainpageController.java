package com.example.book.controller;

import com.example.book.domain.finance.Subscriptions;
import com.example.book.domain.user.Users;
import com.example.book.domain.finance.Categories;
import com.example.book.dto.*;
import com.example.book.security.dto.UsersSecurityDTO;
import com.example.book.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@Log4j2
@RequestMapping("/mainPage")
@RequiredArgsConstructor
public class mainpageController {
    private final TransactionsService transactionsService;
    private final BudgetsService budgetsService;
    private final CategoriesService categoriesService;
    private final StatisticsService statisticsService;
    private final SubscriptionsService subscriptionsService;
    @GetMapping("/mainpage")
    public void getMain(@AuthenticationPrincipal UsersSecurityDTO users, PageRequestDTO pageRequestDTO, Model model, TransactionsDTO transactionsDTO, BudgetsDTO budgetsDTO){
        Long userNo = users.getUserNo();
        log.info("--------get mainpage---------");
        PageResponseDTO<TransactionsDTO> responseDTO = transactionsService.listByUser(userNo,pageRequestDTO);
        log.info(responseDTO);
        List<Categories> categories = categoriesService.getCategoriesForUser(users.getUserNo());
        List<Subscriptions> subs = subscriptionsService.getSubscriptions(userNo);
        model.addAttribute("subsList",subs);
//        Long totalAmount = transactionsService.totals(userNo);
        model.addAttribute("users",userNo);
//        model.addAttribute("totals",totalAmount);
        model.addAttribute("categories", categories);
        model.addAttribute("responseDTO",responseDTO);
    }
    @GetMapping("/statistics")
    public void showStatistics(Model model) throws JsonProcessingException {
        log.info("----------------------------344444------------------------------");
        Long userNo = 1L;
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        // 1. 이번 달 카테고리별 도넛
        List<StatisticsDTO> monthlyStats = statisticsService.getMonthlyStatistics(userNo, year, month);

        // 2. 최근 3개월 막대그래프
        List<StatisticsDTO> recent3 = statisticsService.getRecent3MonthsStatistics(userNo);

        // 3. 당월 총 입금/지출 막대그래프
        Map<String, Long> currentMonth = statisticsService.getCurrentMonthIncomeExpense(userNo);

        // 4. 연간 꺾은선
        List<YearlyTransactionDTO> yearlyStats = statisticsService.getYearlyTransactions(userNo, year);

        ObjectMapper mapper = new ObjectMapper();

        model.addAttribute("monthlyStats", mapper.writeValueAsString(monthlyStats));
        model.addAttribute("recent3", mapper.writeValueAsString(recent3));
        model.addAttribute("currentMonth", mapper.writeValueAsString(currentMonth));
        model.addAttribute("yearlyStats", mapper.writeValueAsString(yearlyStats));
        log.info(monthlyStats);
        log.info(recent3);
        log.info(currentMonth);
        log.info(yearlyStats);
    }

}
