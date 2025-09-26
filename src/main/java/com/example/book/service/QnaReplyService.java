package com.example.book.service;

import com.example.book.dto.PageRequestDTO;
import com.example.book.dto.PageResponseDTO;
import com.example.book.dto.QnaReplyDTO;

import java.util.List;

public interface QnaReplyService {
    Long register(QnaReplyDTO dto, Long qbId, Long userNo);
    QnaReplyDTO read(Long replyId);
    void modify(QnaReplyDTO dto, Long editorUserNo);
    void remove(Long replyId, Long actorUserNo);
    void removeByAdmin(Long replyId);
    PageResponseDTO<QnaReplyDTO> list(Long qbId, PageRequestDTO page);

    /** ✅ 상세에선 dto 투영 목록을 바로 쓰는 경량 메서드 */
    List<QnaReplyDTO> listSimple(Long qbId, int limit);
}
