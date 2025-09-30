package com.example.book.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class PageResponseDTO<E>  {
  private int page;
  private int size;
  private int total;
  private int start;   // 시작 페이지
  private int end;     // 끝 페이지
  private boolean prev;
  private boolean next;
  private List<E> dtoList;

  @Builder(builderMethodName = "withAll")
  public PageResponseDTO(PageRequestDTO pageRequestDTO, List<E> dtoList, int total) {
    this.size = pageRequestDTO.getSize();
    this.total = total;
    this.dtoList = dtoList;

    // 총 페이지 수
    int last = (int) Math.ceil(total / (double) size);

    // 🔒 검색 결과 0건일 때: 네비게이션 숨김 신호(start=end=0)
    if (last <= 0) {
      this.page = 1;        // 뷰에서 표시용(네비는 렌더 안 함)
      this.start = 0;
      this.end = 0;
      this.prev = false;
      this.next = false;
      return;
    }

    // ✅ 현재 페이지 보정 (1 ~ last 범위로 clamp)
    int reqPage = pageRequestDTO.getPage();
    this.page = Math.max(1, Math.min(reqPage, last));

    // 페이지 블록 계산(10개 단위)
    int tempEnd = (int) (Math.ceil(this.page / 10.0)) * 10;
    this.start = tempEnd - 9;
    this.end = Math.min(tempEnd, last);

    this.prev = this.start > 1;
    this.next = this.end < last;
  }
}
