package com.example.book.service;

import com.example.book.dto.PointListDTO;
import com.example.book.dto.PointSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PointService {
    /**
     * 목록 조회
     * @param ym   YYYY-MM (null이면 전체)
     * @param sort date|delta|type
     * @param dir  asc|desc
     */
    Page<PointListDTO> findPointPage(Long userNo, Pageable pageable, String sort, String dir, String ym);

    default Page<PointListDTO> findPointPage(Long userNo, Pageable pageable) {
        // 기본: 최신순(날짜 desc)
        return findPointPage(userNo, pageable, "date", "desc", null);
    }

    /** 화면 하단 요약(이번 달 합계/월말 잔액/전체 누적 등) */
    PointSummaryDTO getSummary(Long userNo, Integer year, Integer month);
}
