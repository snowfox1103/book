package com.example.book.service;

import com.example.book.dto.PendingPointDTO;
import java.util.List;

public interface PointScanService {
    int scanMonth(int year, int month);
    List<PendingPointDTO> listPending();
    void approve(Long userPointId);
    void reject(Long userPointId);
}
