package com.example.book.domain.finance;

import com.example.book.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "budgets")
@Getter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class Budgets extends BaseEntity {

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

    @Column(name = "budNotice")
    private Boolean budNotice;


    public void changeBudget(Long budAmount,boolean budNotice){
        if(budAmount != null) this.budAmount = budAmount;
        if(this.budCurrent != null && this.budAmount != null){
            this.budIsOver = this.budCurrent > this.budAmount;
        }
        if(this.budNotice != null) this.budNotice = budNotice;
    }
    public void autoUpdateCurrentMoney(Long budCurrent){
        if(budCurrent != null) this.budCurrent = budCurrent;
        if(this.budCurrent != null && this.budAmount != null){
            this.budIsOver = this.budCurrent > this.budAmount;
        }
    }
}
