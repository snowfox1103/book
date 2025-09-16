package com.example.book.repository;

import com.example.book.domain.Board;
import com.example.book.domain.Transactions;
import com.example.book.repository.search.TransactionsSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransactionsRepository extends JpaRepository<Transactions, Long>, TransactionsSearch {
}
