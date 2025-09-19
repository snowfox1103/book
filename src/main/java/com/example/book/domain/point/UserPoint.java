package com.example.book.domain.point;

import com.example.book.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "userPoint")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserPoint extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pointId")
    private Long pointId;

    @Column(name = "userNo")
    private Long userNo;

    @Column(name = "budId")
    private Long budId;

    @Column(name = "pointStartDate")
    private LocalDate pointStartDate;

    @Column(name = "pointAmount")
    private Long pointAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "pointType")
    private PointType pointType;

    @Column(name = "pointReason")
    private String pointReason;

}
