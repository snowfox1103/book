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
            "and month(t.transDate) = :month ")
    Long totalUseByCategory(Long catId,int year,int month, Long userNo);
    //해당 달 해당 카테고리 사용 금액 총합 계산

    @Query("select COALESCE(SUM(t.transAmount),0)"+
            "from Transactions t "+
            "where t.userNo = :userNo "+
            "and year(t.transDate) = :year "+
            "and month(t.transDate) = :month ")
    Long totalUseByMonth(Long catId, int year, int month, Long userNo);
    //해당 달 모든 사용 금액 총합 계산
}
