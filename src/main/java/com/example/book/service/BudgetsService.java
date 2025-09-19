package com.example.book.service;

import com.example.book.dto.BudgetsDTO;
import com.example.book.dto.PageRequestDTO;
import com.example.book.dto.PageResponseDTO;

public interface BudgetsService {
    Long registerBudget(BudgetsDTO budgetsDTO);
    void modifyBudget(BudgetsDTO budgetsDTO);
    void removeBudget(Long budgetId);
    PageResponseDTO<BudgetsDTO> budgetList(PageRequestDTO pageRequestDTO);
}
