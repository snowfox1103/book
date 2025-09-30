package com.example.book.domain.finance;

import com.example.book.domain.common.BaseEntity;
import com.example.book.domain.user.Users;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Categories extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "catId")
    private Long catId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userNo")
    private Users users;

    @Column(name = "catName", length = 20)
    private String catName;

    @Column(name = "isSystemDefault")
    private boolean isSystemDefault;
}
