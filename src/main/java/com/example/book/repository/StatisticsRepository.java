package com.example.book.repository;

import com.example.book.domain.finance.Budgets;
import com.example.book.domain.finance.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StatisticsRepository extends JpaRepository<Transactions, Long> {

    /** 1. 특정 달 카테고리별 출금 내역 */
    @Query("select t from Transactions t where t.userNo = :userNo and year(t.transDate) = :year and month(t.transDate) = :month and t.transInOut = 'OUT'")
    List<Transactions> findExpensesByCategory(@Param("userNo") Long userNo,
                                              @Param("year") int year,
                                              @Param("month") int month);

    /** 2. 특정 연도 전체 거래 */
    @Query("select t from Transactions t where t.userNo = :userNo and year(t.transDate) = :year")
    List<Transactions> findTransactionsByYear(@Param("userNo") Long userNo,
                                              @Param("year") int year);

    /** 3. 이번 달 입출금 */
    @Query("select t from Transactions t where t.userNo = :userNo and year(t.transDate) = :year and month(t.transDate) = :month")
    List<Transactions> findTransactionsByMonth(@Param("userNo") Long userNo,
                                               @Param("year") int year,
                                               @Param("month") int month);

    /** 4. 특정 달 예산 */
    @Query("select b from Budgets b where b.userNo = :userNo and b.budYear = :year and b.budMonth = :month")
    List<Budgets> findBudgetsByMonth(@Param("userNo") Long userNo,
                                     @Param("year") int year,
                                     @Param("month") int month);

    /** 5. 특정 연도 예산 */
    @Query("select b from Budgets b where b.userNo = :userNo and b.budYear = :year")
    List<Budgets> findBudgetsByYear(@Param("userNo") Long userNo,
                                    @Param("year") int year);

}
