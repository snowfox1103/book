package com.example.book.repository;

import com.example.book.domain.Budgets;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetsRepository extends JpaRepository<Budgets,Long> {
}
