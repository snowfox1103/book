package com.example.book.service;

import com.example.book.domain.qna.QnaReply;
import java.util.List;

public interface QnaReplyService {
    List<QnaReply> list(Long qbId);
    void create(Long qbId, Long userNo, String content);
    void edit(Long qrId, Long userNo, String content);
    void deleteByAdmin(Long qrId);
}
