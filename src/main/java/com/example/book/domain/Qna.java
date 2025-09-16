package com.example.book.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Qna")
@Getter
@Builder
@AllArgsConstructor
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
        this.qBTitle = title;
        this.qBContent = content;
        this.qBBlind = blind;
    }
}
