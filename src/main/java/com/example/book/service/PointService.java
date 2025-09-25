package com.example.book.service;

import com.example.book.dto.PointListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PointService {
    Page<PointListDTO> findPointPage(Long userNo, Pageable pageable, String sort, String dir);
}
