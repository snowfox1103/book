package com.example.book.service;

import com.example.book.domain.user.ApprovalStatus;
import com.example.book.dto.PendingPointDTO;
import com.example.book.dto.PointSettingsDTO;
import com.example.book.dto.RuleDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    // ===== 기존 설정 관리 =====
    private final Map<Integer, RuleDTO> rules = new TreeMap<>(); // key = threshold
    private List<String> excluded = new ArrayList<>();
    private Integer monthlyCap = 500;

    @Override
    public PointSettingsDTO getSettings() {
        return new PointSettingsDTO(
                monthlyCap,
                new ArrayList<>(rules.values()),
                new ArrayList<>(excluded)
        );
    }

    @Override public void addOrUpdateRule(RuleDTO dto){ rules.put(dto.threshold(), dto); }
    @Override public void deleteRule(Integer threshold){ rules.remove(threshold); }
    @Override public void saveExcludedCategories(String csv){
        if (csv == null || csv.isBlank()) {
            excluded = new ArrayList<>();
        } else {
            excluded = Arrays.stream(csv.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .distinct()
                    .collect(Collectors.toList());
        }
    }
    @Override public void saveMonthlyCap(Integer cap){ monthlyCap = (cap==null||cap<0)?0:cap; }

    // ===== 신규: 승인 대기 포인트 관리 =====
    private final Map<Long, PendingPointDTO> pendings = new LinkedHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    @Override
    public List<PendingPointDTO> listPendings() {
        return pendings.values().stream()
                .sorted(Comparator.comparing(PendingPointDTO::year)
                        .thenComparing(PendingPointDTO::month)
                        .thenComparing(PendingPointDTO::userNo))
                .collect(Collectors.toList());
    }

    @Override
    public int scanMonthlyAccruals(int year, int month) {
        // TODO: 여기서 실제 예산/지출 테이블과 연결해서 percentUsed 계산해야 함
        // 지금은 더미 유저 하나만 생성
        int percentUsed = 76; // 예시
        int reward = rules.entrySet().stream()
                .filter(e -> percentUsed <= e.getKey())
                .map(e -> e.getValue().rewardPoints())
                .findFirst().orElse(0);
        if (reward > monthlyCap) reward = monthlyCap;

        if (reward > 0) {
            Long id = seq.getAndIncrement();
            PendingPointDTO dto = new PendingPointDTO(
                    id,
                    1L,
                    "user1",
                    percentUsed,
                    reward,
                    year,
                    month,
                    "≤" + rules.keySet().iterator().next() + "% 규칙 충족",
                    ApprovalStatus.PENDING
            );
            pendings.put(id, dto);
            return 1;
        }
        return 0;
    }

    @Override
    public void approvePending(Long pendingId) {
        PendingPointDTO dto = pendings.get(pendingId);
        if (dto == null) return;

        // TODO: 실제 User 포인트 적립 로직 연결 필요
        PendingPointDTO approved = new PendingPointDTO(
                dto.id(),
                dto.userNo(),
                dto.userName(),
                dto.percentUsed(),
                dto.points(),
                dto.year(),
                dto.month(),
                dto.reason(),
                ApprovalStatus.APPROVED
        );
        pendings.put(pendingId, approved);
    }

    @Override
    public void rejectPending(Long pendingId) {
        PendingPointDTO dto = pendings.get(pendingId);
        if (dto == null) return;

        PendingPointDTO rejected = new PendingPointDTO(
                dto.id(),
                dto.userNo(),
                dto.userName(),
                dto.percentUsed(),
                dto.points(),
                dto.year(),
                dto.month(),
                dto.reason(),
                ApprovalStatus.REJECTED
        );
        pendings.put(pendingId, rejected);
    }
}
