package com.example.book.service;

import com.example.book.domain.point.PendingPoint;
import com.example.book.domain.point.PendingPointStatus;
import com.example.book.domain.user.ApprovalStatus;
import com.example.book.service.PendingPointService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PendingPointServiceImpl implements PendingPointService {

    @PersistenceContext
    private final EntityManager em;

    // ========= 조회 =========

    @Override
    @Transactional(readOnly = true)
    public List<PendingPoint> findAllOrderByCreatedAtDesc() {
        return em.createQuery(
                "select p from PendingPoint p order by p.createdAt desc",
                PendingPoint.class
        ).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PendingPoint> findAllByStatusOrderByCreatedAtDesc(ApprovalStatus status) {
        PendingPointStatus s = toEntityStatus(status);
        return em.createQuery(
                "select p from PendingPoint p where p.status = :st order by p.createdAt desc",
                PendingPoint.class
        ).setParameter("st", s).getResultList();
    }

    // ========= 생성 =========

    @Override
    public PendingPoint createPending(
            Long userNo,
            String username,
            int points,
            int ratePercent,
            String reason,
            ApprovalStatus status,
            YearMonth ym
    ) {
        // YYYY-MM 문자열
        final String ymStr = (ym != null) ? ym.toString() : null;

        // ApprovalStatus -> 테이블 enum(PENDING/APPROVED/REJECTED) 문자열 매핑
        final String statusDb =
                (status == null) ? "PENDING" :
                        switch (status) {
                            case PENDING  -> "PENDING";
                            case APPROVED -> "APPROVED";
                            case REJECTED -> "REJECTED";
                        };

        // 1) 네이티브 INSERT (세터/빌더 없이 값 주입)
        //    - createdAt 은 DB default(now) 쓰거나, 명시하려면 VALUES 에 now() 추가
        //    - 유니크 충돌 시 그대로 두고(예외 유도) 중복 방지
        em.createNativeQuery("""
        INSERT INTO pendingPoint
          (userNo, username, points, ratePercent, reason, status, yearMonth, createdAt)
        VALUES
          (?,      ?,        ?,      ?,          ?,      ?,      ?,        now())
        """)
                .setParameter(1, userNo)
                .setParameter(2, username)
                .setParameter(3, points)        // DB가 BIGINT면 드라이버가 int->long 자동승격
                .setParameter(4, ratePercent)
                .setParameter(5, reason)
                .setParameter(6, statusDb)
                .setParameter(7, ymStr)
                .executeUpdate();

        // 2) 방금 넣은 행을 (userNo, yearMonth)로 다시 로드해서 엔티티 반환
        return em.createQuery("""
            select p
            from PendingPoint p
            where p.userNo = :u and p.yearMonth = :ym
            """, PendingPoint.class)
                .setParameter("u", userNo)
                .setParameter("ym", ymStr)
                .getSingleResult();
    }

    // ========= 상태 변경 (setter 없이 JPQL 업데이트) =========

    @Override
    public void approve(Long id, String reason, String decidedBy) {
        PendingPoint p = em.find(PendingPoint.class, id);
        if (p == null) throw new IllegalArgumentException("pending not found: " + id);

        em.createQuery(
                        "update PendingPoint p set " +
                                "p.status = :st, " +
                                "p.reason = coalesce(:rsn, p.reason), " +
                                "p.decidedAt = :now, " +
                                "p.decidedBy = :by " +
                                "where p.id = :id"
                )
                .setParameter("st", PendingPointStatus.APPROVED)
                .setParameter("rsn", (reason != null && !reason.isBlank()) ? reason : null)
                .setParameter("now", LocalDateTime.now())
                .setParameter("by", decidedBy)
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public void reject(Long id, String reason, String decidedBy) {
        PendingPoint p = em.find(PendingPoint.class, id);
        if (p == null) throw new IllegalArgumentException("pending not found: " + id);

        em.createQuery(
                        "update PendingPoint p set " +
                                "p.status = :st, " +
                                "p.reason = coalesce(:rsn, p.reason), " +
                                "p.decidedAt = :now, " +
                                "p.decidedBy = :by " +
                                "where p.id = :id"
                )
                .setParameter("st", PendingPointStatus.REJECTED)
                .setParameter("rsn", (reason != null && !reason.isBlank()) ? reason : null)
                .setParameter("now", LocalDateTime.now())
                .setParameter("by", decidedBy)
                .setParameter("id", id)
                .executeUpdate();
    }

    // ========= 월별 정리 =========

    @Override
    public int deleteAllByYearMonth(YearMonth ym) {
        String key = ym.toString(); // "YYYY-MM"
        return em.createQuery("delete from PendingPoint p where p.yearMonth = :ym")
                .setParameter("ym", key)
                .executeUpdate();
    }

    // ========= 내부 변환기 =========

    private static PendingPointStatus toEntityStatus(ApprovalStatus s) {
        if (s == null) return PendingPointStatus.PENDING;
        return switch (s) {
            case PENDING  -> PendingPointStatus.PENDING;
            case APPROVED -> PendingPointStatus.APPROVED;
            case REJECTED -> PendingPointStatus.REJECTED;
        };
    }
}
