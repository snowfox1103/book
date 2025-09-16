package com.example.book.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pointManage")
@Getter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class PointManage extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PMId")
    private Long PMId;

    @Column(name = "PMMax")
    private Long PMMax;

    @Column(name = "PMCat")
    private Long PMCat;

}
