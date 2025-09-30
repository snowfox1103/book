package com.example.book.repository;

import com.example.book.domain.point.PointManageRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PointManageRuleRepository extends JpaRepository<PointManageRule, Long> {

    // 특정 PM(설정 묶음) 기준 정렬 조회 (필요 없으면 PMId는 무시하고 아래 all만 써도 됨)
    List<PointManageRule> findByPMIdOrderByPercentThresholdAsc(Long PMId);

    // 전체 규칙을 임계치 오름차순으로
    @Query("select r from PointManageRule r order by r.percentThreshold asc")
    List<PointManageRule> findAllOrderByPercentThresholdAsc();
}
