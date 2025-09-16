package com.example.book.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscription")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Subscriptions extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subId")
    private Long subId;

    @Column(name = "userNo")
    private Long userNo;

    @Column(name = "subTitle")
    private String subTitle;

    @Column(name = "subAmount")
    private Long subAmount;

    @Column(name = "subPayDate")
    private int subPayDate;

    @Column(name = "subCategory")
    private Long subCategory;

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
