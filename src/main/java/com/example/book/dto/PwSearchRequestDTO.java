package com.example.book.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PwSearchRequestDTO {
  @Column(nullable = false)
  String userId;

  @Column(nullable = false)
  String email;
}
