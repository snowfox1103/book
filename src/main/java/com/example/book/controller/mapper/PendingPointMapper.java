package com.example.book.controller.mapper;

import com.example.book.domain.point.PendingPoint;
import com.example.book.dto.PendingPointDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;

@Component
public class PendingPointMapper {

    /** 엔티티 → DTO (yearMonth = "YYYY-MM" 그대로 사용) */
    public PendingPointDTO toDTO(PendingPoint p) {
        if (p == null) return null;

        // year/month 파생이 필요하면 아래 보조 유틸로 만들 수 있지만
        // DTO에는 yearMonth 문자열을 그대로 둡니다.
        return PendingPointDTO.ofCanonical(
                p.getId(),
                p.getUserNo(),
                p.getUsername(),
                p.getRatePercent(),
                p.getPoints(),
                p.getYearMonth(),    // "YYYY-MM"
                p.getReason(),
                p.getStatus()        // 이미 ApprovalStatus
        );
    }

    /** 엔티티 → DTO (외부 eventDate 기준으로 연/월을 파생하고 싶을 때) */
    public PendingPointDTO toDTOWithEventDate(PendingPoint p, LocalDate eventDate) {
        if (p == null) return null;
        return PendingPointDTO.ofCanonical(
                p.getId(),
                p.getUserNo(),
                p.getUsername(),
                p.getRatePercent(),
                p.getPoints(),
                eventDate,           // LocalDate 오버로드
                p.getReason(),
                p.getStatus()
        );
    }

    /** 필요 시 year,month에서 "YYYY-MM" 문자열 생성 */
    public String toYearMonthString(int year, int month) {
        try { return YearMonth.of(year, month).toString(); }
        catch (Exception e) { return null; }
    }
}
