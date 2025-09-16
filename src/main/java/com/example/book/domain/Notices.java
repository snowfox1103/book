package com.example.book.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notices")
@Getter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class Notices extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nBId")
    private Long nBId;

    @Column(name = "userNo")
    private Long userNo;

    @Column(name = "nBTitle")
    private String nBTitle;

    @Column(name = "nBContent")
    private String nBContent;
}
