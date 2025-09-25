package com.example.book.service;

import com.example.book.domain.qna.Qna;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QnaService {
    Page<Qna> listForUser(Long currentUserNo, boolean isAdmin, Pageable pageable);
    Qna getForRead(Long qbId, Long currentUserNo, boolean isAdmin);
    Long create(Long currentUserNo, String title, String content, boolean blind);
    void update(Long qbId, Long currentUserNo, String title, String content, boolean blind);
    void delete(Long qbId, Long currentUserNo);
}
