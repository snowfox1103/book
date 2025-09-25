package com.example.book.service;

import com.example.book.domain.finance.Budgets;
import com.example.book.dto.BudgetsDTO;
import com.example.book.dto.PageRequestDTO;
import com.example.book.dto.PageResponseDTO;
import com.example.book.repository.BudgetsRepository;
import com.example.book.repository.TransactionsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class BudgetsServiceImpl implements BudgetsService {
    private final TransactionsRepository transactionsRepository;
    private final BudgetsRepository budgetsRepository;
    private final ModelMapper modelMapper;
    @Override
    public Long registerBudget(BudgetsDTO budgetsDTO){
        Optional<Budgets> existingBudget = budgetsRepository.usedBudgetByCategory(budgetsDTO.getBudCategory(), budgetsDTO.getBudYear(), budgetsDTO.getBudMonth(), budgetsDTO.getUserNo());
        if(existingBudget.isPresent()){
            throw new IllegalArgumentException("이미 해당 카테고리의 예산이 존재합니다.");
        }
        Budgets budgets = modelMapper.map(budgetsDTO,Budgets.class);
        Long budgetId = budgetsRepository.save(budgets).getBudgetId();
        return budgetId;
    }
    @Override
    public BudgetsDTO readOneBudget(Long budgetId){
        Optional<Budgets> result = budgetsRepository.findById(budgetId);
        Budgets budgets = result.orElseThrow();
        BudgetsDTO budgetsDTO = modelMapper.map(budgets,BudgetsDTO.class);
        return budgetsDTO;
    }
    @Override
    public void modifyBudget(BudgetsDTO budgetsDTO){
        Optional<Budgets> result = budgetsRepository.findById(budgetsDTO.getBudgetId());
        Budgets budgets = result.orElseThrow();
        budgets.changeBudget(budgetsDTO.getBudAmount());
        budgetsRepository.save(budgets);
    }
    @Override
    public void removeBudget(Long budgetId){
        budgetsRepository.deleteById(budgetId);
    }
    @Override
    public PageResponseDTO<BudgetsDTO> budgetListByUser(Long userNo, PageRequestDTO pageRequestDTO){
        Integer selectYear = pageRequestDTO.getSelectYear();
        Integer selectMonth = pageRequestDTO.getSelectMonth();
        Long budCategories = pageRequestDTO.getCategoriess();
        Long amountMin = pageRequestDTO.getAmountMin();
        Long amountMax = pageRequestDTO.getAmountMax();
        Pageable pageable = pageRequestDTO.getPageable("budgetId");
        Page<Budgets> result = budgetsRepository.searchAllBuds(userNo, selectYear,selectMonth,budCategories,amountMin,amountMax,pageable);
        List<BudgetsDTO> dtoList = result.getContent().stream()
                .map(budgets -> modelMapper.map(budgets,BudgetsDTO.class)).collect(Collectors.toList());
        return PageResponseDTO.<BudgetsDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();
    }
    @Override
    public Long totalBudAmountsByMonth(int year,int month,Long userNo){
        return budgetsRepository.totalBudAmountByMonth(year,month,userNo);
    }
    @Override //예산 조회 시 모든 달, 카테고리별로 새로 업데이트
    public void autoUpdateUsesByCategoriesWhenAccessBudList(Long userNo){
        int year = 2025; //수정
        int month = 9; //수정
        List<Long> allCategories = budgetsRepository.allCategory(userNo,year,month);
        for(Long i:allCategories){
            Long sumByAllCategories = transactionsRepository.totalUseByCategory(i,year,month,userNo);
            log.info("-----이번 달----"+i+" 카테고리의 총합은: "+sumByAllCategories);
            budgetsRepository.usedBudgetByCategory(i,year,month,userNo)
                    .ifPresent(budget -> {
                        budget.autoUpdateCurrentMoney(sumByAllCategories);
                        budgetsRepository.save(budget);
                        log.info("예산 자동 업데이트 완료: category={}, year={}, month={}, sum={}",
                                i, year, month, sumByAllCategories);
                    });
        }
    }
    @Override //이번 달 총 설정 예산
    public Long wholeSetBudgetAmount(Long userNo){
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();
        return budgetsRepository.totalBudAmountByMonth(year,month,userNo);
    }
    @Override
    public Long budgetUses(Long userNo){
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();
        return budgetsRepository.budgetUsesByMonth(year,month,userNo);
    }
}
