package com.example.book.domain.pointshop;

import com.example.book.domain.common.BaseEntity;
import com.example.book.domain.user.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cart extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cartId")
    private Long cartId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userNo")
    private Users users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itemId")
    private Items items;

    @Column(name = "itemCount")
    private int itemCount;

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
}
