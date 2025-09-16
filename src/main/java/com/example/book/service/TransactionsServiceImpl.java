package com.example.book.service;

import com.example.book.domain.Transactions;
import com.example.book.dto.TransactionsDTO;
import com.example.book.repository.TransactionsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class TransactionsServiceImpl implements TransactionsService{
    private final ModelMapper modelMapper;
    private final TransactionsRepository transactionsRepository;
    @Override
    public Long registerTrans(TransactionsDTO transactionsDTO){
        Transactions transactions = modelMapper.map(transactionsDTO,Transactions.class);
        Long tno = transactionsRepository.save(transactions).getTransId();
        return tno;
    }
}
