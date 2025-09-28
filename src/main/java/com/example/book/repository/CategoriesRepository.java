package com.example.book.repository;

import com.example.book.domain.finance.Categories;
import com.example.book.domain.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CategoriesRepository extends JpaRepository<Categories,Long> {
  Optional<Categories> findByCatId(Long catId);
  List<Categories> findAllByUsers_UserNo(Long userNo);
  boolean existsByUsers_UserNoAndCatName(Long userNo, String catName);
  List<Categories> findByUsers_UserNo(Long userNo);
  List<Categories> findByIsSystemDefaultTrue();

  @Modifying
  @Transactional
  void deleteByCatId(Long catId);

  @Modifying
  @Transactional
  void deleteByCatIdAndUsers_UserNo(Long catId, Long userNo);

  @Modifying
  @Transactional
  @Query("update Categories c set c.catName = :catName where c.catId = :catId")
  void updateCatNameByCatId(Long catId, String catName);

  @Modifying
  @Transactional
  void deleteByUsers_UserNo(Long userNo);
}
