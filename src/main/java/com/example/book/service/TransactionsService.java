package com.example.book.service;

import com.example.book.dto.TransactionsDTO;

public interface TransactionsService {
    Long registerTrans(TransactionsDTO transactionsDTO);
}
