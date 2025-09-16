package com.example.book.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "qnaReply")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class QnaReply extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qRId")
    private Long qRId;

    @Column(name = "qBId")
    private Long qBId;

    @Column(name = "userNo")
    private Long userNo;

    @Column(name = "qBContent")
    private String qBContent;

}
