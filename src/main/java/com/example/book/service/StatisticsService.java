package com.example.book.service;

import com.example.book.dto.StatisticsDTO;
import com.example.book.dto.YearlyTransactionDTO;

import java.util.List;
import java.util.Map;

public interface StatisticsService {
    public List<StatisticsDTO> getMonthlyStatistics(Long userNo, int year, int month);
    /** 최근 3개월 예산/사용금액 막대그래프용 */
    public List<StatisticsDTO> getRecent3MonthsStatistics(Long userNo);

    /** 당월 총 입금/지출 막대그래프용 */
    public Map<String, Long> getCurrentMonthIncomeExpense(Long userNo);

    /** 연간 입출금 꺾은선그래프용 */
    public List<YearlyTransactionDTO> getYearlyTransactions(Long userNo, int year);

}
