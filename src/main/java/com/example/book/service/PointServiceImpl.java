// src/main/java/com/example/book/service/PointServiceImpl.java
package com.example.book.service;

import com.example.book.domain.point.UserPoint;
import com.example.book.dto.PointListDTO;
import com.example.book.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
}
