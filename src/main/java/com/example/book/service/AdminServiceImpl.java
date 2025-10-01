package com.example.book.service;

import com.example.book.domain.finance.Budgets;
import com.example.book.domain.finance.InOrOut;
import com.example.book.domain.finance.Transactions;
import com.example.book.domain.point.PendingPoint;
import com.example.book.domain.point.PointType;
import com.example.book.domain.point.UserPoint;
import com.example.book.domain.user.ApprovalStatus;
import com.example.book.domain.point.RewardType;
import com.example.book.domain.user.Users;
import com.example.book.dto.PendingPointDTO;
import com.example.book.dto.PointSettingsDTO;
import com.example.book.dto.RuleDTO;
import com.example.book.repository.PendingPointRepository;
import com.example.book.repository.UserPointRepository;
import com.example.book.repository.TransactionsRepository;
import com.example.book.repository.UsersRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final PendingPointRepository pendingPointRepository;
    private final UserPointRepository  userPointRepository;
    private final UsersRepository usersRepository;
    private final TransactionsRepository transactionsRepository;

    @PersistenceContext
    private EntityManager em;

    /** 월 상한(포인트) */
    private Integer monthlyCap = 500;

    /** 규칙: threshold(%) -> RuleDTO (오름차순) */
    private final Map<Integer, RuleDTO> rules = new TreeMap<>();

    /** 제외 카테고리 */
    private final List<String> excludedCategories = new ArrayList<>();

    // ───────────────────────── settings ─────────────────────────

    @Override @Transactional(readOnly = true)
    public PointSettingsDTO getSettings() {
        return new PointSettingsDTO(
                monthlyCap,
                new ArrayList<>(rules.values()),
                new ArrayList<>(excludedCategories)
        );
    }

    // ───────────────────────── list ─────────────────────────────

    @Override @Transactional(readOnly = true)
    public List<PendingPointDTO> listPendings() {
        var rows = pendingPointRepository.findAllByOrderByCreatedAtDesc();
        List<PendingPointDTO> list = new ArrayList<>(rows.size());
        for (PendingPoint p : rows) {
            if (p.getCreatedAt() != null) {
                list.add(PendingPointDTO.ofCanonical(
                        p.getId(), p.getUserNo(), p.getUsername(),
                        p.getRatePercent(), p.getPoints(),
                        p.getYearMonth(),
                        p.getReason(), p.getStatus()
                ));
            } else {
                list.add(PendingPointDTO.ofCanonical(
                        p.getId(), p.getUserNo(), p.getUsername(),
                        p.getRatePercent(), p.getPoints(),
                        p.getYearMonth(),
                        p.getReason(), p.getStatus()
                ));
            }
        }
        return list;
    }

    // ─────────────── scan(all users) / user별 accrual ───────────────

    @Override @Transactional
    public int scanMonthlyAccruals(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);

        @SuppressWarnings("unchecked")
        List<Long> fromBudgets = em.createQuery(
                        "select distinct b.userNo from " + Budgets.class.getSimpleName() + " b " +
                                "where b.budYear = :y and b.budMonth = :m")
                .setParameter("y", year).setParameter("m", month).getResultList();

        @SuppressWarnings("unchecked")
        List<Long> fromTrans = em.createQuery(
                        "select distinct t.userNo from " + Transactions.class.getSimpleName() + " t " +
                                "where t.transDate between :s and :e")
                .setParameter("s", ym.atDay(1)).setParameter("e", ym.atEndOfMonth()).getResultList();

        Set<Long> userNos = new LinkedHashSet<>();
        if (fromBudgets != null) userNos.addAll(fromBudgets);
        if (fromTrans  != null) userNos.addAll(fromTrans);

        int affected = 0;
        for (Long u : userNos) {
            String username = null;
            try {
                username = (String) em.createQuery(
                                "select u.userId from Users u where u.userNo = :u")
                        .setParameter("u", u).setMaxResults(1).getSingleResult();
            } catch (Exception ignore) {}
            affected += scanMonthlyAccrualsForUser(year, month, u, username);
        }
        return affected;
    }

    @Transactional
    protected int scanMonthlyAccrualsForUser(int year, int month, long userNo, String username) {
        YearMonth ym = YearMonth.of(year, month);
        String ymStr  = ym.toString();

        // 1) 실사용률 산출
        UsageSnapshot usage = computeUsageSnapshot(userNo, ym);

        // 2) 규칙 매칭
        RuleDTO matched = rules.values().stream()
                .filter(r -> usage.ratePercent() <= (r.threshold() == null ? 100 : r.threshold()))
                .findFirst().orElse(null);

        // 3) 포인트/사유 산정
        AwardResult award = computeAward(matched, usage);

        // 4) upsert
        PendingPoint existing = pendingPointRepository
                .findByUserNoAndYearMonth(userNo, ymStr).orElse(null);

        if (existing == null) {
            PendingPoint p = new PendingPoint();
            p.setUserNo(userNo);
            p.setUsername(username);
            p.setYearMonth(ymStr);
            p.setRatePercent(usage.ratePercent());
            p.setPoints(award.points());
            p.setReason(award.reason());
            p.setStatus(ApprovalStatus.PENDING);
            p.setCreatedAt(LocalDateTime.now());
            pendingPointRepository.save(p);
        } else {
            existing.setRatePercent(usage.ratePercent());
            existing.setPoints(award.points());
            existing.setReason(award.reason());
            existing.setStatus(ApprovalStatus.PENDING);
            pendingPointRepository.save(existing);
        }
        return 1;
    }

    // ─────────────── clear / approve / reject ───────────────

    @Override @Transactional
    public int clearPendingFor(int year, int month) {
        String ymStr = YearMonth.of(year, month).toString();
        return pendingPointRepository.deleteByYearMonth(ymStr);
    }

    @Override @Transactional
    public void approvePending(Long id) {
        var p = pendingPointRepository.findById(id).orElseThrow();
        p.setStatus(ApprovalStatus.APPROVED);
        p.setDecidedAt(LocalDateTime.now());
        p.setDecidedBy("admin");
        pendingPointRepository.save(p);
    }

    @Override @Transactional
    public void rejectPending(Long id) {
        var p = pendingPointRepository.findById(id).orElseThrow();
        p.setStatus(ApprovalStatus.REJECTED);
        p.setDecidedAt(LocalDateTime.now());
        p.setDecidedBy("admin");
        pendingPointRepository.save(p);
    }

    @Override @Transactional
    public int approvePendingBulk(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        var now = LocalDateTime.now();
        var rows = pendingPointRepository.findAllByIdIn(ids);
        for (var p : rows) {
            p.setStatus(ApprovalStatus.APPROVED);
            p.setDecidedAt(now);
            p.setDecidedBy("admin");
        }
        pendingPointRepository.saveAll(rows);
        return rows.size();
    }

    @Override @Transactional
    public int rejectPendingBulk(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        var now = LocalDateTime.now();
        var rows = pendingPointRepository.findAllByIdIn(ids);
        for (var p : rows) {
            p.setStatus(ApprovalStatus.REJECTED);
            p.setDecidedAt(now);
            p.setDecidedBy("admin");
        }
        pendingPointRepository.saveAll(rows);
        return rows.size();
    }

    // ─────────────── paging / commit(실반영) ───────────────

    @Override @Transactional(readOnly = true)
    public Page<PendingPointDTO> findPendingsPage(String yearMonth, Pageable pageable) {
        List<PendingPoint> all = pendingPointRepository.findAllByOrderByCreatedAtDesc();
        List<PendingPoint> filtered = all.stream()
                .filter(p -> Objects.equals(yearMonth, p.getYearMonth()))
                .collect(Collectors.toList());

        int total = filtered.size();
        int from  = (int) pageable.getOffset();
        int to    = Math.min(from + pageable.getPageSize(), total);
        List<PendingPoint> slice = (from > to) ? Collections.emptyList() : filtered.subList(from, to);

        List<PendingPointDTO> content = new ArrayList<>(slice.size());
        for (PendingPoint p : slice) {
            if (p.getCreatedAt() != null) {
                content.add(PendingPointDTO.ofCanonical(
                        p.getId(), p.getUserNo(), p.getUsername(),
                        p.getRatePercent(), p.getPoints(),
                        p.getYearMonth(),
                        p.getReason(), p.getStatus()
                ));
            }
        }
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    @Transactional
    public void commitApproved(int year, int month) {
        String ymStr = YearMonth.of(year, month).toString();

        var approved = pendingPointRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .filter(p -> ymStr.equals(p.getYearMonth()))
                .filter(p -> p.getStatus() == ApprovalStatus.APPROVED)
                .toList();

        // 적립 시각은 "지금" 그대로 사용 (00:00 방지)
        LocalDateTime eventDateTime = LocalDateTime.now();

        for (var p : approved) {

            boolean exists = userPointRepository.existsByUserNoAndPointTypeAndPointAmountAndPointStartDateAndPointReason(
                    p.getUserNo(),
                    PointType.EARN,
                    (long) p.getPoints(),
                    eventDateTime,
                    "[월정산] " + ymStr + " " + p.getReason()
            );
            if (exists) continue;

            // 1) 사용자 포인트 이력(UserPoint) 저장
            var up = new UserPoint();
            up.setUserNo(p.getUserNo());
            up.setPointType(PointType.EARN);
            up.setPointAmount((long) p.getPoints());
            up.setPointStartDate(eventDateTime);       // <-- toLocalDate() 쓰지 않음
            up.setPointReason("[월정산] " + ymStr + " " + p.getReason());
            userPointRepository.save(up);

            // 2) **Users 잔액(balance) 갱신**  ← 핵심
            Users u = usersRepository.findById(p.getUserNo())
                    .orElseThrow(() -> new IllegalStateException("user not found: " + p.getUserNo()));
            long cur = u.getBalance();
            u.setBalance(cur + p.getPoints());                        // setPointBalance(...) 로 변경
            usersRepository.save(u);

            // 4) pending 마무리 정보 채움
            if (p.getDecidedAt() == null) p.setDecidedAt(LocalDateTime.now());
            if (p.getDecidedBy() == null) p.setDecidedBy("admin");
        }

        pendingPointRepository.saveAll(approved);

        // 필요 시 사용자별 러닝밸런스 재계산
         approved.stream().map(PendingPoint::getUserNo).distinct().forEach(this::recomputeRunningBalance);
    }



    // ─────────────── rules / cap / exclude ───────────────

    @Override @Transactional
    public void addOrUpdateRule(RuleDTO dto) {
        if (dto == null || dto.threshold() == null) return;
        rules.put(dto.threshold(), dto);
    }

    @Override @Transactional
    public void deleteRule(Integer threshold) {
        if (threshold == null) return;
        rules.remove(threshold);
    }

    @Override @Transactional
    public void saveExcludedCategories(String csv) {
        excludedCategories.clear();
        if (csv == null || csv.isBlank()) return;
        Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .forEach(excludedCategories::add);
    }

    @Override @Transactional
    public void saveCap(Integer monthlyCap) {
        this.monthlyCap = (monthlyCap == null || monthlyCap < 0) ? 0 : monthlyCap;
    }

    @Override @Transactional
    public void saveMonthlyCap(Integer monthlyCap) {
        saveCap(monthlyCap);
    }

    // ─────────────── helpers ───────────────

    private UsageSnapshot computeUsageSnapshot(long userNo, YearMonth ym) {
        int y = ym.getYear();
        int m = ym.getMonthValue();

        Long budget = (Long) em.createQuery(
                        "select coalesce(sum(b.budAmount),0) from " + Budgets.class.getSimpleName() + " b " +
                                "where b.userNo = :u and b.budYear = :y and b.budMonth = :m")
                .setParameter("u", userNo).setParameter("y", y).setParameter("m", m)
                .getSingleResult();

        LocalDate start = ym.atDay(1);
        LocalDate end   = ym.atEndOfMonth();

        Long spent = (Long) em.createQuery(
                        "select coalesce(sum(t.transAmount),0) from " + Transactions.class.getSimpleName() + " t " +
                                "where t.userNo = :u and t.transInOut = :io and t.transDate between :s and :e")
                .setParameter("u", userNo)
                .setParameter("io", InOrOut.OUT)
                .setParameter("s", start).setParameter("e", end)
                .getSingleResult();

        int rate = 0;
        if (budget != null && budget > 0L) {
            BigDecimal r = BigDecimal.valueOf(spent == null ? 0L : spent)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(budget), 0, RoundingMode.HALF_UP);
            rate = r.intValue();
        }
        return new UsageSnapshot(Math.max(0, Math.min(rate, 100)),
                budget == null ? 0L : budget,
                spent == null ? 0L : spent);
    }

    private AwardResult computeAward(RuleDTO rule, UsageSnapshot u) {
        if (u.budget() <= 0L) return new AwardResult(0, "예산 미설정");
        if (rule == null)    return new AwardResult(0, "예산 미설정");

        int points = 0;
        RewardType rt = rule.rewardType(); // FIXED | PERCENT
        if (rt == RewardType.FIXED) {
            points = rule.rewardPoints();
        } else if (rt == RewardType.PERCENT) {
            int pct = rule.rewardPoints();
            points = BigDecimal.valueOf(u.spent())
                    .multiply(BigDecimal.valueOf(pct))
                    .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP)
                    .intValue();
        }

        if (monthlyCap != null && monthlyCap > 0) points = Math.min(points, monthlyCap);

        String reason = (rt == RewardType.FIXED)
                ? String.format("규칙 매칭: 사용률 %d%% ≤ %d%% → 고정금액 %d",
                u.ratePercent(), rule.threshold(), points)
                : String.format("규칙 매칭: 사용률 %d%% ≤ %d%% → 지출의 %d%%",
                u.ratePercent(), rule.threshold(), rule.rewardPoints());

        return new AwardResult(points, reason);
    }

    // (간단 record)
    private record UsageSnapshot(int ratePercent, long budget, long spent) {}
    private record AwardResult(int points, String reason) {}

    @Override
    @Transactional
    public void clearPendings(int year, int month) {
        String ym = YearMonth.of(year, month).toString();
        List<PendingPoint> rows = pendingPointRepository.findAllByYearMonth(ym);
        int size = rows.size();
        if (size > 0) pendingPointRepository.deleteAll(rows);
    }

    /**
     * 커밋 이후 사용자별 runningBalance 를 다시 계산해 주는 내부 도우미.
     * (프로젝트에서 이미 동일 목적 유틸/서비스가 있다면 그걸 호출해도 무방)
     */
    private void recomputeRunningBalance(Long userNo) {
        // 가장 오래된 순으로 정렬해 누적합 계산
        List<UserPoint> points =
                userPointRepository.findAllByUserNoOrderByPointStartDateAscPointIdAsc(userNo);

        long running = 0L;
        for (UserPoint p : points) {
            long delta = (p.getPointType() == PointType.EARN) ? p.getPointAmount() : -p.getPointAmount();
            running += delta;
            p.setRunningBalance(running);
        }
        userPointRepository.saveAll(points);
    }


}
