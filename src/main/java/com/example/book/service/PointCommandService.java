package com.example.book.service;

import com.example.book.domain.point.PointType;
import com.example.book.domain.point.UserPoint;
import com.example.book.domain.user.Users;
import com.example.book.repository.UserPointRepository;
import com.example.book.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PointCommandService {

    private final UsersRepository usersRepository;
    private final UserPointRepository userPointRepository;

    /**
     * 포인트 적립/사용 반영 (원장 기록 + users.balance 동시 갱신)
     * @param userNo  대상 사용자
     * @param amount  금액(절대값)
     * @param type    EARN/USE
     * @param reason  사유
     * @param when    거래 시각(LocalDateTime)
     */
    @Transactional
    public void applyPoint(Long userNo, long amount, PointType type, String reason, LocalDateTime when) {
        // 1) 사용자 행 잠그기(경합 방지)
        Users user = usersRepository.findByUserNoForUpdate(userNo);
        if (user == null) throw new IllegalArgumentException("user not found");

        long signed = (type == PointType.EARN ? amount : -amount);
        long newBalance = user.getBalance() + signed;
        if (newBalance < 0) throw new IllegalStateException("포인트 잔액 부족");

        // 2) 원장 기록 + 거래 직후 잔액 스냅샷 저장
        UserPoint up = UserPoint.builder()
                .userNo(userNo)
                .pointStartDate(when)
                .pointAmount(amount)
                .pointType(type)
                .pointReason(reason)
                .runningBalance(newBalance)
                .build();
        userPointRepository.save(up);

        // 3) 현재 잔액 갱신
        user.setBalance(newBalance);
        usersRepository.save(user);
    }
}
