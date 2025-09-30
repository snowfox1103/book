// src/main/java/com/example/book/controller/mapper/PendingPointMapper.java
package com.example.book.controller.mapper;

import com.example.book.domain.point.PendingPoint;
import com.example.book.domain.point.PendingPointStatus;
import com.example.book.domain.user.ApprovalStatus;
import com.example.book.dto.PendingPointDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;

@Component
public class PendingPointMapper {

    private ApprovalStatus mapStatus(ApprovalStatus s) {
        return s; // null 그대로 허용
    }
    /** 엔터티 → DTO (createdAt/decidedAt가 아닌 yearMonth 기준으로 year, month 파생) */
    public PendingPointDTO toDTO(PendingPoint p) {
        if (p == null) return null;

        // year/month 파생 (yearMonth: "YYYY-MM")
        int year = 0, month = 0;
        String ym = p.getYearMonth();
        if (ym != null && ym.length() >= 7) {
            try {
                year  = Integer.parseInt(ym.substring(0, 4));
                month = Integer.parseInt(ym.substring(5, 7));
            } catch (NumberFormatException ignored) {}
        }

        // ratePercent → percentUsed (필드명 차이 흡수)
        int percentUsed = p.getRatePercent();

        // points(long?) → int 안전 변환
        int points = safeToInt(p.getPoints());

        // 상태 매핑: PendingPointStatus → ApprovalStatus
        ApprovalStatus status = mapStatus(p.getStatus());

        return new PendingPointDTO(
                p.getId(),
                p.getUserNo(),
                p.getUsername(),   // DTO의 userName
                percentUsed,
                points,
                year,
                month,
                p.getReason(),     // 원문 사유
                status
        );
    }

    /** 엔터티 → DTO (외부 이벤트 날짜로 year/month를 파생하고 싶을 때 사용) */
    public PendingPointDTO toDTOWithEventDate(PendingPoint p, LocalDate eventDate) {
        if (p == null) return null;
        return PendingPointDTO.ofCanonical(
                p.getId(),
                p.getUserNo(),
                p.getUsername(),
                p.getRatePercent(),
                safeToInt(p.getPoints()),
                eventDate,
                p.getReason(),
                mapStatus(p.getStatus())
        );
    }

    /** 필요 시 year, month에서 "YYYY-MM" 문자열 생성 */
    public String toYearMonthString(int year, int month) {
        try {
            return YearMonth.of(year, month).toString(); // "YYYY-MM"
        } catch (Exception e) {
            return null;
        }
    }

    // ===== 내부 유틸 =====

    private int safeToInt(long value) {
        if (value > Integer.MAX_VALUE) return Integer.MAX_VALUE;
        if (value < Integer.MIN_VALUE) return Integer.MIN_VALUE;
        return (int) value;
    }

    /** enum 이름(PENDING/APPROVED/REJECTED)이 동일하다는 전제의 단순 매핑 */
    private ApprovalStatus mapStatus(PendingPointStatus s) {
        if (s == null) return null;
        try {
            return ApprovalStatus.valueOf(s.name());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
