package com.example.book.domain.point;

import com.example.book.domain.user.ApprovalStatus;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "pendingpoint",
        uniqueConstraints = @UniqueConstraint(name = "uq_pending_user_month",
                columnNames = {"userNo","yearMonth"}))
public class PendingPoint {

    // ====== getters / setters ======
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userNo;
    private String username;

    /** "YYYY-MM" */
    @Column(length = 7, nullable = false)
    private String yearMonth;

    private int ratePercent;
    private int points;

    @Column(length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ApprovalStatus status; // PENDING/APPROVED/REJECTED

    @PrePersist
    void prePersist() {
        if (status == null) status = ApprovalStatus.PENDING;
    }

    private LocalDateTime createdAt;
    private LocalDateTime decidedAt;
    private String decidedBy;

    public void setUserNo(Long userNo) { this.userNo = userNo; }

    public void setUsername(String username) { this.username = username; }

    public void setYearMonth(String yearMonth) { this.yearMonth = yearMonth; }

    public void setRatePercent(int ratePercent) { this.ratePercent = ratePercent; }

    public void setPoints(int points) { this.points = points; }

    public void setReason(String reason) { this.reason = reason; }

    public void setStatus(ApprovalStatus status) { this.status = status; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public void setDecidedAt(LocalDateTime decidedAt) { this.decidedAt = decidedAt; }

    public void setDecidedBy(String decidedBy) { this.decidedBy = decidedBy; }
}
