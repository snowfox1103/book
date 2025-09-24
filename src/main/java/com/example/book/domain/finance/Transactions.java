package com.example.book.domain.finance;

import com.example.book.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Transactions extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transId")
    private Long transId;

    @Column(name = "userNo")
    private Long userNo;

    @Column(name = "transTitle")
    private String transTitle;

    @Column(name = "transAmount")
    private Long transAmount;

    @Column(name = "transDate")
    private LocalDate transDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "transInOut")
    private InOrOut transInOut;

    private Long transCategory;

    @Column(name = "transMemo")
    private String transMemo;

    @Column(name = "subId")
    private Long subId;

    public void changeTransaction(String title,Long amount, Long category,String memo, LocalDate date, InOrOut io, Long subId){
        if(title != null) this.transTitle = title;
        if(amount != null) this.transAmount = amount;
        if(category != null) this.transCategory = category;
        if(memo != null) this.transMemo = memo;
        if(date != null) this.transDate = date;
        if(io != null) this.transInOut = io;
        if(subId != null) this.subId = subId;
    }
}
