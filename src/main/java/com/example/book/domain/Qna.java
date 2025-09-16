package com.example.book.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "qna")
@Getter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class Qna extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qBId")
    private Long qBId;

    @Column(name = "userNo")
    private Long userNo;

    @Column(name = "qBTitle")
    private String qBTitle;

    @Column(name = "qBContent")
    private String qBContent;

    @Column(name = "qBBlind")
    private Boolean qBBlind;
}
