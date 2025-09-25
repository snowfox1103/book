package com.example.book.service;

import com.example.book.domain.qna.Qna;
import com.example.book.repository.QnaRepository;
import com.example.book.service.QnaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QnaServiceImpl implements QnaService {

    private final QnaRepository qnaRepository;

    @Override
    public Page<Qna> listForUser(Long currentUserNo, boolean isAdmin, Pageable pageable) {
        return isAdmin
                ? qnaRepository.findAll(pageable)
                : qnaRepository.findVisibleForUser(currentUserNo, pageable);
    }

    @Override
    public Qna getForRead(Long qbId, Long currentUserNo, boolean isAdmin) {
        Qna q = qnaRepository.findById(qbId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        boolean blind = Boolean.TRUE.equals(q.getQBBlind());
        if (blind && !(isAdmin || isOwner(q, currentUserNo))) {
            // 타인 접근 시 존재하지 않는 것처럼 404 위장
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return q;
    }

    @Override
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

    @Override
    @Transactional
    public void update(Long qbId, Long currentUserNo, String title, String content, boolean blind) {
        Qna q = qnaRepository.findById(qbId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!isOwner(q, currentUserNo)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        q.change(title, content, blind);
    }

    @Override
    @Transactional
    public void delete(Long qbId, Long currentUserNo) {
        Qna q = qnaRepository.findById(qbId)
                .orElseThrow(() -> new IllegalArgumentException("QnA not found: " + qbId));

        boolean isOwner = (currentUserNo != null && currentUserNo.equals(q.getUserNo()));

        // ★ 관리자 권한 확인
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);

        if (!(isOwner || isAdmin)) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        qnaRepository.delete(q);
    }

    // ---- private helpers ----
    private boolean isOwner(Qna q, Long userNo) {
        return q.getUserNo() != null && q.getUserNo().equals(userNo);
    }
}
