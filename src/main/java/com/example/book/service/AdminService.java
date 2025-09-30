package com.example.book.service;

import com.example.book.dto.PendingPointDTO;
import com.example.book.dto.PointSettingsDTO;
import com.example.book.dto.RuleDTO;

import java.util.List;

public interface AdminService {

    PointSettingsDTO getSettings();

    List<PendingPointDTO> listPendings();

    int scanMonthlyAccruals(int year, int month);

    int clearPendingFor(int year, int month);

    void approvePending(Long id);
    void rejectPending(Long id);

    int approvePendingBulk(List<Long> ids);
    int rejectPendingBulk(List<Long> ids);

    /** 규칙 추가/수정 (threshold 기준 upsert) */
    void addOrUpdateRule(RuleDTO dto);

    /** 규칙 삭제 */
    void deleteRule(Integer threshold);

    /** 제외 카테고리 CSV 저장 (예: "관리비, 월세") */
    void saveExcludedCategories(String csv);

    /** 월 상한 저장 */
    void saveCap(Integer monthlyCap);

    default void saveMonthlyCap(Integer monthlyCap) {
        saveCap(monthlyCap);
    }
}
