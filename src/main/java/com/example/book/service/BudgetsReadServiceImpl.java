package com.example.book.service;

import com.example.book.repository.BudgetsRepository;
import com.example.book.repository.TransactionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class BudgetsReadServiceImpl implements BudgetsReadService {

    private final BudgetsRepository budgetsRepository;
    private final TransactionsRepository transactionsRepository;

    @Override
    public long getMonthlyBudget(Long userNo, YearMonth ym) {
        // 예산 총액 (없으면 0)
        return budgetsRepository
                .totalBudAmountByMonth(ym.getYear(), ym.getMonthValue(), userNo);
    }

    @Override
    public long getMonthlyUsage(Long userNo, YearMonth ym) {
        // 지출 총액(OUT) (없으면 0)
        return transactionsRepository
                .totalUseByMonth(ym.getYear(), ym.getMonthValue(), userNo);
    }
}
