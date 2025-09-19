package com.example.book.service;

import com.example.book.domain.qna.QnaReply;
import com.example.book.repository.QnaReplyRepository;
import lombok.RequiredArgsConstructor;
import static org.springframework.data.domain.Sort.Direction.ASC;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QnaReplyService {

    private final QnaReplyRepository replyRepo;

    public List<QnaReply> list(Long qBId) {
        return replyRepo.findByQbId(qBId, Sort.by(ASC, "regDate"));
    }

    @Transactional
    public Long create(Long qBId, Long adminUserNo, String content) {
        QnaReply r = QnaReply.builder()
                .qbId(qBId)
                .userNo(adminUserNo)
                .qBContent(content)
                .build();
        return replyRepo.save(r).getQRId();
    }

    @Transactional
    public void edit(Long qRId, Long adminUserNo, String content) {
        QnaReply r = replyRepo.findById(qRId).orElseThrow();
        r.changeContent(content);
    }

    @Transactional
    public void delete(Long qRId, Long adminUserNo) {
        replyRepo.deleteById(qRId);
    }
}
