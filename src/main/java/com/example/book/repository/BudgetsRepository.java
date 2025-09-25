package com.example.book.repository;

import com.example.book.domain.finance.Budgets;
import com.example.book.repository.search.BudgetsSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BudgetsRepository extends JpaRepository<Budgets,Long>, BudgetsSearch {
    @Query("select b "+
            "from Budgets b "+
            "where b.budCategory = :catId "+
            "and b.budYear = :year "+
            "and b.budMonth = :month "+
            "and b.userNo = :userNo ")
    Optional<Budgets> usedBudgetByCategory(Long catId, int year, int month, Long userNo);
    //이번 달 해당 카테고리만 뽑기, 중복 방지용

    @Query("select coalesce(sum(b.budAmount),0) "+
            "from Budgets b "+
            "where b.budYear = :year "+
            "and b.budMonth = :month "+
            "and b.userNo = :userNo or b.userNo = null ")
    Long totalBudAmountByMonth(int year, int month, Long userNo);
    //해당 달 설정한 예산 총 합계

    @Query("select coalesce(sum(b.budCurrent)) "+
            "from Budgets b "+
            "where b.userNo = :userNo "+
            "and b.budYear = :year "+
            "and b.budMonth = :month")
    Long budgetUsesByMonth(int year,int month,Long userNo);
    //유저의 예산 사용 합계 리스트
}

