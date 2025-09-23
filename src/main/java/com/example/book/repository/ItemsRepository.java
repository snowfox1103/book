package com.example.book.repository;

import com.example.book.domain.pointshop.Items;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemsRepository extends JpaRepository<Items, Long> {
  Optional<Items> findByItemId(Long itemId);
}
