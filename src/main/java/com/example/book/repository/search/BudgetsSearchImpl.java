package com.example.book.repository.search;

import com.example.book.domain.finance.Budgets;
import com.example.book.domain.finance.QBudgets;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

//extends 꼭 할 것
//나중에 가능하면 카테고리 추가
public class BudgetsSearchImpl extends QuerydslRepositorySupport implements BudgetsSearch{
    public BudgetsSearchImpl(){super(Budgets.class);} //생성자 추가 필수
    @Override
    public Page<Budgets> searchAllBuds(Long userNo, Integer selectYear, Integer selectMonth, Long budCategories, Long amountMin, Long amountMax, Pageable pageable){
        QBudgets qBudgets = QBudgets.budgets;
        JPQLQuery<Budgets> query = from(qBudgets);
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qBudgets.userNo.eq(userNo));
        if(selectYear != null) booleanBuilder.and(qBudgets.budYear.eq(selectYear));
        if(selectMonth!=null) booleanBuilder.and(qBudgets.budMonth.eq(selectMonth));
        if(budCategories != null) booleanBuilder.and(qBudgets.budCategory.eq(budCategories));
        if(amountMin != null) booleanBuilder.and(qBudgets.budAmount.goe(amountMin));
        if(amountMax != null) booleanBuilder.and(qBudgets.budAmount.loe(amountMax));
        query.where(booleanBuilder);
//        query.where(qBudgets.budgetId.gt(0L)); 검색 조건이 없으면 모든 유저가 포함된 db전체 불러오기
        this.getQuerydsl().applyPagination(pageable,query);
        List<Budgets> list = query.fetch();
        long cnt = query.fetchCount();
        return new PageImpl<>(list,pageable,cnt);
    }
}
