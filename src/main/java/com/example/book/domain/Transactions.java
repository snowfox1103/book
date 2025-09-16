package com.example.book.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Transactions extends BaseEntity{

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

    @Column(name = "transCategory")
    private Long transCategory;

    @Column(name = "transMemo")
    private String transMemo;

    @Column(name = "subId")
    private Long subId;

    public void changeTransaction(String title,Long amount, Long category,String memo, LocalDate date, InOrOut io, Long subId){
        this.transTitle = title;
        this.transAmount = amount;
        this.transCategory = category;
        this.transMemo = memo;
        this.transDate = date;
        this.transInOut = io;
        this.subId = subId;
    }
}
