package com.example.book.service;

import com.example.book.dto.PendingPointDTO;
import com.example.book.dto.PointSettingsDTO;
import com.example.book.dto.RuleDTO;

import java.util.List;

public interface AdminService {

    PointSettingsDTO getSettings();

    List<PendingPointDTO> listPendings();

    /** 모든 사용자에 대해 해당 월 스캔 */
    int scanMonthlyAccruals(int year, int month);

    /** 해당 월의 대기건(승인/반려 전) 삭제 */
    int clearPendingFor(int year, int month);

    /** 단건 승인/반려 */
    void approvePending(Long id);
    void rejectPending(Long id);

    /** 일괄 승인/반려 */
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

    /** (승인된) 대기건을 실제 포인트에 반영 */
    void commitApproved(int year, int month);

    /** 스캔 목록 페이징 조회 */
    org.springframework.data.domain.Page<PendingPointDTO> findPendingsPage(
            String yearMonth,
            org.springframework.data.domain.Pageable pageable
    );

    /** (보조) 해당 월 대기건 비우기 – 컨트롤러의 “비우기” 버튼용 */
    void clearPendings(int year, int month);
}
