package com.example.book.domain;

import com.example.book.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Getter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class Categories extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "catId")
    private Long catId;

    @Column(name = "userNo")
    private Long userNo;

    @Column(name = "catName")
    private String catName;

    @Column(name = "isSystemDefault")
    private boolean isSystemDefault;
}
