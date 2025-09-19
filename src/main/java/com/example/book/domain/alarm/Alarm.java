package com.example.book.domain.alarm;

import com.example.book.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "alarm")
@Getter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class Alarm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarmId")
    private Long alarmId;

    @Column(name = "userNo")
    private Long userNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "alarmType")
    private AlarmType alarmType;

    @Column(name = "alarmContent")
    private String alarmContent;

    @Column(name = "subId")
    private Long subId;

    @Column(name = "budIsOver")
    private Long budIsOver;
}
