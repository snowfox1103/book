package com.example.book.service;

import com.example.book.domain.finance.Transactions;
import com.example.book.domain.point.PointType;
import com.example.book.domain.point.UserPoint;
import com.example.book.domain.user.ApprovalStatus;
import com.example.book.dto.PendingPointDTO;
import com.example.book.repository.TransactionsRepository;
import com.example.book.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PointScanServiceImpl implements PointScanService {

    private final TransactionsRepository txRepo;
    private final UserPointRepository userPointRepo;

    /** 포인트 적립 대상 카테고리(예: 식비=1, 쇼핑=6) */
    private static final List<Long> ELIGIBLE_CATEGORIES = List.of(1L, 6L);

    /** 적립률(%) – 필요시 카테고리별 분기 */
    private int percentFromTx(Transactions tx) { return 1; }

    private long pointsFromTx(Transactions tx, int percent) {
        long amt = tx.getTransAmount();                // Transactions.transAmount
        if (amt <= 0) return 0;
        return Math.round(amt * (percent / 100.0));
    }

    @Override
    public int scanMonth(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate from = ym.atDay(1);
        LocalDate to   = ym.atEndOfMonth();

        List<Transactions> txs = txRepo.findPointable(from, to, ELIGIBLE_CATEGORIES);

        int created = 0;
        for (Transactions tx : txs) {
            // 중복방지 키(사유에 기록) — 상태 접두어 + 스캔정보
            String body = "SCAN " + ym + " tx=" + tx.getTransId();
            String pendingReason = "PENDING|" + body;

            if (userPointRepo.existsByPointReason(pendingReason)) continue;

            int percent = percentFromTx(tx);
            long points = pointsFromTx(tx, percent);
            if (points <= 0) continue;

            LocalDateTime startAt = LocalDateTime.of(tx.getTransDate(), LocalTime.MIN);

            UserPoint up = UserPoint.builder()
                    .userNo(tx.getUserNo())
                    .pointAmount(points)               // UserPoint.pointAmount
                    .pointStartDate(startAt)           // UserPoint.pointStartDate
                    .pointType(PointType.EARN)         // ✅ 적립타입은 EARN 고정
                    .pointReason(pendingReason)        // ✅ 상태는 사유 접두어로 관리
                    .build();

            userPointRepo.save(up);
            created++;
        }
        return created;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PendingPointDTO> listPending() {
        return userPointRepo
                .findByPointReasonStartingWithOrderByPointStartDateDesc("PENDING|")
                .stream()
                .map(up -> PendingPointDTO.ofCanonical(
                        up.getPointId(),
                        up.getUserNo(),
                        String.valueOf(up.getUserNo()),          // 원하면 UsersRepo로 userId 조회해서 교체
                        extractPercent(up.getPointReason()),
                        up.getPointAmount().intValue(),
                        up.getPointStartDate() != null ? up.getPointStartDate().toLocalDate() : null,
                        up.getPointReason(),
                        ApprovalStatus.PENDING                    // 화면 표기를 위해 DTO에 부여
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void approve(Long userPointId) {
        UserPoint up = userPointRepo.findById(userPointId).orElseThrow();
        // PENDING|SCAN ...  → APPROVED|SCAN ...
        if (up.getPointReason() != null && up.getPointReason().startsWith("PENDING|")) {
            up.setPointReason("APPROVED|" + up.getPointReason().substring("PENDING|".length()));
        }
    }

    @Override
    public void reject(Long userPointId) {
        UserPoint up = userPointRepo.findById(userPointId).orElseThrow();
        // PENDING|SCAN ...  → REJECTED|SCAN ...
        if (up.getPointReason() != null && up.getPointReason().startsWith("PENDING|")) {
            up.setPointReason("REJECTED|" + up.getPointReason().substring("PENDING|".length()));
        }
    }

    /** "… (1%)" 형식에서 1만 추출, 없으면 0 */
    private int extractPercent(String note) {
        if (note == null) return 0;
        int l = note.lastIndexOf('(');
        int r = note.lastIndexOf("%)");
        if (l == -1 || r == -1 || r <= l + 1) return 0;
        try { return Integer.parseInt(note.substring(l + 1, r)); }
        catch (NumberFormatException e) { return 0; }
    }
}
