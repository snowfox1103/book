package com.example.book.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(name = "subTitle")
    private String subTitle;

    @Column(name = "subAmount")
    private Long subAmount;

    @Column(name = "subPayDate")
    private int subPayDate;

    @Column(name = "subCategory")
    private Long subCategory; //이거 왜 Long?

    @Column(name = "subNotice")
    private boolean subNotice;

    @Enumerated(EnumType.STRING)
    @Column(name = "subPeriodUnit")
    private SubPeriodUnit subPeriodUnit;

    @Column(name = "subPeriodValue")
    private int subPeriodValue;

    @Column(name = "isSub")
    private boolean isSub;
}
