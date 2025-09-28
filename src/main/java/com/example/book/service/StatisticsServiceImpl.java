package com.example.book.service;

import com.example.book.domain.finance.Categories;
import com.example.book.domain.finance.Transactions;
import com.example.book.domain.finance.Budgets;
import com.example.book.dto.StatisticsDTO;
import com.example.book.dto.YearlyTransactionDTO;
import com.example.book.repository.CategoriesRepository;
import com.example.book.repository.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsRepository statisticsRepository;
    private final CategoriesRepository categoriesRepository;

    /** 1. 특정 달 카테고리별 출금 내역 (도넛 차트) */
    @Override
    public List<StatisticsDTO> getMonthlyCategoryExpenses(Long userNo, int year, int month) {
        List<Transactions> expenses = statisticsRepository.findExpensesByCategory(userNo, year, month);
        List<Categories> categories = categoriesRepository.findAll();

        Map<Long, String> categoryMap = categories.stream()
                .collect(Collectors.toMap(Categories::getCatId, Categories::getCatName));

        return expenses.stream()
                .collect(Collectors.groupingBy(Transactions::getTransCategory,
                        Collectors.summingLong(Transactions::getTransAmount)))
                .entrySet().stream()
                .map(e -> StatisticsDTO.builder()
                        .categoryId(e.getKey())
                        .categoryName(categoryMap.getOrDefault(e.getKey(), "기타"))
                        .totalExpense(e.getValue())
                        .year(year)
                        .month(month)
                        .build())
                .collect(Collectors.toList());
    }

    /** 2. 특정 연도 입출금 월별 내역 (꺾은선 그래프) */
    @Override
    public List<YearlyTransactionDTO> getYearlyTransactions(Long userNo, int year) {
        List<Transactions> transactions = statisticsRepository.findTransactionsByYear(userNo, year);

        Map<Integer, YearlyTransactionDTO> map = new HashMap<>();
        for (int m = 1; m <= 12; m++) {
            map.put(m, YearlyTransactionDTO.builder().month(m).totalIncome(0L).totalExpense(0L).build());
        }

        for (Transactions t : transactions) {
            int m = t.getTransDate().getMonthValue();
            YearlyTransactionDTO oldDto = map.get(m);

            YearlyTransactionDTO newDto;
            if (t.getTransInOut().name().equals("IN")) {
                newDto = YearlyTransactionDTO.builder()
                        .month(m)
                        .totalIncome(oldDto.getTotalIncome() + t.getTransAmount())
                        .totalExpense(oldDto.getTotalExpense())
                        .build();
            } else {
                newDto = YearlyTransactionDTO.builder()
                        .month(m)
                        .totalIncome(oldDto.getTotalIncome())
                        .totalExpense(oldDto.getTotalExpense() + t.getTransAmount())
                        .build();
            }
            map.put(m, newDto);
        }

        return new ArrayList<>(map.values());
    }

    /** 3. 이번 달 입출금 총액 (막대 그래프) */
    @Override
    public Map<String, Long> getCurrentMonthIncomeExpense(Long userNo) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        List<Transactions> transactions = statisticsRepository.findTransactionsByMonth(userNo, year, month);

        long income = transactions.stream()
                .filter(t -> t.getTransInOut().name().equals("IN"))
                .mapToLong(Transactions::getTransAmount).sum();
        long expense = transactions.stream()
                .filter(t -> t.getTransInOut().name().equals("OUT"))
                .mapToLong(Transactions::getTransAmount).sum();

        Map<String, Long> result = new HashMap<>();
        result.put("income", income);
        result.put("expense", expense);
        return result;
    }

    /** 4. 특정 달 예산 vs 사용금액 (막대 그래프) */
    @Override
    public List<StatisticsDTO> getMonthlyBudgetStatistics(Long userNo, int year, int month) {
        List<Budgets> budgets = statisticsRepository.findBudgetsByMonth(userNo, year, month);
        List<Transactions> transactions = statisticsRepository.findTransactionsByMonth(userNo, year, month);
        List<Categories> categories = categoriesRepository.findAll();

        Map<Long, String> categoryMap = categories.stream()
                .collect(Collectors.toMap(Categories::getCatId, Categories::getCatName));

        return budgets.stream()
                .map(b -> {
                    long used = transactions.stream()
                            .filter(t -> t.getTransCategory().equals(b.getBudCategory()) &&
                                    t.getTransInOut().name().equals("OUT"))
                            .mapToLong(Transactions::getTransAmount).sum();
                    return StatisticsDTO.builder()
                            .categoryId(b.getBudCategory())
                            .categoryName(categoryMap.getOrDefault(b.getBudCategory(), "기타"))
                            .budgetAmount(b.getBudAmount())
                            .budgetUsed(used)
                            .year(year)
                            .month(month)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /** 5. 특정 연도 예산 vs 사용금액 (꺾은선 그래프) */
    @Override
    public List<YearlyTransactionDTO> getYearlyBudgetStatistics(Long userNo, int year) {
        List<Budgets> budgets = statisticsRepository.findBudgetsByYear(userNo, year);
        List<Transactions> transactions = statisticsRepository.findTransactionsByYear(userNo, year);

        // 1. 월별 DTO 초기화 (1~12월)
        Map<Integer, YearlyTransactionDTO> monthMap = new HashMap<>();
        for (int m = 1; m <= 12; m++) {
            monthMap.put(m, YearlyTransactionDTO.builder()
                    .month(m)
                    .budgetAmount(0L)
                    .budgetUsed(0L)
                    .totalIncome(0L)
                    .totalExpense(0L)
                    .build());
        }

        // 2. 월별 예산 합계 및 예산 카테고리 매핑
        Map<Integer, List<Long>> monthBudgetCategories = new HashMap<>(); // 월별 예산 카테고리
        budgets.forEach(b -> {
            // 월별 예산 금액 누적
            YearlyTransactionDTO dto = monthMap.get(b.getBudMonth());
            dto.setBudgetAmount(dto.getBudgetAmount() + b.getBudAmount());
            monthMap.put(b.getBudMonth(), dto);

            // 월별 예산 카테고리 저장
            monthBudgetCategories.computeIfAbsent(b.getBudMonth(), k -> new ArrayList<>())
                    .add(b.getBudCategory());
        });

        // 3. 트랜잭션 중 예산 카테고리만 사용 금액 집계
        transactions.stream()
                .filter(t -> t.getTransInOut().name().equals("OUT"))
                .forEach(t -> {
                    int m = t.getTransDate().getMonthValue();
                    List<Long> budgetCats = monthBudgetCategories.getOrDefault(m, List.of());
                    if (budgetCats.contains(t.getTransCategory())) {
                        YearlyTransactionDTO dto = monthMap.get(m);
                        dto.setBudgetUsed(dto.getBudgetUsed() + t.getTransAmount());
                        monthMap.put(m, dto);
                    }
                });

        return new ArrayList<>(monthMap.values());
    }

    @Override
    public Map<String, Map<String, Long>> getDailyIncomeExpense(Long userNo, int year, int month) {
        List<Transactions> txs = statisticsRepository.findTransactionsByMonth(userNo, year, month);

        return txs.stream().collect(Collectors.groupingBy(
                t -> t.getTransDate().toString(),   // LocalDate → String ("2025-09-01")
                Collectors.groupingBy(
                        t -> t.getTransInOut().name(),
                        Collectors.summingLong(Transactions::getTransAmount)
                )
        ));
    }


}
