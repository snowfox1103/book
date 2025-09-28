package com.example.book.service;

import com.example.book.domain.finance.Transactions;
import com.example.book.domain.point.PointType;
import com.example.book.domain.point.UserPoint;
import com.example.book.domain.user.ApprovalStatus;
import com.example.book.domain.user.Users;
import com.example.book.dto.PendingPointDTO;
import com.example.book.repository.BudgetsRepository;
import com.example.book.repository.TransactionsRepository;
import com.example.book.repository.UserPointRepository;
import com.example.book.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PointScanServiceImpl implements PointScanService {

    private final TransactionsRepository txRepo;     // totalUseByMonth, TxRow 등 사용
    private final UserPointRepository userPointRepo;
    private final UsersRepository usersRepository;   // userNo -> userId 매핑
    private final BudgetsRepository budgetsRepository; // totalBudAmountByMonth 사용

    /** 포인트 적립 대상 카테고리(예: 식비=1, 쇼핑=6) */
    private static final List<Long> ELIGIBLE_CATEGORIES = List.of(1L, 6L);

    /** 카테고리별 적립률(%) 필요 시 분기 */
    private int percentFromCategory(Long category) {
        if (category == null) return 0;
        return (category == 1L || category == 6L) ? 1 : 0; // 예: 1% 적립
    }

    private int percentFromTxRow(TransactionsRepository.TxRow row) {
        return percentFromCategory(row.getTransCategory());
    }

    @Override
    public int scanMonth(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate from = ym.atDay(1);
        LocalDate to   = ym.atEndOfMonth();

        // 스캔 대상 거래들 (네이티브 + 프로젝션)
        List<TransactionsRepository.TxRow> txs =
                txRepo.findPointableRows(from, to, ELIGIBLE_CATEGORIES);

        // userNo -> userId 매핑 준비
        Set<Long> userNos = txs.stream().map(TransactionsRepository.TxRow::getUserNo).collect(Collectors.toSet());
        Map<Long, String> userIdMap = usersRepository.findAllById(userNos).stream()
                .collect(Collectors.toMap(Users::getUserNo, Users::getUserId));

        int created = 0;

        for (TransactionsRepository.TxRow tx : txs) {

            String body = "SCAN " + ym + " tx=" + tx.getTransId();
            String pendingReason = "PENDING|" + body;

            // 중복 방지
            if (userPointRepo.existsByPointReason(pendingReason)) continue;

            // 적립 포인트 계산
            int percent  = percentFromTxRow(tx);
            long amount  = tx.getTransAmount();
            long points  = Math.round(amount * (percent / 100.0));
            if (points <= 0) continue;

            // --- (추가) 월 사용률(%) 계산: totalUseByMonth / totalBudAmountByMonth ---
            Long used   = txRepo.totalUseByMonth(year, month, tx.getUserNo());             // 월 사용액 합계
            Long budget = budgetsRepository.totalBudAmountByMonth(year, month, tx.getUserNo()); // 월 예산 합계
            if (used == null) used = 0L;
            if (budget == null) budget = 0L;

            Integer usagePercent = null;
            if (budget > 0) {
                usagePercent = (int) Math.round(used * 100.0 / budget);
                // 사유 뒤에 “예산 사용량 xx%” 덧붙이기
                pendingReason += " | 예산 사용량 " + usagePercent + "%";
            }

            // 포인트 시작일
            LocalDateTime startAt = tx.getTransDate() != null
                    ? tx.getTransDate()
                    : from.atStartOfDay();

            // 저장
            UserPoint up = UserPoint.builder()
                    .userNo(tx.getUserNo())
                    .pointAmount(points)
                    .pointStartDate(startAt)
                    .pointType(PointType.EARN)
                    .pointReason(pendingReason)   // 예산 사용량 포함
                    .build();
            userPointRepo.save(up);

            created++;
        }
        return created;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PendingPointDTO> listPending() {
        // 현재 승인 대기 전체
        List<UserPoint> ups = userPointRepo
                .findByPointReasonStartingWithOrderByPointStartDateDesc("PENDING|");

        // userNo -> userId 캐시 (DTO에 뿌리기 위함)
        Set<Long> userNos = ups.stream().map(UserPoint::getUserNo).collect(Collectors.toSet());
        Map<Long, String> userIdMap = usersRepository.findAllById(userNos).stream()
                .collect(Collectors.toMap(Users::getUserNo, Users::getUserId));

        return ups.stream()
                .map(up -> {
                    // 포인트 적립률은 기존처럼 reason에서 ()% 추출 (없으면 0)
                    int percentUsed = extractPercent(up.getPointReason());

                    // 사용자 표시명: userId가 있으면 userId, 없으면 userNo 문자열
                    String userDisplay = userIdMap.getOrDefault(up.getUserNo(), String.valueOf(up.getUserNo()));

                    return PendingPointDTO.ofCanonical(
                            up.getPointId(),
                            up.getUserNo(),
                            userDisplay,                               // ▶ userId 표기
                            percentUsed,
                            up.getPointAmount().intValue(),
                            up.getPointStartDate() != null ? up.getPointStartDate().toLocalDate() : null,
                            up.getPointReason(),                       // ▶ “예산 사용량 xx% …” 포함됨
                            ApprovalStatus.PENDING
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public void approve(Long userPointId) {
        UserPoint up = userPointRepo.findById(userPointId).orElseThrow();
        if (up.getPointReason() != null && up.getPointReason().startsWith("PENDING|")) {
            up.setPointReason("APPROVED|" + up.getPointReason().substring("PENDING|".length()));
        }
    }

    @Override
    public void reject(Long userPointId) {
        UserPoint up = userPointRepo.findById(userPointId).orElseThrow();
        if (up.getPointReason() != null && up.getPointReason().startsWith("PENDING|")) {
            up.setPointReason("REJECTED|" + up.getPointReason().substring("PENDING|".length()));
        }
    }

    /** reason이 “… (1%)” 형식이면 1만 추출, 없으면 0 */
    private int extractPercent(String note) {
        if (note == null) return 0;
        int l = note.lastIndexOf('(');
        int r = note.lastIndexOf("%)");
        if (l == -1 || r == -1 || r <= l + 1) return 0;
        try { return Integer.parseInt(note.substring(l + 1, r)); }
        catch (NumberFormatException e) { return 0; }
    }
}
