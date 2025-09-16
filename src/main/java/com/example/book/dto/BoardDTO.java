package com.example.book.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO {
  private Long bno;
  //첨부파일의 이름들
  private List<String> fileNames;
  @NotEmpty
  private String title;
  @NotEmpty
  private String content;
  @NotEmpty
  private String writer;
  private LocalDateTime regDate;
  private LocalDateTime modDate;
}
