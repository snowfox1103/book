package com.example.book.service;

import com.example.book.domain.pointshop.Items;
import com.example.book.repository.ItemsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemsServiceImpl implements ItemsService {
  private final ItemsRepository itemsRepository;

  @Override
  public List<Items> getAllItems() {
    return itemsRepository.findAll();
  }
}
