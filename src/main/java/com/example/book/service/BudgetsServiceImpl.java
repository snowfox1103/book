package com.example.book.service;

import com.example.book.domain.finance.Budgets;
import com.example.book.dto.BudgetAlertDTO;
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
import java.time.YearMonth;
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
    private final CategoriesService categoriesService; //0926 조덕진 알림용

    @Override
    public Long registerBudget(BudgetsDTO budgetsDTO){ //이번 달 것만 등록 가능
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
        budgets.changeBudget(budgetsDTO.getBudAmount(),budgetsDTO.getBudNotice());
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
        Long budCategories = pageRequestDTO.getCategories();
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
    @Override //이번 달 총 설정 예산
    public Long wholeSetBudgetAmount(Long userNo){
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();
        return budgetsRepository.totalBudAmountByMonth(year,month,userNo);
    }
    @Override //이번 달 총 예산 사용 내역
    public Long budgetUses(Long userNo){
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();
        return budgetsRepository.budgetUsesByMonth(year,month,userNo);
    }

    //0926 조덕진 수정 알림용
    @Override
    public List<BudgetAlertDTO> getBudgetAlerts(Long userNo) {
        var today = LocalDate.now(java.time.ZoneId.of("Asia/Seoul"));
        var ym = YearMonth.from(today);

        // 🔒 당월(KST) 레코드만 조회 (레포지토리에 메서드 추가 권장)
        List<Budgets> budgets =
                budgetsRepository.findByUserNoAndBudNoticeTrueAndBudYearAndBudMonth(
                        userNo, ym.getYear(), ym.getMonthValue()
                );

        return budgets.stream()
                .filter(b -> b.getBudAmount() != null && b.getBudAmount() > 0)
                .map(b -> {
                    long amount = b.getBudAmount();
                    long current = b.getBudCurrent() == null ? 0 : b.getBudCurrent(); // null 방어
                    int rate = (int) Math.round((double) current / amount * 100);
                    int threshold = (b.getBudThreshold() != null) ? b.getBudThreshold() : 90;
                    return new BudgetAlertDTO(
                            b.getBudgetId(),
                            categoriesService.getCatNameByCatId(b.getBudCategory()),
                            amount,
                            current,
                            rate,
                            threshold
                    );
                })
                .filter(dto -> dto.getRate() >= dto.getThreshold())
                .toList();
    }

    @Override
    @Transactional
    public void updateCategoryThreshold(Long userNo, Long catId, int threshold, int year, int month) {
        Budgets budget = budgetsRepository
          .findByUserNoAndBudCategoryAndBudYearAndBudMonth(userNo, catId, year, month)
          .orElseThrow(() -> new IllegalArgumentException(
            "이번 달 예산을 찾을 수 없습니다. userNo=" + userNo + ", catId=" + catId));

        budget.changeThreshold(threshold);
    }
}
