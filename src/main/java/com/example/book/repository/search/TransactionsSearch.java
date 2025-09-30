package com.example.book.repository.search;

import com.example.book.domain.finance.InOrOut;
import com.example.book.domain.finance.Transactions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface TransactionsSearch {
    Page<Transactions> searchTrans(Pageable pageable);
    Page<Transactions> searchAllTransaction(Long userNo,String[] types, String keyword, Long category, Long minn, Long maxx, LocalDate startDay, LocalDate endDay, InOrOut io, Pageable pageable);
}
