package com.example.book.service;

import java.time.YearMonth;

public interface BudgetsReadService {
    long getMonthlyBudget(Long userNo, YearMonth ym);
    long getMonthlyUsage(Long userNo, YearMonth ym);
}
