package com.example.book.service;

import com.example.book.domain.qna.Qna;
import com.example.book.repository.QnaRepository;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QnaServiceImpl implements QnaService {

    private final QnaRepository qnaRepository;

    @Override
    public Page<Qna> listForUser(Long currentUserNo, boolean isAdmin, Pageable pageable) {
        // ★ 최신순 DESC 메서드로 교체
        return isAdmin
                ? qnaRepository.findAllWithWriterOrderByRegDateDesc(pageable)
                : qnaRepository.findVisibleWithWriterOrderByRegDateDesc(currentUserNo, pageable);
    }

    @Override
    public Qna getForRead(Long qbId, Long currentUserNo, boolean isAdmin) {
        Qna q = qnaRepository.findById(qbId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        boolean blind = Boolean.TRUE.equals(q.getQBBlind());
        if (blind && !(isAdmin || isOwner(q, currentUserNo))) {
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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).anyMatch("ROLE_ADMIN"::equals);

        if (!(isOwner || isAdmin)) throw new AccessDeniedException("삭제 권한이 없습니다.");

        qnaRepository.delete(q);
    }

    private boolean isOwner(Qna q, Long userNo) {
        return q.getUserNo() != null && q.getUserNo().equals(userNo);
    }

    @Override
    public List<Qna> getRecentInquiries(Long userNo) {
        // ★ 최신 10개가 필요하면 DESC가 직관적
        return qnaRepository.findTop10ByUserNoOrderByRegDateDesc(userNo);
    }
}
