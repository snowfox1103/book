package com.example.book.repository;

import com.example.nyjbook.domain.Budgets;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
class BudgetsRepositoryTests {
    @Autowired
    private BudgetsRepository budgetsRepository;
    @Test
    public void insertBudgets(){
        long a = 1L;
        IntStream.rangeClosed(1,12).forEach(i->{
            Budgets budgets = Budgets.builder()
                    .budAmount(250000L)
                    .budCategory(i*a)
                    .budIsOver(false)
                    .budCurrent(90000L)
                    .budMonth(9)
                    .budYear(2025)
                    .userNo(73L)
                    .build();
            budgetsRepository.save(budgets);
        });
    }
    @Test
    public void testSelectBudgets(){
        Long tno = 12L;
        Optional<Budgets> result = budgetsRepository.findById(tno);
        Budgets budgets = result.orElseThrow();
        log.info(budgets);
    }
    @Test
    public void testUpdateBudgets(){
        Long tno = 2L;
        Optional<Budgets> result = budgetsRepository.findById(tno);
        Budgets budgets = result.orElseThrow();
        budgets.changeBudget(120L,null,false,false ,false);
        budgetsRepository.save(budgets);
    }
    @Test
    public void testDeleteBudgets(){
        Long tno = 4L;
        budgetsRepository.deleteById(tno);
    }
    @Test
    public void testPagingBudgets(){
        Pageable pageable = PageRequest.of(0,10, Sort.by("budgetId").descending());
        Page<Budgets> result = budgetsRepository.findAll(pageable);

        log.info("total count: "+result.getTotalElements());
        log.info("total pages: "+result.getTotalPages());
        log.info("page number: "+result.getNumber());
        log.info("page size: "+result.getSize());
        List<Budgets> budgetList = result.getContent();
        budgetList.forEach(bud->log.info(bud));
    }
}