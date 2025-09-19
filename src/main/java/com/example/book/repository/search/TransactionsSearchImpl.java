package com.example.book.repository.search;

import com.example.book.domain.InOrOut;
import com.example.book.domain.QTransactions;
import com.example.book.domain.Transactions;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDate;
import java.util.List;

public class TransactionsSearchImpl extends QuerydslRepositorySupport implements TransactionsSearch {
    public TransactionsSearchImpl(){
        super(Transactions.class);
    }
    @Override
    public Page<Transactions> searchTrans(Pageable pageable){
        QTransactions qTransactions = QTransactions.transactions;
        JPQLQuery<Transactions> query = from(qTransactions);        //select .. from transactions
        query.where(qTransactions.transTitle.contains("1"));        //where title like...
        this.getQuerydsl().applyPagination(pageable,query);
        List<Transactions> list = query.fetch();
        Long count = query.fetchCount();
        return null;
    }
    @Override
    public Page<Transactions> searchAllTrans(String[] types, String keyword, Long category, Long minn, Long maxx, LocalDate startDay, LocalDate endDay, InOrOut io, Pageable pageable){
        QTransactions qTransactions = QTransactions.transactions;
        JPQLQuery<Transactions> query = from(qTransactions);
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if ((types != null) && (keyword != null)) {
            for (String type : types) {
                switch (type) {
                    case "t":
                        booleanBuilder.or(qTransactions.transTitle.contains(keyword));break;
                    case "m":
                        booleanBuilder.or(qTransactions.transMemo.contains(keyword));break;
                }
            }
        }
        if(category != null) booleanBuilder.and(qTransactions.transCategory.eq(category));
        if(minn != null) booleanBuilder.and(qTransactions.transAmount.goe(minn));
        if(maxx != null) booleanBuilder.and(qTransactions.transAmount.loe(maxx));
        if(startDay != null) booleanBuilder.and(qTransactions.transDate.goe(startDay));
        if(endDay != null) booleanBuilder.and(qTransactions.transDate.loe(endDay));
        if(io != null) booleanBuilder.and(qTransactions.transInOut.eq(io));
        query.where(booleanBuilder); //검색 조건이 없으면 booleanBuilder에는 아무 조건도 추가 안 됨
        query.where(qTransactions.transId.gt(0L)); //검색 조건이 없으면 transId>0인 모든 데이터 조회
        this.getQuerydsl().applyPagination(pageable,query);
        List<Transactions> list = query.fetch();
        long cnt = query.fetchCount();
        return new PageImpl<>(list,pageable,cnt);
    }
}
