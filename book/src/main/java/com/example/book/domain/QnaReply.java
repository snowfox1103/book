package com.example.book.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "qnaReply")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QnaReply extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qRId")
    private Long qRId;

    @Column(name = "qBId")
    private Long qBId;

    @Column(name = "userNo")
    private Long userNo;

    @Column(name = "qBContent", nullable = false, length = 1000)
    private String qBContent;

    public void changeContent(String content) {
        this.qBContent = content;
    }
}
