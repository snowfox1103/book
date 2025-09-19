package com.example.book.service;

import com.example.book.domain.notice.Notice;
import com.example.book.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public Page<Notice> list(Pageable pageable) {
        return noticeRepository.findAllByOrderByRegDateDesc(pageable);
    }

    public Notice read(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Long write(Long adminUserNo, String title, String content) {
        Notice n = Notice.builder()
                .userNo(adminUserNo)
                .nBTitle(title)
                .nBContent(content)
                .build();
        return noticeRepository.save(n).getNBId();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void edit(Long id, String title, String content) {
        Notice n = read(id);
        n.change(title, content);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void delete(Long id) {
        noticeRepository.deleteById(id);
    }

    @Transactional
    public Notice save(Notice n) {
        return noticeRepository.save(n);
    }

    public Page<Notice> list(Pageable pageable, Sort sort) {
        // 정렬 파라미터 받도록 구성했으면 여기서 사용
        return noticeRepository.findAll(pageable);
    }
}
