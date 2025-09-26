package com.example.book.repository;

import com.example.book.domain.finance.Budgets;
import com.example.book.domain.finance.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StatisticsRepository extends JpaRepository<Transactions, Long> {

    // 해당 달 입출금 내역
    @Query("select t from Transactions t where t.userNo = :userNo and year(t.transDate) = :year and month(t.transDate) = :month")
    List<Transactions> findTransactionsByUserAndMonth(@Param("userNo") Long userNo,
                                                      @Param("year") int year,
                                                      @Param("month") int month);

    // 해당 달 예산 내역
    @Query("select b from Budgets b where b.userNo = :userNo and b.budYear = :year and b.budMonth = :month")
    List<Budgets> findBudgetsByUserAndMonth(@Param("userNo") Long userNo,
                                            @Param("year") int year,
                                            @Param("month") int month);

    // 최근 3개월 예산 내역
    @Query("select b from Budgets b where b.userNo = :userNo and (b.budYear*100+b.budMonth) >= :fromYearMonth order by b.budYear, b.budMonth")
    List<Budgets> findBudgetsForRecentMonths(@Param("userNo") Long userNo,
                                             @Param("fromYearMonth") int fromYearMonth);

    // 최근 3개월 입출금 내역
    @Query("select t from Transactions t where t.userNo = :userNo and (year(t.transDate)*100+month(t.transDate)) >= :fromYearMonth")
    List<Transactions> findTransactionsForRecentMonths(@Param("userNo") Long userNo,
                                                       @Param("fromYearMonth") int fromYearMonth);

    // 연간 월별 총 입출금
    @Query("select t from Transactions t where t.userNo = :userNo and year(t.transDate) = :year")
    List<Transactions> findTransactionsForYear(@Param("userNo") Long userNo,
                                               @Param("year") int year);
}
