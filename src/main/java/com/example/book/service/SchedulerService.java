package com.example.book.service;

import com.example.book.domain.finance.Subscriptions;

import java.time.LocalDate;

public interface SchedulerService {
    void processSubscriptions();
    void createTransactionFromSubscription(Subscriptions sub);
    boolean isDueToday(Subscriptions sub, LocalDate today);
}
