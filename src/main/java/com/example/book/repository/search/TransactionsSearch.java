package com.example.book.repository.search;

import com.example.book.domain.InOrOut;
import com.example.book.domain.Transactions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface TransactionsSearch {
    Page<Transactions> searchTrans(Pageable pageable);
    Page<Transactions> searchAllTrans(String[] types, String keyword, Long minn, Long maxx, LocalDate a, LocalDate b, InOrOut io, Pageable pageable);
}
