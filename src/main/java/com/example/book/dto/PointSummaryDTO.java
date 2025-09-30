package com.example.book.dto;

public record PointSummaryDTO(
        long currentAll,          // 전체 누적 현재 포인트
        long earnedPeriod,        // (선택) 기간 적립 합
        long usedPeriod,          // (선택) 기간 사용 합
        long balanceAtPeriodEnd,  // (선택) 월말 잔액(누계)
        String periodLabel        // "전체기간" 또는 "YYYY-MM"
) {
    public static PointSummaryDTO zero() {
        return new PointSummaryDTO(0L, 0L, 0L, 0L, "전체기간");
    }
}