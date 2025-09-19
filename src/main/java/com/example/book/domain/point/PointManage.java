package com.example.book.domain.point;

import com.example.book.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pointManage")
@Getter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class PointManage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PMId")
    private Long PMId;

    @Column(name = "PMMax")
    private Long PMMax;

    @Column(name = "PMCat")
    private Long PMCat;

}
