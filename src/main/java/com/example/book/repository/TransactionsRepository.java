package com.example.book.repository;

import com.example.book.domain.finance.Transactions;
import com.example.book.repository.search.TransactionsSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransactionsRepository extends JpaRepository<Transactions, Long>, TransactionsSearch {
    @Query("select COALESCE(SUM(t.transAmount),0)"+
            "from Transactions t "+
            "where t.transCategory = :catId "+
            "and t.userNo = :userNo "+
            "and year(t.transDate) = :year "+
            "and month(t.transDate) = :month "+
            "and t.transInOut = 'OUT'")
    Long totalUseByCategory(Long catId,int year,int month, Long userNo);
    //해당 달 해당 카테고리 출금 금액 총합 계산

    @Query("select COALESCE(SUM(t.transAmount),0)"+
            "from Transactions t "+
            "where t.userNo = :userNo "+
            "and year(t.transDate) = :year "+
            "and month(t.transDate) = :month " +
            "and t.transInOut = 'OUT'")
    Long totalUseByMonth(int year, int month, Long userNo);
    //해당 달 모든 출금 금액 총합 계산

    @Query("select COALESCE(SUM(t.transAmount),0)"+
            "from Transactions t "+
            "where t.userNo = :userNo "+
            "and year(t.transDate) = :year "+
            "and month(t.transDate) = :month " +
            "and t.transInOut = 'IN'")
    Long totalIncomeByMonth(int year,int month,Long userNo);
    //해당 달 모든 입금 금액 계산
}
