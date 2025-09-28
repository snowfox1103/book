package com.example.book.repository;

import com.example.book.domain.qna.Qna;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface QnaRepository extends JpaRepository<Qna, Long> {

    // 공개/본인 글 필터 (정렬은 Pageable 또는 아래 DESC 고정 메서드 사용 권장)
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

    // --- 여기부터 최신순(작성일 DESC) 고정 버전 ---

    // 관리자/전체 목록: 작성자 join + regDate DESC
    @Query("""
        select q from Qna q
        join fetch q.writer w
        order by q.regDate desc
        """)
    Page<Qna> findAllWithWriterOrderByRegDateDesc(Pageable pageable);

    // 사용자 목록(본인+공개): 작성자 join + regDate DESC
    @Query("""
        select q from Qna q
        join fetch q.writer w
        where (q.qBBlind = false or q.userNo = :userNo)
        order by q.regDate desc
        """)
    Page<Qna> findVisibleWithWriterOrderByRegDateDesc(@Param("userNo") Long userNo,
                                                      Pageable pageable);

    // 마이페이지 등 최근 글 조회 시도 최신순이 자연스럽지만,
    // "Top10 최신"이 필요하면 아래처럼 DESC로 두는 게 일반적임.
    List<Qna> findTop10ByUserNoOrderByRegDateDesc(@Param("userNo") Long userNo);

    //0928 조덕진 회원탈퇴용
    @Modifying
    @Query("delete from Qna q where q.userNo = :userNo")
    void deleteAllByUserNo(@Param("userNo") Long userNo);
}
