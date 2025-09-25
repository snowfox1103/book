package com.example.book.service;

import com.example.book.domain.qna.QnaReply;
import com.example.book.repository.QnaReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class QnaReplyServiceImpl implements QnaReplyService {

    private final QnaReplyRepository qnaReplyRepository;

    @Override
    @Transactional(readOnly = true)
    public List<QnaReply> list(Long qbId) {
        return qnaReplyRepository.findByQbIdOrderByQRIdAsc(qbId);
    }

    @Override
    public void create(Long qbId, Long userNo, String content) {
        QnaReply r = QnaReply.builder()
                .qbId(qbId)
                .userNo(userNo)
                .qrContent(content)
                .build();
        qnaReplyRepository.save(r);
    }

    @Override
    public void edit(Long qrId, Long userNo, String content) {
        QnaReply r = qnaReplyRepository.findById(qrId)
                .orElseThrow(() -> new IllegalArgumentException("Reply not found: " + qrId));
        r.changeContent(content);
    }

    @Override
    public void deleteByAdmin(Long qrId) {
        qnaReplyRepository.deleteById(qrId);
    }
}
