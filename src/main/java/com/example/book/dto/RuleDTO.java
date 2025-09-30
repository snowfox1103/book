package com.example.book.dto;

import com.example.book.domain.point.RewardType;

public record RuleDTO(
        Integer threshold,   // 퍼센트(<= threshold)
        Integer reward,      // 보상값
        RewardType rewardType // "FIXED" | "PERCENT" (enum이면 더 좋음)
) {
    public int rewardPoints() {
        return reward == null ? 0 : reward.intValue(); // <-- value가 아니라 reward!
    }
}