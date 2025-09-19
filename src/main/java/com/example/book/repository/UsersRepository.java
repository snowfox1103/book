package com.example.book.repository;


import com.example.book.domain.user.Users;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
  @EntityGraph(attributePaths = "role") //jpa에서 최적화하기 위해 사용함
  @Query("select m from Users m where m.userId = :userId and m.social = false")
  Optional<Users> getWithRoles(String userId);

  @EntityGraph(attributePaths = "role")
  Optional<Users> findByEmail(String email);

  @Modifying
  @Transactional
  @Query("update Users m set m.password = :password where m.userId = :userId")
  void updatePassword(@Param("password") String password, @Param("userId") String userId);

  @Modifying
  @Transactional
  @Query("update Users m set m.email = :email where m.userId = :userId")
  void updateEmail(@Param("email") String email, @Param("userId") String userId);

  @Modifying
  @Transactional
  @Query("delete from Users m where m.userId = :userId")
  void deleteByUserId(@Param("userId") String userId);

  @EntityGraph(attributePaths = "role")
  Optional<Users> findByUserId(String userId);

  boolean existsByUserId(String userId);
  boolean existsByEmail(String email);

  @Query("select m from Users m where m.userNo = :userNo")
  Optional<Users> findByUserNo(Long userNo);
}

