package com.example.book.service;

import com.example.book.domain.InOrOut;
import com.example.book.dto.PageRequestDTO;
import com.example.book.dto.PageResponseDTO;
import com.example.book.dto.TransactionsDTO;
import com.example.book.repository.CategoriesRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
@Log4j2

class TransactionsServiceTests {
    @Autowired
    private TransactionsService transactionsService;
    @Autowired
    private CategoriesRepository categoriesRepository;
    @Test
    public void testRegisterTrans(){
        log.info(transactionsService.getClass().getName());
        TransactionsDTO transactionsDTO = TransactionsDTO.builder()
                .transTitle("카페")
                .transMemo("초코케이크")
                .transInOut(InOrOut.OUT)
                .transDate(LocalDate.of(2025,9,15))
                .transCategory(11L)
                .transAmount(4500L)
                .subId(null)
                .userNo(56L)
                .build();
        Long tno = transactionsService.registerTrans(transactionsDTO);
        log.info("tno: "+tno);
    }
    @Test
    public void testModifyTrans(){
        TransactionsDTO transactionsDTO = TransactionsDTO.builder()
                .transId(110L)
//                .transTitle("감자탕")
//                .transAmount(24000L)
//                .transCategory(1L)
                .transMemo("바나나케이크")
//                .subId(1L)
//                .transDate(LocalDate.of(2025,9,13))
//                .transInOut(InOrOut.OUT)
                .build();
        transactionsService.modifyTrans(transactionsDTO);
    }
    @Test
    public void testReadOneTrans(){
        TransactionsDTO t = transactionsService.readOneTrans(110L);
        log.info(t);
    }
    @Test
    public void testRemoveTrans(){
        transactionsService.removeTrans(110L);
    }
    @Test
    public void testListTrans(){
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .type("tm")
                .keyword("2")
                .page(1)
                .size(10)
                .build();
        PageResponseDTO<TransactionsDTO> responseDTO = transactionsService.list(pageRequestDTO);
        log.info(responseDTO);
    }
}