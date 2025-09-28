package com.example.book.repository;

import com.example.book.domain.qna.QnaReply;
import com.example.book.dto.QnaReplyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/** QnaReplyRepository */
public interface QnaReplyRepository extends JpaRepository<QnaReply, Long> {

    /** qbId 기준 페이징 목록 */
    Page<QnaReply> findByQbId(Long qbId, Pageable pageable);

    /** qbId 기준 오름차순(답변 ID) 목록 */
    @Query("select r from QnaReply r where r.qbId = :qbId order by r.qRId asc")
    List<QnaReply> findByQbIdOrderByQRIdAsc(@Param("qbId") Long qbId);

    /** 여러 게시글의 답변 개수 집계 */
    @Query("""
        select r.qbId as qbId, count(r) as cnt
        from QnaReply r
        where r.qbId in :ids
        group by r.qbId
    """)
    List<ReplyCountRow> countByQbIdIn(@Param("ids") List<Long> ids);

    interface ReplyCountRow {
        Long getQbId();
        Long getCnt();
    }

    @Query("""
    select new com.example.book.dto.QnaReplyDTO(
        r.qRId, r.qbId, r.qrContent, u.userId, r.regDate, r.modDate
    )
    from QnaReply r
    join Users u on u.userNo = r.userNo
    where r.qbId = :qBId
    order by r.qRId asc
""")
    List<QnaReplyDTO> findDtoByQbId(@Param("qBId") Long qBId);

    @Query("""
      select r.qbId
      from QnaReply r
      where r.qbId in :qbIds
      group by r.qbId
    """)
    List<Long> findAnsweredQbIds(@Param("qbIds") List<Long> qbIds);
}
