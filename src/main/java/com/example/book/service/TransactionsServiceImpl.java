package com.example.book.service;

import com.example.book.domain.InOrOut;
import com.example.book.domain.Transactions;
import com.example.book.dto.PageRequestDTO;
import com.example.book.dto.PageResponseDTO;
import com.example.book.dto.TransactionsDTO;
import com.example.book.repository.TransactionsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Override
    public TransactionsDTO readOneTrans(Long transId){
        Optional<Transactions> result = transactionsRepository.findById(transId);
        Transactions transactions = result.orElseThrow();
        TransactionsDTO transactionsDTO = modelMapper.map(transactions,TransactionsDTO.class);
        return transactionsDTO;
    }
    @Override
    public void modifyTrans(TransactionsDTO transactionsDTO){
        Optional<Transactions> result = transactionsRepository.findById(transactionsDTO.getTransId());
        Transactions transactions = result.orElseThrow();
        transactions.changeTransaction(transactionsDTO.getTransTitle(),transactionsDTO.getTransAmount(),
                transactionsDTO.getTransCategory(),transactionsDTO.getTransMemo(),transactionsDTO.getTransDate(),
                transactionsDTO.getTransInOut(),transactionsDTO.getSubId());
        transactionsRepository.save(transactions);
    }
    @Override
    public void removeTrans(Long transId){
        transactionsRepository.deleteById(transId);
    }
    @Override
    public PageResponseDTO<TransactionsDTO> list(PageRequestDTO pageRequestDTO){
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Long category = pageRequestDTO.getCategoriess();
        Long minn = pageRequestDTO.getAmountMin();
        Long maxx = pageRequestDTO.getAmountMax();
        LocalDate startDay = pageRequestDTO.getStartDay();
        LocalDate endDay = pageRequestDTO.getEndDay();
        InOrOut io = pageRequestDTO.getIo();
        Pageable pageable = pageRequestDTO.getPageable("transId");
        //getContent()을 호출하면 현재 페이지에 해당하는 List<Transactions> 가 반환됩니다.
        //즉, DB에서 가져온 Transactions 엔티티들의 리스트예요
        Page<Transactions> result = transactionsRepository.searchAllTrans(types,keyword,category,minn,maxx,startDay,endDay,io,pageable);
        //List<Transactions> → Stream<Transactions> 으로 바꿔줍니다.
        //이제 이 스트림에서 각 원소는 Transactions 타입 객체가 됩니다.
        List<TransactionsDTO> dtoList = result.getContent().stream()
                //map()은 스트림의 각 원소(Transactions)를 다른 타입(TransactionsDTO)으로 변환할 때 쓰는 함수입니다.
                //
                //여기서 transactions는 스트림의 각 원소를 임시로 가리키는 변수예요.
                //
                //즉, for-each 문에서의 for(Transactions t : list)의 t와 똑같은 역할이에요.
                .map(transactions -> modelMapper.map(transactions,TransactionsDTO.class)).collect(Collectors.toList());
        return PageResponseDTO.<TransactionsDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();
    }
}
