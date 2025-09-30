package com.example.book.service;

import com.example.book.domain.point.PointType;
import com.example.book.domain.point.UserPoint;
import com.example.book.dto.PointListDTO;
import com.example.book.dto.PointSummaryDTO;
import com.example.book.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {
    private final UserPointRepository userPointRepository;

    @Override
    public Page<PointListDTO> findPointPage(Long userNo,
                                            Pageable pageable,
                                            String sort, String dir,
                                            String ym) {

        Page<UserPoint> entityPage;
        List<UserPoint> pageRows;
        Map<Long, Long> runningById = new HashMap<>();

        boolean hasMonth = (ym != null && ym.length() >= 7);
        LocalDateTime from = null, to = null;

        if (hasMonth) {
            // 월 범위 [from, to) (상한 미만)
            YearMonth y = YearMonth.parse(ym);
            from = y.atDay(1).atStartOfDay();
            to   = y.plusMonths(1).atDay(1).atStartOfDay();

            // 1) 페이지 데이터 (DB 정렬 + 페이징 그대로)
            entityPage = userPointRepository
                    .findByUserNoAndPointStartDateGreaterThanEqualAndPointStartDateLessThan(userNo, from, to, pageable);
            pageRows = new ArrayList<>(entityPage.getContent());

            // 2) 월 전체(비페이징) + "월 시작 이전 잔액"을 시드로 runningBalance 계산
            //    seed = from 이전까지의 총 누계 (== 전월 말 잔액)
            Long seedBefore = userPointRepository.sumDeltaBefore(userNo, from);
            long bal = (seedBefore == null) ? 0L : seedBefore;

            List<UserPoint> allInMonth = userPointRepository
                    .findByUserNoAndPointStartDateGreaterThanEqualAndPointStartDateLessThanOrderByPointStartDateAscPointIdAsc(
                            userNo, from, to);

            for (UserPoint p : allInMonth) {
                long signed = (p.getPointType() == PointType.EARN)
                        ? Math.abs(p.getPointAmount())
                        : -Math.abs(p.getPointAmount());
                bal += signed;                       // ← 전월 말 잔액에서 월 내 변동 누적
                runningById.put(p.getPointId(), bal); // 월 전체 기준 runningBalance
            }
        } else {
            // 전체기간: 페이지의 최소 날짜 이전까지 누계를 시드로, 페이지 범위 내에서 running 계산
            entityPage = userPointRepository.findByUserNo(userNo, pageable);
            pageRows = new ArrayList<>(entityPage.getContent());

            LocalDateTime minDateOnPage = pageRows.stream()
                    .map(UserPoint::getPointStartDate)
                    .filter(Objects::nonNull)
                    .min(LocalDateTime::compareTo)
                    .orElse(null);

            long seed = 0L;
            if (minDateOnPage != null) {
                Long s = userPointRepository.sumDeltaBefore(userNo, minDateOnPage);
                seed = (s == null) ? 0L : s;
            }

            List<UserPoint> chronological = new ArrayList<>(pageRows);
            chronological.sort(Comparator
                    .comparing(UserPoint::getPointStartDate, Comparator.nullsLast(LocalDateTime::compareTo))
                    .thenComparing(UserPoint::getPointId));

            long bal = seed;
            for (UserPoint p : chronological) {
                long signed = (p.getPointType() == PointType.EARN)
                        ? Math.abs(p.getPointAmount())
                        : -Math.abs(p.getPointAmount());
                bal += signed;
                runningById.put(p.getPointId(), bal);
            }
        }

        // 3) delta만 페이지 내부 정렬 (절대값 기준, 오름/내림 모두 지원)
        boolean asc = "asc".equalsIgnoreCase(dir);
        List<UserPoint> displayRows;
        if ("delta".equalsIgnoreCase(sort)) {
            Comparator<UserPoint> byDeltaAbs = Comparator.<UserPoint>comparingLong(
                            p -> Math.abs(p.getPointAmount() == null ? 0L : p.getPointAmount())
                    ).thenComparing(UserPoint::getPointStartDate)
                    .thenComparing(UserPoint::getPointId);

            displayRows = new ArrayList<>(pageRows);
            displayRows.sort(asc ? byDeltaAbs : byDeltaAbs.reversed());
        } else {
            // date/type 은 DB 정렬 결과 그대로
            displayRows = pageRows;
        }

        // 4) DTO 매핑 (표시 순서 유지, 계산된 runningBalance 매핑)
        List<PointListDTO> dtoList = displayRows.stream()
                .map(p -> PointListDTO.builder()
                        .pointId(p.getPointId())
                        .pointStartDate(p.getPointStartDate())
                        .pointAmount(p.getPointAmount())
                        .pointType(p.getPointType())
                        .pointReason(p.getPointReason())
                        .runningBalance(runningById.get(p.getPointId()))
                        .build())
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public PointSummaryDTO getSummary(Long userNo, Integer year, Integer month) {
        YearMonth ym = (year == null || month == null)
                ? YearMonth.now()
                : YearMonth.of(year, month);

        LocalDateTime from = ym.atDay(1).atStartOfDay();
        LocalDateTime to   = ym.plusMonths(1).atDay(1).atStartOfDay();

        long earnedPeriod = userPointRepository.sumEarnByUserAndPeriod(userNo, from, to);
        long usedPeriod   = userPointRepository.sumUseByUserAndPeriod(userNo, from, to);

        long earnUntilTo  = userPointRepository.sumEarnByUserUntil(userNo, to);
        long useUntilTo   = userPointRepository.sumUseByUserUntil(userNo, to);
        long balanceAtEnd = earnUntilTo - useUntilTo;

        long totalEarn = userPointRepository.sumEarnTotalByUser(userNo);
        long totalUse  = userPointRepository.sumUseTotalByUser(userNo);
        long currentAll = totalEarn - totalUse;

        return new PointSummaryDTO(
                currentAll,
                earnedPeriod,
                usedPeriod,
                balanceAtEnd,
                ym.toString()
        );
    }
}
