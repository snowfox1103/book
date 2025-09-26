package com.example.book.domain.notice;

import com.example.book.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notice")
@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class Notice extends BaseEntity { // Notices -> Notice 0917 석준영

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nBId")
    private Long nBId;

    @Column(name = "userNo")
    private Long userNo;

    @Column(name = "nBTitle")
    private String nBTitle;

    @Column(name = "nBContent")
    private String nBContent;

    @Column(nullable = false)
    private LocalDateTime nBCreatedAt;

    @Column(nullable = false)
    private LocalDateTime nBUpdatedAt;

    @PrePersist
    protected void onCreate() {
        this.nBCreatedAt = LocalDateTime.now();
        this.nBUpdatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.nBUpdatedAt = LocalDateTime.now();
    }

    // notice 작성자 지정용 추가 0919 석준영
    public void assignWriter(Long userNo) {
        this.userNo = userNo;
    }

    // 제목/내용 수정용 헬퍼 추가 0917 석준영
    public void change(String title, String content) {
        this.nBTitle = title;
        this.nBContent = content;
    }
}
