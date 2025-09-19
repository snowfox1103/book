package com.example.book.domain.point;

import com.example.book.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pointManageRule")
@Getter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class PointManageRule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PMRuleId")
    private Long PMRuleId;

    @Column(name = "PMId")
    private Long PMId;

    @Column(name = "percentThreshold")
    private Long percentThreshold;

    @Column(name = "rewardAmount")
    private Long rewardAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "rewardType")
    private RewardType rewardType;
}
