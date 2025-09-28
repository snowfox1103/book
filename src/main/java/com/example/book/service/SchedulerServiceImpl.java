package com.example.book.service;

import com.example.book.domain.finance.InOrOut;
import com.example.book.domain.finance.Subscriptions;
import com.example.book.dto.TransactionsDTO;
import com.example.book.repository.SubscriptionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
@Service
@RequiredArgsConstructor
public class SchedulerServiceImpl implements SchedulerService{

    private final SubscriptionsRepository subscriptionRepository;
    private final TransactionsService transactionsService;

    @Override
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정 실행
    public void processSubscriptions() {
        LocalDate today = LocalDate.now();
        List<Subscriptions> subscriptions = subscriptionRepository.findAll();

        for (Subscriptions sub : subscriptions) {
            if (isDueToday(sub, today)) {
                createTransactionFromSubscription(sub);
            }
        }
    }
    //오늘이 결제일인지 확인
    @Override
    public boolean isDueToday(Subscriptions sub, LocalDate today) {
        LocalDate startDate = LocalDate.of(today.getYear(), today.getMonth(), sub.getSubPayDate());

        return switch (sub.getSubPeriodUnit()) {
            case DAY -> !today.isBefore(startDate) &&
                    (ChronoUnit.DAYS.between(startDate, today) % sub.getSubPeriodValue() == 0);
            case WEEK -> !today.isBefore(startDate) &&
                    (ChronoUnit.WEEKS.between(startDate, today) % sub.getSubPeriodValue() == 0);
            case MONTH -> today.getDayOfMonth() == sub.getSubPayDate() &&
                    (ChronoUnit.MONTHS.between(startDate, today) % sub.getSubPeriodValue() == 0);
            case YEAR -> today.getDayOfMonth() == sub.getSubPayDate() &&
                    today.getMonthValue() == startDate.getMonthValue() &&
                    (ChronoUnit.YEARS.between(startDate, today) % sub.getSubPeriodValue() == 0);
        };
    }
    //입출금 등록
    @Override
    public void createTransactionFromSubscription(Subscriptions sub) {
        TransactionsDTO transactionsDTO = TransactionsDTO.builder()
                .userNo(sub.getUsers().getUserNo())
                .transTitle(sub.getSubTitle())
                .transAmount(sub.getSubAmount())
                .transCategory(sub.getCategories().getCatId())
                .transDate(LocalDate.now())
                .transInOut(InOrOut.OUT)
                .subId(sub.getSubId())
                .build();

        transactionsService.registerTrans(transactionsDTO);
    }
}
