// PendingPointDTO.java
package com.example.book.dto;

import com.example.book.domain.user.ApprovalStatus;

import java.time.LocalDate;

public record PendingPointDTO(
        Long id,
        Long userNo,
        String userName,
        int percentUsed,
        int points,
        int year,
        int month,
        String reason,
        ApprovalStatus status
) {
    public static PendingPointDTO ofCanonical(
            Long id, Long userNo, String userName, int percentUsed, int points,
            LocalDate eventDate, String reason, ApprovalStatus status
    ) {
        return new PendingPointDTO(
                id, userNo, userName, percentUsed, points,
                eventDate != null ? eventDate.getYear() : 0,
                eventDate != null ? eventDate.getMonthValue() : 0,
                reason, status
        );
    }
}
