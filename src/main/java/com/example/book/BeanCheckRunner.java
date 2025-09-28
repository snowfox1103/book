package com.example.book;

import com.example.book.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BeanCheckRunner implements ApplicationRunner {
    private final TransactionsRepository txRepo;
    private final UserPointRepository userPointRepo;
    private final UsersRepository usersRepository;
    private final BudgetsRepository budgetsRepository;

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("[OK] txRepo=" + txRepo.getClass().getName());
        System.out.println("[OK] userPointRepo=" + userPointRepo.getClass().getName());
        System.out.println("[OK] usersRepository=" + usersRepository.getClass().getName());
        System.out.println("[OK] budgetsRepository=" + budgetsRepository.getClass().getName());
    }
}
