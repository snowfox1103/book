package com.example.book.dto;

import com.example.book.domain.point.PointType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointListDTO {

    /* ==== 목록 질의 파라미터 ==== */
    // sort 키: date | delta | type
    private String  sort;              // 정렬 키
    private String  dir;               // asc | desc
    private Integer size;              // 페이지 크기
    private Integer page;              // 0-based 페이지

    private String  from;              // yyyy-MM-dd (선택)
    private String  to;                // yyyy-MM-dd (선택)
    private Boolean onlyPositive;      // 적립만 (선택)
    private Boolean onlyNegative;      // 사용만 (선택)

    /* ==== 목록 행 데이터 ==== */
    private Long          pointId;         // UserPoint.pointId
    private LocalDateTime pointStartDate;  // UserPoint.pointStartDate
    private Long          pointAmount;     // UserPoint.pointAmount
    private PointType     pointType;       // UserPoint.pointType (EARN/USE)
    private String        pointReason;     // UserPoint.pointReason
    private Long          runningBalance;
    private Long          balance;
}
