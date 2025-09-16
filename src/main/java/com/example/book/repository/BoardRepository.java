package com.example.book.repository;

import com.example.book.domain.Board;
import com.example.book.repository.search.BoardSearch;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardSearch {
  @Query(value = "select now()", nativeQuery = true)
  String getTime();

  @EntityGraph(attributePaths = {"imageSet"})
  @Query("select b from Board b where b.bno =:bno")
  Optional<Board> findByIdWithImages(Long bno);
}
