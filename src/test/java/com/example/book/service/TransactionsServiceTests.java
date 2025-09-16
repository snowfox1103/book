package com.example.book.service;

import com.example.book.domain.InOrOut;
import com.example.book.dto.TransactionsDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Log4j2
class TransactionsServiceTests {
    @Autowired
    private TransactionsService transactionsService;
    @Test
    public void testRegisterTrans(){
//        log.info(transactionsService.getClass().getName());
//        TransactionsDTO transactionsDTO = TransactionsDTO.builder()
//                .transTitle("영화관")
//                .transMemo("엑시트")
//                .transInOut(InOrOut.OUT)
//                .transDate(LocalDate.of(2025,9,15))
//                .transCategory()
//                .transAmount()
//                .subId()
//                .userNo()
//
    }
}