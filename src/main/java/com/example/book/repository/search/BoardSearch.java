package com.example.book.repository.search;

import com.example.book.domain.Board;
import com.example.book.dto.BoardListAllDTO;
import com.example.book.dto.BoardListReplyCountDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardSearch {
  Page<Board> search1(Pageable pageable);
  Page<Board> searchAll(String[] types, String keyword, Pageable pageable);
  Page<BoardListReplyCountDTO> searchWithReplyCount(String[] types,
                                                    String keyword,
                                                    Pageable pageable);
  Page<BoardListAllDTO> searchWithAll(String[] types, String keyword, Pageable pageable);
}
