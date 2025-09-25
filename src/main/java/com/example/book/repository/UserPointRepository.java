package com.example.book.repository;

import com.example.book.domain.point.UserPoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface UserPointRepository extends JpaRepository<UserPoint, Long> {

    /** 일반 목록 조회(정렬은 Pageable에서 전달) */
    Page<UserPoint> findByUserNo(Long userNo, Pageable pageable);

    /** 변동액(적립=+, 사용=-) 기준 ASC 정렬 */
    @Query("""
        select p from UserPoint p
        where p.userNo = :userNo
        order by (case when p.pointType = com.example.book.domain.point.PointType.EARN
                       then p.pointAmount else -p.pointAmount end) asc
    """)
    Page<UserPoint> findPageOrderBySignedAmountAsc(@Param("userNo") Long userNo, Pageable pageable);

    /** 변동액(적립=+, 사용=-) 기준 DESC 정렬 */
    @Query("""
        select p from UserPoint p
        where p.userNo = :userNo
        order by (case when p.pointType = com.example.book.domain.point.PointType.EARN
                       then p.pointAmount else -p.pointAmount end) desc
    """)
    Page<UserPoint> findPageOrderBySignedAmountDesc(@Param("userNo") Long userNo, Pageable pageable);

    /** 페이지 시작 이전까지의 누적 총액(정확 잔액 초기값 계산용) */
    @Query("""
      select coalesce(sum(case when p.pointType = com.example.book.domain.point.PointType.EARN
                               then p.pointAmount else -p.pointAmount end), 0)
      from UserPoint p
      where p.userNo = :userNo
        and p.pointStartDate < :cutoff
    """)
    long sumBefore(@Param("userNo") Long userNo, @Param("cutoff") LocalDateTime cutoff);
}
