package com.example.book.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "budgets")
@Getter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class Budgets extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budgetId")
    private Long budgetId;

    @Column(name = "userNo")
    private Long userNo;

    @Column(name = "budCategory")
    private Long budCategory;

    @Column(name = "budAmount")
    private Long budAmount;

    @Column(name = "budCurrent")
    private Long budCurrent;

    @Column(name = "budIsOver")
    private Boolean budIsOver;

    @Column(name = "budYear")
    private Integer budYear;

    @Column(name = "budMonth")
    private Integer budMonth;

    @Column(name = "budOver")
    private Boolean budOver;

    @Column(name = "budReduction")
    private Boolean budReduction;

    @Column(name = "budNotice")
    private Boolean budNotice;
    //-------------------------------파라미터 수정 요망
    public void changeBudget(Long budAmount,Long budCategory,Long budCurrent,boolean budIsOver, int budYear, int budMonth){
        this.budAmount = budAmount;
        this.budCategory = budCategory;
        this.budCurrent = budCurrent;
        this.budIsOver = budIsOver;
        this.budYear = budYear;
        this.budMonth = budMonth;
    }
}
