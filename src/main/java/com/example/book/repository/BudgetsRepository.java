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
    //해당 달 해당 카테고리만 뽑기, 해당 월 카테고리 등록 중복 방지,없으면 null

    @Query("select coalesce(sum(b.budAmount),0) "+
            "from Budgets b "+
            "where b.budYear = :year "+
            "and b.budMonth = :month "+
            "and b.userNo = :userNo or b.userNo = null ")
    Long totalBudAmountByMonth(int year, int month, Long userNo);
    //해당 달 설정한 예산 총 합계

    @Query("select coalesce(sum(b.budCurrent),0) "+
            "from Budgets b "+
            "where b.userNo = :userNo "+
            "and b.budYear = :year "+
            "and b.budMonth = :month")
    Long budgetUsesByMonth(int year,int month,Long userNo);
    //해당 달 유저의 예산 사용 합계 리스트

    List<Budgets> findByUserNoAndBudNoticeTrue(Long userNo); //0926 조덕진 수정 예산 알림용
    Optional<Budgets> findByUserNoAndBudCategoryAndBudYearAndBudMonth(
      Long userNo, Long budCategory, Integer budYear, Integer budMonth); //마찬가지
    List<Budgets> findByUserNoAndBudYearAndBudMonthAndBudNoticeTrue(
      Long userNo, Integer budYear, Integer budMonth
    ); //마찬가지
    List<Budgets> findByUserNoAndBudNoticeTrueAndBudYearAndBudMonth(
            Long userNo, int budYear, int budMonth); //마찬가지
}

