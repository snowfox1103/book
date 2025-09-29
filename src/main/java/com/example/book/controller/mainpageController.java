package com.example.book.controller;

import com.example.book.domain.finance.Categories;
import com.example.book.domain.finance.Subscriptions;
import com.example.book.domain.user.Users;
import com.example.book.dto.*;
import com.example.book.security.dto.UsersSecurityDTO;
import com.example.book.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@Log4j2
@RequestMapping("/mainPage")
@RequiredArgsConstructor
public class mainpageController {
    private final TransactionsService transactionsService;
    private final SubscriptionsService subscriptionsService;
    private final CategoriesService categoriesService;
    private final StatisticsService statisticsService;
    @GetMapping("/mainpage")
    public void getMain(@AuthenticationPrincipal UsersSecurityDTO authUser, PageRequestDTO pageRequestDTO, Model model, TransactionsDTO transactionsDTO, BudgetsDTO budgetsDTO) {
        Long userNo = authUser.getUserNo();
        log.info("--------get mainpage---------");

        PageResponseDTO<TransactionsDTO> responseDTO = transactionsService.listByUser(userNo, pageRequestDTO);
        List<Categories> categories = categoriesService.categoriesList(userNo);
        Long incomes = transactionsService.wholeIncome(userNo);
        Long uses = transactionsService.wholeUses(userNo);

        List<StatisticsDTO> monthlyExpenses = statisticsService.getMonthlyCategoryExpenses(userNo, LocalDate.now().getYear(), LocalDate.now().getMonthValue());

        List<String> categoryLabels = monthlyExpenses.stream()
                .map(s -> s.getCategoryName() == null ? "기타" : s.getCategoryName())
                .toList();

        List<Long> categoryData = monthlyExpenses.stream()
                .map(s -> s.getTotalExpense() == null ? 0L : s.getTotalExpense())
                .toList();

        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();
        Map<String, Map<String, Long>> dailyStats = statisticsService.getDailyIncomeExpense(userNo, year, month);
        List<Subscriptions> subs = subscriptionsService.getSubscriptions(userNo);
        List<StatisticsDTO> monthlyBudgets = statisticsService.getMonthlyBudgetStatistics(userNo, year, month);
        model.addAttribute("monthlyBudgets", monthlyBudgets);
        model.addAttribute("dailyStats", dailyStats);
        model.addAttribute("subsList", subs);
        model.addAttribute("responseDTO", responseDTO);

        model.addAttribute("categoryLabels", categoryLabels);
        model.addAttribute("categoryData", categoryData);
        // ===== 모델 바인딩 =====
        model.addAttribute("monthlyExpenses", monthlyExpenses);
        model.addAttribute("users", userNo);
        model.addAttribute("income", incomes);
        model.addAttribute("uses", uses);
        model.addAttribute("totals", incomes - uses);
        model.addAttribute("categories", categories);
        model.addAttribute("responseDTO", responseDTO);
    }

    @GetMapping("/statistics")
    public void showStatisticsPage(@AuthenticationPrincipal UsersSecurityDTO authUser, Model model,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month) {

        Long userNo = authUser.getUserNo();
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();

        if(year == null) year = currentYear;
        if (month == null) month = now.getMonthValue();

        // 1. 입출금 도넛: 해당 달 카테고리별 출금
        List<StatisticsDTO> monthlyExpenses = statisticsService.getMonthlyCategoryExpenses(userNo, year, month);

        // 2. 입출금 꺾은선: 해당 연도 1~12월 총 입금/총 출금
        List<YearlyTransactionDTO> yearlyTransactions = statisticsService.getYearlyTransactions(userNo, year);

        // 3. 예산 막대: 해당 달 카테고리별 설정금액/사용금액
        List<StatisticsDTO> monthlyBudgets = statisticsService.getMonthlyBudgetStatistics(userNo, year, month);

        // 4. 예산 꺾은선: 해당 연도 1~12월 총 예산 설정금액/총 사용금액
        List<YearlyTransactionDTO> yearlyBudgets = statisticsService.getYearlyBudgetStatistics(userNo, year);

        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("monthlyExpenses", monthlyExpenses);
        model.addAttribute("yearlyTransactions", yearlyTransactions);
        model.addAttribute("monthlyBudgets", monthlyBudgets);
        model.addAttribute("yearlyBudgets", yearlyBudgets);
    }

    // 기존 mainpageController 안
    @GetMapping("/dailyStats")
    @ResponseBody
    public Map<String, Map<String, Long>> getDailyStats(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam Long userNo) {

        return statisticsService.getDailyIncomeExpense(userNo, year, month);
    }

    //인트로 페이지
    @GetMapping("/intro")
    public void introTestGet() {
        log.info("--------introGet---------");
    }
}
