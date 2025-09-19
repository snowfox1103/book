package com.example.book.repository.search;

import com.example.book.domain.finance.InOrOut;
import com.example.book.domain.finance.QTransactions;
import com.example.book.domain.finance.Transactions;
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
    public Page<Transactions> searchAllTrans(String[] types, String keyword, Long minn, Long maxx, LocalDate a, LocalDate b, InOrOut io, Pageable pageable){
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
        if(minn != null) booleanBuilder.and(qTransactions.transAmount.goe(minn));
        if(maxx != null) booleanBuilder.and(qTransactions.transAmount.loe(maxx));
        if(a != null) booleanBuilder.and(qTransactions.transDate.goe(a));
        if(b != null) booleanBuilder.and(qTransactions.transDate.loe(b));
        if(io != null) booleanBuilder.and(qTransactions.transInOut.eq(io));
        query.where(booleanBuilder);
        query.where(qTransactions.transId.gt(0L));
        this.getQuerydsl().applyPagination(pageable,query);
        List<Transactions> list = query.fetch();
        long cnt = query.fetchCount();
        return new PageImpl<>(list,pageable,cnt);
    }
}
