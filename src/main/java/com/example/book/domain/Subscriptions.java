package com.example.book.domain;

import com.example.book.dto.SubscriptionsDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscription")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Subscriptions extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subId")
    private Long subId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userNo") // DB 컬럼
    private Users users;

    @OneToOne(fetch = FetchType.LAZY)
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
    }
}
