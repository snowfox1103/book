package com.example.book.service;

import com.example.book.domain.point.PendingPoint;
import com.example.book.domain.user.ApprovalStatus;

import java.time.YearMonth;
import java.util.List;

public interface PendingPointService {

    List<PendingPoint> findAllOrderByCreatedAtDesc();

    List<PendingPoint> findAllByStatusOrderByCreatedAtDesc(ApprovalStatus status);

    PendingPoint createPending(Long userNo,
                               String username,
                               int points,
                               int ratePercent,
                               String reason,
                               ApprovalStatus status,
                               YearMonth yearMonth);

    void approve(Long id, String reason, String decidedBy);

    void reject(Long id, String reason, String decidedBy);

    int deleteAllByYearMonth(YearMonth ym);
}
