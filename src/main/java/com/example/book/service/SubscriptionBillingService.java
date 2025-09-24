package com.example.book.service;

import java.util.Map;

public interface SubscriptionBillingService {
  int ensurePostedForToday(Long userNo);
  Map<String, Object> threeMonthTrend(Long userNo);
}
