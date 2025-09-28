package com.example.book.service;

import com.example.book.dto.StatisticsDTO;
import com.example.book.dto.YearlyTransactionDTO;

import java.util.List;
import java.util.Map;

public interface StatisticsService {

//    /** 1. 특정 달 카테고리별 출금 내역 (도넛 차트) */
    List<StatisticsDTO> getMonthlyCategoryExpenses(Long userNo, int year, int month);

//    /** 2. 특정 연도 입출금 월별 내역 (꺾은선 그래프) */
    List<YearlyTransactionDTO> getYearlyTransactions(Long userNo, int year);

//    /** 3. 이번 달 입출금 총액 (막대 그래프) */
    Map<String, Long> getCurrentMonthIncomeExpense(Long userNo);

//    /** 4. 특정 달 예산 vs 사용금액 (막대 그래프) */
    List<StatisticsDTO> getMonthlyBudgetStatistics(Long userNo, int year, int month);

//    /** 5. 특정 연도 예산 vs 사용금액 (꺾은선 그래프) */
    List<YearlyTransactionDTO> getYearlyBudgetStatistics(Long userNo, int year);

    //날짜별로 총입금,총지출
    Map<String, Map<String, Long>> getDailyIncomeExpense(Long userNo, int year, int month);
}
