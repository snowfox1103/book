package com.example.book.service;

import com.example.book.dto.PageRequestDTO;
import com.example.book.dto.PageResponseDTO;
import com.example.book.dto.TransactionsDTO;

public interface TransactionsService {
    Long registerTrans(TransactionsDTO transactionsDTO);
    TransactionsDTO readOneTrans(Long transId);
    void modifyTrans(TransactionsDTO transactionsDTO);
    void removeTrans(Long transId);
    PageResponseDTO<TransactionsDTO> list(PageRequestDTO pageRequestDTO);
}
