package com.example.book.service;

import com.example.book.domain.notice.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface NoticeService {
    Page<Notice> list(Pageable pageable, Sort sort);

    Notice read(Long id);

    Long write(Long adminUserNo, String title, String content);

    void edit(Long id, String title, String content);

    void delete(Long id);

    Notice save(Notice n);
}
