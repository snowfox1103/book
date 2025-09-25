package com.example.book.service;

import com.example.book.domain.notice.Notice;
import com.example.book.repository.NoticeRepository;
import com.example.book.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    @Override
    public Page<Notice> list(Pageable pageable) {
        return noticeRepository.findAll(pageable);
    }

    @Override
    public Page<Notice> list(Pageable pageable, Sort sort) {
        return noticeRepository.findAll(pageable); // 필요시 sort 반영
    }

    @Override
    public Notice read(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
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

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void edit(Long id, String title, String content) {
        Notice n = read(id);
        n.change(title, content);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void delete(Long id) {
        noticeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Notice save(Notice n) {
        return noticeRepository.save(n);
    }
}
