package com.example.book.service;

import com.example.book.domain.notice.Notice;
import com.example.book.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    /** 목록 */
    @Override
    public Page<Notice> list(Pageable pageable, Sort sort) {
        // 전달받은 sort를 강제로 적용한 Pageable로 변환
        Pageable pageReq = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );
        return noticeRepository.findAll(pageReq);
    }

    /** 단건 조회 */
    @Override
    public Notice read(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("notice not found: " + id));
    }

    /** 등록 */
    @Override
    @Transactional
    public Long write(Long adminUserNo, String title, String content) {
        Notice n = new Notice();
        n.setUserNo(adminUserNo);
        n.setNBTitle(title);
        n.setNBContent(content);

        return noticeRepository.save(n).getNBId();
    }

    /** 수정 */
    @Override
    @Transactional
    public void edit(Long id, String title, String content) {
        Notice n = read(id);
        n.setNBTitle(title);
        n.setNBContent(content);
    }

    /** 삭제 */
    @Override
    @Transactional
    public void delete(Long id) {
        noticeRepository.deleteById(id);
    }

    /** 저장(직접 저장이 필요할 때) */
    @Override
    @Transactional
    public Notice save(Notice n) {
        return noticeRepository.save(n);
    }
}
