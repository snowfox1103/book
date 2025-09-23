package com.example.book.repository;

import com.example.book.domain.qna.Qna;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QnaRepository extends JpaRepository<Qna, Long> {

    @Query("select q from Qna q where q.qBBlind = false or q.userNo = :userNo")
    Page<Qna> findVisibleForUser(@Param("userNo") Long userNo, Pageable pageable);

    Page<Qna> findByUserNo(Long userNo, Pageable pageable);

    @Query("""
        select q from Qna q
        where (q.qBBlind = false or q.userNo = :userNo)
          and (lower(q.qBTitle) like lower(concat('%', :kw, '%'))
               or lower(q.qBContent) like lower(concat('%', :kw, '%')))
        """)
    Page<Qna> searchVisible(@Param("userNo") Long userNo,
                            @Param("kw") String keyword,
                            Pageable pageable);

    List<Qna> findTop10ByUserNoOrderByRegDateDesc(@Param("userNo") Long userNo); //마이페이지 문의글 리스트 조회 목적
}
