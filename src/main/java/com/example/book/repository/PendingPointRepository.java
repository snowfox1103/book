package com.example.book.repository;

import com.example.book.domain.point.PendingPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PendingPointRepository extends JpaRepository<PendingPoint, Long> {

    @Query("select p from PendingPoint p order by p.createdAt desc")
    List<PendingPoint> findAllOrderByCreatedAtDesc();

    Optional<PendingPoint> findByUserNoAndYearMonth(Long userNo, String yearMonth);

    int deleteByYearMonth(String yearMonth);

    List<PendingPoint> findAllByIdIn(Collection<Long> ids);
}
