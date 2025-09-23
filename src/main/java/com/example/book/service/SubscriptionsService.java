package com.example.book.service;

import com.example.book.domain.finance.Subscriptions;
import com.example.book.dto.SubscriptionsDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface SubscriptionsService  {
  void addSubscription(SubscriptionsDTO dto);
  List<Subscriptions> getSubscriptions(Long userNo);
  void deleteSubscription(Long userNo, Long subId);
  void updateSubscription(Long subId, SubscriptionsDTO dto);
  Map<String, Long> getCategorySummary(Long userNo);
  List<Subscriptions> getDueAlertsInWindow(Long userNo, LocalDate today);
  void markAlertsShown(List<Long> ids);
}
