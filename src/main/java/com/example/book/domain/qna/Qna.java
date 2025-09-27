package com.example.book.domain.qna;

import com.example.book.domain.common.BaseEntity;
import com.example.book.domain.user.Users;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "qna")
@Getter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class Qna extends BaseEntity {

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

    public void change(String title, String content, boolean blind) {
        this.qBTitle  = title;
        this.qBContent = content;
        this.qBBlind   = blind;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userNo", insertable = false, updatable = false)
    private Users writer;

}
