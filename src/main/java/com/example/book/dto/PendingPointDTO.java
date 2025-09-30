package com.example.book.dto;

import com.example.book.domain.point.PendingPoint;
import com.example.book.domain.user.ApprovalStatus;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class PendingPointDTO {
    Long id;
    Long userNo;
    String username;       // entity: username
    int ratePercent;       // entity: ratePercent
    int points;            // entity: points (int)
    String yearMonth;      // "YYYY-MM"
    String reason;
    ApprovalStatus status;

    /** 엔티티 -> DTO 기본 매핑 */
    public static PendingPointDTO from(PendingPoint p) {
        return PendingPointDTO.builder()
                .id(p.getId())
                .userNo(p.getUserNo())
                .username(p.getUsername())
                .ratePercent(p.getRatePercent())
                .points(p.getPoints())
                .yearMonth(p.getYearMonth())
                .reason(p.getReason())
                .status(p.getStatus())
                .build();
    }

    /* ===== 호환용 팩토리 메서드 (기존 코드 깨지지 않게) ===== */

    /** ① yearMonth 문자열로 직접 생성 */
    public static PendingPointDTO ofCanonical(Long id, Long userNo, String username,
                                              int ratePercent, int points,
                                              String yearMonth, String reason,
                                              ApprovalStatus status) {
        return PendingPointDTO.builder()
                .id(id).userNo(userNo).username(username)
                .ratePercent(ratePercent).points(points)
                .yearMonth(yearMonth).reason(reason).status(status)
                .build();
    }

    /** ② (year, month) 정수로 생성 */
    public static PendingPointDTO ofCanonical(Long id, Long userNo, String username,
                                              int ratePercent, int points,
                                              int year, int month,
                                              String reason, ApprovalStatus status) {
        String ym = String.format("%04d-%02d", year, month);
        return ofCanonical(id, userNo, username, ratePercent, points, ym, reason, status);
    }

    /** ③ LocalDate 로부터 (해당 날짜의 연/월) 생성 */
    public static PendingPointDTO ofCanonical(Long id, Long userNo, String username,
                                              int ratePercent, int points,
                                              LocalDate eventDate,
                                              String reason, ApprovalStatus status) {
        String ym = (eventDate == null)
                ? null
                : String.format("%04d-%02d", eventDate.getYear(), eventDate.getMonthValue());
        return ofCanonical(id, userNo, username, ratePercent, points, ym, reason, status);
    }
}
