// src/main/java/com/example/book/service/PointServiceImpl.java
package com.example.book.service;

import com.example.book.domain.point.UserPoint;
import com.example.book.dto.PointListDTO;
import com.example.book.dto.PointSummaryDTO;
import com.example.book.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final UserPointRepository userPointRepository;

    @Override
    public Page<PointListDTO> findPointPage(Long userNo, Pageable pageable, String sort, String dir) {

        Page<UserPoint> entityPage;
        Pageable effective = pageable;

        if ("delta".equalsIgnoreCase(sort)) {
            Pageable unsorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
            entityPage = "asc".equalsIgnoreCase(dir)
                    ? userPointRepository.findPageOrderBySignedAmountAsc(userNo, unsorted)
                    : userPointRepository.findPageOrderBySignedAmountDesc(userNo, unsorted);
            effective = unsorted;

        } else {
            // date / type → 일반 정렬
            String prop = switch (sort == null ? "date" : sort.toLowerCase()) {
                case "type" -> "pointType";
                case "date" -> "pointStartDate";
                default     -> "pointStartDate";
            };
            Sort.Direction direction = ("asc".equalsIgnoreCase(dir)) ? Sort.Direction.ASC : Sort.Direction.DESC;
            effective = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, prop));

            entityPage = userPointRepository.findByUserNo(userNo, effective);
        }

        List<PointListDTO> rows = entityPage.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        // 누적 잔액 채우기 (date & asc 에서만)
        fillBalanceForPage(userNo, rows, sort, dir);

        return new PageImpl<>(rows, effective, entityPage.getTotalElements());
    }

    private PointListDTO toDto(UserPoint p) {
        return PointListDTO.builder()
                .pointId(p.getPointId())
                .pointStartDate(p.getPointStartDate())
                .pointAmount(p.getPointAmount())
                .pointType(p.getPointType())
                .pointReason(p.getPointReason())
                .runningBalance(p.getRunningBalance())
                .build();
    }

    private void fillBalanceForPage(Long userNo, List<PointListDTO> rows, String sort, String dir) {
        boolean enable = "date".equalsIgnoreCase(sort) && "asc".equalsIgnoreCase(dir);
        if (!enable || rows == null || rows.isEmpty()) {
            if (rows != null) rows.forEach(r -> r.setBalance(null));
            return;
        }

        long running = 0L;

        // (선택) 페이지 이전 누적 포함하려면 주석 해제
        // LocalDate firstDate = (rows.get(0).getPointStartDate() instanceof LocalDate ld) ? ld : null;
        // if (firstDate != null) {
        //     running = userPointRepository.sumBefore(userNo, firstDate);
        // }

        for (PointListDTO r : rows) {
            String type = (r.getPointType() == null ? null : r.getPointType().toString());
            boolean isEarn = type != null && type.equalsIgnoreCase("EARN");
            long amt = (r.getPointAmount() == null ? 0L : r.getPointAmount());
            running += isEarn ? amt : -amt;
            r.setBalance(running);
        }
    }

    @Override
    public PointSummaryDTO getSummary(Long userNo, Integer year, Integer month) {
        if (userNo == null) {
            // 안전장치: 로그인 정보가 없거나 잘못 들어오면 0으로 리턴
            return PointSummaryDTO.zero();
        }

        // ===== 기간/라벨 결정 =====
        final boolean hasYm = (year != null && month != null);
        String label;
        LocalDateTime from = null;
        LocalDateTime to   = null;

        if (hasYm) {
            YearMonth ym = YearMonth.of(year, month);
            label = ym.toString(); // "YYYY-MM"
            from  = ym.atDay(1).atStartOfDay();
            to    = ym.plusMonths(1).atDay(1).atStartOfDay(); // [from, to) 구간
        } else {
            label = "전체기간";
        }

        // ===== 기간 합계 =====
        long earnedPeriod = 0L;
        long usedPeriod   = 0L;
        long balanceAtPeriodEnd;

        if (hasYm) {
            // 기간 지정된 경우만 기간 합계/월말잔액 계산
            earnedPeriod = nz(userPointRepository.sumEarnByUserAndPeriod(userNo, from, to));
            usedPeriod   = nz(userPointRepository.sumUseByUserAndPeriod(userNo, from, to));

            long earnUntilTo = nz(userPointRepository.sumEarnByUserUntil(userNo, to));
            long useUntilTo  = nz(userPointRepository.sumUseByUserUntil(userNo, to));
            balanceAtPeriodEnd = earnUntilTo - useUntilTo;
        } else {
            // 전체기간이면 월말잔액 대신 현재 누계와 동일하게
            balanceAtPeriodEnd = 0L; // 일단 0; 아래 currentAll 계산 후 덮어씀
        }

        // ===== 전체 누적 현재 포인트 =====
        long totalEarn = nz(userPointRepository.sumEarnTotalByUser(userNo));
        long totalUse  = nz(userPointRepository.sumUseTotalByUser(userNo));
        long currentAll = totalEarn - totalUse;

        if (!hasYm) {
            // 전체기간이면 '월말잔액' 의미가 없으므로 현재 누계로 설정
            balanceAtPeriodEnd = currentAll;
        }

        return new PointSummaryDTO(
                currentAll,
                earnedPeriod,
                usedPeriod,
                balanceAtPeriodEnd,
                label
        );
    }

    /** null 이면 0L 로 변환 (리포지토리가 Long 또는 Optional<Long> 인 경우 모두 대응) */
    private static long nz(Long v) { return v == null ? 0L : v; }
}
