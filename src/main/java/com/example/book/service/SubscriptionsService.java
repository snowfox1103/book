package com.example.book.service;

import com.example.book.domain.finance.Subscriptions;
import com.example.book.dto.SubscriptionsDTO;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface SubscriptionsService  {
  void addSubscription(SubscriptionsDTO dto);
  List<Subscriptions> getSubscriptions(Long userNo);
  void deleteSubscription(Long userNo, Long subId);
  void updateSubscription(Long subId, SubscriptionsDTO dto);
  Map<String, Long> getCategorySummary(Long userNo);
}
