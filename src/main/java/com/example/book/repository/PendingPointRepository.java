package com.example.book.repository;

import com.example.book.domain.point.PendingPoint;
import com.example.book.domain.user.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PendingPointRepository extends JpaRepository<PendingPoint, Long> {

    /* --- 조회 --- */

    // 관리자 리스트(상단 테이블)용: 생성일 내림차순 전체
    List<PendingPoint> findAllByOrderByCreatedAtDesc();

    // 특정 연월 + 상태 페이지 조회 (관리 화면 페이징)
    Page<PendingPoint> findByYearMonthAndStatus(String yearMonth,
                                                ApprovalStatus status,
                                                Pageable pageable);

    // 특정 사용자 + 연월 1건 (스캔/upsert 시 중복 방지)
    Optional<PendingPoint> findByUserNoAndYearMonth(Long userNo, String yearMonth);

    // id IN 조회 (일괄 승인/반려)
    List<PendingPoint> findAllByIdIn(List<Long> ids);

    /* --- 변경 --- */

    // 상태 단건 변경
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update PendingPoint p set p.status = :status where p.id = :id")
    int updateStatus(@Param("id") Long id, @Param("status") ApprovalStatus status);

    // 상태 일괄 변경
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update PendingPoint p set p.status = :status where p.id in :ids")
    int updateStatusIn(@Param("ids") List<Long> ids, @Param("status") ApprovalStatus status);

    // 특정 연월 전체 삭제
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from PendingPoint p where p.yearMonth = :ym")
    int deleteByYearMonth(@Param("ym") String yearMonth);

    // 조회 후 deleteAll() 사용할 때 필요
    List<PendingPoint> findAllByYearMonth(String yearMonth);
}
