package com.example.book.service;

import com.example.book.domain.user.ApprovalStatus;
import com.example.book.domain.point.PendingPoint;
import com.example.book.dto.PendingPointDTO;
import com.example.book.dto.PointSettingsDTO;
import com.example.book.dto.RuleDTO;
import com.example.book.repository.PendingPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final PendingPointRepository pendingPointRepository;

    private Integer monthlyCap = 500;

    // 규칙: threshold(%) -> RuleDTO 매핑 (정렬 유지)
    private final Map<Integer, RuleDTO> rules = new TreeMap<>();

    // 제외 카테고리
    private final List<String> excludedCategories = new ArrayList<>();

    // ===== 설정/요약 =====
    @Override
    @Transactional(readOnly = true)
    public PointSettingsDTO getSettings() {
        return new PointSettingsDTO(
                monthlyCap,
                new ArrayList<>(rules.values()),
                new ArrayList<>(excludedCategories)
        );
    }

    // ===== 승인 대기 목록 =====
    @Override
    @Transactional(readOnly = true)
    public List<PendingPointDTO> listPendings() {
        var rows = pendingPointRepository.findAllOrderByCreatedAtDesc();
        List<PendingPointDTO> list = new ArrayList<>();
        for (PendingPoint p : rows) {
            // createdAt 기준으로 year/month 계산 또는 엔티티 필드 그대로 사용
            int y, m;
            if (p.getCreatedAt() != null) {
                y = p.getCreatedAt().getYear();
                m = p.getCreatedAt().getMonthValue();
            } else {
                y = 0; m = 0;
            }
            list.add(new PendingPointDTO(
                    p.getId(),
                    p.getUserNo(),
                    p.getUsername(),
                    p.getRatePercent(),
                    p.getPoints(),
                    y,
                    m,
                    p.getReason(),
                    p.getStatus()   // <-- ApprovalStatus
            ));
        }
        return list;
    }

    // ===== 스캔(중복 방지: upsert) =====
    @Override
    @Transactional
    public int scanMonthlyAccruals(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        String ymStr = ym.toString(); // "YYYY-MM"

        // TODO: 실제 사용률/리워드 규칙으로부터 후보 계산
        long userNo = 1L;              // 샘플
        String username = "admin";     // 샘플
        int ratePercent = 44;          // 샘플
        int points = 300;              // 샘플
        String reason = "규칙 매칭: 사용률 " + ratePercent + "% ≤ 60% → 고정금액 300";

        var existing = pendingPointRepository
                .findByUserNoAndYearMonth(userNo, ymStr)
                .orElse(null);

        if (existing == null) {
            PendingPoint p = new PendingPoint();
            p.setUserNo(userNo);
            p.setUsername(username);
            p.setYearMonth(ymStr);
            p.setRatePercent(ratePercent);
            p.setPoints(points);
            p.setReason(reason);
            p.setStatus(ApprovalStatus.PENDING);
            p.setCreatedAt(LocalDateTime.now());
            pendingPointRepository.save(p);
            return 1;
        } else {
            // 이미 있으면 갱신 (insert 금지 → Duplicate 방지)
            existing.setRatePercent(ratePercent);
            existing.setPoints(points);
            existing.setReason(reason);
            existing.setStatus(ApprovalStatus.PENDING);
            // createdAt은 그대로 두고 필요시 updatedAt만
            pendingPointRepository.save(existing);
            return 1;
        }
    }

    // ===== 해당 월 대기 전부 삭제 =====
    @Override
    @Transactional
    public int clearPendingFor(int year, int month) {
        String ymStr = YearMonth.of(year, month).toString();
        return pendingPointRepository.deleteByYearMonth(ymStr);
    }

    // ===== 단건 승인/반려 =====
    @Override
    @Transactional
    public void approvePending(Long id) {
        var p = pendingPointRepository.findById(id).orElseThrow();
        p.setStatus(ApprovalStatus.APPROVED);
        p.setDecidedAt(LocalDateTime.now());
        p.setDecidedBy("admin"); // 필요 시 로그인 사용자
        pendingPointRepository.save(p);
    }

    @Override
    @Transactional
    public void rejectPending(Long id) {
        var p = pendingPointRepository.findById(id).orElseThrow();
        p.setStatus(ApprovalStatus.REJECTED);
        p.setDecidedAt(LocalDateTime.now());
        p.setDecidedBy("admin");
        pendingPointRepository.save(p);
    }

    // ===== 일괄 승인/반려 =====
    @Override
    @Transactional
    public int approvePendingBulk(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        var rows = pendingPointRepository.findAllByIdIn(ids);
        var now = LocalDateTime.now();
        for (var p : rows) {
            p.setStatus(ApprovalStatus.APPROVED);
            p.setDecidedAt(now);
            p.setDecidedBy("admin");
        }
        pendingPointRepository.saveAll(rows);
        return rows.size();
    }

    @Override
    @Transactional
    public int rejectPendingBulk(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        var rows = pendingPointRepository.findAllByIdIn(ids);
        var now = LocalDateTime.now();
        for (var p : rows) {
            p.setStatus(ApprovalStatus.REJECTED);
            p.setDecidedAt(now);
            p.setDecidedBy("admin");
        }
        pendingPointRepository.saveAll(rows);
        return rows.size();
    }

    @Override
    @Transactional
    public void addOrUpdateRule(RuleDTO dto) {
        if (dto == null || dto.threshold() == null) return;
        rules.put(dto.threshold(), dto);
    }

    @Override
    @Transactional
    public void deleteRule(Integer threshold) {
        if (threshold == null) return;
        rules.remove(threshold);
    }

    @Override
    @Transactional
    public void saveExcludedCategories(String csv) {
        excludedCategories.clear();
        if (csv == null || csv.isBlank()) return;
        List<String> parsed = Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());
        excludedCategories.addAll(parsed);
    }

    @Override
    @Transactional
    public void saveCap(Integer monthlyCap) {
        this.monthlyCap = (monthlyCap == null || monthlyCap < 0) ? 0 : monthlyCap;
    }

    // ✅ 컨트롤러가 부르는 이름도 지원 (saveCap 위임)
    @Override
    @Transactional
    public void saveMonthlyCap(Integer monthlyCap) {
        saveCap(monthlyCap);
    }
}
