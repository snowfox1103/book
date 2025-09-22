package com.example.book.domain.pointshop;

import com.example.book.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart")
@Getter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class Cart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cartId")
    private Long cartId;

    @Column(name = "userNo")
    private Long userNo;

    @Column(name = "itemId")
    private Long itemId;

    @Column(name = "itemCount")
    private Long itemCount;
}
