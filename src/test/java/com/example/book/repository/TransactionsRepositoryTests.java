package com.example.book.repository;

import com.example.book.domain.finance.InOrOut;
import com.example.book.domain.finance.Transactions;
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
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
class TransactionsRepositoryTests {
    @Autowired
    private TransactionsRepository transactionsRepository;
    @Autowired
    private BoardRepository boardRepository;

    @Test
    public void insertTranscations(){
        long a = 1L;
        IntStream.rangeClosed(12,100).forEach(i->{
            Transactions trans = Transactions.builder()
                    .transTitle("넷플릭스"+i)
                    .transAmount(14900L)
                    .transCategory(3L)
                    .transInOut(InOrOut.OUT)
                    .transDate(LocalDate.of(2025,9,9))
                    .subId(1L)
                    .userNo(35L)
                    .build();
            transactionsRepository.save(trans);
        });
    }
    @Test
    public void testSelectTransactions(){
        Long tno = 4L;
        Optional<Transactions> result = transactionsRepository.findById(tno);
        Transactions transactions = result.orElseThrow();
        log.info(transactions);
    }
    @Test
    public void testUpdateTransactions(){
        Long tno = 4L;
        Optional<Transactions> result = transactionsRepository.findById(tno);
        Transactions transactions = result.orElseThrow();
        transactions.changeTransaction("김밥천국",12200L,1L,"라면,김밥",LocalDate.of(2025,9,14),InOrOut.OUT,null);
        transactionsRepository.save(transactions);
    }
    @Test
    public void testDeleteTransactions(){
        Long tno = 10L;
        transactionsRepository.deleteById(tno);
    }
    @Test
    public void testPagingTransactions(){
        Pageable pageable = PageRequest.of(3,10, Sort.by("transId").descending());
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
        String keyword = "라면";
        Long minn = null;
        Long maxx = null;
        LocalDate a = LocalDate.of(2025,9,7);
        LocalDate b = null;
        InOrOut io = InOrOut.OUT;
        Pageable pageable = PageRequest.of(0,10,Sort.by("transId").descending());
        Page<Transactions> result = transactionsRepository.searchAllTrans(types,keyword,minn,maxx,a,b,io,pageable);
        log.info(result.getTotalPages());
        log.info(result.getSize());
        log.info(result.getNumber());
        log.info(result.hasPrevious()+": "+result.hasNext());
        result.getContent().forEach(trans->log.info(trans));
    }
}