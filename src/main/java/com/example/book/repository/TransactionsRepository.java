package com.example.book.repository;

import com.example.book.domain.finance.Transactions;
import com.example.book.repository.search.TransactionsSearch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionsRepository extends JpaRepository<Transactions, Long>, TransactionsSearch {
}
