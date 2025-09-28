package com.example.book.service;

import com.example.book.domain.finance.Budgets;
import com.example.book.dto.BudgetAlertDTO;
import com.example.book.dto.BudgetsDTO;
import com.example.book.dto.PageRequestDTO;
import com.example.book.dto.PageResponseDTO;

import java.util.List;

public interface BudgetsService {
    Long registerBudget(BudgetsDTO budgetsDTO);
    BudgetsDTO readOneBudget(Long budgetId);
    void modifyBudget(BudgetsDTO budgetsDTO);
    void removeBudget(Long budgetId);
    PageResponseDTO<BudgetsDTO> budgetListByUser(Long userNo,PageRequestDTO pageRequestDTO);
    Long wholeSetBudgetAmount(Long userNo); //이번 달 총 설정 예산
    Long budgetUses(Long userNo); //이번 달 총 사용 예산
    List<BudgetAlertDTO> getBudgetAlerts(Long userNo); //0926 조덕진 수정 알림용
    void updateCategoryThreshold(Long userNo, Long catId, int threshold, int year, int month); //0926 조덕진 수정 알림용
}
