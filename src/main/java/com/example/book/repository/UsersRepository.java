package com.example.book.repository;

import com.example.book.domain.notice.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import com.example.book.domain.user.Users;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
  boolean existsByUserId(String userId);
  boolean existsByEmail(String email);

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

  @Query("select m from Users m where m.userNo = :userNo")
  Optional<Users> findByUserNo(Long userNo);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select u from Users u where u.userNo = :userNo")
  Users findByUserNoForUpdate(@Param("userNo") Long userNo);

  @Query("select u.balance from Users u where u.userNo = :userNo")
  int getBalance(@Param("userNo") Long userNo);

}

