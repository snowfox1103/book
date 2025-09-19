package com.example.book.dto;

public record RuleDTO(
        Integer threshold,  // 퍼센트(<= threshold)
        Integer reward,     // 보상값
        String rewardType   // "FIXED" | "PERCENT"
) {}
