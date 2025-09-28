package com.example.book.service;

import com.example.book.dto.PageRequestDTO;
import com.example.book.dto.PageResponseDTO;
import com.example.book.dto.TransactionsDTO;

public interface TransactionsService {
    Long registerTrans(TransactionsDTO transactionsDTO);
    TransactionsDTO readOneTrans(Long transId);
    void modifyTrans(TransactionsDTO transactionsDTO);
    void removeTrans(Long transId);
    PageResponseDTO<TransactionsDTO> listByUser(Long userNo,PageRequestDTO pageRequestDTO);
    void autoUpdateBudgetCurrent(Long userNo, Long category, int year, int month);
    Long wholeUses(Long userNo); //이번 달 총 사용 금액
    Long wholeIncome(Long userNo);
}
