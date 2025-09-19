package com.example.book.service;

import com.example.book.domain.Budgets;
import com.example.book.dto.BudgetsDTO;
import com.example.book.dto.PageRequestDTO;
import com.example.book.dto.PageResponseDTO;
import com.example.book.repository.BudgetsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class BudgetsServiceImpl implements BudgetsService {
    private final BudgetsRepository budgetsRepository;
    private final ModelMapper modelMapper;
    @Override
    public Long registerBudget(BudgetsDTO budgetsDTO){
        Budgets budgets = modelMapper.map(budgetsDTO,Budgets.class);
        Long budgetId = budgetsRepository.save(budgets).getBudgetId();
        return budgetId;
    }
    @Override
    public void modifyBudget(BudgetsDTO budgetsDTO){
        Optional<Budgets> result = budgetsRepository.findById(budgetsDTO.getBudgetId());
        Budgets budgets = result.orElseThrow();
        budgets.changeBudget(budgetsDTO.getBudAmount(),budgetsDTO.getBudCategory(),budgetsDTO.getBudOver(),budgetsDTO.getBudReduction(),budgetsDTO.getBudNotice());
        budgetsRepository.save(budgets);
    }
    @Override
    public void removeBudget(Long budgetId){
        budgetsRepository.deleteById(budgetId);
    }
    @Override
    public PageResponseDTO<BudgetsDTO> budgetList(PageRequestDTO pageRequestDTO){
        Integer selectYear = pageRequestDTO.getSelectYear();
        Integer selectMonth = pageRequestDTO.getSelectMonth();
        Long budCategories = pageRequestDTO.getCategoriess();
        Long amountMin = pageRequestDTO.getAmountMin();
        Long amountMax = pageRequestDTO.getAmountMax();
        Pageable pageable = pageRequestDTO.getPageable("budgetId");
        Page<Budgets> result = budgetsRepository.searchAllBuds(selectYear,selectMonth,budCategories,amountMin,amountMax,pageable);
        List<BudgetsDTO> dtoList = result.getContent().stream()
                .map(budgets -> modelMapper.map(budgets,BudgetsDTO.class)).collect(Collectors.toList());
        return PageResponseDTO.<BudgetsDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();
    }
}
