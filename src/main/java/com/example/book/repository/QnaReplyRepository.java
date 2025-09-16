package com.example.book.repository;

import com.example.book.domain.QnaReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QnaReplyRepository extends JpaRepository<QnaReply,Long> {
    List<QnaReply> findByQBIdOrderByRegdateAsc(Long qBId);
}
