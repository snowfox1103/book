package com.example.book.service;

import com.example.book.domain.Qna;
import com.example.book.repository.QnaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QnaService {

    private final QnaRepository qnaRepository;

    /** 목록: 관리자면 전체, 일반 사용자는 공개글+내 글 */
    public Page<Qna> listForUser(Long currentUserNo, boolean isAdmin, Pageable pageable) {
        return isAdmin
                ? qnaRepository.findAll(pageable)
                : qnaRepository.findVisibleForUser(currentUserNo, pageable);
    }

    /** 조회: blind 글은 작성자/관리자만, 타인은 404 위장 */
    public Qna getForRead(Long qBId, Long currentUserNo, boolean isAdmin) {
        Qna q = qnaRepository.findById(qBId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        boolean blind = Boolean.TRUE.equals(q.getQBBlind());
        if (blind && !(isAdmin || isOwner(q, currentUserNo))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return q;
    }

    /** 작성 */
    @Transactional
    public Long create(Long currentUserNo, String title, String content, boolean blind) {
        Qna q = Qna.builder()
                .userNo(currentUserNo)
                .qBTitle(title)
                .qBContent(content)
                .qBBlind(blind)
                .build();
        return qnaRepository.save(q).getQBId();
    }

    /** 수정: 작성자 본인만(관리자 수정 권한은 정책에 따라 추가 가능) */
    @Transactional
    public void update(Long qBId, Long currentUserNo, String title, String content, boolean blind) {
        Qna q = qnaRepository.findById(qBId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!isOwner(q, currentUserNo)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        q.change(title, content, blind);
    }

    /** 삭제: 작성자 본인만 */
    @Transactional
    public void delete(Long qBId, Long currentUserNo) {
        Qna q = qnaRepository.findById(qBId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!isOwner(q, currentUserNo)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        qnaRepository.delete(q);
    }

    private boolean isOwner(Qna q, Long userNo) {
        return q.getUserNo() != null && q.getUserNo().equals(userNo);
    }
    testing...
}
