package com.example.book.service;

import com.example.book.dto.BudgetsDTO;
import com.example.book.dto.PageRequestDTO;
import com.example.book.dto.PageResponseDTO;

public interface BudgetsService {
    Long registerBudget(BudgetsDTO budgetsDTO);
    BudgetsDTO readOneBudget(Long budgetId);
    void modifyBudget(BudgetsDTO budgetsDTO);
    void removeBudget(Long budgetId);
    PageResponseDTO<BudgetsDTO> budgetListByUser(Long userNo,PageRequestDTO pageRequestDTO);
    Long wholeSetBudgetAmount(Long userNo); //이번 달 총 설정 예산
    Long budgetUses(Long userNo); //이번 달 총 사용 예산
}
