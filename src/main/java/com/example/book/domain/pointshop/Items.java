package com.example.book.domain.pointshop;

import com.example.book.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "items")
@Getter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class Items extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "itemId")
    private Long itemId;

    @Column(name = "itemImage")
    private String itemImage;

    @Column(name = "itemName")
    private String itemName;

    @Column(name = "itemPrice")
    private Long itemPrice;
}
