package com.example.book.service;

import com.example.book.dto.PointSettingsDTO;
import com.example.book.dto.RuleDTO;
import com.example.book.dto.PendingPointDTO;

import java.util.List;

public interface AdminService {
    // 기존
    PointSettingsDTO getSettings();
    void addOrUpdateRule(RuleDTO dto);
    void deleteRule(Integer threshold);
    void saveExcludedCategories(String csv);
    void saveMonthlyCap(Integer cap);

    // 신규: 포인트 산출/승인
    /** 특정 연/월에 대해 규칙을 적용해 "승인 대기" 항목을 생성한다. 반환값은 생성 건수 */
    int scanMonthlyAccruals(int year, int month);

    /** 승인 대기 목록 */
    List<PendingPointDTO> listPendings();

    /** 승인/거절 처리 */
    void approvePending(Long pendingId);
    void rejectPending(Long pendingId);
}
