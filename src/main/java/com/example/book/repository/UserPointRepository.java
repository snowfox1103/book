package com.example.book.repository;

import com.example.book.domain.point.UserPoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserPointRepository extends JpaRepository<UserPoint, Long> {

    Page<UserPoint> findByUserNo(Long userNo, Pageable pageable);

    @Query("""
        select p from UserPoint p
        where p.userNo = :userNo
        order by (case when p.pointType = com.example.book.domain.point.PointType.EARN
                       then p.pointAmount else -p.pointAmount end) asc
    """)
    Page<UserPoint> findPageOrderBySignedAmountAsc(@Param("userNo") Long userNo, Pageable pageable);

    @Query("""
        select p from UserPoint p
        where p.userNo = :userNo
        order by (case when p.pointType = com.example.book.domain.point.PointType.EARN
                       then p.pointAmount else -p.pointAmount end) desc
    """)
    Page<UserPoint> findPageOrderBySignedAmountDesc(@Param("userNo") Long userNo, Pageable pageable);

    @Query("""
      select coalesce(sum(case when p.pointType = com.example.book.domain.point.PointType.EARN
                               then p.pointAmount else -p.pointAmount end), 0)
      from UserPoint p
      where p.userNo = :userNo
        and p.pointStartDate < :cutoff
    """)
    long sumBefore(@Param("userNo") Long userNo, @Param("cutoff") LocalDateTime cutoff);

    // 중복생성 방지 키(사유)에 대한 존재 확인
    boolean existsByPointReason(String pointReason);

    // ✅ 승인대기 목록: 사유가 'PENDING|' 로 시작하는 행만
    List<UserPoint> findByPointReasonStartingWithOrderByPointStartDateDesc(String prefix);
}
