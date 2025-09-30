package com.example.book.repository;

import com.example.book.domain.point.PointType;
import com.example.book.domain.point.UserPoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserPointRepository extends JpaRepository<UserPoint, Long> {

    Page<UserPoint> findByUserNo(Long userNo, Pageable pageable);

    // ✅ 기본 목록: 승인건(= PENDING|*, REJECTED|* 제외)만
    @Query("""
           select p from UserPoint p
           where p.userNo = :userNo
             and p.pointReason not like 'PENDING|%%'
             and p.pointReason not like 'REJECTED|%%'
           """)
    Page<UserPoint> findApprovedByUserNo(@Param("userNo") Long userNo, Pageable pageable);

    boolean existsByPointReasonStartingWith(String pointReason);

    //point.scan 중복방지 0928 석준영
    @Modifying
    @Query("delete from UserPoint u where u.pointReason like concat(:prefix, '%')")
    void deleteByPointReasonPrefix(@Param("prefix") String prefix);

    // ✅ delta 정렬(오름차순): 승인건만
    @Query("""
           select p from UserPoint p
           where p.userNo = :userNo
             and p.pointReason not like 'PENDING|%%'
             and p.pointReason not like 'REJECTED|%%'
           order by (case when p.pointType = com.example.book.domain.point.PointType.EARN
                          then p.pointAmount else -p.pointAmount end) asc
           """)
    Page<UserPoint> findPageOrderBySignedAmountAsc(@Param("userNo") Long userNo, Pageable pageable);

    // ✅ delta 정렬(내림차순): 승인건만
    @Query("""
           select p from UserPoint p
           where p.userNo = :userNo
             and p.pointReason not like 'PENDING|%%'
             and p.pointReason not like 'REJECTED|%%'
           order by (case when p.pointType = com.example.book.domain.point.PointType.EARN
                          then p.pointAmount else -p.pointAmount end) desc
           """)
    Page<UserPoint> findPageOrderBySignedAmountDesc(@Param("userNo") Long userNo, Pageable pageable);

    // ✅ 페이지 이전 누계(승인건만, < cutoff)
    @Query("""
           select coalesce(sum(case when p.pointType = com.example.book.domain.point.PointType.EARN
                                    then p.pointAmount else -p.pointAmount end), 0)
           from UserPoint p
           where p.userNo = :userNo
             and p.pointStartDate < :cutoff
             and p.pointReason not like 'PENDING|%%'
             and p.pointReason not like 'REJECTED|%%'
           """)
    long sumBefore(@Param("userNo") Long userNo, @Param("cutoff") LocalDateTime cutoff);

    // 중복생성 방지 키(사유)에 대한 존재 확인
    boolean existsByPointReason(String pointReason);

    // ✅ 승인대기 목록 등 별도 조회가 필요할 때만 사용
    List<UserPoint> findByPointReasonStartingWithOrderByPointStartDateDesc(String prefix);

    // ✅ 전체 합계(타입별) — 승인건만
    @Query("""
           select coalesce(sum(case when u.pointType = :type then abs(u.pointAmount) else 0 end), 0)
           from UserPoint u
           where u.userNo = :userNo
             and u.pointReason not like 'PENDING|%%'
             and u.pointReason not like 'REJECTED|%%'
           """)
    long sumAmountByTypeAll(@Param("userNo") Long userNo, @Param("type") PointType type);

    // ✅ 기간 합계(타입별) — 승인건만
    @Query("""
           select coalesce(sum(case when u.pointType = :type then abs(u.pointAmount) else 0 end), 0)
           from UserPoint u
           where u.userNo = :userNo
             and u.pointStartDate between :start and :end
             and u.pointReason not like 'PENDING|%%'
             and u.pointReason not like 'REJECTED|%%'
           """)
    long sumAmountByTypeBetween(@Param("userNo") Long userNo,
                                @Param("type") PointType type,
                                @Param("start") LocalDateTime start,
                                @Param("end") LocalDateTime end);

    // ✅ 월말 누계(적립-사용) — 승인건만
    @Query("""
           select coalesce(sum(
             case
               when u.pointType = :earn then abs(u.pointAmount)
               when u.pointType = :use  then -abs(u.pointAmount)
               else 0 end
           ), 0)
           from UserPoint u
           where u.userNo = :userNo
             and u.pointStartDate <= :end
             and u.pointReason not like 'PENDING|%%'
             and u.pointReason not like 'REJECTED|%%'
           """)
    long sumSignedUntil(@Param("userNo") Long userNo,
                        @Param("end") LocalDateTime end,
                        @Param("earn") PointType earn,
                        @Param("use") PointType use);

    @Query("""
           select coalesce(sum(u.pointAmount), 0)
           from UserPoint u
           where u.userNo = :userNo
             and u.pointStartDate >= :from
             and u.pointStartDate <  :to
             and u.pointType = com.example.book.domain.point.PointType.EARN
           """)
    Long sumEarnByUserAndPeriod(@Param("userNo") Long userNo,
                                @Param("from") LocalDateTime from,
                                @Param("to") LocalDateTime to);

    @Query("""
           select coalesce(sum(u.pointAmount), 0)
           from UserPoint u
           where u.userNo = :userNo
             and u.pointStartDate >= :from
             and u.pointStartDate <  :to
             and u.pointType = com.example.book.domain.point.PointType.USE
           """)
    Long sumUseByUserAndPeriod(@Param("userNo") Long userNo,
                               @Param("from") LocalDateTime from,
                               @Param("to") LocalDateTime to);

    @Query("""
           select coalesce(sum(u.pointAmount), 0)
           from UserPoint u
           where u.userNo = :userNo
             and u.pointStartDate < :to
             and u.pointType = com.example.book.domain.point.PointType.EARN
           """)
    Long sumEarnByUserUntil(@Param("userNo") Long userNo,
                            @Param("to") LocalDateTime to);

    @Query("""
           select coalesce(sum(u.pointAmount), 0)
           from UserPoint u
           where u.userNo = :userNo
             and u.pointStartDate < :to
             and u.pointType = com.example.book.domain.point.PointType.USE
           """)
    Long sumUseByUserUntil(@Param("userNo") Long userNo,
                           @Param("to") LocalDateTime to);

    // === 전체 누계 (전체기간) ===
    @Query("""
           select coalesce(sum(u.pointAmount), 0)
           from UserPoint u
           where u.userNo = :userNo
             and u.pointType = com.example.book.domain.point.PointType.EARN
           """)
    Long sumEarnTotalByUser(@Param("userNo") Long userNo);

    @Query("""
           select coalesce(sum(u.pointAmount), 0)
           from UserPoint u
           where u.userNo = :userNo
             and u.pointType = com.example.book.domain.point.PointType.USE
           """)
    Long sumUseTotalByUser(@Param("userNo") Long userNo);
}
