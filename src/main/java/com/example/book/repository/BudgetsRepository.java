package com.example.book.repository;

import com.example.book.domain.finance.Budgets;
import com.example.book.repository.search.BudgetsSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BudgetsRepository extends JpaRepository<Budgets,Long>, BudgetsSearch {
    @Query("select b "+
            "from Budgets b "+
            "where b.budCategory = :catId "+
            "and b.budYear = :year "+
            "and b.budMonth = :month "+
            "and b.userNo = :userNo ")
    Optional<Budgets> usedBudgetByCategory(Long catId, int year, int month, Long userNo);
    //해당 달 해당 카테고리만 뽑기

    @Query("select coalesce(sum(b.budAmount),0) "+
            "from Budgets b "+
            "where b.budYear = :year "+
            "and b.budMonth = :month "+
            "and b.userNo = :userNo ")
    Long totalBudAmountByMonth(Long catId, int year, int month, Long userNo);
    //해당 달 설정한 예산 총 합계
}

