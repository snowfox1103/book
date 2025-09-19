package com.example.book.repository;

import com.example.book.domain.qna.QnaReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QnaReplyRepository extends JpaRepository<QnaReply,Long> {
    List<QnaReply> findByQbId(Long qBId, org.springframework.data.domain.Sort sort);

    //집계
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
}
