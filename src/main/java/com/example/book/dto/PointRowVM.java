// src/main/java/com/example/book/dto/PointRowVM.java
package com.example.book.dto;

import java.time.LocalDateTime;

public class PointRowVM {
    private LocalDateTime regdate;
    private String type;   // "EARN" | "USE"
    private String memo;
    private long delta;    // +적립 / -사용
    private long balance;  // 누적 잔액

    public PointRowVM(LocalDateTime regdate, String type, String memo, long delta, long balance) {
        this.regdate = regdate; this.type = type; this.memo = memo; this.delta = delta; this.balance = balance;
    }
    public LocalDateTime getRegdate(){ return regdate; }
    public String getType(){ return type; }
    public String getMemo(){ return memo; }
    public long getDelta(){ return delta; }
    public long getBalance(){ return balance; }
}
