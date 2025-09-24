package com.example.book.domain.finance;

import com.example.book.domain.common.BaseEntity;
import com.example.book.domain.finance.Categories;
import com.example.book.domain.finance.SubPeriodUnit;
import com.example.book.domain.user.Users;
import com.example.book.dto.SubscriptionsDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscription")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Subscriptions extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subId")
    private Long subId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userNo") // DB 컬럼
    private Users users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catId")
    private Categories categories;

    @Column(name = "subTitle")
    private String subTitle;

    @Column(name = "subAmount")
    private Long subAmount;

    @Column(name = "subPayDate")
    private int subPayDate;

    @Column(name = "subNotice")
    private boolean subNotice;

    @Enumerated(EnumType.STRING)
    @Column(name = "subPeriodUnit")
    private SubPeriodUnit subPeriodUnit;

    @Column(name = "subPeriodValue")
    private int subPeriodValue;

    @Column(name = "isSub")
    private boolean isSub;

    @Column(name = "nextPayDate")
    private java.time.LocalDate nextPayDate;

    @Column(name = "notifyWindowDays")
    private int notifyWindowDays; // 기본 3

    @Column(name = "lastNotifiedAt")
    private LocalDateTime lastNotifiedAt;

    @Column(name = "lastNotifiedFor") // 이번 회차 결제일에 대해 이미 알렸는지
    private LocalDate lastNotifiedFor;

    @Column(name = "anchorToMonthEnd")
    private boolean anchorToMonthEnd;

    public void updateFromDTO(SubscriptionsDTO dto, Users users, Categories category) {
        this.users = users;
        this.categories = category;
        this.subTitle = dto.getSubTitle();
        this.subAmount = dto.getSubAmount();
        this.subPayDate = dto.getSubPayDate();
        this.subPeriodUnit = SubPeriodUnit.valueOf(dto.getSubPeriodUnit());
        this.subPeriodValue = dto.getSubPeriodValue();
        this.subNotice = dto.isSubNotice();
        this.isSub = dto.isSub();
        this.nextPayDate = null;             // 재계산 트리거
        this.lastNotifiedFor = null;         // 회차 변경 시 알림 기록 리셋
        initNextPayDateIfNeeded();
    }

    public LocalDate computeNextDate(LocalDate from) {
        int interval = Math.max(1, this.subPeriodValue);

        switch (this.subPeriodUnit) {
            case DAY -> {
                return from.plusDays(interval);
            }
            case WEEK -> {
                return from.plusWeeks(interval);
            }
            case MONTH -> {
                // “매월 n일” 형태 지원
                LocalDate base = from.plusMonths(interval);
                if (anchorToMonthEnd || subPayDate <= 0) {
                    // 말일 고정 또는 subPayDate 미지정이면 말일
                    return base.withDayOfMonth(base.lengthOfMonth());
                }
                int dom = Math.min(subPayDate, base.lengthOfMonth());
                return base.withDayOfMonth(dom);
            }
            case YEAR -> {
                LocalDate base = from.plusYears(interval);
                if (anchorToMonthEnd || subPayDate <= 0) {
                    return base.withDayOfMonth(base.lengthOfMonth());
                }
                int dom = Math.min(subPayDate, base.lengthOfMonth());
                return base.withDayOfMonth(dom);
            }
            default -> throw new IllegalArgumentException("Unsupported unit: " + this.subPeriodUnit);
        }
    }

    /** 최초 생성 시 nextPayDate 설정(없으면 오늘 기준으로 계산) */
    public void initNextPayDateIfNeeded() {
        if (this.nextPayDate == null) {
            LocalDate today = LocalDate.now();

            // 월 단위
            if (this.subPeriodUnit == SubPeriodUnit.MONTH && subPayDate > 0) {
                int dom = Math.min(subPayDate, today.lengthOfMonth());
                LocalDate target = today.withDayOfMonth(dom);
                this.nextPayDate = (target.isAfter(today) || target.isEqual(today))
                  ? target
                  : computeNextDate(target);

                // 주 단위 → 이번 달 subPayDate 우선 고려
            } else if (this.subPeriodUnit == SubPeriodUnit.WEEK && subPayDate > 0) {
                int dom = Math.min(subPayDate, today.lengthOfMonth());
                LocalDate target = today.withDayOfMonth(dom);
                this.nextPayDate = (target.isAfter(today) || target.isEqual(today))
                  ? target
                  : computeNextDate(target);

                // 년 단위 → 이번 해 subPayDate 고려
            } else if (this.subPeriodUnit == SubPeriodUnit.YEAR && subPayDate > 0) {
                int dom = Math.min(subPayDate, today.lengthOfMonth());
                LocalDate target = today.withDayOfMonth(dom);
                this.nextPayDate = (target.isAfter(today) || target.isEqual(today))
                  ? target
                  : computeNextDate(target);

                // 일 단위 → 그냥 interval만큼 더하기
            } else {
                this.nextPayDate = computeNextDate(today);
            }
        }
        if (this.notifyWindowDays <= 0) this.notifyWindowDays = 3;
    }

    /** 결제 처리가 끝났거나 결제일이 지난 경우 다음 주기로 이동 */
    public void rollToNext() {
        LocalDate base = (this.nextPayDate != null) ? this.nextPayDate : LocalDate.now();
        this.nextPayDate = computeNextDate(base);
        this.lastNotifiedAt = null; // 다음 회차에 다시 알림 가능
    }

    /** 오늘 알림을 노출했는지 기록 */
    public void markNotifiedNow() {
        this.lastNotifiedAt = LocalDateTime.now();
        this.lastNotifiedFor = this.nextPayDate; // ★ 이번 회차에 대해 마킹
    }
}
