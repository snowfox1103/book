package com.example.book.domain.point;

import com.example.book.domain.user.ApprovalStatus;

/**
 * 호환용 래퍼.
 * 실제 저장/비즈니스는 ApprovalStatus를 쓰지만,
 * 과거 코드가 import com.example.book.domain.point.PendingPointStatus;
 * 를 참조할 때를 대비해 만든 얇은 Alias enum.
 */
public enum PendingPointStatus {
    PENDING, APPROVED, REJECTED;

    /** ApprovalStatus로 변환 */
    public ApprovalStatus toApproval() {
        return switch (this) {
            case PENDING   -> ApprovalStatus.PENDING;
            case APPROVED  -> ApprovalStatus.APPROVED;
            case REJECTED  -> ApprovalStatus.REJECTED;
        };
    }

    /** ApprovalStatus에서 역변환 */
    public static PendingPointStatus from(ApprovalStatus s) {
        if (s == null) return null;
        return switch (s) {
            case PENDING   -> PENDING;
            case APPROVED  -> APPROVED;
            case REJECTED  -> REJECTED;
        };
    }
}
