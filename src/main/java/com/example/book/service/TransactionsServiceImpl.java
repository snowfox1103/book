package com.example.book.service;

import com.example.book.domain.finance.InOrOut;
import com.example.book.domain.finance.Transactions;
import com.example.book.dto.PageRequestDTO;
import com.example.book.dto.PageResponseDTO;
import com.example.book.dto.TransactionsDTO;
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
public class TransactionsServiceImpl implements TransactionsService{
    private final ModelMapper modelMapper;
    private final TransactionsRepository transactionsRepository;
    private final BudgetsRepository budgetsRepository;
    @Override
    public Long registerTrans(TransactionsDTO transactionsDTO){
        Transactions transactions = modelMapper.map(transactionsDTO,Transactions.class);
        Long transId = transactionsRepository.save(transactions).getTransId();
        return transId;
    }
    @Override
    public TransactionsDTO readOneTrans(Long transId){
        Optional<Transactions> result = transactionsRepository.findById(transId);
        Transactions transactions = result.orElseThrow();
        TransactionsDTO transactionsDTO = modelMapper.map(transactions,TransactionsDTO.class);
        return transactionsDTO;
    }
    @Override
    public void modifyTrans(TransactionsDTO transactionsDTO){
        Optional<Transactions> result = transactionsRepository.findById(transactionsDTO.getTransId());
        Transactions transactions = result.orElseThrow();
        transactions.changeTransaction(transactionsDTO.getTransTitle(),transactionsDTO.getTransAmount(),
                transactionsDTO.getTransCategory(),transactionsDTO.getTransMemo(),transactionsDTO.getTransDate(),
                transactionsDTO.getTransInOut(),transactionsDTO.getSubId());
        transactionsRepository.save(transactions);
    }
    @Override
    public void removeTrans(Long transId){
        transactionsRepository.deleteById(transId);
    }
    @Override
    public PageResponseDTO<TransactionsDTO> listByUser(Long userNo, PageRequestDTO pageRequestDTO){
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Long category = pageRequestDTO.getCategoriess();
        Long minn = pageRequestDTO.getAmountMin();
        Long maxx = pageRequestDTO.getAmountMax();
        LocalDate startDay = pageRequestDTO.getStartDay();
        LocalDate endDay = pageRequestDTO.getEndDay();
        InOrOut io = pageRequestDTO.getIo();
        Pageable pageable = pageRequestDTO.getPageable("transId");
        Page<Transactions> result = transactionsRepository.searchAllTrans(userNo, types,keyword,category,minn,maxx,startDay,endDay,io,pageable);
        List<TransactionsDTO> dtoList = result.getContent().stream()
                .map(transactions -> modelMapper.map(transactions,TransactionsDTO.class)).collect(Collectors.toList());
        return PageResponseDTO.<TransactionsDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();
    }

    @Override //입출금 등록,수정,삭제 시 전체 출금 내역 자동 집계
    public void autoUpdateBudgetCurrent(Long userNo, Long catId, int year, int month){
        log.info("------------!!!!!!!!!!!!-----userNo: -------"+userNo);
        Long totalUseByCategories = transactionsRepository.totalUseByCategory(catId, year, month, userNo);
        log.info("-------------!!!!!!!!--------sum: ---------"+totalUseByCategories);
        budgetsRepository.usedBudgetByCategory(catId, year, month, userNo)
                .ifPresent(budget -> {
                    budget.autoUpdateCurrentMoney(totalUseByCategories);
                    budgetsRepository.save(budget);
                    log.info("예산 자동 업데이트 완료: category={}, year={}, month={}, sum={}",
                            catId, year, month, totalUseByCategories);
                    });
    }
    @Override //이번 달 총 사용 금액 집계
    public Long wholeUses(Long userNo){
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();
        return transactionsRepository.totalUseByMonth(year,month,userNo);
    }
    //해당 달 총 사용 금액 집계

    //
}
