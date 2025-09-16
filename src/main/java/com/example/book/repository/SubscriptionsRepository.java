package com.example.book.repository;

import com.example.book.domain.Subscriptions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionsRepository extends JpaRepository<Subscriptions, Long> {
//  @EntityGraph(attributePaths = "subPeriodUnit") //jpa에서 최적화하기 위해 사용함
//  @Query("select m from Subscriptions m where m.userId = :userId and m.social = false")
//  Optional<Users> getWithRoles(String userId);
}
