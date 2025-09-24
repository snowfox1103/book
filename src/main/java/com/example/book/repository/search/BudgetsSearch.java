package com.example.book.repository.search;

import com.example.book.domain.finance.Budgets;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BudgetsSearch {
    Page<Budgets> searchAllBuds(Long userNo, Integer selectYear, Integer selectMonth, Long budCategories, Long amountMin, Long amountMax, Pageable pageable);

}
