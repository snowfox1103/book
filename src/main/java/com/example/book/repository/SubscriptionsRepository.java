package com.example.book.repository;

import com.example.book.domain.finance.Subscriptions;
import com.example.book.domain.user.Users;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface SubscriptionsRepository extends JpaRepository<Subscriptions, Long> {
  List<Subscriptions> findByUsers_UserNo(Long userNo);
  Optional<Subscriptions> findBySubId(Long subId);

  @Modifying
  @Transactional
  void deleteByUsers_UserNoAndSubId(Long userNo, Long subId);

  @Query("SELECT s.categories.catName, SUM(s.subAmount) " +
    "FROM Subscriptions s " +
    "WHERE s.users.userNo = :userNo " +
    "GROUP BY s.categories.catName")
  List<Object[]> getCategoryAmountSummary(@Param("userNo") Long userNo);
}
