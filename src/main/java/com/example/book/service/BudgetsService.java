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
    //리스트 불러올 때마다 예산 사용 내역 자동 집계

}
