package com.example.book.domain.qna;

import com.example.book.domain.common.BaseEntity;
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
    private Long qbId;

    @Column(name = "userNo")
    private Long userNo;

    @Column(name = "qRContent", nullable = false, length = 1000)
    private String qrContent;

    public void changeContent(String content) {
        this.qrContent = content;
    }
}
