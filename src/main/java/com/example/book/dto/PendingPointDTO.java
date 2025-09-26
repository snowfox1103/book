package com.example.book.dto;

import com.example.book.domain.user.ApprovalStatus;

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
) { }