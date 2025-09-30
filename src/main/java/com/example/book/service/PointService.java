package com.example.book.service;

import com.example.book.dto.PointListDTO;
import com.example.book.dto.PointSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PointService {
    Page<PointListDTO> findPointPage(Long userNo, Pageable pageable, String sort, String dir);

    default Page<PointListDTO> findPointPage(Long userNo, Pageable pageable) {
        // 기본: 최신순(날짜 desc)
        return findPointPage(userNo, pageable, "date", "desc");
    }

    // 화면 하단 요약(이번달 합계 등)
    PointSummaryDTO getSummary(Long userNo, Integer year, Integer month);
}
