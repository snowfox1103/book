package com.example.book.repository;

import com.example.book.domain.point.UserPoint;
import com.example.book.domain.point.PointType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UserPointRepository extends JpaRepository<UserPoint, Long> {

    /* ===== 목록 조회(월 범위) – 정렬은 서비스에서 처리하기 위해 List로 일단 모두 가져온다 ===== */
    List<UserPoint> findByUserNoAndPointStartDateBetweenOrderByPointStartDateAscPointIdAsc(
            Long userNo,
            LocalDateTime from,
            LocalDateTime to
    );

    /* ===== 시드(해당 시점 이전까지의 누계) ===== */
    @Query("""
            select coalesce(
                sum(case when p.pointType = com.example.book.domain.point.PointType.EARN
                         then p.pointAmount else -p.pointAmount end), 0)
            from UserPoint p
            where p.userNo = :userNo and p.pointStartDate < :ts
           """)
    Long sumDeltaBefore(@Param("userNo") Long userNo, @Param("ts") LocalDateTime ts);

    /* ===== 전체 누적 ===== */
    @Query("""
           select coalesce(sum(p.pointAmount), 0)
           from UserPoint p
           where p.userNo = :userNo and p.pointType = 'EARN'
           """)
    Long sumEarnTotalByUser(@Param("userNo") Long userNo);

    @Query("""
           select coalesce(sum(p.pointAmount), 0)
           from UserPoint p
           where p.userNo = :userNo and p.pointType = 'USE'
           """)
    Long sumUseTotalByUser(@Param("userNo") Long userNo);

    /* ===== 기간 누적 ===== */
    @Query("""
           select coalesce(sum(p.pointAmount), 0)
           from UserPoint p
           where p.userNo = :userNo and p.pointType = 'EARN'
             and p.pointStartDate >= :from and p.pointStartDate < :to
           """)
    Long sumEarnByUserAndPeriod(@Param("userNo") Long userNo,
                                @Param("from") LocalDateTime from,
                                @Param("to") LocalDateTime to);

    @Query("""
           select coalesce(sum(p.pointAmount), 0)
           from UserPoint p
           where p.userNo = :userNo and p.pointType = 'USE'
             and p.pointStartDate >= :from and p.pointStartDate < :to
           """)
    Long sumUseByUserAndPeriod(@Param("userNo") Long userNo,
                               @Param("from") LocalDateTime from,
                               @Param("to") LocalDateTime to);

    /* ===== 특정 시점까지(월말 잔액 계산에 사용) ===== */
    @Query("""
           select coalesce(sum(p.pointAmount), 0)
           from UserPoint p
           where p.userNo = :userNo and p.pointType = 'EARN'
             and p.pointStartDate < :to
           """)
    Long sumEarnByUserUntil(@Param("userNo") Long userNo, @Param("to") LocalDateTime to);

    @Query("""
           select coalesce(sum(p.pointAmount), 0)
           from UserPoint p
           where p.userNo = :userNo and p.pointType = 'USE'
             and p.pointStartDate < :to
           """)
    Long sumUseByUserUntil(@Param("userNo") Long userNo, @Param("to") LocalDateTime to);

    /** 승인 대기/승인/반려 구분을 reason prefix 로 관리할 때 중복 체크용 */
    boolean existsByPointReason(String pointReason);

    /** 대기 목록 최신순 */
    List<UserPoint> findByPointReasonStartingWithOrderByPointStartDateDesc(String prefix);

    /** 사용자의 전체 내역 */
    Page<UserPoint> findByUserNo(Long userNo, Pageable pageable);

    // 기존 Between(양끝 포함) 대신, [from, to) 형태로 조회
    Page<UserPoint> findByUserNoAndPointStartDateGreaterThanEqualAndPointStartDateLessThan(
            Long userNo, LocalDateTime from, LocalDateTime to, Pageable pageable);

    // 달 전체 누계 계산용: [from, to) 범위, 날짜↑/ID↑ 정렬로 모두 가져오기
    List<UserPoint> findByUserNoAndPointStartDateGreaterThanEqualAndPointStartDateLessThanOrderByPointStartDateAscPointIdAsc(
            Long userNo, LocalDateTime from, LocalDateTime to);

    boolean existsByUserNoAndPointTypeAndPointAmountAndPointStartDateAndPointReason(
            Long userNo,
            PointType pointType,
            Long pointAmount,
            LocalDateTime pointStartDate,
            String pointReason
    );

    List<UserPoint> findAllByUserNoOrderByPointStartDateAscPointIdAsc(Long userNo);
}
