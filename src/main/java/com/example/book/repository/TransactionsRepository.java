package com.example.book.repository;

import com.example.book.domain.finance.InOrOut;
import com.example.book.domain.finance.Transactions;
import com.example.book.repository.search.TransactionsSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


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

    default Page<Transactions> searchAllTrans(Long userNo,
                                              String[] types,
                                              String keyword,
                                              Long category,
                                              Long minn,
                                              Long maxx,
                                              LocalDate startDay,
                                              LocalDate endDay,
                                              InOrOut io,
                                              Pageable pageable) {
        boolean title = false;
        boolean memo  = false;

        if (types != null && types.length > 0) {
            List<String> list = Arrays.asList(types);
            title = list.contains("title") || list.contains("TITLE");
            memo  = list.contains("memo")  || list.contains("MEMO");
            if (!title && !memo) { title = true; memo = true; }
        } else {
            title = true; memo = true; // 기본: 둘 다 검색
        }

        return searchAllTransImpl(userNo, title, memo, keyword,
                category, minn, maxx, startDay, endDay, io, pageable);
    }

    @Query("""
        select t
          from Transactions t
         where t.userNo = :userNo
           and (:io is null or t.transInOut = :io)
           and (:category is null or t.transCategory = :category)
           and (:minn is null or t.transAmount >= :minn)
           and (:maxx is null or t.transAmount <= :maxx)
           and (:startDay is null or function('date', t.transDate) >= :startDay)
           and (:endDay   is null or function('date', t.transDate) <= :endDay)
           and (
                :keyword is null
                or (
                      (:title = true and lower(t.transTitle) like lower(concat('%', :keyword, '%')))
                   or (:memo  = true and lower(t.transMemo)  like lower(concat('%', :keyword, '%')))
                   )
               )
        """)
    Page<Transactions> searchAllTransImpl(@Param("userNo") Long userNo,
                                          @Param("title") boolean title,
                                          @Param("memo") boolean memo,
                                          @Param("keyword") String keyword,
                                          @Param("category") Long category,
                                          @Param("minn") Long minn,
                                          @Param("maxx") Long maxx,
                                          @Param("startDay") LocalDate startDay,
                                          @Param("endDay") LocalDate endDay,
                                          @Param("io") InOrOut io,
                                          Pageable pageable);

    @Query(value = """
        select
          transId        as transId,
          userNo         as userNo,
          transAmount    as transAmount,
          transDate      as transDate,
          transCategory  as transCategory
          from `transaction`
         where date(transDate) between :from and :to
           and transCategory in (:categories)
           and transAmount > 0
        """, nativeQuery = true)
    List<TxRow> findPointableRows(@Param("from") LocalDate from,
                                  @Param("to") LocalDate to,
                                  @Param("categories") Collection<Long> categories);

    interface TxRow {
        Long getTransId();
        Long getUserNo();
        Long getTransAmount();
        LocalDateTime getTransDate();   // TIMESTAMP → LocalDateTime 변환
        Long getTransCategory();
    }

    // 이번 달 특정 구독건이 이미 결제됐는지 여부 0929 조덕진
    @Query("""
      SELECT COUNT(t) > 0
      FROM Transactions t
      WHERE t.userNo = :userNo
        AND t.subId = :subId
        AND FUNCTION('YEAR', t.transDate) = :year
        AND FUNCTION('MONTH', t.transDate) = :month
    """)
    boolean existsThisMonth(@Param("userNo") Long userNo,
                            @Param("subId") Long subId,
                            @Param("year") int year,
                            @Param("month") int month);
}
