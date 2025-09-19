package com.example.book.service;

import com.example.book.dto.BudgetsDTO;
import com.example.book.dto.PageRequestDTO;
import com.example.book.dto.PageResponseDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
class budgetsServiceTests {
    @Autowired
    private BudgetsService budgetsService;
    @Test
    public void testRegisterBudget(){
        BudgetsDTO budgetsDTO = BudgetsDTO.builder()
                .budCategory(3L)
                .budAmount(450000L)
                .budCurrent(120000L)
                .budIsOver(false)
                .budOver(false)
                .budReduction(false)
                .budNotice(false)
                .budYear(2025L)
                .budMonth(12L)
                .userNo(22L)
                .build();
        Long bno = budgetsService.registerBudget(budgetsDTO);
        log.info("bno: "+bno);
    }
    @Test
    public void testModifyBudget(){
        BudgetsDTO budgetsDTO = BudgetsDTO.builder()
                .budgetId(25L)
                .budAmount(890000L)
                .build();
        budgetsService.modifyBudget(budgetsDTO);
    }
    @Test
    public void testRemoveBudget(){
        budgetsService.removeBudget(2L);
    }
    @Test
    public void testBudgetList(){
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .selectMonth(12)
                .selectYear(2025)
                .build();
        PageResponseDTO pageResponseDTO = budgetsService.budgetList(pageRequestDTO);
        log.info(pageResponseDTO);
    }
}