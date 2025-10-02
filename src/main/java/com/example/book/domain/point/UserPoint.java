package com.example.book.domain.point;

import com.example.book.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "userpoint")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserPoint extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pointId")
    private Long pointId;

    @Column(name = "userNo")
    private Long userNo;

    @Column(name = "budId")
    private Long budId;

    @Column(name = "pointStartDate")
    private LocalDateTime pointStartDate;

    @Column(name = "pointAmount")
    private Long pointAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "pointType")
    private PointType pointType;

    @Column(name = "pointReason")
    private String pointReason;

    // 거래 직후 잔액(선택)
    private Long runningBalance;

    // 화면 누계 계산 시 PointServiceImpl 에서 사용
    public void setRunningBalance(Long runningBalance) {
        this.runningBalance = runningBalance;
    }

    // 승인/반려 표시 바꿀 때 사용
    public void setPointReason(String pointReason) {
        this.pointReason = pointReason;
    }

    // (선택) 승인/반려 헬퍼
    public void markApprovedFromPending() {
        if (this.pointReason != null && this.pointReason.startsWith("PENDING|")) {
            this.pointReason = "APPROVED|" + this.pointReason.substring("PENDING|".length());
        }
    }

    public void markRejectedFromPending() {
        if (this.pointReason != null && this.pointReason.startsWith("PENDING|")) {
            this.pointReason = "REJECTED|" + this.pointReason.substring("PENDING|".length());
        }
    }

}
