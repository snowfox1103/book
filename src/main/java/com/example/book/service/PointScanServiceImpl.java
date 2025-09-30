package com.example.book.service;

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

    private final TransactionsRepository txRepo;
    private final UserPointRepository userPointRepo;
    private final UsersRepository usersRepository;
    private final BudgetsRepository budgetsRepository;

    private static final List<Long> ELIGIBLE_CATEGORIES = List.of(1L, 6L);

    private int percentFromCategory(Long c) {
        return (c != null && (c == 1L || c == 6L)) ? 1 : 0;  // 예: 1%
    }

    private int percentFromTxRow(TransactionsRepository.TxRow row) {
        return percentFromCategory(row.getTransCategory());
    }

    @Override
    public int scanMonth(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate from = ym.atDay(1);
        LocalDate to   = ym.atEndOfMonth();

        List<TransactionsRepository.TxRow> txs =
                txRepo.findPointableRows(from, to, ELIGIBLE_CATEGORIES);

        int created = 0;

        for (TransactionsRepository.TxRow tx : txs) {
            String base = "SCAN " + ym + " tx=" + tx.getTransId();
            String pendingReason = "PENDING|" + base;

            // 중복 방지
            if (userPointRepo.existsByPointReason(pendingReason)) continue;

            int percent  = percentFromTxRow(tx);
            long amount  = tx.getTransAmount();
            long points  = Math.round(amount * (percent / 100.0));
            if (points <= 0) continue;

            // 부가정보(월 예산 대비 사용률)
            Long used   = txRepo.totalUseByMonth(year, month, tx.getUserNo());
            Long budget = budgetsRepository.totalBudAmountByMonth(year, month, tx.getUserNo());
            if (used == null) used = 0L;
            if (budget == null) budget = 0L;
            if (budget > 0) {
                int usagePercent = (int)Math.round(used * 100.0 / budget);
                pendingReason += " | 예산 사용량 " + usagePercent + "%";
            }

            LocalDateTime startAt = tx.getTransDate() != null
                    ? tx.getTransDate()
                    : from.atStartOfDay();

            userPointRepo.save(UserPoint.builder()
                    .userNo(tx.getUserNo())
                    .pointStartDate(startAt)
                    .pointAmount(points)
                    .pointType(PointType.EARN)
                    .pointReason(pendingReason)
                    .build());
            created++;
        }
        return created;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PendingPointDTO> listPending() {
        List<UserPoint> ups =
                userPointRepo.findByPointReasonStartingWithOrderByPointStartDateDesc("PENDING|");

        Map<Long, String> userIdMap = usersRepository
                .findAllById(ups.stream().map(UserPoint::getUserNo).collect(Collectors.toSet()))
                .stream()
                .collect(Collectors.toMap(Users::getUserNo, Users::getUserId));

        return ups.stream().map(up -> {
            String display = userIdMap.getOrDefault(up.getUserNo(), String.valueOf(up.getUserNo()));
            return PendingPointDTO.ofCanonical(
                    up.getPointId(),
                    up.getUserNo(),
                    display,
                    extractPercent(up.getPointReason()),
                    up.getPointAmount().intValue(),
                    up.getPointStartDate() != null ? up.getPointStartDate().toLocalDate() : null,
                    up.getPointReason(),
                    ApprovalStatus.PENDING
            );
        }).toList();
    }

    @Override
    public void approve(Long id) {
        userPointRepo.findById(id).ifPresent(up -> {
            if (up.getPointReason() != null && up.getPointReason().startsWith("PENDING|")) {
                up.setPointReason("APPROVED|" + up.getPointReason().substring("PENDING|".length()));
            }
        });
    }

    @Override
    public void reject(Long id) {
        userPointRepo.findById(id).ifPresent(up -> {
            if (up.getPointReason() != null && up.getPointReason().startsWith("PENDING|")) {
                up.setPointReason("REJECTED|" + up.getPointReason().substring("PENDING|".length()));
            }
        });
    }

    private int extractPercent(String note) {
        if (note == null) return 0;
        int l = note.lastIndexOf('('), r = note.lastIndexOf("%)");
        if (l < 0 || r < 0 || r <= l + 1) return 0;
        try { return Integer.parseInt(note.substring(l + 1, r)); }
        catch (NumberFormatException e) { return 0; }
    }
}
