package com.example.book.domain.board;

import com.example.book.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Reply", indexes = {
    @Index(name = "idx_reply_board_bno", columnList = "board_bno")})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "board")
public class Reply extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;
    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "bno") // FK 컬럼명
    private Board board;
    private String replyText;
    private String replyer;
    
    public void changeText(String text){
        this.replyText = text;
    }
}
