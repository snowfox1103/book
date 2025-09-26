package com.example.book.service;

import com.example.book.domain.finance.Budgets;
import com.example.book.domain.finance.Transactions;
import com.example.book.dto.StatisticsDTO;
import com.example.book.dto.YearlyTransactionDTO;
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
public class StatisticsServiceImpl implements StatisticsService{
    private final StatisticsRepository statisticsRepository;

    /** 이번 달 카테고리별 도넛 + 예산 사용현황 */
    @Override
    public List<StatisticsDTO> getMonthlyStatistics(Long userNo, int year, int month) {
        List<Transactions> transactions = statisticsRepository.findTransactionsByUserAndMonth(userNo, year, month);
        List<Budgets> budgets = statisticsRepository.findBudgetsByUserAndMonth(userNo, year, month);

        return budgets.stream()
                .map(b -> {
                    long expense = transactions.stream()
                            .filter(t -> t.getTransCategory().equals(b.getBudCategory()) && t.getTransInOut().name().equals("OUT"))
                            .mapToLong(Transactions::getTransAmount)
                            .sum();
                    long income = transactions.stream()
                            .filter(t -> t.getTransCategory().equals(b.getBudCategory()) && t.getTransInOut().name().equals("IN"))
                            .mapToLong(Transactions::getTransAmount)
                            .sum();
                    return StatisticsDTO.builder()
                            .categoryId(b.getBudCategory())
                            .categoryName("") // 카테고리명은 나중에 서비스에서 매핑 가능
                            .budgetAmount(b.getBudAmount())
                            .budgetUsed(b.getBudCurrent())
                            .totalExpense(expense)
                            .totalIncome(income)
                            .year(b.getBudYear())
                            .month(b.getBudMonth())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /** 최근 3개월 예산/사용금액 막대그래프용 */
    @Override
    public List<StatisticsDTO> getRecent3MonthsStatistics(Long userNo) {
        LocalDate now = LocalDate.now();
        LocalDate threeMonthsAgo = now.minusMonths(2); // 이번 달 포함 3개월
        int fromYM = threeMonthsAgo.getYear() * 100 + threeMonthsAgo.getMonthValue();

        List<Transactions> transactions = statisticsRepository.findTransactionsForRecentMonths(userNo, fromYM);
        List<Budgets> budgets = statisticsRepository.findBudgetsForRecentMonths(userNo, fromYM);

        return budgets.stream()
                .map(b -> {
                    long used = transactions.stream()
                            .filter(t -> t.getTransCategory().equals(b.getBudCategory()) && t.getTransInOut().name().equals("OUT") &&
                                    t.getTransDate().getYear() == b.getBudYear() && t.getTransDate().getMonthValue() == b.getBudMonth())
                            .mapToLong(Transactions::getTransAmount)
                            .sum();
                    return StatisticsDTO.builder()
                            .categoryId(b.getBudCategory())
                            .budgetAmount(b.getBudAmount())
                            .budgetUsed(used)
                            .year(b.getBudYear())
                            .month(b.getBudMonth())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /** 당월 총 입금/지출 막대그래프용 */
    @Override
    public Map<String, Long> getCurrentMonthIncomeExpense(Long userNo) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        List<Transactions> transactions = statisticsRepository.findTransactionsByUserAndMonth(userNo, year, month);

        long totalIncome = transactions.stream()
                .filter(t -> t.getTransInOut().name().equals("IN"))
                .mapToLong(Transactions::getTransAmount)
                .sum();
        long totalExpense = transactions.stream()
                .filter(t -> t.getTransInOut().name().equals("OUT"))
                .mapToLong(Transactions::getTransAmount)
                .sum();

        Map<String, Long> result = new HashMap<>();
        result.put("income", totalIncome);
        result.put("expense", totalExpense);
        return result;
    }

    /** 연간 입출금 꺾은선그래프용 */
    @Override
    public List<YearlyTransactionDTO> getYearlyTransactions(Long userNo, int year) {
        List<Transactions> transactions = statisticsRepository.findTransactionsForYear(userNo, year);

        Map<Integer, YearlyTransactionDTO> monthMap = new HashMap<>();
        for (int m = 1; m <= 12; m++) {
            monthMap.put(m, YearlyTransactionDTO.builder().month(m).totalIncome(0L).totalExpense(0L).build());
        }

        for (Transactions t : transactions) {
            int m = t.getTransDate().getMonthValue();
            YearlyTransactionDTO dto = monthMap.get(m);
            if (t.getTransInOut().name().equals("IN")) {
                dto.setTotalIncome(dto.getTotalIncome() + t.getTransAmount());
            } else {
                dto.setTotalExpense(dto.getTotalExpense() + t.getTransAmount());
            }
        }
        return new ArrayList<>(monthMap.values());
    }
}
