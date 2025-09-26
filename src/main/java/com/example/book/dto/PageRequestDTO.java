package com.example.book.dto;

import com.example.book.domain.finance.InOrOut;
import com.example.book.domain.qna.Qna;
import com.example.book.repository.QnaRepository;
import com.example.book.security.dto.UsersSecurityDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {

  @Builder.Default
  private int page = 1;
  @Builder.Default
  private int size = 10;
  private String type; // 검색의 종류 t,m,tm
  private String keyword;
  private Long amountMin;
  private Long amountMax;
  private LocalDate startDay;
  private LocalDate endDay;
  private InOrOut io;
  private Integer selectYear;
  private Integer selectMonth;
  private Long categories;
  private Long userNo;

  public String[] getTypes(){
    if(type == null || type.isEmpty()){
      return null;
    }
    return type.split("");
  }
  public Pageable getPageable(String...props) {
    return PageRequest.of(this.page -1, this.size, Sort.by(props).descending());
  }
  private String link;

  public String getLink() {
    if(link == null){
      StringBuilder builder = new StringBuilder();
      builder.append("page=" + this.page);
      builder.append("&size=" + this.size);
      if(type != null && type.length() > 0){
        builder.append("&type=" + type);
      }
      if(keyword != null){
        try {
          builder.append("&keyword=" + URLEncoder.encode(keyword,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
        }
      }
      if(amountMin != null && amountMin >= 0) builder.append("&amountMin="+amountMin);
      if(amountMax != null) builder.append("&amountMax="+amountMax);
      if(startDay != null) builder.append("&startDay="+startDay);
      if(endDay != null) builder.append("&endDay="+endDay);
      if(io != null) builder.append("&io="+io);

      link = builder.toString();
    }
    return link;
  }

  public Pageable getPageable(String sort) {
    int pageIndex = Math.max(this.page - 1, 0);
    return PageRequest.of(pageIndex, this.size, Sort.by(Sort.Direction.DESC, sort));
  }
}
