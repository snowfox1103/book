package com.example.book.repository;

import com.example.book.domain.InOrOut;
import com.example.book.domain.Transactions;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Log4j2
class TransactionsRepositoryTests {
    @Autowired
    private TransactionsRepository transactionsRepository;
    @Test
    public void testInsertTranscations(){
        long a = 1L;
        Transactions trans = Transactions.builder()
                .transTitle("dddd")
                .transAmount(12121L)
                .transCategory(1L)
                .transInOut(InOrOut.OUT)
                .transDate(LocalDate.of(2025,9,17))
                .subId(1L)
                .userNo(15L)
                .build();
        transactionsRepository.save(trans);
}
    @Test
    public void testSelectTransactions(){
        Long tno = 109L;
        Optional<Transactions> result = transactionsRepository.findById(tno);
        Transactions transactions = result.orElseThrow();
        log.info(transactions);
    }
    @Test
    public void testUpdateTransactions(){
        Long tno = 109L;
        Optional<Transactions> result = transactionsRepository.findById(tno);
        Transactions transactions = result.orElseThrow();
        transactions.changeTransaction(null,null,null,"포카칩",null,null,null);
        transactionsRepository.save(transactions);
    }
    @Test
    public void testDeleteTransactions(){
        Long tno = 108L;
        transactionsRepository.deleteById(tno);
    }
    @Test
    public void testPagingTransactions(){
        Pageable pageable = PageRequest.of(1,10, Sort.by("transId").descending());
        Page<Transactions> result = transactionsRepository.findAll(pageable);

        log.info("total count: "+result.getTotalElements());
        log.info("total pages: "+result.getTotalPages());
        log.info("page number: "+result.getNumber());
        log.info("page size: "+result.getSize());
        List<Transactions> transList = result.getContent();
        transList.forEach(trans->log.info(trans));
    }
    @Test
    public void testSearchTransactions(){
        Pageable pageable = PageRequest.of(1,10,Sort.by("transId").descending());
        transactionsRepository.searchTrans(pageable);
    }
    @Test
    public void testSearchAllTrans(){
        String[] types = {"t","m"};
        String keyword = "3";
        Long category = 1L;
        Long minn = null;
        Long maxx = null;
        LocalDate start = LocalDate.of(2025,9,7);
        LocalDate end = null;
        InOrOut io = InOrOut.OUT;
        Pageable pageable = PageRequest.of(0,10,Sort.by("transId").descending());
        Page<Transactions> result = transactionsRepository.searchAllTrans(types,keyword,category,minn,maxx,start,end,io,pageable);
        log.info(result.getTotalPages());
        log.info(result.getSize());
        log.info(result.getNumber());
        log.info(result.hasPrevious()+": "+result.hasNext());
        result.getContent().forEach(trans->log.info(trans));
    }
}