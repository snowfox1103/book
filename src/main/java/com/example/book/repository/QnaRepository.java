package com.example.book.repository;

import com.example.book.domain.Qna;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QnaRepository extends JpaRepository<Qna, Long> {

    // 일반 사용자 목록: 공개글 + 내 글
    @Query("select q from Qna q where q.qBBlind = false or q.userNo = :userNo")
    Page<Qna> findVisibleForUser(@Param("userNo") Long userNo, Pageable pageable);

    // 마이페이지: 내 글만
    Page<Qna> findByUserNo(Long userNo, Pageable pageable);

    // 검색(제목/내용) + 가시성 적용
    @Query("""
        select q from Qna q
        where (q.qBBlind = false or q.userNo = :userNo)
          and (lower(q.qBTitle) like lower(concat('%', :kw, '%'))
               or lower(q.qBContent) like lower(concat('%', :kw, '%')))
        """)
    Page<Qna> searchVisible(@Param("userNo") Long userNo,
                            @Param("kw") String keyword,
                            Pageable pageable);
}
